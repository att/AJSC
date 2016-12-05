/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cdp.zones.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonRootName;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A template defines a set of hardware resources that are required in order to run servers. There can be any number of
 * templates defined. A server is associated with a template to define the hardware environment that server requires. A
 * template conversely may be used by zero or many server definitions, but any one server can be associated with at most
 * only one Template.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
@JsonRootName("tenant")
public class Tenant extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A description of the tenant
     */
    private String description;

    /**
     * True if the tenant is enabled, false otherwise
     */
    private Boolean enabled;

    /**
     * The unique identifier of this tenant
     */
    private String id;

    /**
     * The name of this tenant
     */
    private String name;

    /**
     * Default constructor
     */
    public Tenant() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Tenant(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Tenant other = (Tenant) obj;
        return id.equals(other.id);
    }

    /**
     * Returns the list of access control lists that are defined for the tenant.
     * 
     * @return The list of access controls defined
     * @throws ZoneException
     *             If the ACL lists cannot be obtained
     */
    @JsonIgnore
    public List<ACL> getAccessControls() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the value of enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * JavaBean accessor to obtain the value of tenantId
     * 
     * @return the tenantId value
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of tenantName
     * 
     * @return the tenantName value
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the indicated host using the specified identification token
     * 
     * @param id
     *            The identification of the server
     * @return The server
     * @throws ZoneException
     *             If the host cannot be found
     */
    @JsonIgnore
    public Server getServer(String id) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Obtain a list of servers from the compute service.
     * 
     * @return The list of servers that are defined.
     * @throws ZoneException
     *             If any of the following conditions are true:
     *             <ul>
     *             <li>the user has not successfully logged in to the provider</li>
     *             <li>the context has been closed and this service is requested</li>
     *             <li>the current user does not have the rights to perform this operation</li>
     *             <li>the user and/or credentials are not valid</li>
     *             </ul>
     */
    @JsonIgnore
    public List<Server> getServers() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Returns the list of servers that match the name pattern supplied.
     * 
     * @param name
     *            A regular expression that can be used to filter server names. A string that is suitable to use in the
     *            Java <code>String.matches()</code> method.
     * @return The server
     * @throws ZoneException
     *             If the host cannot be found
     * @see java.lang.String#matches(String)
     */
    @JsonIgnore
    public List<Server> getServers(String name) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * This method returns a list of templates that are available.
     * <p>
     * A template represents a definition of a hardware environment that is used to create an image. This includes
     * number of cpu's, amount of memory, etc.
     * </p>
     * 
     * @return A list of available templates
     * @throws ZoneException
     *             If the templates cannot be listed
     */
    @JsonIgnore
    public List<Template> getTemplates() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Returns information about the volume with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the volume that we want to find information about
     * @return The volume if it exists
     * @throws ZoneException
     *             If the volume cannot be listed, or the volume does not exist
     */
    @JsonIgnore
    public Volume getVolume(String id) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Retrieves the list of volumes defined for this service.
     * 
     * @return The list of volumes for this tenant, if any. The list may be empty if there are no volumes defined.
     * @throws ZoneException
     *             If the volume service cannot be accessed.
     */
    @JsonIgnore
    public List<Volume> getVolumes() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Returns a list of volumes that match the supplied name
     * 
     * @param name
     *            The name pattern of the volumes to be located. The name is a regular expression that is suitable for
     *            use in the Java <code>String.matches()</code> method.
     * @return A list (potentially empty) of all volumes that match the specified name pattern
     * @see java.lang.String#matches(String)
     * @throws ZoneException
     *             If the volume service cannot be accessed.
     */
    @JsonIgnore
    public List<Volume> getVolumes(String name) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @param description
     *            the value for description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param enabled
     *            the value for enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Tenant: id(%s), name(%s), desc(%s), enabled(%s)", id, name, description,
            enabled.toString());
    }
}
