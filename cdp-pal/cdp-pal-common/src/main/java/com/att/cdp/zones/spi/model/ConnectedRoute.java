/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Route;

/**
 * The definition of a connected route on a subnet
 * 
 * @since May 07, 2015
 * @version $Id$
 */

public class ConnectedRoute extends Route {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Route</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedRoute(Context context) {
        super(context);
    }
}
