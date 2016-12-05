/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.ACL;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */
public class ConnectedACL extends ACL {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected ACL object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedACL(Context context) {
        super(context);
    }
}
