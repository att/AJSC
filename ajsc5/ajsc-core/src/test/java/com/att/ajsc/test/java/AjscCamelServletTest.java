/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.ComputeService;
import ajsc.servlet.AjscCamelServlet;

public class AjscCamelServletTest {/*
	
	@Test
	public void serviceMethodCallShouldSetResponse() throws ServletException, IOException{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		when(mockRequest.getMethod()).thenReturn("head");
		when(mockRequest.getServletPath()).thenReturn(" ", " ");
		when(mockRequest.getPathInfo()).thenReturn("/hello/test");
		ComputeService.endpointUriMap.put("/hello/test", "/hello/service1/test");
		
		AjscCamelServlet camelServlet = new AjscCamelServlet();
		
		
		camelServlet.service(mockRequest, mockResponse);
		
		verify(mockResponse).setStatus(200);
		
	}

*/}
