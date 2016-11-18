package ajsc


import org.apache.commons.lang.builder.HashCodeBuilder

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

class ComputeRoute implements Serializable {
	
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static MAPNAME = "${NAMESPACE}.ComputeRoute"
	def static SERVICENAME = "ComputeService"
	
	final static Logger audit 	= LoggerFactory.getLogger("${ComputeRoute.class.name}.AUDIT")
	final static Logger logger = LoggerFactory.getLogger(ComputeRoute.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"ComputeRoute",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	String id
	String namespace="ajsc"
	String routeName
	String routeVersion	
	String routeDefinition
	String deployStatus="Active"
	String contextId
	
	static transients = ['status', 'deferoService', 'riakService']
	String status
	def static transient deferoService
	def static transient riakService
	
	static constraints = {
		routeDefinition(blank:false,maxSize:100000)
		namespace(blank:false)
		routeName(blank:false)
		routeVersion(blank:false)
		deployStatus(inList: ["Active", "Inactive"])
		namespace(unique:['routeName','routeVersion'])
		contextId(blank:false)
	}
	static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"
	
	def static initRiakService = {
		riakService = RouteMgmtService.persistenceService
		}

	boolean validateExtended() {
//		def baseResult = this.validate()
		def baseResult = true
		def duplicate = ComputeRoute.findAllByNamespaceAndRouteNameAndRouteVersion(this.namespace, this.routeName, this.routeVersion)        
		if((duplicate != null) && (duplicate.size > 0)) {			
			//this.errors.rejectValue("routeName", "ajsc.ComputeRoute.routeName.not.unique")
			println 'ajsc.ComputeRoute.routeName.not.unique'
			baseResult = false
		}				
		return baseResult
	}
	

	String generateId() {
		return "${this.namespace}:${this.routeName}:${this.routeVersion}"
	}

    boolean equals(other) {
        if (!(other instanceof ComputeRoute)) {
            return false
        }
        other.namespace == namespace && other.routeName == routeName && other.routeVersion == routeVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append namespace
        builder.append routeName
        builder.append routeVersion
        builder.toHashCode()
    }

	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${routeName}","${routeVersion}"] as Object[])
		}/* else {
			deferoService.publishMessage(ComputeService.TOPIC_NAME, this)
		}*/
		return saveResult
	}
	
	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"

		def deleteResult = true
		try {
			this.delete()
//			deferoService.publishMessage(ComputeService.TOPIC_NAME, "delete route ${generateId()}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${routeName}","${routeVersion}"] as Object[])
		}
		return deleteResult
	}

	static ComputeRoute findById(routeId) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoute = null
		def foundJSONRoute = riakService.getMapEntry(MAPNAME, routeId)
		if (foundJSONRoute) {
			foundRoute = new ComputeRoute(AjscMetaDataUtil.fromJson(ComputeRoute,foundJSONRoute))
			if (!foundRoute.id) { foundRoute.id = foundRoute.generateId() }
		}
		return foundRoute
	}

	static List<ComputeRoute> list() {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				if (!route.contextId) { 
					println "adding default:0 context reference to legacy route"
					route.contextId = Context.findById('default:0').id
					route = route.saveWithNotify()
				}
				foundRoutes.add(route)
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespaceLikeAndRouteNameLike(namespaceFilter, nameFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.routeName ==~ /${nameFilter}/) &&
					(route.namespace ==~ /${namespaceFilter}/)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespaceLikeAndRouteNameLikeAndRouteVersionLike(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.routeName ==~ /${nameFilter}/) &&
					(route.namespace ==~ /${namespaceFilter}/) &&
					(route.routeVersion ==~ /${versionFilter}/)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespaceAndRouteNameAndRouteVersion(namespace, routeName, routeVersion) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.routeName == routeName) &&
					(route.namespace == namespace) &&
					(route.routeVersion == routeVersion)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespaceLikeAndRouteVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.namespace ==~ /${namespaceFilter}/) &&
					(route.routeVersion ==~ /${versionFilter}/)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static String getAllByNamespaceLikeAndRouteVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.namespace ==~ /${namespaceFilter}/) &&
					(route.routeVersion ==~ /${versionFilter}/)) {
					valueList << jsonRoute
				}
			}
		}
		return valueList
	}
	
	static List<ComputeRoute> findAllByNamespaceAndRouteVersion(namespace, routeVersion) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.namespace == namespace) &&
					(route.routeVersion == routeVersion)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespaceLike(namespaceFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.namespace ==~ /${namespaceFilter}/)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByNamespace(namespace) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if (route.namespace == namespace) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByContextIdLike(contextIdFilter) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if ((route.contextId ==~ /${contextIdFilter}/)) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	static List<ComputeRoute> findAllByContextId(contextId) {
		// Read with Protocol Buffers client
		if (!riakService) { initRiakService() }
		def foundRoutes = new ArrayList()
		def foundJSONRoutes = riakService.getAllEntries(MAPNAME)
		foundJSONRoutes.each { jsonRoute ->
			if (jsonRoute) {
				def route = AjscMetaDataUtil.fromJson(ComputeRoute, jsonRoute)
				if (!route.id) { route.id = route.generateId() }
				
				if (route.contextId == contextId) {
					foundRoutes.add(route)
				}
			}
		}
		return foundRoutes
	}
	
	ComputeRoute save() {
		def LMETHOD = "save"
		
		if (!riakService) {
			initRiakService()
		}
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			riakService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(Exception all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${routeName}","${routeVersion}"] as Object[])
			throw all
		}
		return returnVal
	}
	
	void delete() {
		def LMETHOD = "delete"
		try {
			riakService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${routeName}","${routeVersion}"] as Object[])
			throw all
		}
	}
}
