/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.att.ajsc.csi.writeablerequestfilter.WriteableRequestFilter;
import com.att.ajsc.csi.writeablerequestfilter.WriteableRequestWrapper;

public class WriteableRequestFilterTest {

	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);
	FilterChain chain = mock(FilterChain.class);
	

	@Test
	public void shouldCallDoFilter() throws IOException, ServletException {
		WriteableRequestFilter requestFilter = new WriteableRequestFilter();
		requestFilter.doFilter(request, response, chain);
		
		WriteableRequestWrapper wreq = new WriteableRequestWrapper((HttpServletRequest)request);
		wreq.addHeader(WriteableRequestFilter.WRITEABLE_FILTER_INDICATOR, "true");
		
		verify(chain).doFilter(any(WriteableRequestWrapper.class),any(HttpServletResponse.class));
		
	}

}
