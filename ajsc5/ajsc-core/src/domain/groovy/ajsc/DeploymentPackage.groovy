package ajsc



import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder
//import org.springframework.beans.BeansException;


import bsh.This;

//import org.apache.shiro.SecurityUtils


import org.slf4j.Logger
import org.slf4j.LoggerFactory
//import ajsc.auth.ShiroRole;

import ajsc.util.MessageMgr

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

class DeploymentPackage implements Serializable {
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static MAPNAME = "${NAMESPACE}.DeploymentPackage"
	def static SERVICENAME = "DeploymentPackage"

	final static Logger audit 	= LoggerFactory.getLogger("${DeploymentPackage.class.name}.AUDIT")
	
	final static Logger logger = LoggerFactory.getLogger(DeploymentPackage.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"DeploymentPackage",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	String id
	String namespace
	String namespaceVersion
	String userId
	String description
	Date dateCreated
	Date lastUpdated


	static transients = [
		'deferoService',
		'ajscMetaDataService',
		'swaggerService'
	]
	def static transient deferoService
	def static transient ajscMetaDataService
	//def static transient swaggerService

	static constraints = {
		namespace(blank:false)
		namespaceVersion(blank:false)
		userId(blank:false)
		description(blank:false, maxSize:1000)
		dateCreated(blank:false)
		lastUpdated(blank:false)
		namespace(unique:['namespaceVersion'])
	}
	static mapping = { id generator:'assigned' }
	static mapWith = "none"

	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}


	String generateId() {
		return "${this.namespace}:${this.namespaceVersion}"
	}

	static String generateId(namespace, namespaceVersion) {
		return "${namespace}:${namespaceVersion}"
	}

	boolean equals(other) {
		if (!(other instanceof DeploymentPackage)) {
			return false
		}
		other.namespace == namespace && other.namespaceVersion == namespaceVersion
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append namespace
		builder.append namespaceVersion
		builder.toHashCode()
	}

	static DeploymentPackage getInstance(namespace, namespaceVersion, description) {
		def aDeploymentPackage
		aDeploymentPackage = findByNamespaceAndVersion(namespace, namespaceVersion)
		if (!aDeploymentPackage) {
//TODO: do something beyond a placeholder where subject is (userId)
			aDeploymentPackage = new DeploymentPackage(namespace:namespace, namespaceVersion:namespaceVersion, description:description, userId:"ajsc", dateCreated: new Date(), lastUpdated: new Date() )
			aDeploymentPackage.id = aDeploymentPackage.generateId()
		}
		return aDeploymentPackage
	}

