/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.HttpMethodType;
import com.att.cdp.zones.model.ProtocolType;
import com.att.cdp.zones.spi.model.ConnectedLoadBalancerHealthMonitor;
import com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor.Pool;

/**
 * @since May 04, 2015
 * @version $Id$
 */

public class OpenStackLoadBalancerHealthMonitor extends ConnectedLoadBalancerHealthMonitor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param healthMonitor
     *            The health monitor we are mapping
     */
    public OpenStackLoadBalancerHealthMonitor(Context context, com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor healthMonitor) {
        super(context);

       setId(healthMonitor.getId());
       String type = healthMonitor.getType();
       if (type != null) {
    	   try {
    		   setType(ProtocolType.valueOf(type));
    	   } catch (IllegalArgumentException  e) {
    		   setType(null);
    	   }
       }
       setDelay(healthMonitor.getDelay());
       setTimeout(healthMonitor.getTimeout());
       setMaxRetries(healthMonitor.getMaxRetries());
       String httpMethod = healthMonitor.getHttpMethod();
       if (httpMethod != null) {
    	   try {
    		   setHttpMethod(HttpMethodType.valueOf(httpMethod));
    	   } catch (IllegalArgumentException  e) {
    		   setHttpMethod(null);
    	   }
       }
       setUrlPath(healthMonitor.getUrlPath());
       setExpectedCodes(healthMonitor.getExpectedCodes());
       setAdminStateUp(healthMonitor.getState());
       List<Pool> pools = healthMonitor.getPools();
       if (pools != null ) {
    	   List<String> poolIds = new ArrayList<>();
    	   for (Pool pool:pools) {
    		   poolIds.add(pool.getId());
    	   }
    	   setPoolIds(poolIds);
       }
      
       
       //TODO status
  }

}
