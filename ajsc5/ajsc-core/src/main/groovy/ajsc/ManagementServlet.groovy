/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.camel.Route
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spring.SpringCamelContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

import groovy.json.JsonBuilder


class ManagementServlet extends HttpServlet {

	def routesInfo = { List<Route> routes ->
		def infos = []
		routes.each { route ->
			def routeInfo = [:]
			routeInfo['uri'] = route.endpoint.endpointUri
		}
	}
	
	void doGet(HttpServletRequest req, HttpServletResponse resp) {
		println "in management servlet"
		def sac = ContextLoader.getCurrentWebApplicationContext()
		println "sac: " + sac
		RouteMgmtService routemgmtsvc = sac.getBean("routeMgmtService")
		println "contexts: " + routemgmtsvc.computeService.ctxMap

		def contexts = [:]
		
		routemgmtsvc.computeService.ctxMap.each { mapentry ->
			SpringCamelContext context = mapentry.getValue()
			contexts[context.name] = [ 'routes' : routesInfo(context.getRoutes()) ]
		}

		resp.contentType = "text/html"
		
		def jsonBuilder = new JsonBuilder(contexts)
		resp.writer.print(jsonBuilder.toString())
	}
}
