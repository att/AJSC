/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedVirtualInterface;

/**
 * This class implements the OpenStack Template abstraction
 * 
 * @since Oct 18, 2013
 * @version $Id$
 */

public class OpenStackVirtualInterface extends ConnectedVirtualInterface{

    /**
     * The serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param vi
     *            The OpenStack virtual interface associated with a server
     */
    @SuppressWarnings("nls")
    public OpenStackVirtualInterface (Context context, com.woorea.openstack.nova.model.VirtualInterface vi) {
        super(context);

        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("mac_addr", "mac_addr");
        dictionary.put("net_id", "net_id");
        ObjectMapper.map(vi, this, dictionary);
        
    }
}
