package ajsc

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang.builder.HashCodeBuilder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader
//import org.apache.shiro.SecurityUtils

class UserDefinedJar implements Serializable {

	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "UserDefinedJar"
	def static MAPNAME = "${NAMESPACE}.UserDefinedJar"

	final static Logger audit = LoggerFactory.getLogger("${UserDefinedJar.class.name}.AUDIT")
	final static Logger logger = LoggerFactory.getLogger(UserDefinedJar.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"UserDefinedJar",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	
	String id
	String namespace="ajsc"
	String jarName
	String jarVersion
	byte[] jarContent
	
	static transients = ['deferoService','ajscMetaDataService']
	def static transient deferoService
	def static transient ajscMetaDataService
	
    static constraints = {
		jarContent(blank:false)
		namespace(blank:false)
		jarVersion(blank:false)
		namespace(unique:['jarName','jarVersion'])
    }
	static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"
	
	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}
	
	String generateId() {
		return "${this.namespace}:${this.jarName}:${this.jarVersion}"
	}
	
	String getTransientId() {
		return "${this.namespace}:${this.jarName}:${this.jarVersion}"
	}

    boolean equals(other) {
        if (!(other instanceof UserDefinedJar)) {
            return false
        }
        other.namespace == namespace && other.jarName == jarName && other.jarVersion == jarVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append namespace
        builder.append jarName
        builder.append jarVersion
        builder.toHashCode()
    }

	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${jarName}","${jarVersion}"] as Object[])
		} else {
//		    deferoService.publishMessage(UserDefinedJarService.TOPIC_NAME,this)
		}
		return saveResult
	}
	
	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"

		def deleteResult = true
		try {
			this.delete()
//			deferoService.publishMessage(UserDefinedJarService.TOPIC_NAME,"delete ${this.generateId()}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${jarName}","${jarVersion}"] as Object[])
		}
		return deleteResult
	}
	
	UserDefinedJar save() {
		def LMETHOD ="save"
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putByteArrayMapEntry(MAPNAME, this.id, this.jarContent)
			returnVal = this
//			audit.info("Principal {} saved UserDefinedJar {}",  SecurityUtils.subject.principal, "${this.id}")
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${jarName}","${jarVersion}"] as Object[])
			throw all
		}
		return returnVal
	}
	
	void delete() {
		def LMETHOD = "delete"
		try {
			UserDefinedJarContext.findAllByUserDefinedJarId(generateId()).each { it.delete() }
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
//			audit.info("Principal {} deleted UserDefinedJar {}",  SecurityUtils.subject.principal, "${this.id}")
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${jarName}","${jarVersion}"] as Object[])
			throw all
		}
	}
	
	static Map<String, String> parseUserDefinedJarId(userDefinedJarId) {
		def elements = userDefinedJarId.split(":")
		[
		 "namespace": elements[0],
		 "jarName": elements[1],
		 "jarVersion": elements[2]
		]
	}
	

	def addToContexts(context) {
		if (!this.id) { this.id = this.generateId() }
		//println "UserDefinedJar.addToContexts(this.id:${this.id} context.id:${context.id})"
		def userDefinedJarContext = UserDefinedJarContext.findByUserDefinedJarIdAndContextId(this.id as String, context.id as String)
		//println "UserDefinedJar.addToContexts userDefinedJarContext: ${userDefinedJarContext}"
		if (!userDefinedJarContext) {
			//println "UserDefinedJar.addToContexts(id:${context.id}) not found, adding"
			userDefinedJarContext = new UserDefinedJarContext(userDefinedJarId: this.id as String, contextId: context.id as String).save()
		}
	}
	
	def removeFromContexts(context) {
		UserDefinedJarContext.findByUserDefinedJarIdAndContextId(this.id as String, context.id as String).delete()
	}

	def clearContexts() {
		//println "UserDefinedJar.clearContexts()"
		getContexts().each {
			removeFromContexts(it)
		}
	}
	
	def getContexts() {
		def userDefinedJarContexts = UserDefinedJarContext.findAllByUserDefinedJarId(this.id as String)
		//println "UserDefinedJar.getContexts userDefinedJarContexts: ${userDefinedJarContexts}"
		return userDefinedJarContexts.collect { userDefinedJarContext ->
			//println "UserDefinedJar.getContexts userDefinedJarContext.contextId: ${userDefinedJarContext.contextId}"
			Context.findById(userDefinedJarContext.contextId)	
		}
	}
	
	static List<UserDefinedJar> list(Map params) {
		return list()
	}
	static List<UserDefinedJar> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJars = new ArrayList()
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				foundUserDefinedJars.add(userDefinedJar)
			}
		}

		return foundUserDefinedJars
	}
	
	static List<UserDefinedJar> findAllByNamespaceLikeAndJarNameLike(namespaceFilter, nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJars = new ArrayList()
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				
				if ((userDefinedJar.jarName ==~ /${nameFilter}/) &&
					(userDefinedJar.namespace ==~ /${namespaceFilter}/)) {
					foundUserDefinedJars.add(userDefinedJar)
				}
			}
		}
		return foundUserDefinedJars
	}
	
	static String getAllByNamespaceLikeAndJarVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				
				if ((userDefinedJar.jarVersion ==~ /${versionFilter}/) &&
					(userDefinedJar.namespace ==~ /${namespaceFilter}/)) {
					
					valueList << AjscMetaDataUtil.asJson(userDefinedJar)
				}
			}
		}
		return valueList
	}
	
	static UserDefinedJar findById(String theId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def userDefinedJar = null
		def jarContent = ajscMetaDataService.getByteArrayMapEntry(MAPNAME, theId)
		if (jarContent) {
			def elements = parseUserDefinedJarId(theId)
			userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: jarContent)
			if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
		}
		return userDefinedJar
	}
	
	static List<UserDefinedJar> findAllByNamespaceAndJarVersion(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJars = new ArrayList()
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				
				if ((userDefinedJar.namespace == namespaceFilter) &&
					(userDefinedJar.jarVersion == versionFilter)) {
					foundUserDefinedJars.add(userDefinedJar)
				}
			}
		}
		return foundUserDefinedJars
	}
	
	static List<UserDefinedJar> findAllByNamespaceAndJarNameAndJarVersion(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJars = new ArrayList()
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				
				if ((userDefinedJar.namespace == namespaceFilter) &&
					(userDefinedJar.jarVersion == versionFilter) &&
					(userDefinedJar.jarName == nameFilter)) {
					foundUserDefinedJars.add(userDefinedJar)
				}
			}
		}
		return foundUserDefinedJars
	}
	
	static List<UserDefinedJar> findAllByNamespace(namespaceFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJars = new ArrayList()
		def foundUserDefinedJarsMap = ajscMetaDataService.getAllByteArrayEntriesMap(MAPNAME)
		foundUserDefinedJarsMap.each { key, value ->
			if (value) {
				def elements = parseUserDefinedJarId(key as String)
				def userDefinedJar = new UserDefinedJar(namespace: elements['namespace'], jarName: elements['jarName'], jarVersion: elements['jarVersion'], jarContent: value)
				if (!userDefinedJar.id) { userDefinedJar.id = userDefinedJar.generateId() }
				
				if ((userDefinedJar.namespace ==namespaceFilter)) {
					foundUserDefinedJars.add(userDefinedJar)
				}
			}
		}
		return foundUserDefinedJars
	}
	
	static List<UserDefinedJar> findAllByContextId(contextId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJarContexts = UserDefinedJarContext.findAllByContextId(contextId)
		def foundUserDefinedJars = new ArrayList()
		foundUserDefinedJarContexts.each { foundUserDefinedJarContext ->
			//println "UserDefinedJar.findAllByContextId foundUserDefinedJarContext: ${foundUserDefinedJarContext.contextId} ${foundUserDefinedJarContext.userDefinedJarId}"
			def foundUserDefinedJar = UserDefinedJar.findById(foundUserDefinedJarContext.userDefinedJarId)
			if (foundUserDefinedJar) {
				foundUserDefinedJars.add(foundUserDefinedJar)
			}
		}
		return foundUserDefinedJars
	}
}
