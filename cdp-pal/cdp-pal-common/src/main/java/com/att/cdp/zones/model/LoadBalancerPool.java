/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.List;

import com.att.cdp.zones.Context;

/**
 * The definition of a load balancer pool
 * 
 * @since May 04, 2015
 * @version $Id$
 */
public class LoadBalancerPool extends ModelObject {

    /**
     * How the load balancer distributes load
     */

    public enum AlgorithmType {
        /**
         * Load is distributed evenly across all members
         */
        ROUND_ROBIN,

        /**
         * Load is sent to system with least connection loading
         */
        LEAST_CONNECTIONS,

        /**
         * Load is sent to the same system used the first time for the source ip address, otherwise it is round-robin if
         * never used before.
         */
        SOURCE_IP
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The status of the load balancer pool
     */
    private LoadBalancerStatusType status;

    /**
     * The load-balancer algorithm, which is round-robin, least-connections, and so on
     */
    private AlgorithmType lbAlgorithm;

    /**
     * The protocol of the pool, which is TCP, HTTP, or HTTPS.
     */
    private ProtocolType protocol;

    /**
     * The description of the pool
     */
    private String description;

    /**
     * The list containing health monitor id.
     */
    private List<String> healthMonitorIds;

    /**
     * The session persistence algorithm that should be used (if any)
     */
    private SessionPersistence sessionPersistence;

    /**
     * Administrative state of the load balancer member
     */
    private boolean adminStateUp;

    /**
     * The name of the pool
     */
    private String name;

    /**
     * The list of member ids that belong to the pool.
     */
    private List<String> memberIds;

    /**
     * The tenantId who owns the pool
     */
    private String id;

    /**
     * The subnet id on which to allocate the pool
     */
    private String subnetId;

    /**
     * The listner id allocated to the pool
     */
    private String listenerId;

    /**
     * Default constructor
     */
    public LoadBalancerPool() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected LoadBalancerPool(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        LoadBalancerPool other = (LoadBalancerPool) obj;
        return id.equals(other.id);
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
     * @return status
     */
    public LoadBalancerStatusType getStatus() {
        return status;
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
     * Standard JavaBean mutator method to set the value of name
     * 
     * @param name
     *            the value to be set into name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(LoadBalancerStatusType status) {
        this.status = status;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("LoadBalancerPool id(%s), name(%s)", id, name);
    }

    /**
     * @return the load balancer algorithm used
     */
    public AlgorithmType getLbAlgorithm() {
        return lbAlgorithm;
    }

    /**
     * @param lbAlgorithm
     *            The load balancer algorithm to use
     */
    public void setLbAlgorithm(AlgorithmType lbAlgorithm) {
        this.lbAlgorithm = lbAlgorithm;
    }

    /**
     * @return The protocol we are balancing
     */
    public ProtocolType getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            The protocol we are balancing
     */
    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The list of monitors
     */
    public List<String> getHealthMonitorIds() {
        return healthMonitorIds;
    }

    /**
     * @param healthMonitorIds
     *            The list of monitors
     */
    public void setHealthMonitorIds(List<String> healthMonitorIds) {
        this.healthMonitorIds = healthMonitorIds;
    }

    /**
     * @return The list of members
     */
    public List<String> getMemberIds() {
        return memberIds;
    }

    /**
     * @param memberIds
     *            The list of members
     */
    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    /**
     * @return How we want to persist user sessions
     */
    public SessionPersistence getSessionPersistence() {
        return sessionPersistence;
    }

    /**
     * @param sessionPersistence
     *            How we want to persist user sessions
     */
    public void setSessionPersistence(SessionPersistence sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }

    /**
     * @return The subnet id
     */
    public String getSubnetId() {
        return subnetId;
    }

    /**
     * @param subnetId
     *            The subnet id
     */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    /**
     * @return The id of the load balancer listener
     */
    public String getListenerId() {
        return listenerId;
    }

    /**
     * @param listenerId
     *            The id of the load balancer listener
     */
    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }

}
