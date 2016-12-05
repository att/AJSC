/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * Describes the way user sessions are persisted by a load balancer.
 *
 * @since Oct 29, 2015
 * @version $Id$
 */
public class SessionPersistence {
    
    private String type;
    
    private String cookieName = null;

    /**
     * @return The type of persistence
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type
     *            The type of persistence
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The name of the cookie if cookies are used
     */
    public String getCookieName() {
        return cookieName;
    }

    /**
     * @param cookieName
     *            The name of the cookie
     */
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}