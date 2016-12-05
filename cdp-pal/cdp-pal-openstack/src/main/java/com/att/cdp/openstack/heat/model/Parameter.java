/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Parameter extends ModelObject implements Cloneable {
    @JsonProperty("constraints")
    private List<Constraint> constraints;

    @JsonProperty("default")
    private Scalar defaultValue;

    @JsonProperty("description")
    private Scalar description;

    @JsonProperty("hidden")
    private Scalar hidden;

    @JsonProperty("label")
    private Scalar label;

    @JsonProperty("type")
    private Scalar type;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * JavaBean accessor to obtain the value of constraints
     * 
     * @return the constraints value
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (defaultValue == null ? 0 : defaultValue.hashCode())
            + (description == null ? 0 : description.hashCode()) + (hidden == null ? 0 : hidden.hashCode())
            + (label == null ? 0 : label.hashCode()) + (type == null ? 0 : type.hashCode())
            + (constraints == null ? 0 : constraints.hashCode());
    }

    /**
     * JavaBean accessor to obtain the value of defaultValue
     * 
     * @return the defaultValue value
     */
    public Scalar getDefaultValue() {
        return defaultValue;
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
     * JavaBean accessor to obtain the value of hidden
     * 
     * @return the hidden value
     */
    public Scalar getHidden() {
        return hidden;
    }

    /**
     * JavaBean accessor to obtain the value of label
     * 
     * @return the label value
     */
    public Scalar getLabel() {
        return label;
    }

    /**
     * JavaBean accessor to obtain the value of type
     * 
     * @return the type value
     */
    public Scalar getType() {
        return type;
    }

    /**
     * Standard JavaBean mutator method to set the value of constraints
     * 
     * @param constraints
     *            the value to be set into constraints
     */
    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * Standard JavaBean mutator method to set the value of defaultValue
     * 
     * @param defaultValue
     *            the value to be set into defaultValue
     */
    public void setDefaultValue(Scalar defaultValue) {
        this.defaultValue = defaultValue;
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
     * Standard JavaBean mutator method to set the value of hidden
     * 
     * @param hidden
     *            the value to be set into hidden
     */
    public void setHidden(Scalar hidden) {
        this.hidden = hidden;
    }

    /**
     * Standard JavaBean mutator method to set the value of label
     * 
     * @param label
     *            the value to be set into label
     */
    public void setLabel(Scalar label) {
        this.label = label;
    }

    /**
     * Standard JavaBean mutator method to set the value of type
     * 
     * @param type
     *            the value to be set into type
     */
    public void setType(Scalar type) {
        this.type = type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Parameter: type [%s], default[%s], label[%s], desc[%s], hidden[%s], constraints[%s]",
            type, defaultValue, label, description, hidden, constraints == null ? 0 : constraints.size());
    }

    /**
     * @see com.att.cdp.openstack.heat.model.ModelObject#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Parameter clone = (Parameter) super.clone();
        if (constraints != null) {
            clone.constraints = new ArrayList<Constraint>();
            for (Constraint constraint : constraints) {
                clone.constraints.add((Constraint) constraint.clone());
            }
        }
        return clone;
    }
}
