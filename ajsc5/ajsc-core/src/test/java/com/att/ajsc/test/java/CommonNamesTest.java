/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ajsc.common.CommonNames;

public class CommonNamesTest {

	
	@Test
	public void shouldHaveConstants(){
		assertEquals("X-CSI-Version", CommonNames.CSI_VERSION);
		assertEquals("X-CSI-OriginalVersion", CommonNames.CSI_ORIGINAL_VERSION);
		assertEquals("X-CSI-ConversationId", CommonNames.CSI_CONVERSATION_ID);
		assertEquals("X-CSI-UniqueTransactionId", CommonNames.CSI_UNIQUE_TXN_ID);
		assertEquals("X-CSI-MessageId", CommonNames.CSI_MESSAGE_ID);
		assertEquals("X-CSI-TimeToLive", CommonNames.CSI_TIME_TO_LIVE);
	}
	
}
