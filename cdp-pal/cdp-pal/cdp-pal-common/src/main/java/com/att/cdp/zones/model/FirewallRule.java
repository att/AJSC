/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * @since Oct 29, 2015
 * @version $Id$
 */

public class FirewallRule extends ModelObject {

    enum Protocol {
        ICMP, TCP, UDP,
    }

    private static final long serialVersionUID = 1L;

    /**
     * False blocks traffic matching the rule. True allows traffic to pass.
     */
    private boolean allowTraffic;

    /**
     * Description of the firewall rule
     */
    private String description;

    /**
     * Destination IP address or CIDR
     */
    private String dstAddr;

    /**
     * The ending port of the destination port range
     */
    private int dstPortEnd;

    /**
     * The starting port of the destination port range.
     */
    private int dstPortStart;

    /**
     * Is the rule enabled. When set to false the rule will be disabled in the firewall
     */
    private boolean enabled;

    /**
     * Unique identifier for the firewall rule
     */
    private String id;

    /**
     * True if the IP version of the rule is IPv4. False if is IPv6
     */
    private boolean ipVersionIs4;

    /**
     * Name for the firewall rule
     */
    private String name;

    /**
     * IP protocol
     */
    private Protocol protocol;

    /**
     * Is the firewall rule visible to users other than the tenant owner
     */
    private boolean shared;
    /**
     * Source IP address or CIDR
     */
    private String srcAddr;

    /**
     * The ending port of the source port range
     */
    private int srcPortEnd;

    /**
     * The starting port of the source port range.
     */
    private int srcPortStart;

    /**
     * Default constructor
     */
    public FirewallRule() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected FirewallRule(Context context) {
        super(context);
    }

    /**
     * Check if the destination port is a single port or a range of ports.
     * 
     * @return False if destination port is a single port True if destination port is a range of ports
     */
    public boolean dstPortIsRange() {
        return dstPortStart != dstPortEnd;
    }

    /**
     * @return The description of the firewall rule
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The destination IP address for the firewall rule to match
     */
    public String getDstAddr() {
        return dstAddr;
    }

    /**
     * @return The destination port or the upper bound of the destination port range.
     */
    public int getDstPortEnd() {
        return dstPortEnd;
    }

    /**
     * @return The destination port or the lower bound of the destination port range.
     */
    public int getDstPortStart() {
        return dstPortStart;
    }

    /**
     * @return The UUID for this firewall rule
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return The name of the firewall rule
     */
    public String getName() {
        return name;
    }

    /**
     * @return The protocol for the firewall rule to match
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return The source IP address for the firewall rule to match
     */
    public String getSrcAddr() {
        return srcAddr;
    }

    /**
     * @return The source port or the upper bound of the source port range.
     */
    public int getSrcPortEnd() {
        return srcPortEnd;
    }

    /**
     * @return The source port or the lower bound of the source port range.
     */
    public int getSrcPortStart() {
        return srcPortStart;
    }

    /**
     * @return True if traffic matching this rule should be allowed to pass. False if traffic matching this rule should
     *         be blocked.
     */
    public boolean isAllowTraffic() {
        return allowTraffic;
    }

    /**
     * @return True if the rule is currently enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return the value of ipVersionIs4
     */
    public boolean isIpVersionIs4() {
        return ipVersionIs4;
    }

    /**
     * @return True if the firewall rule is visible to users besides the tenant owner False if the rule is only visible
     *         to the tenant owner
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * @param allowTraffic
     *            True if traffic matching this rule should be allowed to pass. False if traffic matching this rule
     *            should be blocked.
     */
    public void setAllowTraffic(boolean allowTraffic) {
        this.allowTraffic = allowTraffic;
    }

    /**
     * @param description
     *            The description of the firewall rule
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param dstAddr
     *            The destination IP address for the firewall rule to match
     */
    public void setDstAddr(String dstAddr) {
        this.dstAddr = dstAddr;
    }

    /**
     * Sets a single destination port.
     * 
     * @param port
     *            The destination port
     */
    public void setDstPort(int port) {
        setSrcPort(port, port);
    }

    /**
     * Sets a range of ports for the destination port
     * 
     * @param startPort
     *            The starting port in the destination port range
     * @param endPort
     *            The ending port in the destination port range
     */
    public void setDstPort(int startPort, int endPort) {
        if (startPort <= endPort) {
            dstPortStart = startPort;
            dstPortEnd = endPort;
        } else { // Start and end ports are flipped
            dstPortStart = endPort;
            dstPortEnd = startPort;
        }
    }

    /**
     * @param dstPortEnd
     *            the value for dstPortEnd
     */
    public void setDstPortEnd(int dstPortEnd) {
        this.dstPortEnd = dstPortEnd;
    }

    /**
     * @param dstPortStart
     *            the value for dstPortStart
     */
    public void setDstPortStart(int dstPortStart) {
        this.dstPortStart = dstPortStart;
    }

    /**
     * @param enabled
     *            True if the rule should be activated.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param ipVersionIs4
     *            the value for ipVersionIs4
     */
    public void setIpVersionIs4(boolean ipVersionIs4) {
        this.ipVersionIs4 = ipVersionIs4;
    }

    /**
     * @param name
     *            The name of the firewall rule
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param protocol
     *            The protocol for the firewall rule to match
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @param isShared
     *            True if the firewall rule is visible to users besides the tenant owner False if the rule is only
     *            visible to the tenant owner
     */
    public void setShared(boolean isShared) {
        shared = isShared;
    }

    /**
     * @param srcAddr
     *            The source IP address for the firewall rule to match
     */
    public void setSrcAddr(String srcAddr) {
        this.srcAddr = srcAddr;
    }

    /**
     * Sets a single source port.
     * 
     * @param port
     *            The source port
     */
    public void setSrcPort(int port) {
        setSrcPort(port, port);
    }

    /**
     * Sets a range of ports for the source port
     * 
     * @param startPort
     *            The starting port in the source port range
     * @param endPort
     *            The ending port in the source port range
     */
    public void setSrcPort(int startPort, int endPort) {
        if (startPort <= endPort) {
            srcPortStart = startPort;
            srcPortEnd = endPort;
        } else { // Start and end ports are flipped
            srcPortStart = endPort;
            srcPortEnd = startPort;
        }
    }

    /**
     * @param srcPortEnd
     *            the value for srcPortEnd
     */
    public void setSrcPortEnd(int srcPortEnd) {
        this.srcPortEnd = srcPortEnd;
    }

    /**
     * @param srcPortStart
     *            the value for srcPortStart
     */
    public void setSrcPortStart(int srcPortStart) {
        this.srcPortStart = srcPortStart;
    }

    /**
     * Check if the source port is a single port or a range of ports.
     * 
     * @return False if srcPort is a single port True if srcPort is a range of ports
     */
    public boolean srcPortIsRange() {
        return srcPortStart != srcPortEnd;
    }

}
