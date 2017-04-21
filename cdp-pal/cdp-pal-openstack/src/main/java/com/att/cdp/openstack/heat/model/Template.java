/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.List;
import java.util.Map;

import com.att.cdp.openstack.exception.UnmarshallException;
import com.att.cdp.pal.util.ObjectHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The root of the object graph that represents a Heat template
 * 
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Template extends ModelObject {
    @JsonProperty("heat_template_version")
    private Scalar heatTemplateVersion;

    @JsonProperty("description")
    private Scalar description;

    @JsonProperty("parameter_groups")
    private List<ParameterGroup> parameterGroups;

    @JsonProperty("parameters")
    private Map<String, Parameter> parameters;

    @JsonProperty("resources")
    private Map<String, Resource> resources;

    @JsonProperty("outputs")
    private Map<String, Output> outputs;

    /**
     * JavaBean accessor to obtain the value of heatTemplateVersion
     * 
     * @return the heatTemplateVersion value
     */
    public Scalar getHeatTemplateVersion() {
        return heatTemplateVersion;
    }

    /**
     * Standard JavaBean mutator method to set the value of heatTemplateVersion
     * 
     * @param heatTemplateVersion
     *            the value to be set into heatTemplateVersion
     * @throws UnmarshallException
     *             If the template cannot be unmarshalled
     */
    public void setHeatTemplateVersion(Scalar heatTemplateVersion) throws UnmarshallException {
        this.heatTemplateVersion = heatTemplateVersion;
    }

    /**
     * JavaBean accessor to obtain the value of description
     * 
     * @return the description value
     */
    public String getDescription() {
        return description.toString();
    }

    /**
     * Standard JavaBean mutator method to set the value of description
     * 
     * @param description
     *            the value to be set into description
     * @throws UnmarshallException
     *             If the template cannot be unmarshalled
     */
    public void setDescription(Scalar description) throws UnmarshallException {
        this.description = description;
    }

    /**
     * JavaBean accessor to obtain the value of parameterGroups
     * 
     * @return the parameterGroups value
     */
    public List<ParameterGroup> getParameterGroups() {
        return parameterGroups;
    }

    /**
     * Standard JavaBean mutator method to set the value of parameterGroups
     * 
     * @param parameterGroups
     *            the value to be set into parameterGroups
     */
    public void setParameterGroups(List<ParameterGroup> parameterGroups) {
        this.parameterGroups = parameterGroups;
    }

    /**
     * JavaBean accessor to obtain the value of parameters
     * 
     * @return the parameters value
     */
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    /**
     * Standard JavaBean mutator method to set the value of parameters
     * 
     * @param parameters
     *            the value to be set into parameters
     */
    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * JavaBean accessor to obtain the value of resources
     * 
     * @return the resources value
     */
    public Map<String, Resource> getResources() {
        return resources;
    }

    /**
     * Standard JavaBean mutator method to set the value of resources
     * 
     * @param resources
     *            the value to be set into resources
     */
    public void setResources(Map<String, Resource> resources) {
        this.resources = resources;
    }

    /**
     * JavaBean accessor to obtain the value of outputs
     * 
     * @return the outputs value
     */
    public Map<String, Output> getOutputs() {
        return outputs;
    }

    /**
     * Standard JavaBean mutator method to set the value of outputs
     * 
     * @param outputs
     *            the value to be set into outputs
     */
    public void setOutputs(Map<String, Output> outputs) {
        this.outputs = outputs;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String
            .format("Template version %s, \"%s\", %d parameter groups, %d parameters, %d resources, %d outputs",
                heatTemplateVersion, description, parameterGroups == null ? 0 : parameterGroups.size(),
                parameters == null ? 0 : parameters.size(), resources == null ? 0 : resources.size(), outputs == null
                    ? 0 : outputs.size());
    }
}
