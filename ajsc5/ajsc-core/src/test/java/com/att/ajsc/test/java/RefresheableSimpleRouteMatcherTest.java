/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import ajsc.BaseTestCase;

import com.att.ajsc.csi.restmethodmap.RefresheableSimpleRouteMatcher;
import com.att.ajsc.csi.restmethodmap.SimpleRouteMatcher;

public class RefresheableSimpleRouteMatcherTest extends BaseTestCase{
	
	@Test
	public void testRouteMatcher() throws Exception{
		
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		SimpleRouteMatcher matcher = (SimpleRouteMatcher)RefresheableSimpleRouteMatcher.getRouteMatcher();

		assertEquals(3,matcher.getRoutes().size());
	}

}
