/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.spi.model.ConnectedPort;
import com.woorea.openstack.nova.model.FixedIp;
import com.woorea.openstack.nova.model.InterfaceAttachment;

/**
 * @since Mar 4, 2015
 * @version $Id$
 */
@SuppressWarnings("nls")
public class OpenStackPort extends ConnectedPort {

    /**
     * Mappings of Openstack's port state to the abstraction state
     */
    private static final String[][] STATUS_MAPPING = {
        {
            "UP", Port.Status.ONLINE.toString()
        }, {
            "ACTIVE", Port.Status.ONLINE.toString()
        }, {
            "DOWN", Port.Status.OFFLINE.toString()
        }, {
            null, Port.Status.UNKNOWN.toString()
        }
    };

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a Port from a Quantum port object
     * 
     * @param context
     *            The context we are servicing
     * @param port
     *            The port
     */
    public OpenStackPort(Context context, com.woorea.openstack.quantum.model.Port port) {
        super(context);
        setMacAddr(port.getMacAddress());
        setNetwork(port.getNetworkId());
        setId(port.getId());
        setPortState(mapState(port.getStatus()));
        List<String> addresses = new ArrayList<String>();
        for (com.woorea.openstack.quantum.model.Port.Ip ip : port.getList()) {
            addresses.add(ip.getAddress());
            setSubnetId(ip.getSubnetId());
        }
        setAddresses(addresses);
    }

    /**
     * Create a Port object from a Nova InterfaceAttachment object from OpenStack.
     * 
     * @param context
     *            The context that represents the connection to the provider
     * @param attachment
     *            The interface attachment object that defines the attachment to the server
     */
    public OpenStackPort(Context context, InterfaceAttachment attachment) {
        super(context);
        setMacAddr(attachment.getMacAddress());
        setNetwork(attachment.getNetworkId());
        setId(attachment.getPortId());
        setPortState(mapState(attachment.getPortState()));
        List<String> addresses = new ArrayList<String>();
        for (FixedIp ip : attachment.getFixedIps()) {
            addresses.add(ip.getIpAddress());
            setSubnetId(ip.getSubnetId());
        }
        setAddresses(addresses);
    }

    /**
     * This method performs the mapping of the Openstack port state to the abstraction states
     * 
     * @param state
     *            The OpenStack port state
     * @return The abstraction state
     */
    public static Port.Status mapState(String state) {
        Port.Status value = Port.Status.UNKNOWN;

        if (state != null) {
            for (int i = 0; i < 4; i++) {
                if (state.equalsIgnoreCase(STATUS_MAPPING[i][0])) {
                    value = Port.Status.valueOf(STATUS_MAPPING[i][1]);
                    break;
                }
            }
        }

        return value;
    }
}
