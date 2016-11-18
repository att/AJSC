/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.exceptions.CSIRESTException;
import ajsc.exceptions.RESTExceptionUtil;
import ajsc.utils.ExceptionInfo;

public class CSIRESTExceptionTest {
	
	private CSIRESTException restException;
	

	@Test
	public void testCreateErrorFromSoapFault(){
		restException = RESTExceptionUtil.csiRESTError("203","Resource not available");
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
