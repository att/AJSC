/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

/**
 * This interface is implemented by the provider and allows the client to obtain information about the network
 * capabilities of the provider.
 * 
 * @since Aug 19, 2014
 * @version $Id$
 */

public interface NetworkMetadata {

    /**
     * @return a description of the firewall support if available
     */
    String getFirewallDescription();

    /**
     * @return a description of the Ipv6 support if available
     */
    String getIpv6Description();

    /**
     * @return a description of the load balancer support if available
     */
    String getLoadBalancerDescription();

    /**
     * @return a description of the quota support if available
     */
    String getQuotaDescription();

    /**
     * @return a description of the router support if available
     */
    String getRouterDescription();

    /**
     * @return a description of the security group support if available
     */
    String getSecurityGroupDescription();

    /**
     * @return a description of the vpn support if available
     */
    String getVpnDescription();

    /**
     * @return True if the provider supports Firewalls
     */
    boolean isFirewallSupported();

    /**
     * @return True if the provider supports IPv6
     */
    boolean isIpv6Supported();

    /**
     * @return True if the provider supports Load Balancers
     */
    boolean isLoadBalancerSupported();

    /**
     * @return True if the provider supports Quotas
     */
    boolean isQuotaSupported();

    /**
     * @return True if the provider supports Routers
     */
    boolean isRouterSupported();

    /**
     * @return True if the provider supports Security Groups
     */
    boolean isSecurityGroupSupported();

    /**
     * @return True if the provider supports VPNs
     */
    boolean isVpnSupported();
}
