/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.List;

import com.att.cdp.pal.util.ObjectHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParameterGroup extends ModelObject {
    @JsonProperty("description")
    private Scalar description;

    @JsonProperty("label")
    private Scalar label;

    @JsonProperty("parameters")
    private List<Scalar> parameters;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
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
     * JavaBean accessor to obtain the value of label
     * 
     * @return the label value
     */
    public Scalar getLabel() {
        return label;
    }

    /**
     * JavaBean accessor to obtain the value of parameters
     * 
     * @return the parameters value
     */
    public List<Scalar> getParameters() {
        return parameters;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (label == null ? 0 : label.hashCode()) + (description == null ? 0 : description.hashCode())
            + (parameters == null ? 0 : parameters.hashCode());
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
     * Standard JavaBean mutator method to set the value of label
     * 
     * @param label
     *            the value to be set into label
     */
    public void setLabel(Scalar label) {
        this.label = label;
    }

    /**
     * Standard JavaBean mutator method to set the value of parameters
     * 
     * @param parameters
     *            the value to be set into parameters
     */
    public void setParameters(List<Scalar> parameters) {
        this.parameters = parameters;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("%s: label[%s], desc[%s], params %s", this.getClass().getSimpleName(), label, description,
            parameters);
    }

}
