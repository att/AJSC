/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Subnet;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */

public class ConnectedNetwork extends Network {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Network</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedNetwork(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.Network#getSubnet(java.lang.String)
     */
    @Override
    public Subnet getSubnet(String id) throws ZoneException {
        Context context = getContext();
        return context.getNetworkService().getSubnetById(id);
    }

    @Override
    public void refresh() throws ZoneException {
        Context context = getContext();
        Network copy = context.getNetworkService().getNetworkById(getId());
        ObjectMapper.map(copy, this);
    }
}
