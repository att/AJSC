/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ajsc.BaseTestCase;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;


public class AJSCPropertiesMapTest extends BaseTestCase{

	@Test
	public void shouldReturnValue() throws Exception{
		File propertiesFile = new File(TEST_RSC_DIR+"sys-props.properties");
		AJSCPropertiesMap.refresh(propertiesFile);
		String actualTimeOut = AJSCPropertiesMap.getProperty("sys-props.properties", "AFT_DME2_CONN_IDLE_TIMEOUTMS");
		assertEquals("5000",actualTimeOut);
		String camelMaxPoolSize = AJSCPropertiesMap.getProperty("sys-props.properties", "CAMEL_MAX_POOL_SIZE");
		assertEquals("20",camelMaxPoolSize);
		assertNotNull(AJSCPropertiesMap.getProperties(propertiesFile.getName()));
		
	}
}
