/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.writeablerequestfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WriteableRequestFilter implements Filter 
{
	public static final String WRITEABLE_FILTER_INDICATOR = "X-CSI-Internal-WriteableRequest";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException 
	{
		// Nothing to initialize
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		WriteableRequestWrapper wreq = new WriteableRequestWrapper((HttpServletRequest)request);
		wreq.addHeader(WRITEABLE_FILTER_INDICATOR, "true");
		chain.doFilter(wreq, response);
	}

	@Override
	public void destroy() 
	{
		// No resources to destroy
	}
}
