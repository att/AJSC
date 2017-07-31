/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedRoute;

/**
 * @since May 7, 2015
 * @version $Id$
 */

public class OpenStackRoute extends ConnectedRoute {

    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param route
     *            The route we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackRoute(Context context, com.woorea.openstack.quantum.model.Route route) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("destination", "destination");
        dictionary.put("nexthop", "nexthop");
        ObjectMapper.map(route, this, dictionary);
    }
}
