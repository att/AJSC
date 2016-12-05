/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * This class defines a rule that is part of an ACL
 * 
 * @since Oct 23, 2013
 * @version $Id$
 */
public class Rule extends ModelObject {

    /**
     * The protocol that is allowed for the port range
     */
    public enum PROTOCOL {

        /**
         * ANY protocol, TCP or UDP
         */
        ANY,

        /**
         * Transmission Control Protocol
         */
        TCP,

        /**
         * User Datagram Protocol
         */
        UDP;
    }
    
    /**
     * The action taken by the when this rule matches. Can be either ALLOW or DENY.
     */
    public enum ACTION {
    	 /**
         * Allow
         */
        ALLOW,

        /**
         * Deny
         */
        DENY,

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The starting port number being defined, inclusive
     */
    private Integer fromPort;

    /**
     * The ID of the rule
     */
    private String id;

    /**
     * The range of IP addresses that this rule will act on.
     */
    private String sourceIpRange;
    
   
    
    /**
     * The action that is allowed
     */
    private ACTION action;


    /**
     * It specifies the outgoing traffic for the specified destination IP address range
     */
    private String destinationIpRange;
    
   
    /**
     * True if the rule applies to incoming traffic. False if it applies to outgoing traffic.
     * NOTE: This is not yet implemented in openstack-api so we will just set it to the default.
     */
    private Boolean isIncoming = true;

    /**
     * The rule name
     */
    private String name;

    /**
     * The protocol that is allowed
     */
    private PROTOCOL protocol;

    /**
     * The ending port number being defined, inclusive
     */
    private Integer toPort;

    /**
     * Default constructor
     */
    public Rule() {}

    /**
     * @param protocol
     *            The protocol to be controlled by the rule
     * @param fromPort
     *            The starting port number in a range
     * @param toPort
     *            The ending port number in a range
     * @param sourceIpRange
     *            An source ip address range
     */
    public Rule(PROTOCOL protocol, Integer fromPort, Integer toPort, String sourceIpRange) {
        this.protocol = protocol;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.sourceIpRange = sourceIpRange;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Rule(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Rule other = (Rule) obj;
        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of fromPort
     * 
     * @return the fromPort value
     */
    public Integer getFromPort() {
        return fromPort;
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of ipRange
     * 
     * @return the ipRange value
     */
    public String getSourceIpRange() {
        return sourceIpRange;
    }

    /**
     * @return the value of isIncoming
     */
    public Boolean getIsIncoming() {
        return isIncoming;
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
     * JavaBean accessor to obtain the value of protocol
     * 
     * @return the protocol value
     */
    public PROTOCOL getProtocol() {
        return protocol;
    }

    /**
     * JavaBean accessor to obtain the value of toPort
     * 
     * @return the toPort value
     */
    public Integer getToPort() {
        return toPort;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * JavaBean accessor to obtain the value of isIncoming
     * 
     * @return the isIncoming value
     */
    public boolean isIncoming() {
        return isIncoming;
    }

    /**
     * Set the value of fromPort
     * 
     * @param port
     *            The new value of fromPort
     */
    public void setFromPort(Integer port) {
        this.fromPort = port;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the value of ipRange
     * 
     * @param ipRange
     *      The new value of ipRange
     */
    public void setIpRange(String sourceIpRange) {
        this.sourceIpRange = sourceIpRange;
    }

    /**
     * @param isIncoming
     *            the value for isIncoming
     */
    public void setIsIncoming(Boolean isIncoming) {
        this.isIncoming = isIncoming;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Set the value of protocol
     * 
     * @param protocol
     *      The new value of protocol
     */
    public void setProtocol(PROTOCOL protocol) {
        this.protocol = protocol;
    }
    
    /**
     * Set the value of toPort
     * 
     * @param port
     *      The new value of toPort
     */
    public void setToPort(Integer port) {
        this.toPort = port;
    }
    
    
    
    public ACTION getAction() {
		return action;
	}

	public void setAction(ACTION action) {
		this.action = action;
	}
	
	



	public String getDestinationIpRange() {
		return destinationIpRange;
	}

	public void setDestinationIpRange(String destinationIpRange) {
		this.destinationIpRange = destinationIpRange;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Rule: id(%s), name(%s), Port Range %s-%s, protocol %s", id, name == null ? "" : name,
            fromPort == null ? "" : fromPort.toString(), toPort == null ? "" : toPort.toString(), protocol == null ? ""
                : protocol);
    }

}
