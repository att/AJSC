/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import java.util.Properties;

import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.AbstractProvider;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Access.Service;

/**
 * This provider implementation is used to bootstrap the OpenStack provider implementation.
 * <p>
 * Connectivity to a specific zone is controlled through the properties used to configure the provider. There are some
 * properties that are common and are defined in the <code>Provider</code> interface that all providers implement.
 * However, for provider-specific properties, they are defined in this class.
 * </p>
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */

public class OpenStackProvider extends AbstractProvider {

    /**
     * This method is used to locate a service from the OpenStack service catalog if it is available.
     * 
     * @param serviceType
     *            The type of service requested (such as "image").
     * @param access
     *            The security access object to be used
     * @return A reference to the <code>Service</code> object, or null if not available.
     */
    public Service locateService(String serviceType, Access access) {
        for (Service service : access.getServiceCatalog()) {
            if (service.getType().equals(serviceType)) {
                return service;
            }
        }
        getLogger().error(EELFResourceManager.format(OSMsg.PAL_OS_LOCATE_SERVICE_FAILED, serviceType));
        return null;
    }

    /**
     * This method is used to open and initialize the context based on the supplied properties. These properties are
     * used to configure the context to connect to a specific cloud infrastructure zone, of type OpenStack.
     * 
     * @see com.att.cdp.zones.Provider#openContext(java.util.Properties)
     */
    @Override
    public Context openContext(Properties properties) {
        return new OpenStackContext(this, getDefaults(), properties);
    }
}
