/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import com.att.cdp.zones.IdentityService;
import com.woorea.openstack.keystone.model.Access;

/**
 * This interface is used to expose common internal (not exposed to an external client) behaviors that must be provided
 * for any version implementation of the OpenStack Identity Service.
 * 
 * @since May 2, 2014
 * @version $Id$
 */

public interface CommonIdentityService extends IdentityService {

    /**
     * this method is used to destroy the authentication token and effectively logout of OpenStack.
     */
    void destroyToken();

    /**
     * Returns the <code>Access</code> object that contains the authorization token granted when the identity service
     * logged into OpenStack.
     * 
     * @return The identity access object
     */
    Access getAccess();
}
