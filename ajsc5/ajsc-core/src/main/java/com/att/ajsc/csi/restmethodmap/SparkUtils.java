/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Some utility methods
 */
public final class SparkUtils {

    public static final String ALL_PATHS = "+/*paths";
    
    private SparkUtils() {}
    
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }
    
    public static boolean isParam(String routePart) {
        return routePart.startsWith("{") && routePart.endsWith("}");
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }
    
}
