/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.restlet

import org.apache.camel.component.restlet.RestletComponent
import org.restlet.Component
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware

class RestletComponentFactory implements ApplicationContextAware{

	private static final String RESTLET_COMPONENT_BEAN = "restletComponent"
	def applicationContext
	def rcMap = new HashMap<String, RestletComponent>()
	
	def getRestletComponent(key) {
		println "RestletComponentFactory.getRestletComponent for key: ${key}"
		def rc = rcMap.get(key as String)
		if (!rc) {
			// Create a new org.apache.camel.component.restlet.RestletComponent for this key
			rc = getNewRestletComponent()
			rcMap.put(key as String, rc)
		}
		println "RestletComponentFactory.getRestletComponent returning RestletComponent: ${rc}"
		return rc
	}

	def getNewRestletComponent(key) {
		println "RestletComponentFactory.getNewRestletComponent for key: ${key}"
		// Create a new org.apache.camel.component.restlet.RestletComponent for this key
		def rc = getNewRestletComponent()
		rcMap.put(key as String, rc)
		println "RestletComponentFactory.getNewRestletComponent returning RestletComponent: ${rc}"
		return rc
	}

	def getNewRestletComponent() {
		// Get the singleton org.restlet.Component
		def comp = applicationContext.getBean(RESTLET_COMPONENT_BEAN)
		comp.defaultHost.setDefaultMatchingMode(org.restlet.routing.Template.MODE_EQUALS)
		return new RestletComponent(comp)
	}	
	
	def remove(key) {
		rcMap.remove(key)
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) 	throws BeansException {
		this.applicationContext = applicationContext
	}
}
