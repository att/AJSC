/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import ajsc.util.MessageMgr

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.io.FileType


/**
 * VandelayService is a static Ajsc service providing functions for importing and exporting Ajsc
 * objects (Routes, Docs, and Properties). Ajsc objects are imported from and exported to a zip file.
 * VandelayService is stateless, providing only static behavior and thus does not need to be initialized.
 */
class VandelayService {
	def routeValidatorService
	def swaggerService
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "VandelayService"
	def static DEFAULT_EXPORT_DIR = "/export/"
	def static STAGEDEPLOYMENT ="stageDeployment"
	def ajscHome = System.getProperty("AJSC_HOME") ?: System.getenv("AJSC_HOME")

	static transactional = false

	final static Logger logger = LoggerFactory.getLogger(VandelayService.class)

	final static Logger audit = LoggerFactory.getLogger("${VandelayService.class.name}.AUDIT")

	def messageMap = ["MODULE":"ajsc","COMPONENT":"VandelayService","METHOD":"onMessage(Message<Object> msg)","MSGNUM":"3"]
	def msgCode

	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"VandelayService",
			"METHOD":method,
			"MSGNUM":msgnum
		]
	}

	/**
	 * Takes a zip file and creates Ajsc objects from the contents.  The zip file should be
	 * structured as follows:
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/routes/*.route</code>
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/docs/*.*</code>
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/props/&lt;serviceName&gt;.props</code>
	 *
	 * @param file the zip file
	 * @return a Map containing information about what was imported
	 * <ul>
	 * <li><code>payloadOriginalFilename</code>: zip file name String
	 * <li><code>payloadSize</code>: size in bytes of zip file,
	 * <li><code>entries</code>: Map<String, String> of ZipEntry key and contents for all entries,
	 * <li><code>routes</code>: subset of entries containing only routes,
	 * <li><code>docs</code>: subset of entries containing only docs,
	 * <li><code>props</code>: subset of entries containing only props
	 */
	def importRuntimeEnvironment(file){
		def LMETHOD = "importRuntimeEnvironment(file)"

		if (file != null && !file.empty) {
			return processImportRuntime(file.inputStream)
		}
	}

	def processImportRuntime( fis) {

		def LMETHOD = "processImportRuntime(fis)"

		def CONTEXT_PATTERN =/runtime\/context\/.*\.context/
		def DEPLOYMENT_PACKAGE_PATTERN =/runtime\/deploymentPackage\/.*\.json/
		def SERVICE_PROPERTIES_PATTERN =/runtime\/serviceProperties\/.*\.props/
		def SHIRO_ROLE_PATTERN =/runtime\/shiroRole\/.*\.json/
		def SHIRO_USER_PATTERN = /runtime\/shiroUser\/.*\.json/
		def SHIRO_USER_ROLE_PATTERN =/runtime\/shiroUserRole\/.*\.json/


		def displayContexts = []
		def importErrors = []

		if (fis != null) {

			def allEntries
			try {
				allEntries = getAllEntries(fis)

				//contexts
				def contexts = allEntries.findAll {e -> e.key ==~ CONTEXT_PATTERN}
				contexts.each {contextEntry ->
					displayContexts.add(processImportContextEntry(contextEntry))

				}

				//Deployment Packages
				def deploymentPackages = allEntries.findAll {e -> e.key ==~ DEPLOYMENT_PACKAGE_PATTERN}
				deploymentPackages.each {dp ->
					displayContexts.add(processImportDeploymentPackgeEntry(dp))

				}
				//Services Properties
				def serviceProperties = allEntries.findAll {e -> e.key ==~ SERVICE_PROPERTIES_PATTERN}
				serviceProperties.each {sp ->
					displayContexts.add(processImportServicePropertyEntry(sp))

				}

			} catch (all) {
				importErrors += all.getMessage()
				MessageMgr.logMessage(logger,'error', getMessageMap(LMETHOD,1))
				throw all
			}


		}

	}

	private def processImportContextEntry(contextEntry){

		def LMETHOD = "processImportContextEntry(contextEntry)"

		def result = null;
		try{
			def slurper = new groovy.json.JsonSlurper()
			result = slurper.parseText(contextEntry.value)
			def context = Context.getInstance(result.context.contextName, result.context.contextVersion, result.context.description)
			context.save()
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),[
				"${result.context.contextName}",
				all] as Object[])
			throw all
		}
	}
	private def processImportDeploymentPackgeEntry(dp){

		def LMETHOD = "processImportDeploymentPackgeEntry(dp)"
		def result = null;
		try{
			def slurper = new groovy.json.JsonSlurper()
			result = slurper.parseText(dp.value)
			DeploymentPackage.createDeploymentpackage(result.deploymentPackage.namespace,result.deploymentPackage.namespaceVersion,result.deploymentPackage.description);
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),[
				"${result.deploymentPackage.namespace}",
				all] as Object[])

			throw all
		}

	}
	private def processImportServicePropertyEntry(propsEntry) {

		def LMETHOD = "processImportServicePropertyEntry(propsEntry)"
		try{
			def elements = (propsEntry.key as String).split("/")
			def props =elements[2].split(":")
			def namespace = props[0]
			def serviceVersion = props[1]
			def serviceName = props[2]
			Properties sourceProperties = new Properties()
			sourceProperties.load(new StringReader(propsEntry.value))
			def aServiceProperty
			aServiceProperty = ServiceProperties.findAllByNamespaceAndServiceNameAndServiceVersion(namespace, serviceName, serviceVersion)
			if (!aServiceProperty) {
				aServiceProperty = new ServiceProperties(namespace:namespace, serviceName:serviceName, serviceVersion:serviceVersion)
				sourceProperties.each {
					def serviceProperty = new ServiceProperty(name:it.key, value:it.value)
					aServiceProperty.addToProps(serviceProperty)
				}
				aServiceProperty.id = aServiceProperty.generateId()
				aServiceProperty.save()
			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${namespace}", all] as Object[])

			throw all
		}
	}

	def importZip(file, contextId=null, updateOpts, deploymentPackageDescription) {

		def LMETHOD = "importZip(file, contextId=null, updateOpts, deploymentPackageDescription)"

		try{
			if (file != null && !file.empty) {
				processImportServices(file.inputStream, contextId, updateOpts,  deploymentPackageDescription)
			}
		}
		catch(all){
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2))
			throw all
		}
	}
	
	
	def processImportServices( file, contextId=null, updateOpts, deploymentPackageDescription) {

		def LMETHOD = "processImportServices( file, contextId=null, updateOpts, deploymentPackageDescription)"

		def ROUTES_PATTERN = /.*\/routes\/.*\.route/
		def DOCS_PATTERN = /.*\/docs\/.*\..*/
		def PROPS_PATTERN = /.*\/props\/.*\.props/
		def JARS_PATTERN = /.*\/lib\/.*\.jar/
		//def BEANS_DEFS_PATTERN = /.*\/conf\/.*\.groovy/
		def BEANS_DEFS_PATTERN = /.*\/conf\/.*\.(groovy||xml)/
		def fileformat = /.*\/(props||docs||routes||lib||conf)\/.*\.*/
		def allEntries = null

		if (deploymentPackageDescription.equals(STAGEDEPLOYMENT)) {

			try {

				allEntries= getAllEntries(file)
				def files = allEntries.findAll {e -> e.key ==~ fileformat}

				files.each { fileEntry ->
					def elements = parsePath(fileEntry.key as String)
					//contextId = elements['namespace']+":"+elements['version']
					contextId = "default:0";
					//println "************************ contextId= "+ contextId
					try {

						def aContext = Context.findById(contextId);
						//contextId = "default:0"
						
						if(aContext == null){
							contextId = "default:0"
							def serviceName = elements['namespace']+" service"
							MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,3), ["${serviceName}"] as Object[])
						}

					} catch (all) {
						MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1))
						throw all
					}
					return true
				}

			}
			catch (all) {
				MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1))
				throw all
			}
		}

		if (contextId == null) {
			return [ 'transStatus' : 'IMPORT FAILED: CONTEXT NOT SPECIFIED','contextAvail':'NO']
		} else if (file != null) {


			def importErrors = []
			def displayProps = []
			def displayJars = []
			def displayBeansDefs = []
			def displayDocs = []
			def displayRoutes = []
			def displayApiCatalogResources=[]
			def apiList = [] 

			try {
				if(allEntries == null){
					allEntries = getAllEntries(file)
				}
				// Props
				def props = allEntries.findAll {e -> e.key ==~ PROPS_PATTERN}
				props.each { propsEntry ->
					displayProps.add(processImportPropsEntry(propsEntry, contextId, updateOpts, deploymentPackageDescription))

				}

				// User Defined Jars
				def jars = allEntries.findAll {e -> e.key ==~ JARS_PATTERN}
				jars.each { jar ->
					displayJars.add(processImportJar(jar, contextId, updateOpts, deploymentPackageDescription))
				}

				// Docs
				def docs = allEntries.findAll {e -> e.key ==~ DOCS_PATTERN}
				docs.each { doc ->

					displayDocs.add(processImportDoc(doc, contextId, updateOpts, deploymentPackageDescription))
				}

				// Beans Defs
				def beansDefs = allEntries.findAll {e -> e.key ==~ BEANS_DEFS_PATTERN}
				beansDefs.each { beansDef ->

					displayBeansDefs.add(processImportBeansDef(beansDef, contextId, updateOpts, deploymentPackageDescription))
				}

				// Routes
				def routes = allEntries.findAll {e -> e.key ==~ ROUTES_PATTERN}
				routes.each { route ->

					displayRoutes.add(processImportRoute(route, contextId, updateOpts,apiList, deploymentPackageDescription))
				}

			} catch (all) {
				importErrors += all.getMessage()
			}
			
			if(System.getProperty("isApiDoc") != null && System.getProperty("isApiDoc").equals("true")){
				swaggerService.generateDoc(apiList)
			}
				
			// Return map for display on results page
			[
				transStatus: 'IMPORT RESULTS: ',
				'contextAvail':'YES',
				entries: allEntries,
				importErrors: importErrors,
				routes: displayRoutes,
				docs: displayDocs,
				props: displayProps,
				jars: displayJars,
				beansDefs: displayBeansDefs,
				swaggerResources:displayApiCatalogResources
			]
			
			
		} else {

			MessageMgr.logMessage(logger, 'info', getMessageMap(LMETHOD,2))

		}
	}

	private def processImportPropsEntry(propsEntry, contextId, updateOpts, deploymentPackagexeDescription) {

		def LMETHOD = "processImportPropsEntry(propsEntry, contextId, updateOpts, deploymentPackageDescription)"
		def f = propsEntry

		MessageMgr.logMessage(logger, 'debug', getMessageMap(LMETHOD,1), ["${f.key}"] as Object[])
		def displayProp = f.key as String
		def elements = parsePath(f.key as String)
		def propertyName = getFileNameBase(elements['fileName'])

		MessageMgr.logMessage(logger, 'debug', getMessageMap(LMETHOD,2), [
			"${elements['namespace']}",
			"${elements['version']}",
			"${elements['fileName']}"
		] as Object[])

		def destServicePropertiesList = ServiceProperties.findAllByNamespaceAndServiceNameAndServiceVersion(NAMESPACE, SERVICENAME, "0")
		def destServiceProperties
		def updatePermitted = true

		if(destServicePropertiesList.size() <= 0) {

			MessageMgr.logMessage(logger,'debug', getMessageMap(LMETHOD,3))
			destServiceProperties = new ServiceProperties(namespace:elements['namespace'], serviceName:propertyName, serviceVersion:elements['version'])
			displayProp += " added"

		} else {

			MessageMgr.logMessage(logger,'debug', getMessageMap(LMETHOD,4))

			// Delete and re-create destServiceProperties as a workaround
			// for an apparent bug in Grails Riak Plugin
			destServiceProperties = destServicePropertiesList[0]
			def storeNamespace = destServiceProperties.namespace.toString()
			def storeServiceName = destServiceProperties.serviceName.toString()
			def storeServiceVersion = destServiceProperties.serviceVersion.toString()
			// since they already exist, we don't know who might be referring to them, and need to
			// restrict updates to only those that have contextadmin on all contexts with route,
			// beans, and jar artifact mappings of the same namespace + version
			def existingSuspectContextIds = [] as Set

			def matchedRoutes  = ComputeRoute.findAllByNamespaceAndRouteVersion(
					storeNamespace, storeServiceVersion)
			matchedRoutes.each { mrte ->
				existingSuspectContextIds.add(mrte?.contextId)
			}

			def matchedBeansDefs = UserDefinedBeansDef.findAllByNamespaceAndBeansDefVersion(
					storeNamespace, storeServiceVersion)
			matchedBeansDefs.each { mbndf ->
				mbndf.getContexts().each { bndfctx ->
					existingSuspectContextIds.add(bndfctx?.id)
				}
			}
			def matchedJars = UserDefinedJar.findAllByNamespaceAndJarVersion(
					storeNamespace, storeServiceVersion)
			matchedJars.each { mjar ->
				mjar.getContexts().each { jarctx ->
					existingSuspectContextIds.add(jarctx?.id)
				}
			}

			// now permission checks for result
			existingSuspectContextIds.each{ ctxIdName ->
				if(!(SecurityUtils.subject.hasRole("contextadmin:${ctxIdName.split(':')[0]}") ||
				SecurityUtils.subject.hasRole("ajscadmin")))
					updatePermitted = false
			}

			destServiceProperties.deleteWithNotify()
			destServiceProperties = new ServiceProperties()
			destServiceProperties.namespace = storeNamespace
			destServiceProperties.serviceName = storeServiceName
			destServiceProperties.serviceVersion = storeServiceVersion
			displayProp += " replaced"
		}

		MessageMgr.logMessage(logger,'debug', getMessageMap(LMETHOD,5), ["${destServiceProperties}"] as Object[])


		destServiceProperties.saveWithNotify()

		Properties sourceProperties = new Properties()
		sourceProperties.load(new StringReader(f.value))
		sourceProperties.each {
			def sp = new ServiceProperty(name:it.key, value:it.value)
			destServiceProperties.addToProps(sp)
		}
		destServiceProperties.saveWithNotify()
		return displayProp
	}


	private def processImportJar(jar, contextId, updateOpts, deploymentPackageDescription) {

		def LMETHOD = "processImportJar(jar, contextId, updateOpts, deploymentPackageDescription)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1), [
			"${jar?.key}",
			"${jar?.value?.class?.name}",
			"${jar?.value}"
		] as Object[])

		def displayJar = jar.key as String
		def elements = parsePath(jar.key as String)
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2), [
			"${elements['namespace']}",
			"${elements['version']}",
			"${elements['fileName']}"
		] as Object[])

		UserDefinedJar jarInstance
		UserDefinedJarContext jarContextMapping
		def saveJar = false
		def saveJarMapping = false
		def namespace = elements['namespace']
		def jarVersion = elements['version']
		def jarName = elements['fileName']
		def jarList = UserDefinedJar.findAllByNamespaceAndJarNameAndJarVersion(namespace, jarName, jarVersion)
		def otherJarContainingContexts = []
		if (jarList == null || jarList.size() == 0)	{

			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3))
			jarInstance = new UserDefinedJar(namespace: elements['namespace'], jarVersion: elements['version'], jarName: elements['fileName'], jarContent: jar.value)
			jarContextMapping = new UserDefinedJarContext(userDefinedJarId: jarInstance.id, contextId: contextId)
			DeploymentPackage.createDeploymentpackage(elements['namespace'], elements['version'], deploymentPackageDescription)
			displayJar += " added"
			saveJar = true
			saveJarMapping = true
		} else {

			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4))
			UserDefinedJar existingJar = jarList[0]
			def jarExistsInTargetContext = false

			UserDefinedJarContext.findAllByUserDefinedJarId(existingJar.id).each { containingJarCtx ->
				if(containingJarCtx.contextId.equals(contextId)) {
					jarExistsInTargetContext = true
				} else {
					otherJarContainingContexts.add(containingJarCtx)
				}
			}
			def ctxWriteForAllJarContainingContexts = true
			otherJarContainingContexts.each { ctxWriteForAllJarContainingContexts = false }
			if(ctxWriteForAllJarContainingContexts) {
				// if we have write for all the contexts we will replace jar definition
				// and relink all existing context mappings for this jar + the targetContext
				// used with this command
				existingJar.deleteWithNotify()
				jarInstance = new UserDefinedJar(
						namespace: elements['namespace'], jarVersion: elements['version'],
						jarName: elements['fileName'], jarContent: jar.value)

				displayJar += " replaced jar file content, and context assignment added to ${contextId}"
				saveJar = true
				saveJarMapping = true
			} else {
				// if jar existed already in other Context but we didn't have write to the other context
				// we will NOT delete the jar, but just add this new context mapping
				displayJar += " using pre-upload jar file content due to non writable context references for this jar id, added target context ref ${contextId} for pre-upload content "
				saveJar = false

				// user can specify whether to support adding targetr context reference to pre-existing bean he
				// cannot change due to other permissions, or to atomically fail the operation
				if(true.equals(updateOpts?.atomicUpdate)) {
					displayJar += " ERROR - not permitted"
					saveJarMapping = false
				} else {
					displayJar += " added ref to ${contextId} context, using pre-upload version of"+
							" jar file due to presence in contexts without contextadmin rights"
					saveJarMapping = true
				}
			}
		}

		if(saveJarMapping) {
			jarContextMapping =
					UserDefinedJarContext.findById("${jarInstance.getTransientId()}.${contextId}") ?:
					new UserDefinedJarContext([userDefinedJarId:jarInstance.getTransientId(),contextId: contextId])
			jarContextMapping.save()
		}
		if(saveJar) {

			// if "makeExclusive=true" option set, and we have perms to overwrite the def,
			// then we are allowed to remove it
			if(true.equals(updateOpts?.makeExclusive)) {
				displayJar += "\n\t\t\t-> and removed from contexts [" +
						otherJarContainingContexts.collect { "${it.contextId}" }.join(",") +
						"] effective their next restart"
				otherJarContainingContexts.each {
					jarInstance.removeFromContexts(Context.findById("${it.contextId}".toString()))
				}

			}
			jarInstance.saveWithNotify()
		}


		return displayJar
	}

	private def processImportDoc(doc, contextId, updateOpts, deploymentPackageDescription) {

		def LMETHOD = "processImportDoc(doc, contextId, updateOpts, deploymentPackageDescription)"
		def f = doc

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1), ["${f.key}"] as Object[])

		def displayDoc = f.key as String
		def elements = parsePath(f.key as String)
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2), [
			"${elements['namespace']}",
			"${elements['version']}",
			"${elements['fileName']}"
		] as Object[])

		Doc docInstance
		def namespace = elements['namespace']
		def docVersion = elements['version']
		def docName = elements['fileName']
		def docList = Doc.findAllByNamespaceAndDocNameAndDocVersion(namespace, docName, docVersion)
		def updatePermitted = true
		if (docList == null || docList.size() == 0)	{
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3))
			docInstance = new Doc(namespace: elements['namespace'], docVersion: elements['version'], docName: elements['fileName'], docContent: f.value)
			DeploymentPackage.createDeploymentpackage(elements['namespace'], elements['version'], deploymentPackageDescription)
			displayDoc += " added"
		} else {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4))
			Doc existingDoc = docList[0]
			def existingSuspectContextIds = [] as Set

			def matchedRoutes  = ComputeRoute.findAllByNamespaceAndRouteVersion(
					namespace, docVersion)
			matchedRoutes.each { mrte ->
				existingSuspectContextIds.add(mrte?.contextId)
			}

			def matchedBeansDefs = UserDefinedBeansDef.findAllByNamespaceAndBeansDefVersion(
					namespace, docVersion)
			matchedBeansDefs.each { mbndf ->
				mbndf.getContexts().each { bndfctx ->
					existingSuspectContextIds.add(bndfctx?.id)
				}
			}
			def matchedJars = UserDefinedJar.findAllByNamespaceAndJarVersion(
					namespace, docVersion)
			matchedJars.each { mjar ->
				mjar.getContexts().each { jarctx ->
					existingSuspectContextIds.add(jarctx?.id)
				}
			}


			existingDoc.deleteWithNotify()
			docInstance = new Doc(namespace: elements['namespace'], docVersion: elements['version'], docName: elements['fileName'], docContent: f.value)
			displayDoc += " replaced"
		}
		docInstance.saveWithNotify()
		return displayDoc
	}

	private def processImportBeansDef(beansDef, contextId, updateOpts, deploymentPackageDescription) {

		def LMETHOD = "processImportBeansDef(beansDef, contextId, updateOpts, deploymentPackageDescription)"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1), ["${beansDef.key}"] as Object[])
		def displayBeansDef = beansDef.key as String
		def elements = parsePath(beansDef.key as String)
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2), [
			"${elements['namespace']}",
			"${elements['version']}",
			"${elements['fileName']}"
		] as Object[])
		UserDefinedBeansDef beansDefInstance
		UserDefinedBeansDefContext beansContextMapping
		def saveBeans = false
		def saveBeansMapping = false
		def namespace = elements['namespace']
		def beansDefVersion = elements['version']
		//def beansDefName = "${elements['fileName']}${!elements['fileName'].endsWith('.groovy') ? '.groovy' : ''}"
		def beansDefName = "${elements['fileName']}"
		def beansDefList = UserDefinedBeansDef.findAllByNamespaceAndBeansDefNameAndBeansDefVersion(namespace, beansDefName, beansDefVersion)
		def otherBeansContainingContexts = []
		if (beansDefList == null || beansDefList.size() == 0)	{
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3))
			beansDefInstance = new UserDefinedBeansDef(namespace: namespace, beansDefVersion: beansDefVersion, beansDefName: beansDefName, beansDefContent: beansDef.value)
			DeploymentPackage.createDeploymentpackage(elements['namespace'], elements['version'], deploymentPackageDescription)
			displayBeansDef += " added bean to target ${contextId}"
			saveBeans = true
			saveBeansMapping = true
		} else {

			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4))

			UserDefinedBeansDef existingBeansDef = beansDefList[0]
			def beansExistsInTargetContext = false
			UserDefinedBeansDefContext.findAllByUserDefinedBeansDefId(existingBeansDef.id).each { containingBeansCtx ->
				if(containingBeansCtx.contextId.equals(contextId)) {
					beansExistsInTargetContext = true
				} else {
					otherBeansContainingContexts.add(containingBeansCtx)
				}
			}
			def ctxWriteForAllBeansContainingContexts = true
			if(ctxWriteForAllBeansContainingContexts) {
				// if we have write for all the contexts we will replace beans definition
				// and relink all existing context mappings for this beansDef + the targetContext
				// used with this command
				existingBeansDef.deleteWithNotify()
				beansDefInstance = new UserDefinedBeansDef(namespace: namespace, beansDefVersion: beansDefVersion,
				beansDefName: beansDefName, beansDefContent: beansDef.value)

				displayBeansDef += " replaced beans file content and added to context ${contextId}"
				saveBeans = true
				saveBeansMapping = true    // saveBeansMapping should always be true if saveBeans is true

			} else {
				// if beans existed already in other Context but we didn't have write to the other context
				// we will NOT delete the beans, but just add this new context mapping
				beansDefInstance = existingBeansDef

				saveBeans = false

				// user can specify whether to support adding targetr context reference to pre-existing bean he
				// cannot change due to other permissions, or to atomically fail the operation
				if(true.equals(updateOpts?.atomicUpdate)) {
					displayBeansDef += " ERROR - not permitted"
					saveBeansMapping = false
				} else {
					displayBeansDef += " added ref to ${contextId} context, using pre-upload version of"+
							" beans definition due to presence in contexts without contextadmin rights"
					saveBeansMapping = true
				}
			}

		}


		if(saveBeansMapping) {  // should always be true if "saveBeans" = true, but may be true/false otherwise
			beansContextMapping =
					UserDefinedBeansDefContext.findById("${beansDefInstance.getTransientId()}.${contextId}") ?:
					new UserDefinedBeansDefContext(userDefinedBeansDefId: beansDefInstance.getTransientId(), contextId: contextId)
			beansContextMapping.save()

		}
		if(saveBeans) {
			// if "makeExclusive=true" option set, and we have perms to overwrite the def,
			// then we are allowed to remove it
			if(true.equals(updateOpts?.makeExclusive)) {
				displayBeansDef += "\n\t\t\t-> and removed from contexts [" +
						otherBeansContainingContexts.collect { "${it.contextId}" }.join(",") +
						"] effective their next restart"
				otherBeansContainingContexts.each {
					beansDefInstance.removeFromContexts(Context.findById("${it.contextId}".toString()))
				}

			}
			// finally save the updated bean
			beansDefInstance.saveWithNotify()
		}

		return displayBeansDef

	}

	private def processImportRoute(route, contextId, updateOpts, apiList, deploymentPackageDescription) {

		def LMETHOD = "processImportRoute(route, contextId, updateOpts, apiList, deploymentPackageDescription)"
		def f = route

		if(true.equals(updateOpts?.makeActive) && true.equals(updateOpts?.makeInactive)) {
			throw new Exception("IMPORT ERROR: CANNOT SET BOTH makeActive AND makeInactive TO TRUE!")
		}

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1), ["${f.key}"] as Object[])
		def displayRoute = "${f.key}:" as String
		def elements = parsePath(f.key as String)
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2), [
			"${elements['namespace']}",
			"${elements['version']}",
			"${elements['fileName']}"
		] as Object[] )

		ComputeRoute computeRouteInstance
		def saveComputeRoute = false
		def namespace = elements['namespace']
		def routeVersion = elements['version']
		def routeName = getFileNameBase(elements['fileName'])
		def routeList = ComputeRoute.findAllByNamespaceAndRouteNameAndRouteVersion(namespace, routeName, routeVersion)
		if (routeList == null || routeList.size() == 0)	{
			// route does not exist yet...
			MessageMgr.logMessage(logger,'debug', getMessageMap(LMETHOD,3))
			computeRouteInstance = new ComputeRoute(namespace: elements['namespace'], routeVersion: elements['version'], routeName: getFileNameBase(elements['fileName']), deployStatus: "Active", routeDefinition: f.value)
			computeRouteInstance.contextId = contextId
			// did they want to upload the route as inactive?
			if(true.equals(updateOpts?.makeInactive)) {
				computeRouteInsntace.deployStatus = "Inactive"
				displayRoute += " added to context ${contextId} - as INACTIVE"
			} else {
				displayRoute += " added to context ${contextId}"
			}
			DeploymentPackage.createDeploymentpackage(elements['namespace'], elements['version'], deploymentPackageDescription)
			saveComputeRoute = true
		} else {
			MessageMgr.logMessage(logger,'debug', getMessageMap(LMETHOD,4))
			ComputeRoute existingComputeRoute = routeList[0]
			def storeDeployStatus = existingComputeRoute.deployStatus
			def existingContextId = existingComputeRoute.contextId ?: 'unassigned'

			// route is either currently unassigned to a context or --
			if('unassigned'.equals(existingContextId) ||
			'null'.equals(existingContextId)// ||
			) {


				computeRouteInstance = new ComputeRoute(
						namespace: elements['namespace'], routeVersion: elements['version'],
						routeName: getFileNameBase(elements['fileName']),
						contextId: contextId,
						routeDefinition: f.value)

				existingComputeRoute.deleteWithNotify()

				if(!existingContextId.equals(contextId)) {
					saveComputeRoute = true
					displayRoute += "saving route as DEPLOY_STATUS to context '${contextId}', removed previous instance from context: ${existingContextId}"
				}
				else {
					saveComputeRoute = true
					displayRoute += " replacing route as DEPLOY_STATUS in context '${contextId}'"
				}

				// if user param "makeActive=true" the route deploy status set to "Active" regardless
				// of prior state
				if(true.equals(updateOpts?.makeActive)) {
					computeRouteInstance.deployStatus = "Active"
				}  // else if "makeInactive=true" set to "Inactive" regardless of prior state
				else if (true.equals(updateOpts?.makeInactive)) {
					computeRouteInstance.deployStatus = "Inactive"
				}  // else use whatever the prior state was
				else {
					computeRouteInstance.deployStatus = storeDeployStatus?:"Active"
				}

				displayRoute = displayRoute.replace("DEPLOY_STATUS", "${computeRouteInstance.deployStatus}".toString())

			} else {
				displayRoute += " ERROR - route '${existingComputeRoute.id}' already exists in another context ${existingContextId} you have no write permission to"
			}
		}

		if(saveComputeRoute) {
			computeRouteInstance.contextId = contextId
			def apiName= "${namespace}|${routeVersion}"
			apiList.add(apiName.toString())
			computeRouteInstance.saveWithNotify()
		}

		return displayRoute
	}


	/**
	 * Creates an export zip file of the Ajsc objects specified by namespace and version.  The zip file
	 * will be created at the location specified by the targetFilename parameter.  The zip file will be
	 * structured as follows:
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/routes/*.route</code>
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/docs/*.*</code>
	 * <p><code>/&lt;namespace&gt;/&lt;version&gt;/props/&lt;serviceName&gt;.props</code>
	 *
	 * @param namespace
	 * @param theVersion
	 * @param targetFilename
	 * @return a Map containing information about what was exported
	 * <ul>
	 * <li><code>namespace</code>: namespace of the Ajsc objects exported String
	 * <li><code>version</code>: version of the Ajsc objects exported String
	 * <li><code>targetFilename</code>: location of the export zip file String
	 */
	def exportRuntimeEnvironment(){

		def LMETHOD = "exportRuntimeEnvironment()"
		def baseDir = null
		def exportFilename
		def exportFileStatus = "SUCCESS"
		def exportFileMessage

		try {
			// Set up directory and filename
			baseDir = System.getenv("AJSC_HOME")
			if (!baseDir) {
				baseDir = System.getProperty("AJSC_HOME")
				if (!baseDir) {
					baseDir = "/"
				}
				else {
					baseDir += DEFAULT_EXPORT_DIR
				}
			}

			else {
				baseDir += DEFAULT_EXPORT_DIR
			}
			// Create the export dir, if it does not exist
			if (!createDir(baseDir)) {
				throw new Exception("Could not create directory: ${baseDir}")
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		exportFilename = createExportFilename(baseDir, "runtimeEnvironment")

		// Set up Zip File
		ZipOutputStream targetRuntimeZipOutputStream
		try {

			targetRuntimeZipOutputStream = new ZipOutputStream(new FileOutputStream(exportFilename))
		} catch (all) {

			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${exportFilename}", all] as Object[])

			throw all

		}
		try{

			def contexList = Context.list();
			contexList.each { context ->

				def jsonBuilder = new groovy.json.JsonBuilder()
				jsonBuilder."context"(
						contextClass:"ajsc.Context",
						contextId: "${context.id}",
						contextName: "${context.contextName}",
						contextVersion: "${context.contextVersion}",
						description: "${context.description}",
						)
				def filename = "runtime/context/${context.id}.context"
				writeEntry(targetRuntimeZipOutputStream, jsonBuilder.toString(), filename)

			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),["${filename}", all] as Object[])
			throw all
		}
		try{
			def deploymentPackageList = DeploymentPackage.list()
			deploymentPackageList.each { deploymentPackage ->

				def jsonBuilder = new groovy.json.JsonBuilder()
				jsonBuilder."deploymentPackage"(
						Class:"ajsc.DeploymentPackage",
						Id: "${deploymentPackage.id}",
						namespace: "${deploymentPackage.namespace}",
						namespaceVersion: "${deploymentPackage.namespaceVersion}",
						description: "${deploymentPackage.description}",
						lastUpdated: "${deploymentPackage.lastUpdated}",
						dateCreated: "${deploymentPackage.dateCreated}",
						userId:"${deploymentPackage.userId}",
						)
				def filename = "runtime/deploymentPackage/${deploymentPackage.id}.json"
				writeEntry(targetRuntimeZipOutputStream, jsonBuilder.toString(), filename)

			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${filename}", all] as Object[])

			throw all
		}
		try{
			def shiroUserRoleList = ShiroUserRole.list()
			shiroUserRoleList.each { shiroUserRole ->

				def jsonBuilder = new groovy.json.JsonBuilder()
				jsonBuilder(
						shiroUserRoleClass:"ajsc.auth.ShiroUserRole",
						shiroUserRoleId: "${shiroUserRole.userId}:${shiroUserRole.roleId}",
						lastUpdated: "${shiroUserRole.lastUpdated}",
						roleId: "${shiroUserRole.roleId}",
						userId:"${shiroUserRole.userId}",
						)
				def filename = "runtime/shiroUserRole/${shiroUserRole.userId}:${shiroUserRole.roleId}.json"
				writeEntry(targetRuntimeZipOutputStream, jsonBuilder.toString(), filename)

			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${filename}", all] as Object[])

			throw all
		}
		try{
			def shiroRoleList = ShiroRole.list()
			shiroRoleList.each { shiroRole ->

				def jsonBuilder = new groovy.json.JsonBuilder()
				jsonBuilder(
						shiroRoleClass:"ajsc.auth.ShiroRole",
						shiroRoleId: "${shiroRole.name}",
						lastUpdated: "${shiroRole.lastUpdated}",
						name: "${shiroRole.name}",
						permissions:"${shiroRole.permissions}",
						)
				def filename = "runtime/shiroRole/${shiroRole.name}.json"
				writeEntry(targetRuntimeZipOutputStream, jsonBuilder.toString(), filename)

			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,5),["${filename}", all] as Object[])

			throw all
		}
		try{
			def shiroUserList =ShiroUser.list();
			shiroUserList.each { shiroUser ->

				def jsonBuilder = new groovy.json.JsonBuilder()
				jsonBuilder(
						shiroUserClass:"ajsc.auth.ShiroUser",
						shiroUserId: "${shiroUser.id}",
						passwordHash: "${shiroUser.passwordHash}",
						lastUpdated: "${shiroUser.lastUpdated}",
						permissions:"${shiroUser.permissions}",
						username:"${shiroUser.username}",
						)
				def filename = "runtime/shiroUser/${shiroUser.username}.json"
				writeEntry(targetRuntimeZipOutputStream, jsonBuilder.toString(), filename)

			}
		}catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,6),["${filename}", all] as Object[])

			throw all
		}
		try{
			def servicePropertiesList = ServiceProperties.list()
			servicePropertiesList.each{ sp ->
				def spId = (sp.id).split(":")
				if( spId[0] == "ajsc"){
					def props = sp.toProperties()
					def writer = new StringWriter()
					props.store(writer, "Ajsc Service Properties namespace=${sp.namespace} serviceVersion=${sp.serviceVersion} serviceName=${sp.serviceName}")
					def filename = "runtime/serviceProperties/${sp.namespace}:${sp.serviceVersion}:${sp.serviceName}:service.props"
					writeEntry(targetRuntimeZipOutputStream, writer.toString(), filename)

				}
			}
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,7),["${filename}", all] as Object[])

			throw all
		}
		try{

			targetRuntimeZipOutputStream.close();
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error', getMessageMap(LMETHOD,8), [
				"${exportFilename}",
				all
			] as Object[] )
			throw all
		}

	}


	def exportZip(namespace, theVersion, targetFilename, targetPropsFilename) {

		def LMETHOD = "exportZip(namespace, theVersion, targetFilename, targetPropsFilename)"
		def exportFileStatus = "SUCCESS"
		def exportPropsFileStatus = "SUCCESS"
		def exportFileMessage
		def exportPropsFileMessage
		def baseDir = null
		def exportFilename
		def exportPropsFilename

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),[
			"${namespace}",
			"${theVersion}"
		] as Object[])

		try {
			// Set up directory and filename
			baseDir = System.getenv("AJSC_HOME")
			if (!baseDir) {
				baseDir = System.getProperty("AJSC_HOME")
				if (!baseDir) {
					baseDir = "/"
				} else {
					baseDir += DEFAULT_EXPORT_DIR
				}
			} else {
				baseDir += DEFAULT_EXPORT_DIR
			}
			if (targetFilename.startsWith("/")) {
				// Absolute path specified
				baseDir = ""
			} else {
				// Create the export dir, if it does not exist
				if (!createDir(baseDir)) {
					throw new Exception("Could not create directory: ${baseDir}")
				}
			}

			exportFilename = createExportFilename(baseDir, targetFilename)
			exportPropsFilename = createExportFilename(baseDir, targetPropsFilename)

			// Set up Zip File
			ZipOutputStream targetZipOutputStream
			try {
				targetZipOutputStream = new ZipOutputStream(new FileOutputStream(exportFilename))
			} catch (all) {

				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),[
					"${exportFilename}",
					all
				] as Object[])

				exportFileStatus = "FAIL"
				exportFileMessage = all.getLocalizedMessage()
			}

			// Set up Props Zip File
			ZipOutputStream targetPropsZipOutputStream
			try {
				targetPropsZipOutputStream = new ZipOutputStream(new FileOutputStream(exportPropsFilename))
			} catch (all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3), [
					"${exportPropsFilename}",
					all
				] as Object[])
				exportPropsFileStatus = "FAIL"
				exportPropsFileMessage = all.getLocalizedMessage()
			}

			if (exportFileStatus != "FAIL") {
				// Docs

				def docList = Doc.findAllByNamespaceAndDocVersion(namespace, theVersion)
				docList.each { doc ->
					def filename = "${namespace}/${theVersion}/docs/${doc.docName}"
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4), ["${filename}"] as Object[])
					writeEntry(targetZipOutputStream, doc.docContent, filename)
				}

				// Routes
				def routeList = ComputeRoute.findAllByNamespaceAndRouteVersion(namespace, theVersion)
				routeList.each { cr ->
					def filename = "${namespace}/${theVersion}/routes/${cr.routeName}.route"
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5), ["${filename}"] as Object[])
					writeEntry(targetZipOutputStream, cr.routeDefinition, filename)
				}

				// User Defined Jars
				def jarsList = UserDefinedJar.findAllByNamespaceAndJarVersion(namespace, theVersion)
				jarsList.each { jar ->
					def filename = "${namespace}/${theVersion}/lib/${jar.jarName}"
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,6), ["${filename}"] as Object[])
					writeEntry(targetZipOutputStream, jar.jarContent, filename)
				}

				// User Defined Beans Defs
				def beansDefList = UserDefinedBeansDef.findAllByNamespaceAndBeansDefVersion(namespace, theVersion)
				beansDefList.each { beansDef ->
					//def filename = "${namespace}/${theVersion}/conf/${beansDef.beansDefName}${!beansDef.beansDefName.endsWith('.groovy') ? '.groovy' : ''}"
					def filename = "${namespace}/${theVersion}/conf/${beansDef.beansDefName}"
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,7), ["${filename}"] as Object[])
					writeEntry(targetZipOutputStream, beansDef.beansDefContent, filename)
				}
				// No artifacts, empty zip file
				if (!docList && !routeList && !jarsList && !beansDefList) {
					exportFileStatus = "FAIL"
					exportFileMessage = "No routes, docs, beans defs, or jars found for ${namespace}:${theVersion}, cannot create empty zip file"
				}
			}

			if (exportPropsFileStatus != "FAIL") {
				// Props
				def servicePropertiesList = ServiceProperties.findAllByNamespaceAndServiceVersion(namespace, theVersion)
				servicePropertiesList.each { sp ->
					def props = sp.toProperties()
					def writer = new StringWriter()
					props.store(writer, "Ajsc Service Properties namespace=${namespace} serviceVersion=${theVersion} serviceName=${sp.serviceName}")
					def filename = "${namespace}/${theVersion}/props/${sp.serviceName}.props"
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,8), ["${filename}"] as Object[])
					writeEntry(targetPropsZipOutputStream, writer.toString(), filename)
				}
				// No service properties, empty zip file
				if (!servicePropertiesList) {
					exportPropsFileStatus = "FAIL"
					exportPropsFileMessage = "No Service Properties found for ${namespace}:${theVersion}, cannot create empty zip file"
				}
			}

			if (exportFileStatus != "FAIL") {
				try {
					// Finalize Export Zip File
					targetZipOutputStream.close();
				} catch (all) {
					exportFileStatus = "FAIL"
					exportFileMessage = all.getLocalizedMessage()
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,9), [
						"${exportFilename}",
						all
					] as Object[])
					//				throw all

				}
			}
			if (exportPropsFileStatus != "FAIL") {
				try {
					// Finalize Props Zip File
					targetPropsZipOutputStream.close();
				} catch (all) {
					exportPropsFileStatus = "FAIL"
					exportPropsFileMessage = all.getLocalizedMessage()
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,10), [
						"${exportPropsFilename}",
						all
					] as Object[])
				}
			}
		} catch (all) {
			MessageMgr.logMessage(logger,'error', getMessageMap(LMETHOD,11), [
				"${namespace}",
				"${theVersion}",
				"${exportFilename}",
				all
			] as Object[] )
		}
		[
			namespace: namespace,
			version: theVersion,
			targetFile: exportFilename,
			targetPropsFile: exportPropsFilename,
			status: exportFileStatus,
			propsStatus: exportPropsFileStatus,
			message: exportFileMessage,
			propsMessage: exportPropsFileMessage
		]

	}

	String createExportFilename(baseDir, targetFilename) {
		def LMETHOD = "createExportFilename(baseDir, targetFilename)"
		if (!targetFilename.endsWith(".zip")) {
			targetFilename += ".zip"
		}
		def exportFilename = baseDir + targetFilename

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${exportFilename}"] as Object[])

		// Adjust file name if it already exists
		if (new File(exportFilename).exists()) {
			exportFilename = makeUnique(exportFilename)
		}
		return exportFilename
	}

	Map<String, Object> getAllEntries(fis) {

		Map<String, Object> output = new HashMap<String, Object>()
		ZipInputStream zipInputStream = new ZipInputStream(fis)
		ZipEntry currentEntry = zipInputStream.getNextEntry()
		def contents
		while (currentEntry != null) {
			if (!currentEntry.isDirectory()) {
				if (currentEntry.name.endsWith('.jar')) {
					contents = getJarContents(currentEntry, zipInputStream)
				} else {
					contents = getContents(currentEntry, zipInputStream)
				}
				output.put(currentEntry, contents);
			}
			currentEntry = zipInputStream.getNextEntry()
		}
		return output;
	}

	/**
	 * @param ZipEntry
	 * @return
	 */
	String getContents(ZipEntry zipEntry, ZipInputStream zipInputStream) {
		String output = null;
		BufferedInputStream is = null;
		StringWriter writer = new StringWriter();
		def LMETHOD = "getContents(ZipEntry zipEntry, ZipInputStream zipInputStream)"
		try {
			is = new BufferedInputStream(zipInputStream);
			char[] buffer = new char[2048];
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1),e, ["${e}"] as Object[])
			//e.printStackTrace();
		}

		output = writer.toString();

		return output;
	}

	/**
	 * @param zipEntry
	 * @param zipInputStream
	 * @return
	 */
	byte[] getJarContents(ZipEntry zipEntry, ZipInputStream zipInputStream) {

		def LMETHOD = "getJarContents(ZipEntry zipEntry, ZipInputStream zipInputStream)"
		// Read the zipEntry from the zipInputStream into byte array buffers
		List<List> buffers = new ArrayList<List>()
		int bufferSize = 1024
		int bytesRead = 0
		while (bytesRead >= 0) {
			byte[] b = new byte[bufferSize]
			bytesRead = zipInputStream.read(b, 0, bufferSize)
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${bytesRead}"] as Object[])
			if (bytesRead != -1) {
				List bufList
				if (bytesRead != bufferSize) {
					// Trim the last buffer
					byte[] trimmedBuf = b[0..bytesRead-1]
					bufList = trimmedBuf.toList()
				} else {
					bufList = b.toList()
				}
				buffers.add(bufList)
			}
		}

		// Determine total size of buffers
		def sizes = buffers.collect { it.size() }
		int finalSize = 0
		sizes.each { finalSize += it }

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${finalSize}"] as Object[])
		// Concatenate all buffers into one byte[]
		List totalBufList = new ArrayList()
		buffers.each { buffer -> totalBufList += buffer }
		def output = totalBufList.toArray(new byte[0])
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${output}"] as Object[])

		return output
	}

	def parsePath(path) {
		def elements = path.split("/")
		[
			"namespace": elements[0],
			"version": elements[1],
			"type": elements[2],
			"fileName": elements[3]
		]
	}

	def getFileNameBase(fileName) {

		def LMETHOD ="getFileNameBase(fileName)"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${fileName}"] as Object[])

		def i = fileName.lastIndexOf('.')
		fileName.toString().substring(0, i)
	}

	def writeEntry(ZipOutputStream zos, byte[] text, String filename) {

		def LMETHOD = "writeEntry(ZipOutputStream zos, byte[] text, String filename)"
		def sis
		try {
			sis = new ByteArrayInputStream(text)
		} catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1), [all] as Object[])
		}

		// put a new ZipEntry in the ZipOutputStream
		zos.putNextEntry(new ZipEntry(filename))

		int size = 0
		byte[] buffer = new byte[1024]

		// read data to the end of the source file and write it to the zip
		// output stream.
		while ((size = sis.read(buffer, 0, buffer.length)) > 0) {
			zos.write(buffer, 0, size)
		}

		zos.closeEntry()
		sis.close()
	}

	def writeEntry(ZipOutputStream zos, String text, String filename) {
		def sis
		writeEntry(zos, text.getBytes("UTF-8"), filename)
	}

	/**
	 * Uses File.mkdirs to create the given directory if it does not already exist.
	 * @param dir
	 * @return
	 */
	def createDir(dir) {

		def LMETHOD = "createDir(dir)"

		def returnVal = true
		MessageMgr.logMessage(logger, 'debug', getMessageMap(LMETHOD,1), ["${dir}"] as Object[])

		try {
			//println "about to create:\t${dir}"
			def dirFile = new File(dir)
			if (!dirFile.exists()) {
				returnVal = dirFile.mkdirs()
			}

			// terry's hack 101
			try{new File(dir).mkdir}catch(eatit){}
			try{new File(dir).mkdirs}catch(eatit){}

		} catch (all) {
			//all.printStackTrace()
			MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,2), all,["${dir}", all] as Object[])

		}
		if (!returnVal) {
			MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,3), ["${dir}"] as Object[])

		}
		return returnVal
	}

	def makeUnique(filename) {
		def ext = filename.lastIndexOf(".")
		def before = filename.substring(0,ext)
		def after = filename.substring(ext, filename.length())
		def formatter = new SimpleDateFormat("yyyyMMddHHmmss")
		def unique = formatter.format(new Date())

		return "${before}_${unique}${after}"
	}

}
