/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.Map;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * This class implements a generic resource which simply records all of the properties as simple string values. It is
 * used to capture resources that are unknown by the parser and for which no specialized mapping exists.
 * 
 * @since Jun 22, 2015
 * @version $Id$
 */

public class GenericResource extends Resource {
    private Map<String, Scalar> properties;

    /**
     * 
     */
    public GenericResource() {
        super();
    }

    /**
     * @return the value of properties
     */
    public Map<String, Scalar> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the value for properties
     */
    public void setProperties(Map<String, Scalar> properties) {
        this.properties = properties;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += properties == null ? 0 : properties.hashCode();
        return hash;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, %s", super.toString(), properties);
    }
}
