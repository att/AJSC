/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import com.att.cdp.pal.util.ObjectHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Output extends ModelObject {

    @JsonProperty("description")
    private Scalar description;

    @JsonProperty("value")
    private Scalar value;

    /**
     * @return the value of description
     */
    public Scalar getDescription() {
        return description;
    }

    /**
     * @param description
     *            the value for description
     */
    public void setDescription(Scalar description) {
        this.description = description;
    }

    /**
     * @return the value of value
     */
    public Scalar getValue() {
        return value;
    }

    /**
     * @param value
     *            the value for value
     */
    public void setValue(Scalar value) {
        this.value = value;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
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
        return String.format("%s", value);
    }
}
