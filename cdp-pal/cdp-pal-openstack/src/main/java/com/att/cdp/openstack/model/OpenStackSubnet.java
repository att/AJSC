/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Route;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedSubnet;

/**
 * @since Jul 21, 2014
 * @version $Id$
 */

public class OpenStackSubnet extends ConnectedSubnet {

    /**
     * The version number that represents an IPv6 subnet
     */
    public static final int IPV6 = 6;

    /**
     * The version number that represents an IPv4 subnet
     */
    public static final int IPV4 = 4;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param subnet
     *            The subnet we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackSubnet(Context context, com.woorea.openstack.quantum.model.Subnet subnet) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        dictionary.put("cidr", "routing");
        dictionary.put("gw", "gatewayIp");
        dictionary.put("networkId", "network");
        dictionary.put("dnsNames", "dns");
        ObjectMapper.map(subnet, this, dictionary);

        switch (subnet.getIpversion().code()) {
            case IPV6:
                setIpv4(false);
                break;
            case IPV4:
            default:
                setIpv4(true);
        }
        setDhcp(subnet.getEnableDHCP().booleanValue());

        if (!subnet.getHostRoutes().isEmpty()) {
            List<com.woorea.openstack.quantum.model.Route> routes = subnet.getHostRoutes();

            List<Route> newRoutes = new ArrayList<>();

            // List<OpenStackRoute> routes = subnet.getHostRoute();
            for (com.woorea.openstack.quantum.model.Route route : routes) {
                Route newRoute = new Route();
                newRoute.setDestination(route.getDestination());
                newRoute.setNexthop(route.getNexthop());
                newRoutes.add(newRoute);
            }

            setHostRoutes(newRoutes);

        }
    }
}