	/**
	 * Create Deployment Package if it does not exist
	 *
	 * @param namespace
	 * @param namespaceVersion
	 * @param description
	 */
	static void createDeploymentpackage(String namespace, String namespaceVersion, String description)
	{
		if (DeploymentPackage.findByNamespaceAndVersion(namespace, namespaceVersion) == null) {
			if (!description) {
				description = "Deployment Package for Namespace=${namespace} and namespace version=${namespaceVersion}"
			}
			
//TODO: do something beyond a placeholder where subject is (userId)
			def aDeploymentPackage = new DeploymentPackage(namespace:namespace, namespaceVersion:namespaceVersion, description:description, userId:"ajsc", dateCreated: new Date(), lastUpdated: new Date() )
			aDeploymentPackage.id = aDeploymentPackage.generateId()
			aDeploymentPackage.saveWithNotify()

		}

	}


	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${this.id}"] as Object[])
		} else {
			// TODO
//			deferoService.publishMessage(ComputeService.TOPIC_NAME, this)
		}
		return saveResult
	}

	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"

		def deleteResult = true
		try {
			this.delete()
			// TODO
//			deferoService.publishMessage(ComputeService.TOPIC_NAME, "delete ${generateId()}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}"] as Object[])
		}
		return deleteResult
	}

	static List<DeploymentPackage> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDeploymentPackages = new ArrayList()
		def foundJSONDeploymentPackages = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDeploymentPackages.each { jsonDeploymentPackage ->
			if (jsonDeploymentPackage) {
				def deploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, jsonDeploymentPackage)
				if (!deploymentPackage.id) { deploymentPackage.id = deploymentPackage.generateId() }
				foundDeploymentPackages.add(deploymentPackage)
			}
		}
		return foundDeploymentPackages
	}

	static List<DeploymentPackage> findAllByNamespaceLike(nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDeploymentPackages = new ArrayList()
		def foundJSONDeploymentPackages = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDeploymentPackages.each { jsonDeploymentPackage ->
			if (jsonDeploymentPackage) {
				def deploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, jsonDeploymentPackage)
				if (!deploymentPackage.id) { deploymentPackage.id = deploymentPackage.generateId() }

				if ((deploymentPackage.namespace ==~ /${nameFilter}/)) {
					foundDeploymentPackages.add(deploymentPackage)
				}
			}
		}
		return foundDeploymentPackages
	}

	static List<DeploymentPackage> findAllByNamespaceLikeAndVersionLike(nameFilter, namespaceVersionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDeploymentPackages = new ArrayList()
		def foundJSONDeploymentPackages = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDeploymentPackages.each { jsonDeploymentPackage ->
			if (jsonDeploymentPackage) {
				def deploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, jsonDeploymentPackage)
				if (!deploymentPackage.id) { deploymentPackage.id = deploymentPackage.generateId() }

				if ((deploymentPackage.namespace ==~ /${nameFilter}/) &&
				(deploymentPackage.namespaceVersion ==~ /${namespaceVersionFilter}/)) {
					foundDeploymentPackages.add(deploymentPackage)
				}
			}
		}
		return foundDeploymentPackages
	}
	
	static String getAllByNamespaceLikeAndVersionLike(String namespaceFilter, String versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundJSONDeploymentPackages = ajscMetaDataService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundJSONDeploymentPackages.each { jsonDeploymentPackage ->
			if (jsonDeploymentPackage) {
				def deploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, jsonDeploymentPackage)
				if (!deploymentPackage.id) { deploymentPackage.id = deploymentPackage.generateId() }

				if ((deploymentPackage.namespace ==~ /${namespaceFilter}/) &&
				(deploymentPackage.namespaceVersion ==~ /${versionFilter}/)) {
					valueList << jsonDeploymentPackage
				}
			}
		}
		return valueList
	}

	static DeploymentPackage findByNamespaceAndVersion(namespace, namespaceVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDeploymentPackage = null
		def foundJSONDeploymentPackage = ajscMetaDataService.getMapEntry(MAPNAME, generateId(namespace, namespaceVersion))
		if (foundJSONDeploymentPackage) {
			foundDeploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, foundJSONDeploymentPackage)
			if (!foundDeploymentPackage.id) { foundDeploymentPackage.id = foundDeploymentPackage.generateId() }
		}
		return foundDeploymentPackage
	}

	static DeploymentPackage findById(String deploymentPackageId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDeploymentPackage = null
		def foundJSONDeploymentPackage = ajscMetaDataService.getMapEntry(MAPNAME, deploymentPackageId)
		if (foundJSONDeploymentPackage) {
			foundDeploymentPackage = AjscMetaDataUtil.fromJson(DeploymentPackage, foundJSONDeploymentPackage)
			if (!foundDeploymentPackage.id) { foundDeploymentPackage.id = foundDeploymentPackage.generateId() }
		}
		return foundDeploymentPackage
	}

	DeploymentPackage save() {
		def LMETHOD = "save"
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}"] as Object[])
			throw all
		}
		return returnVal
	}


	void delete() {
		def LMETHOD = "delete"
		try {
			// Remove references in ComputeRoute, UserDefinedBeansDef, and UserDefinedJar
			ComputeRoute.findAllByNamespaceAndRouteVersion(this.namespace, this.namespaceVersion).each { it.delete() }
			UserDefinedBeansDef.findAllByNamespaceAndBeansDefVersion(this.namespace, this.namespaceVersion).each { it.delete() }
			UserDefinedJar.findAllByNamespaceAndJarVersion(this.namespace, this.namespaceVersion).each { it.delete() }
			Doc.findAllByNamespaceAndDocVersion(this.namespace, this.namespaceVersion).each { it.delete() }
			ServiceProperties.findAllByNamespaceAndServiceVersion(this.namespace, this.namespaceVersion).each { it.delete() }
			def apiName = "${this.namespace}_${this.namespaceVersion}"
			//remove Swagger dir
			//swaggerService.deleteDir(swaggerService.AJSC_REST_DOCS_OUT_PATH +apiName)
			// Remove Context
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}"] as Object[])
			throw all
		}
	}

	/**
	 * Get a list of unique namespaces
	 * 
	 * @param rights ShiroRole.Rights.READ or ShiroRole.Rights.RW
	 * @return  List<String> List of Namespaces
	 */
	public static List<String> getNamespaces(String rights) {
		//println "getNamespaces()"
		def namespaces =[]// set selected to default value *
		def LMETHOD = "List<String> getNamespaces(String rights)"
//TODO: figure out what to do about permissions		
//		if (ShiroRole.Rights.READ ==rights)
		{
			namespaces.add("*")
		}

		def nslist=[]
		try {
			DeploymentPackage.list().each{dp->
				def perm = dp.namespace+":" +rights
//				if( SecurityUtils.subject.isPermitted(perm) )
					nslist.add((String)dp.namespace)
			}
			nslist = nslist.unique()
			namespaces.addAll(nslist)

		} catch(Throwable all){
			//all.printStackTrace()
			MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1),all, [ "${all}"] as Object[])
			println "\t\toops:\t\t${all.getMessage()}"
		}

		return namespaces
	}

	
	/**
	 * Get a list of unique namespace versions by namespace.
	 * @param namespace
	 * @return List<String> versions List
	 */
	public static List<String> getNamespaceVersions(String namespace) {
		def namespaceVersions=[]
		def nsVersionList=[]
		def LMETHOD = "List<String> getNamespaceVersions(String namespace)"
		try {
			DeploymentPackage.list().each{dp->
				
			if (dp.namespace == namespace)
					nsVersionList.add((String)dp.namespaceVersion)
			}
			nsVersionList = nsVersionList.unique()
			namespaceVersions.addAll(nsVersionList)

		}catch(Throwable all){
		    //all.printStackTrace()
			MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1),all, [ "${all}"] as Object[])
			
			throw all
			
		}
		
		return namespaceVersions;
	}

}
