/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * The definition of a load balancer on a network
 * 
 * @since Sep 24, 2014
 * @version $Id$
 */
public class LoadBalancer extends ModelObject {

    enum LoadBalanceMethod {
        ROUND_ROBIN
    }

    enum Protocol {
        HTTP, HTTPS, TCP
    }

    enum Status {
        ACTIVE, BUILD, DOWN, ERROR, PENDING_CREATE, PENDING_DELETE, PENDING_UPDATE
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Administrative state of the load balancer
     */
    private boolean adminStateUp;

    /**
     * The id of the load balancer definition
     */
    private String id;

    /**
     * The load balance method
     */
    private LoadBalanceMethod lbMethod;

    /**
     * The name of the load balancer
     */
    private String name;

    /**
     * The protocol of the load balancer
     */
    private Protocol protocol;

    /**
     * The status of the load balancer
     */
    private Status status;

    /**
     * The subnet id
     */
    private String subnetId;

    /**
     * Default constructor
     */
    public LoadBalancer() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected LoadBalancer(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        LoadBalancer other = (LoadBalancer) obj;
        return id.equals(other.id);
    }

    /**
     * @return the id of the subnet
     */
    public String getId() {
        return id;
    }

    /**
     * @return load balancer method
     */
    public LoadBalanceMethod getLbMethod() {
        return lbMethod;
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
     * @return protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return subnetId
     */
    public String getSubnetId() {
        return subnetId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @return True if the load balancer is up
     */
    public boolean isAdminStateUp() {
        return adminStateUp;
    }

    /**
     * @param adminStateUp
     *            the value for adminStateUp
     */
    public void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param lbMethod
     *            The load balancer method to be added to this load balancer. Methods represent how the lb checks the
     *            application for failure.
     */
    public void setLbMethod(LoadBalanceMethod lbMethod) {
        this.lbMethod = lbMethod;
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
     * @param protocol
     *            The protocol to be load balanced
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param subnetId
     *            The subnet id to be load balanced
     */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("LoadBalancer id(%s), name(%s), protocol(%s)", id, name, protocol);
    }
}
