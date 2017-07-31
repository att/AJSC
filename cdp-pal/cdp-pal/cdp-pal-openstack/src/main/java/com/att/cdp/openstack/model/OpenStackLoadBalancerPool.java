/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.ProtocolType;
import com.att.cdp.zones.spi.model.ConnectedLoadBalancerPool;

/**
 * @since May 04, 2015
 * @version $Id$
 */

public class OpenStackLoadBalancerPool extends ConnectedLoadBalancerPool {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param pool
     *            the load balancer pool
     */
    public OpenStackLoadBalancerPool(Context context, com.woorea.openstack.quantum.model.LoadBalancerPool pool) {
        super(context);

        // TODO pool status
        setId(pool.getId());
        String algorithm = pool.getMethod();
        if (algorithm != null) {
            try {
                setLbAlgorithm(AlgorithmType.valueOf(algorithm));
            } catch (IllegalArgumentException e) {
                setLbAlgorithm(null);
            }
        }

        String protocol = pool.getProtocol();
        if (protocol != null) {
            try {
                setProtocol(ProtocolType.valueOf(protocol));
            } catch (IllegalArgumentException e) {
                setProtocol(null);
            }
        }
        // TODO pool description
        setHealthMonitorIds(pool.getMonitors());
        setAdminStateUp(pool.getState());
        setName(pool.getName());
        // TODO session persistence
        setMemberIds(pool.getMembers());
        setSubnetId(pool.getSubnetId());
        setListenerId(pool.getVipId());

    }

}
