/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;
import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedNetwork;

/**
 * This class implements an OpenStack specific implementation of the Network abstract object.
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class OpenStackNetwork extends ConnectedNetwork {
    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The open stack context we are servicing
     * @param net
     *            The open stack server object we are representing
     */
    @SuppressWarnings("nls")
    public OpenStackNetwork(Context context, com.woorea.openstack.quantum.model.Network net) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        dictionary.put("status", "status");
        dictionary.put("subnets", "subnets");
        dictionary.put("providerNetworkType", "type");
        dictionary.put("providerPhysicalNetwork", "physicalNet");
        dictionary.put("providerSegmentationId", "vlanId");
        dictionary.put("vlanTransparent", "vlanTransparent");
        ObjectMapper.map(net, this, dictionary);

        List<com.woorea.openstack.quantum.model.Segment> segments = net.getSegments();

        if (segments != null && !segments.isEmpty()) {
            HashMap<String, String> mappings = new HashMap<>();
            mappings.put("providerNetworkType", "type");
            mappings.put("providerPhysicalNetwork", "physicalNet");
            dictionary.put("providerSegmentationId", "vlanId");

            for (com.woorea.openstack.quantum.model.Segment segment : segments) {
                Network.VLanSegment vlanSegment = new Network.VLanSegment();
                ObjectMapper.map(segment, vlanSegment, mappings);
                getSegments().add(vlanSegment);
            }
        }

        setShared(false);
        if (net.getShared() != null) {
            if (net.getShared().equalsIgnoreCase("yes")) {
                setShared(true);
            }
        }

        setExternalNetwork(false);
        if (net.getRouterExternal() != null) {
            if (net.getRouterExternal().equalsIgnoreCase("yes")) {
                setExternalNetwork(true);
            }
        }
    }
    
    /**
     * @param context
     *            The context we are servicing
     * @param net
     *            The Nova network object (old version, backwards support)
     */
    @SuppressWarnings("nls")
    public OpenStackNetwork(Context context, com.woorea.openstack.nova.model.Network net) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("allowedEnd", "allowedEnd");
        dictionary.put("allowedStart", "allowedStart");
        dictionary.put("bridge", "bridge");
        dictionary.put("bridgeInterface", "bridgeInterface");
        dictionary.put("broadcast", "broadcast");
        dictionary.put("cidr", "cidr");
        dictionary.put("cidrV6", "cidrV6");
        dictionary.put("createdAt", "createdAt");
        dictionary.put("deleted", "deleted");
        dictionary.put("deletedAt", "deletedAt");
        dictionary.put("dhcpServer", "dhcpServer");
        dictionary.put("dhcpStart", "dhcpStart");
        dictionary.put("dns1", "dns1");
        dictionary.put("dns2", "dns2");
        dictionary.put("enableDhcp", "enableDhcp");
        dictionary.put("gateway", "gateway");
        dictionary.put("gatewayV6", "gatewayV6");
        dictionary.put("host", "host");
        dictionary.put("id", "id");
        dictionary.put("injected", "injected");
        dictionary.put("label", "label");
        dictionary.put("mtu", "mtu");
        dictionary.put("multihost", "multihost");
        dictionary.put("netmask", "netmask");
        dictionary.put("netmaskV6", "netmaskV6");
        dictionary.put("priority", "priority");
        dictionary.put("projectId", "projectId");
        dictionary.put("rxtxBase", "rxtxBase");
        dictionary.put("shareAddress", "shareAddress");
        dictionary.put("updatedAt", "updatedAt");
        dictionary.put("vlan", "vlan");
        dictionary.put("vpnPrivateAddress", "vpnPrivateAddress");
        dictionary.put("vpnPublicAddress", "vpnPublicAddress");
        dictionary.put("vpnPublicPort", "vpnPublicPort");
        ObjectMapper.map(net, this, dictionary);


    }

}
