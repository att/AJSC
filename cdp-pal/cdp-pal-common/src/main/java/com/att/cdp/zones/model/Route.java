/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * The definition of a route on a subnet
 * 
 * @since May 07, 2015
 * @version $Id$
 */
public class Route extends ModelObject {
    private static final long serialVersionUID = 1L;

    /**
     * The CIDR of the destination network
     */
    private String destination;

    /**
     * The IP address of the next hop
     */
    private String nexthop;

    /**
     * Default constructor
     */
    public Route() {

    }

    /**
     * Basic constructor
     * 
     * @param destination
     *            The CIDR of the destination network
     * @param nexthop
     *            The IP address of the next hop
     */
    public Route(String destination, String nexthop) {
        this.destination = destination;
        this.nexthop = nexthop;

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Route(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Route other = (Route) obj;
        return destination.equals(other.destination) && nexthop.equals(other.nexthop);
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @return nexthop
     */
    public String getNexthop() {
        return nexthop;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @param destination
     *            The CIDR of the destination network
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @param nexthop
     *            The IP address of the next hop
     */
    public void setNexthop(String nexthop) {
        this.nexthop = nexthop;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Route [destination=" + destination + ", nexthop=" + nexthop + "]";
    }
}
