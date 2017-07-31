/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.Properties;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.spi.model.ConnectedTenant;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */

public class OpenStackTenant extends ConnectedTenant {

    /**
     * The class serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the Tenant object implementation for the OpenStack provider
     * 
     * @param context
     *            The OpenStack context we are servicing
     * @param tenant
     *            The OpenStack tenant definition
     */
    public OpenStackTenant(OpenStackContext context, com.woorea.openstack.keystone.model.Tenant tenant) {
        super(context);
        setId(tenant.getId());
        setName(tenant.getName());
        setDescription(tenant.getDescription());
        setEnabled(tenant.getEnabled());

        Properties properties = context.getProperties();
        setName(properties.getProperty(ContextFactory.PROPERTY_TENANT));
    }
}
