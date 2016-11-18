/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.att.ajsc.csi.restmethodmap.HttpMethod;
import com.att.ajsc.csi.restmethodmap.RouteMatch;
import com.att.ajsc.csi.restmethodmap.SimpleRouteMatcher;

public class SimpleRouteMatcherTest {
	SimpleRouteMatcher matcher = null;
	
	@Before
	public void setup(){
		matcher = new SimpleRouteMatcher();
	    String logicalMethod = "lookup";
	    String url = "/namespace/1.0/helloservice";
	    String dme2url = "/dme2/test/hello";
		
		matcher.parseValidateAddRoute("helloservice", "*", url, logicalMethod, dme2url,"rest","worldservice","201,401");
		matcher.parseValidateAddRoute("worldservice", "*", url, logicalMethod, dme2url,"rest","worldservice","201,401");
	}

	@Test
	public void testRoutesCount(){
		
		assertEquals(2,matcher.getRoutes().size());
	}
	
	@Test
	public void testShouldReturnRoute(){

		RouteMatch routeMatch = matcher.findTargetForRequestedRoute(HttpMethod.starstar, "/namespace/1.0/helloservice");
		assertEquals("helloservice",routeMatch.getService());
	}
	
	@Test
	public void testShouldReturnAllRoutes(){
		
		List<RouteMatch> routeMatches = matcher.findTargetsForRequestedRoute(HttpMethod.starstar, "/namespace/1.0/helloservice");
		assertEquals(2,routeMatches.size());
	}
}
