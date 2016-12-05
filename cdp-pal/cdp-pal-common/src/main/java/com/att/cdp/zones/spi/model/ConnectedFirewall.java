/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Firewall;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @since Sep 15, 2014
 * @version $Id$
 */

public class ConnectedFirewall extends Firewall {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected firewall object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedFirewall(Context context) {
        super(context);
    }

    /**
     * This method can be called to refresh the firewall from the provider
     * 
     * @throws ZoneException
     *             If the firewall cannot be refreshed
     */
    public void refresh() throws ZoneException {
        throw new NotNavigableException(
            "Firewall object is not connected to a context, no model navigation is possible!");
        // Context context = getContext();
        // Firewall copy = context.getNetworkService().getFirewallById(getId());
        // ObjectMapper.map(copy, this);
    }
}
