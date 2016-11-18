package ajsc

import java.util.List;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

//import org.apache.shiro.SecurityUtils

class UserDefinedBeansDefContext {

	def static MAPNAME = "ajsc.UserDefinedBeansDefContext"
	
	final static Logger audit = LoggerFactory.getLogger("${UserDefinedBeansDefContext.class.name}.AUDIT")
	
	String id
	String userDefinedBeansDefId
	String contextId
	Date lastUpdated = new Date()
	
	static transients = ['ajscMetaDataService']
	def static transient ajscMetaDataService
	
    static constraints = {
		userDefinedBeansDefId(nullable: false, blank: false)
		contextId(nullable: false, blank: false)
    }
    static mapping = {
		id generator:'assigned'
    }
	static mapWith = "none"
	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}
	
	static String generateId(userDefinedBeansDefId, contextId) {
		return "${userDefinedBeansDefId}:${contextId}"
	}
	
	String generateId() {
		return generateId(this.userDefinedBeansDefId, this.contextId)
	}

	def assignId() {
		this.id = generateId()	
	}
	
	static List<UserDefinedBeansDefContext> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefContexts = new ArrayList()
		def foundJSONUserDefinedBeansDefContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONUserDefinedBeansDefContexts.each { jsonUserDefinedBeansDefContext ->
			if (jsonUserDefinedBeansDefContext) {
				def userDefinedBeansDefContext = AjscMetaDataUtil.fromJson(UserDefinedBeansDefContext, jsonUserDefinedBeansDefContext)
				userDefinedBeansDefContext.assignId()
				foundUserDefinedBeansDefContexts.add(userDefinedBeansDefContext)
			}
		}
		return foundUserDefinedBeansDefContexts
	}
	
	static List<UserDefinedBeansDefContext> findAllByUserDefinedBeansDefId(userDefinedBeansDefId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefContexts = new ArrayList()
		def foundJSONUserDefinedBeansDefContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		//println "UserDefinedBeansDefContext.findAllByUserDefinedBeansDefId foundJSONUserDefinedBeansDefContexts: ${foundJSONUserDefinedBeansDefContexts}"
		foundJSONUserDefinedBeansDefContexts.each { jsonUserDefinedBeansDefContext ->
			if (jsonUserDefinedBeansDefContext) {
				def userDefinedBeansDefContext = AjscMetaDataUtil.fromJson(UserDefinedBeansDefContext, jsonUserDefinedBeansDefContext)
				userDefinedBeansDefContext.assignId()
				if (userDefinedBeansDefContext.userDefinedBeansDefId == userDefinedBeansDefId) {
					foundUserDefinedBeansDefContexts.add(userDefinedBeansDefContext)
				}
			}
		}
		return foundUserDefinedBeansDefContexts
	}
	
	static List<UserDefinedBeansDefContext> findAllByContextId(contextId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundUserDefinedBeansDefContexts = new ArrayList()
		def foundJSONUserDefinedBeansDefContexts = ajscMetaDataService.getAllEntries(MAPNAME)
		//println "UserDefinedBeansDefContext.findAllByContextId foundJSONUserDefinedBeansDefContexts: ${foundJSONUserDefinedBeansDefContexts}"
		foundJSONUserDefinedBeansDefContexts.each { jsonUserDefinedBeansDefContext ->
			if (jsonUserDefinedBeansDefContext) {
				def userDefinedBeansDefContext = AjscMetaDataUtil.fromJson(UserDefinedBeansDefContext, jsonUserDefinedBeansDefContext)
				userDefinedBeansDefContext.assignId()
				if (userDefinedBeansDefContext.contextId == contextId) {
					foundUserDefinedBeansDefContexts.add(userDefinedBeansDefContext)
				}
			}
		}
		return foundUserDefinedBeansDefContexts
	}
	
	static UserDefinedBeansDefContext findByUserDefinedBeansDefIdAndContextId(userDefinedBeansDefId, contextId) {
		//println "UserDefinedBeansDefContext.findByUserDefinedBeansDefIdAndContextId userDefinedBeansDefId: ${userDefinedBeansDefId} contextId: ${contextId}"
		return findById(generateId(userDefinedBeansDefId, contextId))
	}
	
	static UserDefinedBeansDefContext findById(String theId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def userDefinedBeansDefContext = null
		def foundJSONUserDefinedBeansDefContext = ajscMetaDataService.getMapEntry(MAPNAME, theId)
		if (foundJSONUserDefinedBeansDefContext) {
			//println "UserDefinedBeansDefContext.findById foundJSONUserDefinedBeansDefContext: ${foundJSONUserDefinedBeansDefContext}"
			println foundJSONUserDefinedBeansDefContext
			userDefinedBeansDefContext = new UserDefinedBeansDefContext(AjscMetaDataUtil.fromJson(UserDefinedBeansDefContext, foundJSONUserDefinedBeansDefContext))
			userDefinedBeansDefContext.assignId()
		}
		return userDefinedBeansDefContext
	}
	
	UserDefinedBeansDefContext save() {
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			this.lastUpdated = new Date()
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
		//TODO: add logging
			throw all
		}
		return returnVal
	}
	
	void delete() {
		try {
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
		//TODO: add logging
			throw all
		}
	}
}
