/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v3;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.impl.AbstractOpenStackIdentityService;
import com.att.cdp.zones.Context;

/**
 * This class implements the identity service for OpenStack.
 * <p>
 * The purpose of this class is to map the abstractions provided by the <code>Identity</code> interface to the services
 * and implementation of OpenStack. This means that these methods in this class must catch all exceptions thrown by
 * OpenStack, as well as handle all data types and return codes, mapping these to the abstraction definitions.
 * </p>
 * <p>
 * The identity service is provided by Keystone within OpenStack. This class interfaces directly with Keystone.
 * </p>
 * 
 * @author <a href="mailto:dh868g@att.com?subject=com.att.cdp.zones.os.services.IdentityImpl">Dewayne Hafenstein</a>
 * @since Sep 24, 2013
 * @version $Id$
 */

public class OpenStackIdentityService extends AbstractOpenStackIdentityService /*
                                                                                * extends AbstractIdentity implements
                                                                                * CommonIdentityService
                                                                                */{

    /**
     * Create the OpenStack Identity implementation.
     * 
     * @param context
     *            The context that we are servicing
     */
    public OpenStackIdentityService(Context context) {
        super(context);
    }

}
