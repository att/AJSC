/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.Port;

/**
 * @since Jul 21, 2014
 * @version $Id$
 */

public class ConnectedPort extends Port {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Object</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedPort(Context context) {
        super(context);
    }

    @Override
    public void delete() throws ZoneException {
        NetworkService service = getContext().getNetworkService();
        service.deletePort(this);
    }

}
