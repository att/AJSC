/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;


import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.model.ConnectedLoadBalancerMember;

/**
 * @since May 04, 2015
 * @version $Id$
 */

public class OpenStackLoadBalancerMember extends ConnectedLoadBalancerMember {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param member
     *            The load balancer member
     */
    public OpenStackLoadBalancerMember(Context context, com.woorea.openstack.quantum.model.LoadBalancerMember member) {
        super(context);

        setId(member.getId());
        setSubnetId(member.getSubnetId());
        setAddress(member.getAddress());
        setProtocolPort(member.getPort());
        setWeight(member.getWeight());
        setAdminStateUp(member.getState());
        setPoolId(member.getPoolId());
    }

}
