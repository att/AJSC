/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import com.att.cdp.zones.NetworkMetadata;

public class OpenStackNetworkMetadata implements NetworkMetadata {

    @SuppressWarnings("nls")
    private static final String NOT_SUPPORTED = "Not supported";

    private String firewallDescription = NOT_SUPPORTED;
    private boolean firewallSupported;
    private String ipv6Description = NOT_SUPPORTED;
    private boolean ipv6Supported;
    private String loadBalancerDescription = NOT_SUPPORTED;
    private boolean loadBalancerSupported;
    private String quotaDescription = NOT_SUPPORTED;
    private boolean quotaSupported;
    private String routerDescription = NOT_SUPPORTED;
    private boolean routerSupported;
    private String securityGroupDescription = NOT_SUPPORTED;
    private boolean securityGroupSupported;
    private String vpnDescription = NOT_SUPPORTED;
    private boolean vpnSupported;

    @Override
    public String getFirewallDescription() {
        return firewallDescription;
    }

    @Override
    public String getIpv6Description() {
        return ipv6Description;
    }

    @Override
    public String getLoadBalancerDescription() {
        return loadBalancerDescription;
    }

    @Override
    public String getQuotaDescription() {
        return quotaDescription;
    }

    @Override
    public String getRouterDescription() {
        return routerDescription;
    }

    @Override
    public String getSecurityGroupDescription() {
        return securityGroupDescription;
    }

    @Override
    public String getVpnDescription() {
        return vpnDescription;
    }

    @Override
    public boolean isFirewallSupported() {
        return firewallSupported;
    }

    @Override
    public boolean isIpv6Supported() {
        return ipv6Supported;
    }

    @Override
    public boolean isLoadBalancerSupported() {
        return loadBalancerSupported;
    }

    @Override
    public boolean isQuotaSupported() {
        return quotaSupported;
    }

    @Override
    public boolean isRouterSupported() {
        return routerSupported;
    }

    @Override
    public boolean isSecurityGroupSupported() {
        return securityGroupSupported;
    }

    @Override
    public boolean isVpnSupported() {
        return vpnSupported;
    }

    /**
     * @param firewallDescription
     *            A description of the firewall support
     */
    public void setFirewallDescription(String firewallDescription) {
        this.firewallDescription = firewallDescription;
    }

    /**
     * @param firewallSupported
     *            True if firewalls are supported
     */
    public void setFirewallSupported(boolean firewallSupported) {
        this.firewallSupported = firewallSupported;
    }

    /**
     * @param ipv6Description
     *            The description of the IPv6 support
     */
    public void setIpv6Description(String ipv6Description) {
        this.ipv6Description = ipv6Description;
    }

    /**
     * @param ipv6Supported
     *            True if ipV6 is supported
     */
    public void setIpv6Supported(boolean ipv6Supported) {
        this.ipv6Supported = ipv6Supported;
    }

    /**
     * @param loadBalancerDescription
     *            A description of the load balancer support
     */
    public void setLoadBalancerDescription(String loadBalancerDescription) {
        this.loadBalancerDescription = loadBalancerDescription;
    }

    /**
     * @param loadBalancerSupported
     *            True if load balancers are supported
     */
    public void setLoadBalancerSupported(boolean loadBalancerSupported) {
        this.loadBalancerSupported = loadBalancerSupported;
    }

    /**
     * @param quotaDescription
     *            A description of the quota support
     */
    public void setQuotaDescription(String quotaDescription) {
        this.quotaDescription = quotaDescription;
    }

    /**
     * @param quotaSupported
     *            True if quota management is supported
     */
    public void setQuotaSupported(boolean quotaSupported) {
        this.quotaSupported = quotaSupported;
    }

    /**
     * @param routerDescription
     *            A description of router support provided
     */
    public void setRouterDescription(String routerDescription) {
        this.routerDescription = routerDescription;
    }

    /**
     * @param routerSupported
     *            True if routers are supported
     */
    public void setRouterSupported(boolean routerSupported) {
        this.routerSupported = routerSupported;
    }

    /**
     * @param securityGroupDescription
     *            A description of the support for security groups
     */
    public void setSecurityGroupDescription(String securityGroupDescription) {
        this.securityGroupDescription = securityGroupDescription;
    }

    /**
     * @param securityGroupSupported
     *            True if security groups are supported
     */
    public void setSecurityGroupSupported(boolean securityGroupSupported) {
        this.securityGroupSupported = securityGroupSupported;
    }

    /**
     * @param vpnDescription
     *            A description of the VPN support provided
     */
    public void setVpnDescription(String vpnDescription) {
        this.vpnDescription = vpnDescription;
    }

    /**
     * @param vpnSupported
     *            True if VPN is supported
     */
    public void setVpnSupported(boolean vpnSupported) {
        this.vpnSupported = vpnSupported;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Metadata: ");
    }

}
