/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.att.ajsc.csi.writeablerequestfilter.WriteableRequestWrapper;

public class WriteableRequestWrapperTest {

	@Test
	public void shouldBeAbleToAddHeader() {

		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		WriteableRequestWrapper wrapper = new WriteableRequestWrapper(mockRequest);
		Enumeration<String> enumStrings = Collections.emptyEnumeration();
		when(mockRequest.getHeaderNames()).thenReturn(enumStrings);
		wrapper.addHeader("Header1", "Value1");
		wrapper.addHeader("Header2", "Value2");

		assertEquals("Value1", wrapper.getHeader("Header1"));
		assertEquals(2, Collections.list(wrapper.getHeaderNames()).size());
		assertEquals(1, Collections.list(wrapper.getHeaders("Header1")).size());
	}

	@Test
	public void shouldAddQueryParams() {

		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		WriteableRequestWrapper wrapper = new WriteableRequestWrapper(mockRequest);
		Enumeration<String> enumStrings = Collections.emptyEnumeration();

		when(mockRequest.getParameterNames()).thenReturn(enumStrings);
		wrapper.addQueryParam("param1", "value1");
		wrapper.addQueryParam("param2", "Value2");

		assertEquals("value1", wrapper.getParameter("param1"));
		assertEquals(2, Collections.list(wrapper.getParameterNames()).size());
		assertEquals("value1", wrapper.getParameter("param1"));

	}

}
