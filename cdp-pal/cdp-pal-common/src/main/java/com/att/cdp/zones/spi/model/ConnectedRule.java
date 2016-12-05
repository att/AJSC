/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Rule;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */
public class ConnectedRule extends Rule {

    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected rule
     * 
     * @param context
     *            The context
     */
    public ConnectedRule(Context context) {
        super(context);
    }

    /**
     * Returns true to indicate that the object is connected to a context
     * 
     * @see com.att.cdp.zones.model.ModelObject#isConnected()
     */
    @Override
    public boolean isConnected() {
        return true;
    }
}
