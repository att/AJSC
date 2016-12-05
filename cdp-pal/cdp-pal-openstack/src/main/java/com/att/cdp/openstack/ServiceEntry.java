/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import java.util.ArrayList;
import java.util.List;

/**
 * @since Jan 20, 2015
 * @version $Id$
 */

public class ServiceEntry {

    /**
     * The name of the service
     */
    private String name;

    /**
     * The type of the service
     */
    private String type;

    /**
     * A list of all endpoints that are exposing this service
     */
    private List<ServiceEndpoint> endpoints = new ArrayList<>();

    /**
     * Construct a service entry in the catalog
     * 
     * @param name
     *            The name of the service
     * @param type
     *            The type of the service
     */
    public ServiceEntry(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @param serviceEndpoint
     *            An endpoint that this service is available on
     */
    public void addEndpoint(ServiceEndpoint serviceEndpoint) {
        endpoints.add(serviceEndpoint);
    }

    /**
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value of type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the value of endpoints
     */
    public List<ServiceEndpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        ServiceEntry other = (ServiceEntry) obj;
        return name.equals(other.getName()) && type.equals(other.getType());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("%s Service = %s, %d endpoints\n%s", type, name, endpoints.size(), endpoints.toString());
    }

}
