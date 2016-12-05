/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomConstraint extends Constraint implements Cloneable {

    @JsonProperty("name")
    private Scalar name;

    /**
     * @see com.att.cdp.openstack.heat.model.Constraint#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the value of name
     */
    public Scalar getName() {
        return name;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(Scalar name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() + name.hashCode();
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
        return String.format("%s: %s", super.toString(), name == null ? "null" : name.toString());
    }

}
