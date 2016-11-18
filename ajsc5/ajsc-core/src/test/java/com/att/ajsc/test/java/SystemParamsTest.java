/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.junit.Ignore;
import org.junit.Test;

import ajsc.utils.SystemParams;

public class SystemParamsTest {
	@Ignore
	@Test
	public void shouldCreateInstance() throws UnknownHostException{
		SystemParams params = SystemParams.instance();
		System.setProperty("lrmHost","12345");
		
		assertTrue(params.getInstanceName().contains("ajsc:N/A-N/A-N/A-"));
		assertNotNull(params.getHostName());
		assertEquals("N/A",params.getAppName());
	}


}
