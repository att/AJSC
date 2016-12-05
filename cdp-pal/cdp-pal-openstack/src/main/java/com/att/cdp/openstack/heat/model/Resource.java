/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type",
              defaultImpl = GenericResource.class)
@JsonSubTypes({
    @Type(value = VolumeResource.class, name = "OS::Cinder::Volume"),
    @Type(value = NovaServerResource.class, name = "OS::Nova::Server"),
    @Type(value = VolumeAttachmentResource.class, name = "OS::Cinder::VolumeAttachment"),
    @Type(value = GenericResource.class, name = "OS::Cinder::VolumeType"),
    @Type(value = GenericResource.class, name = "OS::Glance::Image"),
    @Type(value = GenericResource.class, name = "OS::Heat::SoftwareComponent"),
    @Type(value = GenericResource.class, name = "OS::Heat::SoftwareConfig"),
    @Type(value = GenericResource.class, name = "OS::Heat::SoftwareDeployment"),
    @Type(value = GenericResource.class, name = "OS::Heat::SoftwareDeploymentGroup"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Firewall"),
    @Type(value = GenericResource.class, name = "OS::Neutron::FirewallPolicy"),
    @Type(value = GenericResource.class, name = "OS::Neutron::FirewallRule"),
    @Type(value = GenericResource.class, name = "OS::Neutron::FloatingIP"),
    @Type(value = GenericResource.class, name = "OS::Neutron::FloatingIPAssociation"),
    @Type(value = GenericResource.class, name = "OS::Neutron::LoadBalancer"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Net"),
    @Type(value = GenericResource.class, name = "OS::Neutron::NetworkGateway"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Pool"),
    @Type(value = GenericResource.class, name = "OS::Neutron::PoolMember"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Port"),
    @Type(value = GenericResource.class, name = "OS::Neutron::ProviderNet"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Router"),
    @Type(value = GenericResource.class, name = "OS::Neutron::RouterInterface"),
    @Type(value = GenericResource.class, name = "OS::Neutron::SecurityGroup"),
    @Type(value = GenericResource.class, name = "OS::Neutron::Subnet"),
    @Type(value = GenericResource.class, name = "OS::Nova::Flavor"),
    @Type(value = GenericResource.class, name = "OS::Nova::FloatingIP"),
    @Type(value = GenericResource.class, name = "OS::Nova::FloatingIPAssociation"),
    @Type(value = GenericResource.class, name = "OS::Nova::KeyPair"),
    @Type(value = GenericResource.class, name = "OS::Nova::Server"),
})
public class Resource extends ModelObject {

    @JsonProperty("type")
    private Scalar type;

    @JsonProperty("metadata")
    private Map<String, Scalar> metadata;

    @JsonProperty("depends_on")
    private List<Scalar> dependencies;

    @JsonProperty("update_policy")
    private Map<String, Scalar> updatePolicy;

    @JsonProperty("deletion_policy")
    private Scalar deletePolicy;

    /**
     * JavaBean accessor to obtain the value of type
     * 
     * @return the type value
     */
    public Scalar getType() {
        return type;
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
     * JavaBean accessor to obtain the value of metadata
     * 
     * @return the metadata value
     */
    public Map<String, Scalar> getMetadata() {
        return metadata;
    }

    /**
     * Standard JavaBean mutator method to set the value of metadata
     * 
     * @param metadata
     *            the value to be set into metadata
     */
    public void setMetadata(Map<String, Scalar> metadata) {
        this.metadata = metadata;
    }

    /**
     * JavaBean accessor to obtain the value of dependencies
     * 
     * @return the dependencies value
     */
    public List<Scalar> getDependencies() {
        return dependencies;
    }

    /**
     * Standard JavaBean mutator method to set the value of dependencies
     * 
     * @param dependencies
     *            the value to be set into dependencies
     */
    public void setDependencies(List<Scalar> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * JavaBean accessor to obtain the value of updatePolicy
     * 
     * @return the updatePolicy value
     */
    public Map<String, Scalar> getUpdatePolicy() {
        return updatePolicy;
    }

    /**
     * Standard JavaBean mutator method to set the value of updatePolicy
     * 
     * @param updatePolicy
     *            the value to be set into updatePolicy
     */
    public void setUpdatePolicy(Map<String, Scalar> updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

    /**
     * JavaBean accessor to obtain the value of deletePolicy
     * 
     * @return the deletePolicy value
     */
    public Scalar getDeletePolicy() {
        return deletePolicy;
    }

    /**
     * Standard JavaBean mutator method to set the value of deletePolicy
     * 
     * @param deletePolicy
     *            the value to be set into deletePolicy
     */
    public void setDeletePolicy(Scalar deletePolicy) {
        this.deletePolicy = deletePolicy;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = type == null ? 0 : type.hashCode();
        hash += metadata == null ? 0 : metadata.hashCode();
        hash += dependencies == null ? 0 : dependencies.hashCode();
        hash += updatePolicy == null ? 0 : updatePolicy.hashCode();
        hash += deletePolicy == null ? 0 : deletePolicy.hashCode();

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
        return String.format("%s: type[%s]", getClass().getSimpleName(), type);
    }
}
