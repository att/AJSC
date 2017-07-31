/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.resource.strategy;

import com.att.cdp.openstack.heat.resource.AbstractResourceStrategy;
import com.att.cdp.openstack.model.OpenStackStack;
import com.woorea.openstack.heat.model.Resource;

/**
 * <pre>
 * {
 *   ...
 *   "Resources" : {
 *     "TheResource": {
 *       "Type": "OS::Neutron::ExtraRoute",
 *       "Properties": {
 *         "destination": String,
 *         "nexthop": String,
 *         "router_id": String
 *       }
 *     }
 *   }
 * } 
 * </pre>
 * 
 * @since Feb 11, 2015
 * @version $Id$
 */

public class NeutronExtraRouteResourceStrategy extends AbstractResourceStrategy {

    /**
     * @see com.att.cdp.openstack.heat.resource.AbstractResourceStrategy#isMapped(com.woorea.openstack.heat.model.Resource)
     */
    @Override
    public boolean isMapped(Resource resource) {
        return resource != null && resource.getType().equals("OS::Neutron::ExtraRoute");
    }

    /**
     * @see com.att.cdp.openstack.heat.resource.AbstractResourceStrategy#map(com.att.cdp.openstack.model.OpenStackStack,
     *      com.woorea.openstack.heat.model.Resource)
     */
    @Override
    public void map(OpenStackStack stack, Resource resource) {
        // TODO Auto-generated method stub

    }

}
