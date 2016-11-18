package ajsc

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

//import org.apache.shiro.SecurityUtils

class UserDefinedBeansDef implements Serializable {
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "UserDefinedBeansDef"
	def static MAPNAME = "${NAMESPACE}.UserDefinedBeansDef"
	
	final static Logger audit = LoggerFactory.getLogger("${UserDefinedBeansDef.class.name}.AUDIT")
	
	final static Logger logger = LoggerFactory.getLogger(UserDefinedBeansDef.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"UserDefinedBeansDef",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	String id
	String namespace="ajsc"
	String beansDefName
	String beansDefVersion
	String beansDefContent

	static transients = ['status', 'deferoService', 'ajscMetaDataService']
	String status
	def static transient deferoService
	def static transient ajscMetaDataService
	
	static constraints = {
		beansDefContent(blank:false,maxSize:104857600)
		namespace(blank:false)
		beansDefName(blank:false)
		beansDefVersion(blank:false)
		namespace(unique:['beansDefName','beansDefVersion'])
	}

	static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"
	
	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}

	boolean validateExtended() {
		def baseResult = true//this.validate()
		def duplicate
//		if (this.beansDefName.endsWith(".groovy")) {
		if (this.beansDefName.endsWith(".groovy") || this.beansDefName.endsWith(".xml")) {
			duplicate = UserDefinedBeansDef.findAllByNamespaceAndBeansDefNameAndBeansDefVersion(this.namespace, this.beansDefName, this.beansDefVersion)
		}
//		else {
//			duplicate = UserDefinedBeansDef.findAllByNamespaceAndBeansDefNameAndBeansDefVersion(this.namespace, this.beansDefName + ".groovy", this.beansDefVersion)			
//		}
		//println duplicate.size()
		if((duplicate != null) && (duplicate.size > 0)) {
			//This line is being taken out because its throwing an error. Its being replaced with a println 07/06/2015
			//this.errors.rejectValue("beansDefName", "ajsc.UserDefinedBeansDef.beansDefName.not.unique")
			println 'ajsc.UserDefinedBeansDef.beansDefName.not.unique'
			baseResult = false
		}
		return baseResult
	}

	String generateId() {
		/*if (!this.beansDefName.endsWith(".groovy")) {
			this.beansDefName = this.beansDefName + ".groovy"
		}*/
		return "${this.namespace}:${this.beansDefName}:${this.beansDefVersion}"
	}
	
	String getTransientId() {
		return "${this.namespace}:${this.beansDefName}:${this.beansDefVersion}"
	}

