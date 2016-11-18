/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;


public class RouteMatch {

	private int matchStrength;
	private String service;
    private HttpMethod httpMethod;
    private String logicalMethod;
    private String matchUri;
    private String requestURI;
    private String passThroughRespCode;
    
    public RouteMatch(String service, HttpMethod httpMethod, String logicalMethod, String matchUri, String requestUri,String passThroughRespCode, int matchStrength) {
        super();
        this.service = service;
        this.httpMethod = httpMethod;
        this.logicalMethod = logicalMethod;
        this.matchUri = matchUri;
        this.requestURI = requestUri;
        this.matchStrength = matchStrength;
        this.passThroughRespCode = passThroughRespCode;
    }

    
    /**
     * @return the httpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    /**
     * @return the target
     */
    public String getLogicalMethod() {
        return logicalMethod;
    }

    
    /**
     * @return the matchUri
     */
    public String getMatchUri() {
        return matchUri;
    }

    
    /**
     * @return the requestUri
     */
    public String getRequestURI() {
        return requestURI;
    }
    
    public String getService()
    {
    	return service;
    }
    
    public int getMatchStrength()
    {
    	return matchStrength;
    }


	public String getPassThroughRespCode() {
		return passThroughRespCode;
	}


	public void setPassThroughRespCode(String passThroughRespCode) {
		this.passThroughRespCode = passThroughRespCode;
	}
    
    
}
