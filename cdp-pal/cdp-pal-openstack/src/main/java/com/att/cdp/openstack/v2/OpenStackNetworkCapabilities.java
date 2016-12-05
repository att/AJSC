/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkCapabilities;
import com.att.cdp.zones.spi.AbstractService;

/**
 * @since Mar 4, 2015
 * @version $Id$
 */
public class OpenStackNetworkCapabilities extends AbstractService implements NetworkCapabilities {

    private boolean cidr;
    private boolean fwaas;
    private boolean fwDstPort;

    private boolean fwProtocol;
    private boolean fwSrcHost;

    private boolean fwSrcPort;
    private boolean gateway;
    private boolean ipv4;
    private boolean ipv6;
    private boolean lbaas;

    private boolean lbConnect;
    private boolean lbHttp;
    private boolean lbHttps;
    private boolean lbPing;
    private boolean privateSubnet;

    /**
     * @param context
     *            The reference to the context we are servicing
     */
    public OpenStackNetworkCapabilities(Context context) {
        super(context);

        // Always true on Openstack
        this.ipv4 = true;
        this.cidr = true;
        this.privateSubnet = true;
        this.gateway = true;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasCIDR()
     */
    @Override
    public boolean hasCIDR() {
        return cidr;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasConnectMonitor()
     */
    @Override
    public boolean hasConnectMonitor() {
        lbConnect = this.hasLBaaS();
        return lbConnect;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasFirewallDestPort()
     */
    @Override
    public boolean hasFirewallDestPort() {
        fwDstPort = this.hasFWaaS();
        return fwDstPort;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasFirewallProtocol()
     */
    @Override
    public boolean hasFirewallProtocol() {
        fwProtocol = this.hasFWaaS();
        return fwProtocol;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasFirewallSourceHost()
     */
    @Override
    public boolean hasFirewallSourceHost() {
        fwSrcHost = this.hasFWaaS();
        return fwSrcHost;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasFirewallSourcePort()
     */
    @Override
    public boolean hasFirewallSourcePort() {
        fwSrcPort = this.hasFWaaS();
        return fwSrcPort;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasFWaaS()
     */
    @Override
    public boolean hasFWaaS() {
        Context context = getContext();
        try {
            fwaas = context.getNetworkService().getMetadata().isFirewallSupported();
        } catch (ZoneException e) {
            fwaas = false;
        }
        return fwaas;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasGateway()
     */
    @Override
    public boolean hasGateway() {
        return gateway;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasHttpMonitor()
     */
    @Override
    public boolean hasHttpMonitor() {
        lbHttp = this.hasLBaaS();
        return lbHttp;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasHttpsMonitor()
     */
    @Override
    public boolean hasHttpsMonitor() {
        lbHttps = this.hasLBaaS();
        return lbHttps;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasIPv4()
     */
    @Override
    public boolean hasIPv4() {
        return ipv4;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasIPv6()
     */
    @Override
    public boolean hasIPv6() {
        Context context = getContext();
        try {
            ipv6 = context.getNetworkService().getMetadata().isIpv6Supported();
        } catch (ZoneException e) {
            ipv6 = false;
        }
        return ipv6;

    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasLBaaS()
     */
    @Override
    public boolean hasLBaaS() {
        Context context = getContext();
        try {
            lbaas = context.getNetworkService().getMetadata().isLoadBalancerSupported();
        } catch (ZoneException e) {
            lbaas = false;
        }
        return lbaas;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasPingMonitor()
     */
    @Override
    public boolean hasPingMonitor() {
        lbPing = this.hasLBaaS();
        return lbPing;
    }

    /**
     * @see com.att.cdp.zones.NetworkCapabilities#hasPrivateSubnet()
     */
    @Override
    public boolean hasPrivateSubnet() {
        return privateSubnet;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return "";
    }
}
