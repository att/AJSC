/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import org.junit.Test;

import com.att.ajsc.csi.restmethodmap.HttpMethod;
import com.att.ajsc.csi.restmethodmap.RouteMatch;

public class RouteMatchTest {
	
	@Test
	public void shouldReturnRouteMatch(){
		String service = "helloservice";
	    HttpMethod httpMethod = HttpMethod.get;
	    String logicalMethod = "lookup";
	    String matchUri = "/test/1.0";
	    String requestURI = "/group/1.0";
	    String passThroughRespCode ="201,204";
		
		RouteMatch routeMatch = new RouteMatch(service, httpMethod, logicalMethod, matchUri, requestURI,passThroughRespCode, 0);
		
		assertEquals(HttpMethod.get,routeMatch.getHttpMethod());
		assertEquals("lookup",routeMatch.getLogicalMethod());
		assertEquals("/test/1.0",routeMatch.getMatchUri());
		assertEquals( "/group/1.0",routeMatch.getRequestURI());
		
	}

}