    boolean equals(other) {
        if (!(other instanceof UserDefinedBeansDef)) {
            return false
        }
        other.namespace == namespace && other.beansDefName == beansDefName && other.beansDefVersion == beansDefVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append namespace
        builder.append beansDefName
        builder.append beansDefVersion
        builder.toHashCode()
    }

	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${beansDefName}","${beansDefVersion}"] as Object[])
		} else {
//		    deferoService.publishMessage(UserDefinedBeansDefService.TOPIC_NAME,this)
		}
		return saveResult
	}
	
	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"

		def deleteResult = true
		try {
			this.delete()
//			deferoService.publishMessage(UserDefinedBeansDefService.TOPIC_NAME,"delete ${this.generateId()}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${beansDefName}","${beansDefVersion}"] as Object[])
		}
		return deleteResult
	}
	
	def addToContexts(context) {
		if (!this.id) { this.id = this.generateId() }
		//println "UserDefinedBeansDef.addToContexts(this.id:${this.id} context.id:${context.id})"
		def userDefinedBeansDefContext = UserDefinedBeansDefContext.findByUserDefinedBeansDefIdAndContextId(this.id as String, context.id as String)
		//println "UserDefinedBeansDef.addToContexts userDefinedBeansDefContext: ${userDefinedBeansDefContext}"
		if (!userDefinedBeansDefContext) {
			//println "UserDefinedBeansDef.addToContexts(id:${context.id}) not found, adding"
			userDefinedBeansDefContext = new UserDefinedBeansDefContext(userDefinedBeansDefId: this.id as String, contextId: context.id as String).save()
		}
	}
	
	def removeFromContexts(context) {
		UserDefinedBeansDefContext.findByUserDefinedBeansDefIdAndContextId(this.id as String, context.id as String).delete()
	}

	def clearContexts() {
		//println "UserDefinedBeansDef.clearContexts()"
		getContexts().each {
			removeFromContexts(it)
		}
	}
	
	def getContexts() {
		def userDefinedBeansDefContexts = UserDefinedBeansDefContext.findAllByUserDefinedBeansDefId(this.id as String)
		//println "UserDefinedBeansDef.getContexts userDefinedBeansDefContexts: ${userDefinedBeansDefContexts}"
		return userDefinedBeansDefContexts.collect { userDefinedBeansDefContext ->
			//println "UserDefinedBeansDef.getContexts userDefinedBeansDefContext.contextId: ${userDefinedBeansDefContext.contextId}"
			Context.findById(userDefinedBeansDefContext.contextId)	
		}
	}
	
	static List<UserDefinedBeansDef> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				foundUserDefinedBeansDefs.add(beansDef)
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceLikeAndBeansDefNameLike(namespaceFilter, nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.beansDefName ==~ /${nameFilter}/) &&
					(beansDef.namespace ==~ /${namespaceFilter}/)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceLikeAndBeansDefNameLikeAndBeansDefVersionLike(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.beansDefName ==~ /${nameFilter}/) &&
					(beansDef.namespace ==~ /${namespaceFilter}/) &&
					(beansDef.beansDefVersion ==~ /${versionFilter}/)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceAndBeansDefNameAndBeansDefVersion(namespace, beansDefName, beansDefVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.beansDefName == beansDefName) &&
					(beansDef.namespace == namespace) &&
					(beansDef.beansDefVersion == beansDefVersion)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceLikeAndBeansDefVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.namespace ==~ /${namespaceFilter}/) &&
					(beansDef.beansDefVersion ==~ /${versionFilter}/)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static String getAllByNamespaceLikeAndBeansDefVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.namespace ==~ /${namespaceFilter}/) &&
					(beansDef.beansDefVersion ==~ /${versionFilter}/)) {
					valueList << jsonUserDefinedBeansDef
				}
			}
		}
		return valueList
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceAndBeansDefVersion(namespace, beansDefVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.namespace == namespace) &&
					(beansDef.beansDefVersion == beansDefVersion)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespaceLike(namespaceFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if ((beansDef.namespace ==~ /${namespaceFilter}/)) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static List<UserDefinedBeansDef> findAllByNamespace(namespace) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefs = new ArrayList()
		def foundJSONUserDefinedBeansDefs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefs.each { jsonUserDefinedBeansDef ->
			if (jsonUserDefinedBeansDef) {
				def beansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, jsonUserDefinedBeansDef)
				if (!beansDef.id) { beansDef.id = beansDef.generateId() }
				
				if (beansDef.namespace == namespace) {
					foundUserDefinedBeansDefs.add(beansDef)
				}
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	static UserDefinedBeansDef findById(String theId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def userDefinedBeansDef = null
		def foundJSONUserDefinedBeansDef = ajscMetaDataService.getMapEntry(MAPNAME, theId)
		if (foundJSONUserDefinedBeansDef) {
			userDefinedBeansDef = AjscMetaDataUtil.fromJson(UserDefinedBeansDef, foundJSONUserDefinedBeansDef)
			if (!userDefinedBeansDef.id) { userDefinedBeansDef.id = userDefinedBeansDef.generateId() }
		}

		return userDefinedBeansDef
	}
	
	static List<UserDefinedBeansDef> findAllByContextId(contextId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefContexts = UserDefinedBeansDefContext.findAllByContextId(contextId)
		def foundUserDefinedBeansDefs = new ArrayList()
		foundUserDefinedBeansDefContexts.each { foundUserDefinedBeansDefContext ->
			//println "UserDefinedBeansDef.findAllByContextId foundUserDefinedBeansDefContext: ${foundUserDefinedBeansDefContext.contextId} ${foundUserDefinedBeansDefContext.userDefinedBeansDefId}"
			def foundUserDefinedBeansDef = UserDefinedBeansDef.findById(foundUserDefinedBeansDefContext.userDefinedBeansDefId)
			if (foundUserDefinedBeansDef) {
				foundUserDefinedBeansDefs.add(foundUserDefinedBeansDef)
			}
		}
		return foundUserDefinedBeansDefs
	}
	
	UserDefinedBeansDef save() {
		def LMETHOD = "save"
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${beansDefName}","${beansDefVersion}"] as Object[])
			throw all
		}
		return returnVal
	}
	
	void delete() {
		def LMETHOD = "delete"
		try {
		//	println "Before deleting it from file system"
			UserDefinedBeansDefContext.findAllByUserDefinedBeansDefId(generateId()).each {
				 it.delete() 
			}
		//	println "Before removing map entry using ajscMetaDataService"
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		//	println "After removing map entry using ajscMetaDataService"
			

			} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${beansDefName}","${beansDefVersion}"] as Object[])
			throw all
		}
	}
	
	static Map<String, String> parseUserDefinedBeansDefId(userDefinedBeansDefId) {
		def elements = userDefinedBeansDefId.split(":")
		[
		 "namespace": elements[0],
		 "beansDefName": elements[1],
		 "beansDefVersion": elements[2]
		]
	}
	
}
