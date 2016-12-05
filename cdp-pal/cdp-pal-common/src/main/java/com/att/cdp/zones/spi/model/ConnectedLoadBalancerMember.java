/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.LoadBalancerMember;
import com.att.cdp.zones.model.Tenant;

/**
 * @since 09/24/2014
 * @version $Id$
 */

public class ConnectedLoadBalancerMember extends LoadBalancerMember {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected LoadBalancer object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedLoadBalancerMember(Context context) {
        super(context);
    }

    /**
     * This method can be called to refresh the load balancer from the provider
     * 
     * @throws ZoneException
     *             If the firewall cannot be refreshed
     */
    public void refresh() throws ZoneException {
        throw new NotNavigableException("LoadBalancerMember object refresh not yet implemented");
    }
}
