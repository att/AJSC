/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.model.Volume;

/**
 * The connected tenant is used to maintain and navigate the object model for the caller.
 * <p>
 * Depending on the implementation, the connected object may cache all, parts, or none of the object model, electing
 * instead to always make calls to the back-end service. This can vary from one provider to another as needed to meet
 * the way the various providers may work.
 * </p>
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class ConnectedTenant extends Tenant {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Tenant</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedTenant(Context context) {
        super(context);
    }

    /**
     * Returns the list of access control lists that are defined for the tenant.
     * 
     * @return The list of access controls defined
     * @throws ZoneException
     *             If the ACL lists cannot be obtained
     * @see com.att.cdp.zones.model.Tenant#getAccessControls()
     */
    @Override
    public List<ACL> getAccessControls() throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getAccessControlLists();
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
    @Override
    public Server getServer(String id) throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getServer(id);
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
    @Override
    public List<Server> getServers() throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getServers();
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
    @Override
    public List<Server> getServers(String name) throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getServers(name);
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
    @Override
    public List<Template> getTemplates() throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getTemplates();
    }

    /**
     * Returns information about the volume with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the volume that we want to find information about
     * @return The volume if it exists
     * @throws ZoneException
     *             - If the volume cannot be listed, or the volume does not exist
     * @see com.att.cdp.zones.model.Tenant#getVolume(java.lang.String)
     */
    @Override
    public Volume getVolume(String id) throws ZoneException {
        Context context = getContext();
        return context.getVolumeService().getVolume(id);
    }

    /**
     * Retrieves the list of volumes defined for this service.
     * 
     * @returns The list of volumes for this tenant, if any. The list may be empty if there are no volumes defined.
     * @throws ZoneException
     *             - If the volume service cannot be accessed.
     * @see com.att.cdp.zones.model.Tenant#getVolumes()
     */
    @Override
    public List<Volume> getVolumes() throws ZoneException {
        Context context = getContext();
        return context.getVolumeService().getVolumes();
    }

    /**
     * Returns a list of volumes that match the supplied name
     * 
     * @param name
     *            The name pattern of the volumes to be located. The name is a regular expression that is suitable for
     *            use in the Java String.matches() method.
     * @return A list (potentially empty) of all volumes that match the specified name pattern
     * @throws ZoneException
     *             - If the volume service cannot be accessed.
     * @see java.lang.String#matches(String)
     * @see com.att.cdp.zones.model.Tenant#getVolumes(java.lang.String)
     */
    @Override
    public List<Volume> getVolumes(String name) throws ZoneException {
        Context context = getContext();
        return context.getVolumeService().getVolumes(name);
    }
}
