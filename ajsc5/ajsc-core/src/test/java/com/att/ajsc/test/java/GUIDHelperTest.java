/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import org.junit.Test;

import ajsc.common.CommonNames;
import ajsc.utils.GUIDHelper;

public class GUIDHelperTest {
	
	@Test
	public void shouldReturnCSIConversationId(){
		String converationId = GUIDHelper.createCSIConversationId("partnerName");
		assertTrue(converationId.contains("partnerName"+"~CNG-CSI~"));
	}
	
	@Test
	public void shouldReturnUniqueTransactionId(){
		String converationId = GUIDHelper.createUniqueTransactionId();
		assertTrue(converationId.contains(CommonNames.AJSC_CSI_RESTFUL));
	}
	
	@Test
	public void shouldReturnCSIConversationIdWithGuid(){
		String converationId = GUIDHelper.createCSIConversationId("partnerName","guid");
		assertTrue(converationId.contains("partnerName"+"~CNG-CSI~"));
	}
	
	

}
