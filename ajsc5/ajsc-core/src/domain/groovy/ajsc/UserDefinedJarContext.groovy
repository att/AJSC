package ajsc

import java.util.List;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

//import org.apache.shiro.SecurityUtils

class UserDefinedJarContext {

	def static MAPNAME = "ajsc.UserDefinedJarContext"
	
	final static Logger audit = LoggerFactory.getLogger("${UserDefinedJarContext.class.name}.AUDIT")
	
	String id
	String userDefinedJarId
	String contextId
	Date lastUpdated = new Date()
	
	static transients = ['ajscMetaDataService']
	def static transient ajscMetaDataService
	
    static constraints = {
		userDefinedJarId(nullable: false, blank: false)
		contextId(nullable: false, blank: false)
    }
    static mapping = {
		id generator:'assigned'
    }
	static mapWith = "none"
	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}
	
	static String generateId(userDefinedJarId, contextId) {
		return "${userDefinedJarId}:${contextId}"
	}
	
	String generateId() {
		return generateId(this.userDefinedJarId, this.contextId)
	}

	def assignId() {
		this.id = generateId()	
	}
	
	static List<UserDefinedJarContext> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJarContexts = new ArrayList()
		def foundJSONUserDefinedJarContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedJarContexts.each { jsonUserDefinedJarContext ->
			if (jsonUserDefinedJarContext) {
				def userDefinedJarContext = AjscMetaDataUtil.fromJson(UserDefinedJarContext, jsonUserDefinedJarContext)
				userDefinedJarContext.assignId()
				foundUserDefinedJarContexts.add(userDefinedJarContext)
			}
		}
		return foundUserDefinedJarContexts
	}
	
	static List<UserDefinedJarContext> findAllByUserDefinedJarId(userDefinedJarId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJarContexts = new ArrayList()
		def foundJSONUserDefinedJarContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		//println "UserDefinedJarContext.findAllByUserDefinedJarId foundJSONUserDefinedJarContexts: ${foundJSONUserDefinedJarContexts}"
		foundJSONUserDefinedJarContexts.each { jsonUserDefinedJarContext ->
			if (jsonUserDefinedJarContext) {
				def userDefinedJarContext = AjscMetaDataUtil.fromJson(UserDefinedJarContext, jsonUserDefinedJarContext)
				userDefinedJarContext.assignId()
				if (userDefinedJarContext.userDefinedJarId == userDefinedJarId) {
					foundUserDefinedJarContexts.add(userDefinedJarContext)
				}
			}
		}
		return foundUserDefinedJarContexts
	}
	
	static List<UserDefinedJarContext> findAllByContextId(contextId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedJarContexts = new ArrayList()
		def foundJSONUserDefinedJarContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		//println "UserDefinedJarContext.findAllByContextId foundJSONUserDefinedJarContexts: ${foundJSONUserDefinedJarContexts}"
		foundJSONUserDefinedJarContexts.each { jsonUserDefinedJarContext ->
			if (jsonUserDefinedJarContext) {
				def userDefinedJarContext = AjscMetaDataUtil.fromJson(UserDefinedJarContext, jsonUserDefinedJarContext)
				userDefinedJarContext.assignId()
				if (userDefinedJarContext.contextId == contextId) {
					foundUserDefinedJarContexts.add(userDefinedJarContext)
				}
			}
		}
		return foundUserDefinedJarContexts
	}
	
	static UserDefinedJarContext findByUserDefinedJarIdAndContextId(userDefinedJarId, contextId) {
		//println "UserDefinedJarContext.findByUserDefinedJarIdAndContextId userDefinedJarId: ${userDefinedJarId} contextId: ${contextId}"
		return findById(generateId(userDefinedJarId, contextId))
	}
	
	static UserDefinedJarContext findById(String theId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def userDefinedJarContext = null
		def foundJSONUserDefinedJarContext = ajscMetaDataService.getMapEntry(MAPNAME, theId)
		if (foundJSONUserDefinedJarContext) {
			//println "UserDefinedJarContext.findById foundJSONUserDefinedJarContext: ${foundJSONUserDefinedJarContext}"
			userDefinedJarContext = AjscMetaDataUtil.fromJson(UserDefinedJarContext, foundJSONUserDefinedJarContext)
			userDefinedJarContext.assignId()
		}
		return userDefinedJarContext
	}
	
	UserDefinedJarContext save() {
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			this.lastUpdated = new Date()
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
			throw all
		}
		return returnVal
	}
	
	void delete() {
		try {
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			throw all
		}
	}
}
