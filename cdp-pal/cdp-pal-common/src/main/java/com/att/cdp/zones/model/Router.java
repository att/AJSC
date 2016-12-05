/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * The definition of a router on a network
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public class Router extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * External Network id this router is attached to
     */
    private String externalNetworkId;

    /**
     * The id of the router definition
     */
    private String id;

    /**
     * The name of the router
     */
    private String name;

    /**
     * The status of the network
     */
    private String status;

    /**
     * Default constructor
     */
    public Router() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Router(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Router other = (Router) obj;
        return id.equals(other.id);
    }

    /**
     * @return externalNetworkId
     */
    public String getExternalNetworkId() {
        return externalNetworkId;
    }

    /**
     * @return the id of the router
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
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public String getStatus() {
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
     * @param externalNetworkId
     *            the value for externalNetworkId
     */
    public void setExternalNetworkId(String externalNetworkId) {
        this.externalNetworkId = externalNetworkId;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Routing id(%s), name(%s), status(%s), externalNetworkId(%s)", id, name, status,
            externalNetworkId);
    }
}
