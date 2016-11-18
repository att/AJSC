/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import ajsc.utils.DME2Helper;

import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.api.DME2Manager;

public class DME2HelperTest {
	
	@BeforeClass
	public static void setup()
	{
		System.setProperty("AFT_LATITUDE","23.4");
		System.setProperty("AFT_LONGITUDE","33.6");
		System.setProperty("AFT_ENVIRONMENT","AFTUAT");
		System.setProperty("APP_CONTEXT_PATH","appcontext");
		System.setProperty("AJSC_SERVICE_NAMESPACE","helloworld");
		System.setProperty("AJSC_SERVICE_VERSION","1.0.0");
		System.setProperty("SOACLOUD_NAMESPACE","com.att.ajsc");
		System.setProperty("SOACLOUD_SERVICE_VERSION","2.0.0");
		System.setProperty("SOACLOUD_ENV_CONTEXT","DEV");
		System.setProperty("SOACLOUD_ROUTE_OFFER","DEFAULT");
		
	}
	
	@Test
	public void shouldReturnServiceEndPoint() throws DME2Exception, Exception{
		DME2Helper.init();
		DME2Helper helper = new DME2Helper();
		helper.registerServiceToGRM("restlet:///com.test.att", DME2Manager.getDefaultInstance(),true);
		Set<String> restEndpoints = DME2Helper.restletEndpointSet;
		assertNotNull(restEndpoints);
		helper.registerServiceToGRM("att-dme2-servlet:/com.test.att", DME2Manager.getDefaultInstance(),true);
		Set<String> serviceEndpoints = DME2Helper.serviceEndpointSet;
		assertNotNull(serviceEndpoints);
	}

}
