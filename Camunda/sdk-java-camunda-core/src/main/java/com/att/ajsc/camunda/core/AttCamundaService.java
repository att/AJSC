/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import javax.servlet.http.HttpServletRequest;

public class AttCamundaService {
	
	
	private static HttpServletRequest httpRequest;
	

	public static HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public static void setHttpRequest(HttpServletRequest httpRequest) {
		AttCamundaService.httpRequest = httpRequest;
	}
    
}