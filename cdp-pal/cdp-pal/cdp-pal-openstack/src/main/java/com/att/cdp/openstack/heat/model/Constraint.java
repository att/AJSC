/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import com.att.cdp.openstack.heat.ConstraintDeserializer;
import com.att.cdp.openstack.heat.ConstraintResolver;
import com.att.cdp.pal.util.ObjectHelper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
// @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY)
// @JsonTypeIdResolver(ConstraintResolver.class)
public abstract class Constraint extends ModelObject implements Cloneable {

    @JsonProperty("description")
    private Scalar description;

    /**
     * Default no-arg constructor
     */
    public Constraint() {

    }

    /**
     * Create the base class with an initial value for description
     * 
     * @param description
     *            The description of the constraint
     */
    public Constraint(Scalar description) {
        this.description = description;
    }

    /**
     * JavaBean accessor to obtain the value of description
     * 
     * @return the description value
     */
    public Scalar getDescription() {
        return description;
    }

    /**
     * Standard JavaBean mutator method to set the value of description
     * 
     * @param description
     *            the value to be set into description
     */
    public void setDescription(Scalar description) {
        this.description = description;
    }

    /**
     * @see com.att.cdp.openstack.heat.model.ModelObject#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Constraint clone = (Constraint) super.clone();
        return clone;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return description == null ? 0 : description.hashCode();
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
        return String.format("%s: [%s]", getClass().getSimpleName(),
            description == null ? "null" : description.toString());
    }

}
