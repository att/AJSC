/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import ajsc.beans.interceptors.TestInterceptorClass;
import ajsc.common.CommonNames;

public class TestInterceptorClassTest {

	TestInterceptorClass interceptor;
	HttpServletRequest request;
	HttpServletResponse response;
	HashMap<String,Long> map;
	
	
	@Before
	public void setup(){
		interceptor = TestInterceptorClass.getInstance();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		map = new HashMap<>();
		map.put(CommonNames.REQUEST_START_TIME,100L);
		
	}
	
	@Test
	public void testAllowOrReject() throws Exception{
		assertFalse(interceptor.allowOrReject(request, response, map));
	}
}
