/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.LoadBalancerListener;

/**
 * @since 05/04/2015
 * @version $Id$
 */

public class ConnectedLoadBalancerListener extends LoadBalancerListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected LoadBalancerListener object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedLoadBalancerListener(Context context) {
        super(context);
    }

    
}
