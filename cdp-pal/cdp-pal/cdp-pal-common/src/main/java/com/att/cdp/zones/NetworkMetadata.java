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
     * Firewalls are facilities that can be used to configure access to virtual machines by opening or blocking access
     * to specific ports and/or protocols. This metadata setting indicates that the firewall support is built-in to the
     * provider and that native capabilities exist to configure these firewalls, usually using FWaaS API interfaces.
     * 
     * @return True if the provider supports native, built-in Firewalls
     */
    boolean isFirewallSupported();

    /**
     * This metadata element indicates that the provider supports IPv6 protocol. IPv4 is implied.
     * 
     * @return True if the provider supports IPv6
     */
    boolean isIpv6Supported();

    /**
     * This metadata setting indicates that the firewall support is built-in to the provider and that native
     * capabilities exist to configure these firewalls, usually using FWaaS API interfaces. It may be provided by using
     * clustering capabilities for disaster recovery or high availability.
     * 
     * @return True if the provider supports Load Balancers
     */
    boolean isLoadBalancerSupported();

    /**
     * This indicates that the provider has and supports the concept of quotas, or resource limits that are placed on a
     * per-account or per-project basis. These limits set the upper end of what resource utilization will be allowed.
     * 
     * @return True if the provider supports Quotas
     */
    boolean isQuotaSupported();

    /**
     * Indicates that the provider supports native capabilities to configure or manage network router resources.
     * 
     * @return True if the provider supports Routers
     */
    boolean isRouterSupported();

    /**
     * Indicates that the provider supports the ability to define security groups and associate them with the virtual
     * machine. These security groups are similar to ACL's (access control lists) and constitute VM access control,
     * rather than provider access control.
     *
     * @return True if the provider supports Security Groups
     */
    boolean isSecurityGroupSupported();

    /**
     * @return True if the provider supports VPNs
     */
    boolean isVpnSupported();
}
