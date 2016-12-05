/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

/**
 * This interface is implemented by the provider and allows the client to obtain information about the network
 * capabilities of the provider.
 * 
 * @since Sept 2, 2014
 * @version $Id$
 */

public interface NetworkCapabilities {

    // IP Addressing

    /**
     * @return true if the provider supports specification of Class-less Inter-Domain Routing (CIDR)
     */
    boolean hasCIDR();

    /**
     * @return true if the load balancer connect health monitor can be specified
     */
    boolean hasConnectMonitor();

    /**
     * @return true if The destination port can be specified in a firewall rule
     */
    boolean hasFirewallDestPort();

    // Subnet

    /**
     * @return true if the protocol can be specified in a firewall rule
     */
    boolean hasFirewallProtocol();

    /**
     * @return true if the source host can be specified in a firewall rule
     */
    boolean hasFirewallSourceHost();

    // Firewall

    /**
     * @return true if the source port can be specified in a firewall rule
     */
    boolean hasFirewallSourcePort();

    /**
     * @return true if the provider supports Firewall As a Service
     */
    boolean hasFWaaS();

    /**
     * @return true if the provider supports public access gateways (ipv4)
     */
    boolean hasGateway();

    /**
     * @return true if the load balancer http application health monitor can be specified
     */
    boolean hasHttpMonitor();

    /**
     * @return true if the load balancer https application health monitor can be specified
     */
    boolean hasHttpsMonitor();

    // Load Balancer

    /**
     * @return true if ipv4 addressing is supported
     */
    boolean hasIPv4();

    /**
     * @return true if ipv6 addressing is supported
     */
    boolean hasIPv6();

    /**
     * @return true if the provider supports load balancer as a service
     */
    boolean hasLBaaS();

    /**
     * @return true if the load balancer ping health monitor can be specified
     */
    boolean hasPingMonitor();

    /**
     * @return true if the provider supports private subnets
     */
    boolean hasPrivateSubnet();
}
