/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.exceptions.CSIRESTException;
import ajsc.exceptions.RESTExceptionUtil;
import ajsc.utils.ExceptionInfo;

public class RESTExceptionUtilTest {
	
	private CSIRESTException restException;
	
	@Test
	public void shouldReturnNativeRESTError(){
		restException = RESTExceptionUtil.nativeRESTError(500, "SVC9999", "An internal error has occurred: %1", "Resource not available");
		assertTrue(restException.getEx_().getMessageId().equals(expectedExceptionInfo().getMessageId()));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,restException.getHTTPErrorCode());
	}
	
	@Test
	public void shouldReturnNativeRESTErrorFromProvider(){
		restException = RESTExceptionUtil.nativeRESTErrorFromProvider(500, "SVC9999", "An internal error has occurred: %1", "faultEntity", "extCode", "extDesc", "Resource not available");
		assertTrue(restException.getEx_().getMessageId().equals(expectedExceptionInfo().getMessageId()));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,restException.getHTTPErrorCode());
	}
	
	@Test
	public void shouldReturncsiRESTErrorFromProvider(){
		restException = RESTExceptionUtil.csiRESTErrorFromProvider ("9999", "SVC9999", "faultEntity", "extCode", "extDesc");
		assertTrue(restException.getEx_().getMessageId().equals(expectedExceptionInfo().getMessageId()));
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,restException.getHTTPErrorCode());
	}
	
	public ExceptionInfo expectedExceptionInfo(){
		ExceptionInfo info = new ExceptionInfo();
		info.setMessageId("SVC9999");
		info.setText("An internal error has occurred: %1");
		info.getVariables().add("Resource not available");
		return info;
	}

}
