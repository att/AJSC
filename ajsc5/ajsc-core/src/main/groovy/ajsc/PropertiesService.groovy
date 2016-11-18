/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import org.apache.camel.spring.spi.ApplicationContextRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

class PropertiesService {
	final static Logger logger = LoggerFactory.getLogger(PropertiesService.class)
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"PropertiesService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	def deferoService
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "PropertiesService"
	def static TOPIC_NAME = NAMESPACE+"."+SERVICENAME+".topic"
	def static ADMIN_TOPIC = NAMESPACE+".admin.topic"

	def initialized = false

	static transactional = false
	static expose = ['jmx']

	
	def start = {
	}

	def stop = {
	}

	def init() {
		def LMETHOD = "init()"
		start()
		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))
		
		// Add all properties to System.properties
		ServiceProperties.list().each { insertOrUpdateProps(it) }
		
		initialized=true
		println "PropertiesService initialized successfully"
		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,2))
	}
	
	def shutdown = {
		stop()
	}

	def listProps = {
		def returnVal = System.getProperties()
	}
	
	def insertOrUpdateProps(serviceProperties) {
		def LMETHOD = "insertOrUpdateProps(serviceProperties)"
		// Delete existing
		deleteProps(serviceProperties.servicePropertiesName())
		// Add new
		serviceProperties.props.each {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${serviceProperties.serviceName}","${it.name}", "${it.value}"] as Object[])
				System.setProperty("${serviceProperties.servicePropertiesName()}.${it.name}", it.value)
			}	
		}
	
	def deleteProps(servicePropertiesName) {
		def LMETHOD = "deleteProps(servicePropertiesName)"
		def systemProps = System.getProperties()
		// Find
		def matchingSystemProps = systemProps.findAll { it.key.startsWith("${servicePropertiesName}.") }
		// Remove
		matchingSystemProps.each {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${it.key}"] as Object[])
			System.clearProperty( it.key )
		}
	}
}
