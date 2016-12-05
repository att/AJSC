/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Router;
import com.att.cdp.zones.model.Tenant;

/**
 * @since Jul 21, 2014
 * @version $Id$
 */

public class ConnectedRouter extends Router {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Router</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedRouter(Context context) {
        super(context);
    }
}
