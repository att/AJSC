/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.model.ConnectedFault;

/**
 * This class represents a fault condition.
 * 
 * @since Oct 25, 2013
 * @version $Id$
 */
public class OpenStackFault extends ConnectedFault {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     */
    public OpenStackFault(Context context) {
        super(context);
    }

    /**
     * Create the OpenStack fault mapping object
     * 
     * @param context
     *            The context we are servicing
     * @param osFault
     *            the OpenStack fault object we are mapping
     */
    public OpenStackFault(Context context, com.woorea.openstack.nova.model.Server.Fault osFault) {
        super(context);

        setCode(osFault.getClass().toString());
        setMessage(osFault.getMessage());
        setDetails(osFault.getDetails());
    }
}
