/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RangeConstraint extends Constraint implements Cloneable {

    @JsonProperty("min")
    private Scalar min;

    @JsonProperty("max")
    private Scalar max;

    /**
     * Default no-arg constructor
     */
    public RangeConstraint() {
        super();
    }

    /**
     * Create the range constraint with an initial set of values
     * 
     * @param min
     *            The minimum value that can be specified, inclusive
     * @param max
     *            The maximum value that can be specified, inclusive
     * @param description
     *            The description of the constraint
     */
    public RangeConstraint(Scalar min, Scalar max, Scalar description) {
        super(description);
        setMin(min);
        setMax(max);
    }

    /**
     * @see com.att.cdp.openstack.heat.model.Constraint#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * JavaBean accessor to obtain the value of min
     * 
     * @return the min value
     */
    public Scalar getMin() {
        return min;
    }

    /**
     * Standard JavaBean mutator method to set the value of min
     * 
     * @param min
     *            the value to be set into min
     */
    public void setMin(Scalar min) {
        this.min = min;
    }

    /**
     * JavaBean accessor to obtain the value of max
     * 
     * @return the max value
     */
    public Scalar getMax() {
        return max;
    }

    /**
     * Standard JavaBean mutator method to set the value of max
     * 
     * @param max
     *            the value to be set into max
     */
    public void setMax(Scalar max) {
        this.max = max;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() + (min == null ? 0 : min.hashCode()) + (max == null ? 0 : max.hashCode());
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
        return String.format("%s, min[%s], max[%s]", super.toString(), min, max);
    }
}
