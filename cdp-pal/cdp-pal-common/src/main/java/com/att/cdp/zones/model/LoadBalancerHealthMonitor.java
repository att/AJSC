/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.List;

import com.att.cdp.zones.Context;

/**
 * The definition of a load balancer health monitor
 * 
 * @since May 04, 2015
 * @version $Id$
 */
public class LoadBalancerHealthMonitor extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The unique ID for the listener
     */
    private String id;

    /**
     * The type of probe sent by the load balancer to verify the member state
     */
    private ProtocolType type;

    /**
     * The time, in seconds, between sending probes to members
     */
    private int delay;

    /**
     * The maximum number of seconds for a monitor to wait for a connection to be established before it times out
     */
    private int timeout;

    /**
     * Number of allowed connection failures before changing the status of the member to INACTIVE
     */
    private int maxRetries;

    /**
     * The HTTP method that the monitor uses for requests
     */
    private HttpMethodType httpMethod;

    /**
     * The HTTP path of the request sent by the monitor to test the health of a member. Must be a string beginning with
     * a forward slash (/).
     */
    private String urlPath;

    /**
     * Expected HTTP codes for a passing HTTP(S) monitor.
     */
    private String expectedCodes;

    /**
     * The HTTP method that the monitor uses for requests
     */
    private boolean adminStateUp;

    /**
     * The HTTP method that the monitor uses for requests
     */
    private LoadBalancerStatusType status;

    /**
     * The list of pool associated with health monitor
     */
    private List<String> poolIds;

    /**
     * Default constructor
     */
    public LoadBalancerHealthMonitor() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected LoadBalancerHealthMonitor(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        LoadBalancerHealthMonitor other = (LoadBalancerHealthMonitor) obj;
        return id.equals(other.id);
    }

    /**
     * @return the id of the subnet
     */
    public String getId() {
        return id;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("LoadBalancerHealthMonitor id(%s)", id);
    }

    /**
     * @return The protocol
     */
    public ProtocolType getType() {
        return type;
    }

    /**
     * @param type
     *            The protocol
     */
    public void setType(ProtocolType type) {
        this.type = type;
    }

    /**
     * @return The delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @param delay
     *            The delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * @return the time out
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            The time out
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Maximum retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * @param maxRetries
     *            maximum retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * @return The http method to be used
     */
    public HttpMethodType getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod
     *            The http method to be used
     */
    public void setHttpMethod(HttpMethodType httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * @return The URL to be used
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     *            The url to be used
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return List of expected http status codes
     */
    public String getExpectedCodes() {
        return expectedCodes;
    }

    /**
     * @param expectedCodes
     *            List of expected http status codes
     */
    public void setExpectedCodes(String expectedCodes) {
        this.expectedCodes = expectedCodes;
    }

    /**
     * @return True if it is up and ready
     */
    public boolean isAdminStateUp() {
        return adminStateUp;
    }

    /**
     * @param adminStateUp
     *            Set the state
     */
    public void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    /**
     * @return The current status
     */
    public LoadBalancerStatusType getStatus() {
        return status;
    }

    /**
     * @param status
     *            The current status
     */
    public void setStatus(LoadBalancerStatusType status) {
        this.status = status;
    }

    /**
     * @return The list of pools
     */
    public List<String> getPoolIds() {
        return poolIds;
    }

    /**
     * @param poolIds
     *            The list of pools
     */
    public void setPoolIds(List<String> poolIds) {
        this.poolIds = poolIds;
    }

}
