/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.KeyPair;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */
public class ConnectedKeyPair extends KeyPair {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the connected Key-Pair object (to the supplied context)
     * 
     * @param context
     *            The context we are connected to
     */
    public ConnectedKeyPair(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.KeyPair#delete()
     */
    @Override
    public void delete() throws ZoneException {
        Context context = getContext();
        context.getIdentityService().deleteKeyPair(this);
    }
}
