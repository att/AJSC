package ajsc

import groovy.json.JsonSlurper
import java.util.ArrayList;
import java.util.List;

import ajsc.PropertiesService;
import ajsc.ServiceProperties;
import ajsc.ServiceProperty;


import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.context.*
// TODO Remove this workaround in Grails 2.0

import org.slf4j.Logger
import org.slf4j.LoggerFactory

//import org.apache.shiro.SecurityUtils

import ajsc.util.MessageMgr

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

/**
 * Service Properties
 * <p>Creates a list of properties for an application</p>
 * <li>a central location for all properties</li>
 * <li>scopes system properties with a namespace, name, and version</li>
 * <li>a web interface</li>
 * <li>a restful interface</li>
 * <p>
 * @author Terry Walters <mailto:terry.walters@bellsouth.com>
 */
class ServiceProperties  implements Serializable{
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "PropertiesService"
	def static MAPNAME = "${NAMESPACE}.ServiceProperties"

	final static Logger audit = LoggerFactory.getLogger("${ServiceProperties.class.name}.AUDIT")
	
	String id
	String namespace="ajsc"
    String serviceName
    String serviceVersion
	List<ServiceProperty> props = new ArrayList<ServiceProperty>()
	
    static hasMany = [ props:ServiceProperty ]

	static transients = ['status', 'deferoService', 'ajscMetaDataService']
	String status
	def static transient deferoService
	def static transient ajscMetaDataService
	
    static constraints = {
//    	props(nullable:true)
		namespace(unique:['serviceName','serviceVersion'])
    }
    static mapping = {
		id generator:'assigned'
//		props lazy: false
//		props cascade:"all,delete-orphan"
	}
	static mapWith = "none"

	final static Logger logger = LoggerFactory.getLogger(ServiceProperties.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"ServiceProperties",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	

	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
	}

	String generateId() {
		return "${this.namespace}:${this.serviceName}:${this.serviceVersion}"
	}

    boolean equals(other) {
        if (!(other instanceof ServiceProperties)) {
            return false
        }
        other.namespace == namespace && other.serviceName == serviceName && other.serviceVersion == serviceVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append namespace
        builder.append serviceName
        builder.append serviceVersion
        builder.toHashCode()
    }

	String servicePropertiesName() { 
		return "${namespace}.${serviceName}.${serviceVersion}" 
	}
	
    Properties toProperties() {
    	Properties properties = new Properties();
    	props.each{
			properties.setProperty(it.name,it.value)
		}
		return properties
    }
	
	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${serviceName}","${serviceVersion}"] as Object[])
		} else {
//			deferoService.publishMessage(PropertiesService.TOPIC_NAME,this)
			RouteMgmtService.propertiesService.insertOrUpdateProps(this);
		}
		return saveResult
	}
	
	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"
		
		def deleteResult = true
		try {
			this.delete()
//			deferoService.publishMessage(PropertiesService.TOPIC_NAME,"delete ${this.namespace}.${this.serviceName}.${this.serviceVersion}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${serviceName}","${serviceVersion}"] as Object[])
		}
		return deleteResult
	}
	
	static List<ServiceProperties> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				foundServiceProperties.add(serviceProperties)
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespaceLikeAndServiceNameLike(namespaceFilter, nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.serviceName ==~ /${nameFilter}/) &&
					(serviceProperties.namespace ==~ /${namespaceFilter}/)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespaceLikeAndServiceNameLikeAndServiceVersionLike(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.serviceName ==~ /${nameFilter}/) &&
					(serviceProperties.namespace ==~ /${namespaceFilter}/) &&
					(serviceProperties.serviceVersion ==~ /${versionFilter}/)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespaceLikeAndServiceVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.namespace ==~ /${namespaceFilter}/) &&
					(serviceProperties.serviceVersion ==~ /${versionFilter}/)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static String getAllByNamespaceLikeAndServiceVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.serviceVersion ==~ /${versionFilter}/) &&
					(serviceProperties.namespace ==~ /${namespaceFilter}/)) {
					valueList << jsonServiceProperties
				}
			}
		}
		
		return valueList
	}
	
	static List<ServiceProperties> findAllByNamespaceAndServiceVersion(namespace, serviceVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.namespace == namespace) &&
					(serviceProperties.serviceVersion == serviceVersion)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespaceLike(namespaceFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.namespace ==~ /${namespaceFilter}/)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespace(namespace) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if (serviceProperties.namespace == namespace) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static List<ServiceProperties> findAllByNamespaceAndServiceNameAndServiceVersion(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundServiceProperties = new ArrayList()
		def foundJSONServiceProperties = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONServiceProperties.each { jsonServiceProperties ->
			if (jsonServiceProperties) {
				def serviceProperties = ServiceProperties.jsonParse(jsonServiceProperties)
				if ((serviceProperties.serviceName == nameFilter) &&
					(serviceProperties.namespace == namespaceFilter) &&
					(serviceProperties.serviceVersion == versionFilter)) {
					foundServiceProperties.add(serviceProperties)
				}
			}
		}
		return foundServiceProperties
	}
	
	static ServiceProperties findById(String theId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def serviceProperty = null
		def foundJSONServiceProperties = ajscMetaDataService.getMapEntry(MAPNAME, theId)
		if (foundJSONServiceProperties) {
			serviceProperty = ServiceProperties.jsonParse(foundJSONServiceProperties)
		}
		return serviceProperty
	}
	static void createServiceProperty(String namespace, String serviceName, String serviceVersion,String name,String value)
	{
		
		def aServiceProperty
		aServiceProperty = ServiceProperties.findAllByNamespaceAndServiceNameAndServiceVersion(namespace, serviceName, serviceVersion)
		if (!aServiceProperty) {
			
			def prop = new ServiceProperty(name:name, value:value)
			
			aServiceProperty = new ServiceProperties(namespace:namespace, serviceName:serviceName, serviceVersion:serviceVersion,props:prop)
			aServiceProperty.id = aServiceProperty.generateId()
			aServiceProperty.save()
		}
	}
	ServiceProperties save() {
		def LMETHOD = "save"
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${serviceName}","${serviceVersion}"] as Object[])
			throw all
		}
		return returnVal
	}
	
	void delete() {
		def LMETHOD = "delete"
		
		try {
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${serviceName}","${serviceVersion}"] as Object[])		
			throw all
		}
	}
	
	void addToProps(ServiceProperty serviceProperty) {
		props.add(serviceProperty)
	}
	
	static ServiceProperties jsonParse(String jsonServiceProperties) {
		def jsonElement = new JsonSlurper().parseText(jsonServiceProperties)
		def serviceProperties = new ServiceProperties(namespace: jsonElement['namespace'], serviceName: jsonElement['serviceName'], serviceVersion: jsonElement['serviceVersion'])
		def jsonProps = jsonElement['props']
		if (!jsonProps.equals(null)) {
			jsonProps.each { 
				def serviceProperty = new ServiceProperty(name: it.name, value: it.value)
				serviceProperties.addToProps(serviceProperty)
			}
		}
		if (!serviceProperties.id) { serviceProperties.id = serviceProperties.generateId() }
		return serviceProperties
	}
	
	String toString() {
		return "id: ${this.id} namespace: ${this.namespace} serviceName: ${this.serviceName} serviceVersion: ${this.serviceVersion} props: ${this.props}"
	}
}
