/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.PersistentObject;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */

public class ConnectedPersistentObject extends PersistentObject {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Object</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedPersistentObject(Context context) {
        super(context);
    }
}
