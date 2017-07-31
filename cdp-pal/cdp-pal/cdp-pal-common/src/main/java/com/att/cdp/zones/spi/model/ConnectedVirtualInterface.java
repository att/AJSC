/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.VirtualInterface;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */
public class ConnectedVirtualInterface extends VirtualInterface {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>VirtualInterface</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedVirtualInterface(Context context) {
        super(context);
    }
}
