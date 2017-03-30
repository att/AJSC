/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package ${package};

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.camel.AjscRouteBuilder;

@Component
public class Routes extends RouteBuilder {
	@Autowired
	private AjscRouteBuilder ajscRoute;

	@Override
	public void configure() throws Exception {
		ajscRoute.initialize(this);
		ajscRoute
				.setRoute(from("servlet:/?matchOnUriPrefix=true").to("cxfbean:jaxrsServices?providers=jaxrsProviders"));
	}

}
