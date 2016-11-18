/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.*;

import org.junit.Test;

import ajsc.common.CommonErrors;

public class CommonErrorsTest {
	
	@Test
	public void shouldHaveConstants(){
		assertEquals("10000000005", CommonErrors.DEF_401_FAULT_CODE);
		assertEquals("10000000003", CommonErrors.DEF_403_FAULT_CODE);
		assertEquals("20000000005", CommonErrors.DEF_501_FAULT_CODE);
		assertEquals("20000000003", CommonErrors.DEF_503_FAULT_CODE);
		assertEquals("20000000013", CommonErrors.DEF_500_FAULT_CODE);
		assertEquals("20000000013", CommonErrors.DEF_5NN_FAULT_CODE);
	}

}
