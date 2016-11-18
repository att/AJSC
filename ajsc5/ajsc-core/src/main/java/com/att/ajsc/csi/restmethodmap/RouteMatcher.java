/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;

import java.util.List;


public interface RouteMatcher {
    
    String ROOT = "/";
    char SINGLE_QUOTE = '\'';
    
    /**
     * Parses, validates and adds a route
     * 
     * @param route
     * @param logicalMethod
     */
    void parseValidateAddRoute(String service, String httpMethod, String url, String logicalMethod, String dme2url, String type, String serviceName,String passThroughRespCode);
    /**
     * Finds the a target route for the requested route path
     * 
     * @param httpMethod
     * @param route
     * @return
     */
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route);


    /**
     * Finds the targets for a requested route path (used for filters)
     */
    List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path);
    
    /**
     * Clear all routes
     */
    void clearRoutes();
}
