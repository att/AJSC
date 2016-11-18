/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.BaseTestCase;
import ajsc.exceptions.RestError;
import ajsc.utils.RefresheableRESTErrorMap;

public class RefresheableRESTErrorMapTest extends BaseTestCase{
	
	@Test
	public void shouldReturnSOAPAuthError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("100");
		assertEquals(HttpServletResponse.SC_FORBIDDEN,error.status);
		assertEquals("POL1000",error.MessageId);
		assertEquals("The operation is not allowed: %1",error.Message);
	}
	
	@Test
	public void shouldReturnSOAPServiceUnavailableError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("200");
		assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE,error.status);
		assertEquals("SVC2000",error.MessageId);
		assertEquals("A resource required by the service is unavailable: %1",error.Message);
	}
	

	@Test
	public void shouldReturnSOAPDataError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("300");
		assertEquals(HttpServletResponse.SC_BAD_REQUEST,error.status);
		assertEquals("SVC3000",error.MessageId);
		assertEquals("A data error has occurred: %1",error.Message);
	}

	@Test
	public void shouldReturnCSIXMLRequestError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("400");
		assertEquals(HttpServletResponse.SC_BAD_REQUEST,error.status);
		assertEquals("SVC4000",error.MessageId);
		assertEquals("The request is invalid: %1",error.Message);
	}

	@Test
	public void shouldReturnCSIBusProcError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("500");
		assertEquals(HttpServletResponse.SC_BAD_REQUEST,error.status);
		assertEquals("SVC5000",error.MessageId);
		assertEquals("A business processing error has occured: %1",error.Message);
	}
	
	@Test
	public void shouldReturnInternalServerError(){
		RestError error = RefresheableRESTErrorMap.getHttpCodeForCSIError("999");
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,error.status);
		assertEquals("SVC9999",error.MessageId);
		assertEquals("An internal error has occurred: %1",error.Message);
	}
	
}
