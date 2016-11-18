package ajsc

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader
//import org.apache.shiro.SecurityUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ajsc.util.MessageMgr

class Context implements Serializable {
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static MAPNAME = "${NAMESPACE}.Context"
	def static SERVICENAME = "Context"
	def static DEFAULT_CONTEXT_KEY = "default:0"
	
	final static Logger audit 	= LoggerFactory.getLogger("${Context.class.name}.AUDIT")

	String id
	String contextName
	String contextVersion
	String description

	static transients = ['deferoService', 'ajscMetaDataService','applicationContext']
	def static transient deferoService
	def static transient ajscMetaDataService
	def static transient applicationContext
	
	static constraints = {
		contextName(blank:false)
		contextVersion(blank:false)
		description(blank:false,maxSize:1000)
		contextName(unique:['contextVersion'])
	}
	static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"

	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}
	
	
	final static Logger logger = LoggerFactory.getLogger(Context.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"Context",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	String generateId() {
		return "${this.contextName}:${this.contextVersion}"
	}

	static String generateId(contextName, contextVersion) {
		return "${contextName}:${contextVersion}"
	}
	
	String ident() {
		def identity = id ?: generateId()
		return identity
	}
	
    boolean equals(other) {
        if (!(other instanceof Context)) {
            return false
        }
        other.contextName == contextName && other.contextVersion == contextVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append contextName
		builder.append contextVersion
        builder.toHashCode()
    }
	
	static Context getInstance(contextName, contextVersion, description) {
		def aContext
		aContext = findByContextNameAndContextVersion(contextName, contextVersion)
		if (!aContext) {
			aContext = new Context(contextName:contextName, contextVersion:contextVersion, description:description)
			println AjscMetaDataUtil.asJson(aContext)
			aContext.id = aContext.generateId()
		}
		return aContext
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
	
	static List<Context> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundContexts = new ArrayList()
		def foundJSONContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONContexts.each { jsonContext ->
			if (jsonContext) {
				def context = AjscMetaDataUtil.fromJson(ajsc.Context, jsonContext)
				if (!context.id) { context.id = context.generateId() }
				foundContexts.add(context)
			}
		}
		return foundContexts
	}
	
	static List<Context> findAllByContextNameLike(nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundContexts = new ArrayList()
		def foundJSONContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONContexts.each { jsonContext ->
			if (jsonContext) {
				def context = AjscMetaDataUtil.fromJson(ajsc.Context, jsonContext)
				if (!context.id) { context.id = context.generateId() }
				
				if ((context.contextName ==~ /${nameFilter}/)) {
					foundContexts.add(context)
				}
			}
		}
		return foundContexts
	}
	
	static String getAllByContextNameLike(nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundJSONContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		nameFilter = nameFilter.replace(wildcardPattern, ".*")
		
		foundJSONContexts.each { jsonContext ->
			if (jsonContext) {
				def context = AjscMetaDataUtil.fromJson(ajsc.Context, jsonContext)
				if (!context.id) { context.id = context.generateId() }
				if ((context.contextName ==~ /${nameFilter}/)) {
					valueList << jsonContext
				}
			}
		}
		return valueList
	}
	
	static List<Context> findAllByContextNameLikeAndContextVersionLike(nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundContexts = new ArrayList()
		def foundJSONContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONContexts.each { jsonContext ->
			if (jsonContext) {
				def context = AjscMetaDataUtil.fromJson(ajsc.Context, jsonContext)
				if (!context.id) { context.id = context.generateId() }
				
				if ((context.contextName ==~ /${nameFilter}/) &&
					(context.contextVersion ==~ /${versionFilter}/)) {
					foundContexts.add(context)
				}
			}
		}
		return foundContexts
	}
	
	static Context findByContextNameAndContextVersion(contextName, contextVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundContext = null
		def foundJSONContext = ajscMetaDataService.getMapEntry(MAPNAME, generateId(contextName, contextVersion))
		if (foundJSONContext) {
			foundContext = AjscMetaDataUtil.fromJson(ajsc.Context,foundJSONContext)
			if (!foundContext.id) { foundContext.id = foundContext.generateId() }
		}
		return foundContext
	}
	
	static Context findById(String contextId) {
		//println "Context.findById contextId: ${contextId}"
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundContext = null
		def foundJSONContext = ajscMetaDataService.getMapEntry(MAPNAME, contextId)
		//println "Context.findById foundJSONContext: ${foundJSONContext}"
		if (foundJSONContext) {
			foundContext = AjscMetaDataUtil.fromJson(Context, foundJSONContext)
			if (!foundContext.id) { foundContext.id = foundContext.generateId() }
			//println "Context.findById foundContext id: ${foundContext.id}"
		}
		return foundContext
	}
	
	Context save() {
		def LMETHOD = "save"
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
//			audit.info("principal {} saved Context {}",  SecurityUtils.subject.principal, jsonString)
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
			ComputeRoute.findAllByContextId(generateId()).each { 
//				it.contextId = null
//				it.save()
				it.delete()
			 }
			UserDefinedBeansDefContext.findAllByContextId(generateId()).each { it.delete() }
			UserDefinedJarContext.findAllByContextId(generateId()).each {it.delete() }
			
			// Remove Context
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}"] as Object[])
			throw all
		}
	}
	
}
