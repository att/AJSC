/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.att.ajsc.csi.restmethodmap.SparkUtils;

public class SparkUtilsTest {

	
	@Test
	public void shouldReturnArrayList(){
		List<String> paths = SparkUtils.convertRouteToList("file:/data?noops=true ");
		assertEquals(2,paths.size());
	}
	
	@Test
	public void isParam(){
		assertTrue(SparkUtils.isParam("{sleep=1000?convert=true}"));
	}
	
	@Test
	public void isSplat(){
		assertTrue(SparkUtils.isSplat("*"));
	}
	
}
