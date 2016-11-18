/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.filter.PassthruFilter;

public class PassthruFilterTest {
	
	@Test
	public void shouldCallDoFilter() throws IOException, ServletException{
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		PassthruFilter passthruFilter = new PassthruFilter();
		passthruFilter.doFilter(request, response, chain);
		
		verify(chain).doFilter(request, response);
		
	}

}
