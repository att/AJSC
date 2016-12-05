/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.zones.Context;

/**
 * The definition of a subnet on a network
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public class Subnet extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The subnet uses DHCP (Domain Host Configuration Protocol) to configure attached interfaces.
     */
    private boolean dhcp;

    /**
     * A list of host routes for this subnet
     */
    private List<Route> hostRoutes = new ArrayList<>();

    /**
     * A list of DNS servers for this subnet
     */
    private List<String> dns = new ArrayList<>();

    /**
     * The gateway ip address if a gateway exists
     */
    private String gatewayIp;

    /**
     * The id of the subnet definition
     */
    private String id;

    /**
     * True if the subnet is an IPv4 subnet, false if it is IPv6.
     */
    private boolean ipv4;

    /**
     * The name of the subnet
     */
    private String name;

    /**
     * The id of the network definition
     */
    private String network;

    /**
     * For IPv4 subnets, the CIDR routing to be employed, unused for IPv6.
     */
    private String routing;
    
    /**
     * The availability zone of the subnet
     */
    private String availabilityZone;

    /**
     * Default constructor
     */
    public Subnet() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Subnet(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Subnet other = (Subnet) obj;
        return id.equals(other.id);
    }

    /**
     * @return the List of host routes
     */
    public List<Route> getHostRoutes() {
        return hostRoutes;
    }

    /**
     * @return the List of DNS servers
     */
    public List<String> getDns() {
        return dns;
    }

    /**
     * JavaBean accessor to obtain the value of gatewayIp
     * 
     * @return the gatewayIp value
     */
    public String getGatewayIp() {
        return gatewayIp;
    }

    /**
     * @return the id of the subnet
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of name
     * 
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * @return the network ID that this subnet belongs to
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @return the CIDR that defines the subnet routing
     */
    public String getRouting() {
        return routing;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @return True if the subnet uses DHCP
     */
    public boolean isDhcp() {
        return dhcp;
    }

    /**
     * @return True if the subnet has an associated gateway
     */
    public boolean isGateway() {
        return gatewayIp != null;
    }

    /**
     * @return True if the subnet uses IPv4 routing
     */
    public boolean isIpv4() {
        return ipv4;
    }

    /**
     * @param dhcp
     *            True if the subnet is to use DHCP
     */
    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    /**
     * @param hostRoutes
     *            the list of host routes to be used
     */
    public void setHostRoutes(List<Route> hostRoutes) {
        this.hostRoutes = hostRoutes;
    }

    /**
     * @param dns
     *            the list of DNS servers to be used
     */
    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    /**
     * Standard JavaBean mutator method to set the value of gatewayIp
     * 
     * @param gatewayIp
     *            the value to be set into gatewayIp
     */
    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param ipv4
     *            True if the subnet uses IPv4 routing
     */
    public void setIpv4(boolean ipv4) {
        this.ipv4 = ipv4;
    }

    /**
     * Standard JavaBean mutator method to set the value of name
     * 
     * @param name
     *            the value to be set into name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param network
     *            the id of the network that this subnet is to be associated with
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * @param routing
     *            The CIDR that defines the subnet routing
     */
    public void setRouting(String routing) {
        this.routing = routing;
    }

    
    public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Subnet id(%s), network(%s), IPv4(%s), CIDR(%s), gateway(%s)", id, network, ipv4, (ipv4
            ? routing : "N/A"), gatewayIp);
    }
}
