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
public class ValuesConstraint extends Constraint implements Cloneable {

    @JsonProperty("allowed_values")
    private List<Scalar> values;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @return the value of values
     */
    public List<Scalar> getValues() {
        return values;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() + values.hashCode();
    }

    /**
     * @param values
     *            the value for values
     */
    public void setValues(List<Scalar> values) {
        this.values = values;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, values %s", super.toString(), values == null ? "empty" : values.toString());
    }

    /**
     * @see com.att.cdp.openstack.heat.model.Constraint#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
