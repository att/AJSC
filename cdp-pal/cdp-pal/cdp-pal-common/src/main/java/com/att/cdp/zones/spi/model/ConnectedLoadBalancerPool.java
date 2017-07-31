/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.LoadBalancerPool;

/**
 * @since 05/04/2015
 * @version $Id$
 */

public class ConnectedLoadBalancerPool extends LoadBalancerPool {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected LoadBalancerPool object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedLoadBalancerPool(Context context) {
        super(context);
    }

    
}
