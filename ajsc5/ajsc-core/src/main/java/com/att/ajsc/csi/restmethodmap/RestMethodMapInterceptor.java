/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ajsc.csi.writeablerequestfilter.WriteableRequestFilter;
import com.att.ajsc.csi.writeablerequestfilter.WriteableRequestWrapper;

import ajsc.beans.interceptors.AjscInterceptor;

public class RestMethodMapInterceptor implements AjscInterceptor 
{
	private static Logger logger = LoggerFactory.getLogger(RestMethodMapInterceptor.class);
	private static RestMethodMapInterceptor singleton = null;
	
	public static RestMethodMapInterceptor getInstance() throws Exception
	{
		try
		{
			if ( null == singleton )
			{
				synchronized(RestMethodMapInterceptor.class)
				{
					if ( null == singleton )
					{
						logger.info("Creating new RestMethodMapInterceptor...");
						singleton = new RestMethodMapInterceptor();
					}
				}
			}
			return singleton;
		}
		catch ( Exception e )
		{
			logger.error("ERROR retrieving RestMethodMapInterceptor ", e);
			throw(e);
		}
	}
	
	private RestMethodMapInterceptor() throws Exception
	{
		try
		{
			String serviceSpecFileName = System.getProperty("AJSC_CONF_HOME") + "/etc/appprops/methodMapper.properties"; 
			File file = new File(serviceSpecFileName);
			RefresheableSimpleRouteMatcher.refresh(file);
			
		}
		catch ( Exception e )
		{
			logger.error("ERROR instantiating method map ", e);
			throw(e);
		}
	}
	
	@Override
	public boolean allowOrReject(HttpServletRequest req, HttpServletResponse resp, Map<?, ?> params) 
				   throws Exception 
	{
		boolean allow = true;
		try
		{
			WriteableRequestWrapper request = null;
			if ( "true".equalsIgnoreCase(req.getHeader(WriteableRequestFilter.WRITEABLE_FILTER_INDICATOR)) )
				request = (WriteableRequestWrapper)req;
			else
				request = new WriteableRequestWrapper(req);
			
			String uri = request.getRequestURI();
			HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toLowerCase());
			RouteMatcher matcher = RefresheableSimpleRouteMatcher.getRouteMatcher();
			matcher.findTargetForRequestedRoute(httpMethod, uri);
		}
		catch ( Exception e )
		{
			logger.error("ERROR matching request to logical name - ", e);
		}
		return allow;
	}

}
