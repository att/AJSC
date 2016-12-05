/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * Standard enumeration of O/S distributiuon types as defined by the "libosinfo project"
 * 
 * @see <a href="http://libosinfo.org/">libosinfo</a>
 * @since Sep 30, 2013
 * @version $Id$
 */

public class VirtualInterface extends ModelObject {

    private static final long serialVersionUID = 1L;
    
    /**
     * The unique identifier for the virtual interface.
     */
    private String id;
    
    /**
     * The mac address associated with the virtual interface.
     */
    private String mac_addr;
    
    /**
     * The network id associated with the virtual interface.
     */
    private String net_id;
    
    /**
     * @param context
     *            The context we are connected to
     */
    public VirtualInterface(Context context) {
    	super(context);
	}

	/**
     * @return The id of the virtual interface
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return The mac address of the virtual interface
     */
    public String getMacAddress() {
        return mac_addr;
    }
    
    /**
     * @return The network id of the virtual interface
     */
    public String getNetworkId() {
        return net_id;
    }
}
