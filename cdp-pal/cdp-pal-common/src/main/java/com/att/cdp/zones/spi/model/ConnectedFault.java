/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Fault;

/**
 * @since Oct 25, 2013
 * @version $Id$
 */

public class ConnectedFault extends Fault {

    /**
     * The serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected fault
     * 
     * @param context
     *            The context we are servicing
     */
    public ConnectedFault(Context context) {
        super(context);
    }
}
