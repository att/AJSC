/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;

/**
 * This class is an abstract base class that provides common support for network service implementations.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public abstract class AbstractNetwork extends AbstractService implements NetworkService {

    /**
     * Create the abstract network service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractNetwork(Context context) {
        super(context);
    }

}
