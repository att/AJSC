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
 *       "Type": "OS::Cinder::Volume",
 *       "Properties": {
 *         "availability_zone": String,
 *         "backup_id": String,
 *         "description": String,
 *         "image": String,
 *         "metadata": {...},
 *         "name": String,
 *         "scheduler_hints": {...},
 *         "size": Integer,
 *         "snapshot_id": String,
 *         "source_volid": String,
 *         "volume_type": String
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * @since Feb 11, 2015
 * @version $Id$
 */

public class CinderVolumeResourceStrategy extends AbstractResourceStrategy {

    /**
     * @see com.att.cdp.openstack.heat.resource.AbstractResourceStrategy#isMapped(com.woorea.openstack.heat.model.Resource)
     */
    @Override
    public boolean isMapped(Resource resource) {
        return resource != null && resource.getType().equals("OS::Cinder::Volume");
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
