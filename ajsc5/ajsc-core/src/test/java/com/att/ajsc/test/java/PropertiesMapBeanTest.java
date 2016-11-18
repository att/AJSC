/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import ajsc.BaseTestCase;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;

public class PropertiesMapBeanTest extends BaseTestCase{
	
	@Test
	public void shouldReturnProperty() throws Exception{
		File propertiesFile = new File(TEST_RSC_DIR+"sys-props.properties");
		AJSCPropertiesMap.refresh(propertiesFile);
		String actualTimeOut = PropertiesMapBean.getProperty("sys-props.properties", "AFT_DME2_CONN_IDLE_TIMEOUTMS");
		assertEquals("5000",actualTimeOut);
	}

}
