/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.ProtocolType;
import com.att.cdp.zones.spi.model.ConnectedLoadBalancerListener;

/**
 * @since May 04, 2015
 * @version $Id$
 */

public class OpenStackLoadBalancerVIP extends ConnectedLoadBalancerListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param vip
     *            The VIP
     */
    public OpenStackLoadBalancerVIP(Context context, com.woorea.openstack.quantum.model.LoadBalancerVIP vip) {
        super(context);

        setId(vip.getId());
        setName(vip.getName());
        //TODO description
        setSubnetId(vip.getSubnetId());
        setIpAddress(vip.getAddress());
        String protocol = vip.getProtocol();
        if (protocol != null) {
      	   try {
      		   setProtocol(ProtocolType.valueOf(protocol));
      	   } catch (IllegalArgumentException  e) {
      		   setProtocol(null);
      	   }
         }
        setProtocolPort(vip.getPort());
        setPoolId(vip.getPoolId());
        //TODO session persistence
        setConnectionLimit(vip.getConnectionLimit());
        setAdminStateUp(vip.getState());
        //TODO status
        
    }

}
