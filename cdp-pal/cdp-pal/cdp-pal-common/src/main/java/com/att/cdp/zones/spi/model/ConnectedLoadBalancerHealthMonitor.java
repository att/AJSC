/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.LoadBalancerHealthMonitor;

/**
 * @since 05/04/2015
 * @version $Id$
 */

public class ConnectedLoadBalancerHealthMonitor extends LoadBalancerHealthMonitor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected LoadBalancerHealthMonitor object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedLoadBalancerHealthMonitor(Context context) {
        super(context);
    }

   
}
