/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import ajsc.beans.interceptors.HelloWorldLogger;
import ajsc.common.CommonNames;

public class HelloWorldLoggerTest {

	HelloWorldLogger interceptor;
	HttpServletRequest request;
	HttpServletResponse response;
	HashMap<String,Long> map;
	
	
	@Before
	public void setup(){
		interceptor = HelloWorldLogger.getInstance();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		map = new HashMap<>();
		map.put(CommonNames.REQUEST_START_TIME,100L);
		
	}
	
	@Test
	public void testAllowOrReject() throws Exception{
		assertTrue(interceptor.allowOrReject(request, response, map));
	}
}
