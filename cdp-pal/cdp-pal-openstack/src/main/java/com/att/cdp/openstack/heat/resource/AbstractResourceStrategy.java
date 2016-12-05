/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.resource;

import com.att.cdp.openstack.model.OpenStackStack;

/**
 * This is the base class for all resource strategy implementation classes.
 * 
 * @since Feb 11, 2015
 * @version $Id$
 */
public abstract class AbstractResourceStrategy {

    /**
     * This method is called by the strategy for each resource to be processed to determine if the concrete strategy
     * implementation object understands and can map the specified resource.
     * 
     * @param resource
     *            The resource to be mapped
     * @return True if this concrete strategy implementation class handles this resource type.
     */
    public abstract boolean isMapped(com.woorea.openstack.heat.model.Resource resource);

    /**
     * This method is called to actually map the specified resource into the current OpenStack stack model object for
     * the current context.
     * 
     * @param stack
     *            The connected stack model to be mapped. The current context can be obtained from this object if
     *            required.
     * @param resource
     *            The resource object to be mapped.
     */
    public abstract void map(OpenStackStack stack, com.woorea.openstack.heat.model.Resource resource);
}
