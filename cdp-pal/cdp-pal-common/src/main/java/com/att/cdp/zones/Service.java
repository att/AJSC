/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Tenant;

/**
 * The definition of a service. All services must be able to return the tenant that they are servicing as part of the
 * current context.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public interface Service {

    /**
     * All services must be able to return the tenant object that the user has connected to.
     * 
     * @return The tenant object
     * @throws ZoneException
     *             If the user has not logged in
     */
    Tenant getTenant() throws ZoneException;

    /**
     * @return The URL to connect to the service. If this is not applicable to the specific service, then the service
     *         returns an empty string.
     */
    String getURL();
}
