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
 *   "AWSTemplateFormatVersion" : "2010-09-09",
 *   ...
 *   "Resources" : {
 *     "TheResource": {
 *       "Type": "OS::Neutron::FloatingIPAssociation",
 *       "Properties": {
 *         "fixed_ip_address": String,
 *         "floatingip_id": String,
 *         "port_id": String
 *       }
 *     }
 *   }
 * } 
 * </pre>
 * 
 * @since Feb 11, 2015
 * @version $Id$
 */

public class NeutroFloatingIPAssociationResourceStrategy extends AbstractResourceStrategy {

    /**
     * @see com.att.cdp.openstack.heat.resource.AbstractResourceStrategy#isMapped(com.woorea.openstack.heat.model.Resource)
     */
    @Override
    public boolean isMapped(Resource resource) {
        return resource != null && resource.getType().equals("OS::Neutron::FloatingIPAssociation");
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
