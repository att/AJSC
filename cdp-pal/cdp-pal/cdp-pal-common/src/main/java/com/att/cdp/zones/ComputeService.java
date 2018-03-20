/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;
import java.util.Map;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Rule;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.VirtualInterface;
import com.att.cdp.zones.model.Volume;

/**
 * This interface represents the implementation of the Compute service obtained from a provider context.
 * <p>
 * The compute service is used to manage compute resources, ie, servers. The user can obtain lists of existing servers,
 * create new servers, attach and detach volumes, and delete servers using this service. Alternatively, the rime data
 * model can implicitly invoke these services from the various model objects.
 * </p>
 * 
 */

public interface ComputeService extends Service {

    /**
     * Aborts the resize process for a server and return to original size.
     * 
     * @param server
     *            The server to be resized. Must be in a ready state.
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void abortResize(Server server) throws ZoneException;

    /**
     * Add the rule to the ACL with the given id
     * 
     * @param aclId
     *            The id of the ACL that the rule should be added to
     * @param rule
     *            The rule to be added to the ACL
     * @return The Rule that was added to the ACL
     * @throws ZoneException
     *             If the rule could not be added to the given ACL
     */
    Rule addACLRule(String aclId, Rule rule) throws ZoneException;

    /**
     * Forcibly associates the indicated floating IP address with the specified server.
     * 
     * @param server
     *            The server object to be associated with the floating IP address
     * @param address
     *            The floating IP address to be associated with the server
     * @throws ZoneException
     *             if the ip address cannot be assigned
     */
    void assignIpAddress(Server server, String address) throws ZoneException;

    /**
     * Forcibly associates the indicated floating IP address with the specified server.
     * 
     * @param serverId
     *            The ID of the server to be associated with the floating IP address
     * @param address
     *            The floating IP address to be associated with the server
     * @throws ZoneException
     *             if the ip address cannot be assigned
     */
    void assignIpAddress(String serverId, String address) throws ZoneException;

    /**
     * Associate the ACL (Security Group) with the given server
     * 
     * @param serverId
     *            The id of the server to associate the ACL with
     * @param aclName
     *            The name of the ACL to associate
     * @throws ZoneException
     *             If the ACL cannot be associated
     */
    void associateACL(String serverId, String aclName) throws ZoneException;

    /**
     * Attach a volume to a specified server. The volume is then attached to the server for it to use when the server is
     * started.
     * 
     * @param server
     *            The server we are manipulating
     * @param volume
     *            The volume we wish to attach to the server
     * @param deviceName
     *            The device name that will be used to identify this volume, such as /dev/sda
     * @throws ZoneException
     *             If the volume cannot be attached for some reason
     */
    void attachVolume(Server server, Volume volume, String deviceName) throws ZoneException;

    /**
     * Creates a new access control list
     * 
     * @param acl
     *            An object initialized with the contents of the ACL to be created. This object is used as a model of
     *            what to build, but it is likely not the object that will be built. The caller should use the object
     *            returned from this call to access the actually constructed ACL.
     * @return The access control list that was created
     * @throws ZoneException
     *             If the access control list could not be created
     */
    ACL createAccessControlList(ACL acl) throws ZoneException;

    /**
     * Create a server using the supplied server object as the pattern
     * 
     * @param server
     *            The server to create. The host must contain the following attributes in order to be legal for creating
     *            a new host:
     *            <ul>
     *            <li>The name of the host</li>
     *            <li>The id of the template to be used</li>
     *            <li>The id of the image to be used</li>
     *            </ul>
     * @return A reference to the connected server. The template server (the argument passed) remains disconnected. The
     *         user is encouraged to use the referenced returned from this method for any further operations on the
     *         server.
     * @throws ZoneException
     *             If the server cannot be created
     */
    Server createServer(Server server) throws ZoneException;

    /**
     * Requests the creation of a snapshot image from the supplied server object
     * 
     * @param server
     *            The server to snapshot
     * @param name
     *            The name of the snapshot that will be created
     * @throws ZoneException
     *             If the snapshot cannot be created
     */
    void createServerSnapshot(Server server, String name) throws ZoneException;

    /**
     * Delete the specified Access Control List using it's id.
     * 
     * @param id
     *            The id of the access control list to be deleted.
     * @throws ZoneException
     *             If the access control list does not exist or cannot be deleted for some reason.
     */
    void deleteAccessControlList(String id) throws ZoneException;

    /**
     * Delete the specified rule in the access control list using it's id.
     * 
     * @param rule
     *            The rule to be deleted.
     * @throws ZoneException
     *             If the rule does not exist or cannot be deleted for some reason.
     */
    void deleteACLRule(Rule rule) throws ZoneException;

    /**
     * Delete the specified server using it's id.
     * 
     * @param server
     *            The server to be deleted.
     * @throws ZoneException
     *             If the server does not exist or cannot be deleted for some reason.
     */
    void deleteServer(Server server) throws ZoneException;

    /**
     * Delete the specified server using it's id.
     * 
     * @param serverId
     *            The server to be deleted.
     * @throws ZoneException
     *             If the server does not exist or cannot be deleted for some reason.
     */
    void deleteServer(String serverId) throws ZoneException;

    /**
     * Detaches the volume from the specified server.
     * 
     * @param server
     *            The server to be manipulated
     * @param deviceName
     *            The device name to be detached
     * @throws ZoneException
     *             If the volume cannot be detached for some reason
     */
    void detachVolume(Server server, String deviceName) throws ZoneException;

    /**
     * Detaches the volume from the specified server.
     * 
     * @param server
     *            The server to be manipulated
     * @param volume
     *            The volume to be detached
     * @throws ZoneException
     *             If the volume cannot be detached for some reason
     */
    void detachVolume(Server server, Volume volume) throws ZoneException;

    /**
     * Disassociate the ACL (Security Group) from the given server
     * 
     * @param serverId
     *            The id of the server to disassociate the ACL from
     * @param aclName
     *            The name of the ACL to disassociate
     * @throws ZoneException
     *             if the ACL cannot be removed
     */
    void disassociateACL(String serverId, String aclName) throws ZoneException;

    /**
     * Executes a command on the indicated server using the console for that server
     * 
     * @param server
     *            The server object
     * @param command
     *            The command string to be executed
     * @throws ZoneException
     *             If the command cannot be executed
     * @deprecated This feature is no longer supported. Use an SSH command channel to execute commands.
     */
    @Deprecated
    void executeCommand(Server server, String command) throws ZoneException;

    /**
     * This method scans the provider to locate all servers (instances) that are using the specified SSH key pair, if
     * any.
     * 
     * @param keyPair
     *            The name of the key pair to search for
     * @return A list of server id's that are using the indicated key pair. If no servers are using the key pair, then
     *         an empty list is returned.
     * @throws ZoneException
     *             If the servers cannot be listed for some reason.
     */
    List<String> findAllServersUsingKey(String keyPair) throws ZoneException;

    /**
     * Returns the access control list that has the given id.
     * 
     * @param id
     *            The id of the acl to obtain
     * @return The access control list and its rules
     * @throws ZoneException
     *             If the acl list cannot be obtained
     */
    ACL getAccessControlList(String id) throws ZoneException;

    /**
     * Returns the list of access control lists that are defined for the tenant.
     * 
     * @return The list of access control lists and their rules
     * @throws ZoneException
     *             If the acl lists cannot be obtained
     */
    List<ACL> getAccessControlLists() throws ZoneException;

    /**
     * Returns a map of the volume attachments for the specified server. The key of the map is the device name for the
     * volume attachment. This is the device identification which the server will "see".
     * 
     * @param server
     *            The server for which we wish to obtain all attachments (if any).
     * @return A map of the volume attachments, or an empty map if there are none. The map is keyed by the device name
     *         used to attach the volume. The value of the entry is the volume ID of the volume attached at that device.
     * @throws ZoneException
     *             If the attachments cannot be obtained
     */
    Map<String, String> getAttachments(Server server) throws ZoneException;

    /**
     * Returns a map of the volume attachments for the specified server. The key of the map is the device name for the
     * volume attachment. This is the device identification which the server will "see".
     * 
     * @param id
     *            The server ID for which we wish to obtain all attachments (if any).
     * @return A map of the volume attachments, or an empty map if there are none. The map is keyed by the device name
     *         used to attach the volume. The value of the entry is the volume ID of the volume attached at that device.
     * @throws ZoneException
     *             If the attachments cannot be obtained
     */
    Map<String, String> getAttachments(String id) throws ZoneException;

    /**
     * Returns the buffered console output accumulated so far. Once the console output has been returned, it is no
     * longer available.
     * 
     * @param server
     *            The server to obtain the console output for.
     * @return The console output. If no console has been established for this server, then the response is null.
     * @throws ZoneException
     *             If the console output cannot be obtained
     * @deprecated This feature is no longer supported. Use an SSH command channel to obtain output from commands.
     */
    @Deprecated
    List<String> getConsoleOutput(Server server) throws ZoneException;

    /**
     * This method returns a list of OS extended network attributes for the supplied tenant.
     * <p>
     * This lists networks that are available to the tenant. The information in the network list includes extended
     * network attributes.
     * </p>
     * 
     * @return A list of networks and their extended attributes
     * @throws ZoneException
     *             If the networks and extended attributes cannot be listed
     */
    List<Network> getExtendedNetworks() throws ZoneException;

    /**
     * Returns the indicated hypervisor using the specified identification token
     * 
     * @param id
     *            The identification of the hypervisor
     * @return The hypervisor
     * @throws ZoneException
     *             If the hypervisor cannot be found
     */
    Hypervisor getHypervisor(String id) throws ZoneException;

    /**
     * Obtain a list of hypervisors from the compute service.
     * 
     * @return The list of hypervisors that are defined.
     * @throws ZoneException
     *             If any of the following conditions are true:
     *             <ul>
     *             <li>the user has not successfully logged in to the provider</li>
     *             <li>the context has been closed and this service is requested</li>
     *             <li>the current user does not have the rights to perform this operation</li>
     *             <li>the user and/or credentials are not valid</li>
     *             </ul>
     */
    List<Hypervisor> getHypervisors() throws ZoneException;

    /**
     * Returns the list of hypervisors that match the name pattern supplied.
     * 
     * @param id
     *            A regular expression that can be used to filter hypervisor ids. A string that is suitable to use in
     *            the Java <code>String.matches()</code> method. If null, the method behaves identically to
     *            {@link #getHypervisors()}
     * @return The hypervisor
     * @throws ZoneException
     *             If the hypervisor cannot be found
     * @see java.lang.String#matches(String)
     */
    List<Hypervisor> getHypervisors(String id) throws ZoneException;

    /**
     * Returns the indicated host using the specified identification token
     * 
     * @param id
     *            The identification of the server
     * @return The server
     * @throws ZoneException
     *             If the host cannot be found
     */
    Server getServer(String id) throws ZoneException;

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
    List<Server> getServers() throws ZoneException;

    /**
     * Returns the list of servers that match the name pattern supplied.
     * 
     * @param name
     *            A regular expression that can be used to filter server names. A string that is suitable to use in the
     *            Java <code>String.matches()</code> method. If null, the method behaves identically to
     *            {@link #getServers()}
     * @return The server
     * @throws ZoneException
     *             If the host cannot be found
     * @see java.lang.String#matches(String)
     */
    List<Server> getServers(String name) throws ZoneException;

    /**
     * @param string
     *            The ID of the template desired
     * @return The template
     * @throws ZoneException
     *             If the template cannot be obtained
     */
    Template getTemplate(String string) throws ZoneException;

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
    List<Template> getTemplates() throws ZoneException;

    /**
     * This method returns a list of OS Virtual Interfaces for a specified server instance.
     * <p>
     * This includes the ID for the virtual interface as well as the associated mac address.
     * </p>
     * 
     * @param id
     *            The identification of the server
     * @return A list of virtual interfaces
     * @throws ZoneException
     *             If the virtual interfaces cannot be listed
     */
    List<VirtualInterface> getVirtualInterfaces(String id) throws ZoneException;

    /**
     * Allows the caller to migrate a (possibly running) server from one physical host to another, where the provider
     * controls the location and the migration of the running server.
     * 
     * @param serverId
     *            The server to be migrated
     * @throws ZoneException
     *             If the provider does not support migrate, or the server cant be migrated for some reason.
     */
    void migrateServer(String serverId) throws ZoneException;

    /**
     * The ability to move a server from whatever physical host it is located on to a new physical host, possibly
     * supplying the new target physical host id.
     * <p>
     * The server to be moved must be stopped. This is likely caused by a failure of the physical host, and this method
     * allows the instance to be moved to a new physical host. If no target host id is supplied, then the instance is
     * moved to any suitable physical host as determined by the provider.
     * </p>
     * 
     * @param serverId
     *            The ID of the server to be moved
     * @param targetHostId
     *            The physical host id of the new host, or null to allow the provider to control the move. This could be
     *            controlled through policies or strategies, or other mechanisms defined by the provider.
     * @throws ZoneException
     *             If the provider does not support move, or if the server cant be moved for some reason.
     */
    void moveServer(String serverId, String targetHostId) throws ZoneException;

    /**
     * Pauses the server, if supported by the underlying IaaS provider
     * 
     * @param server
     *            The server to be paused
     * @throws ZoneException
     *             If the server cannot be paused, or the provider does not support pause
     */
    void pauseServer(Server server) throws ZoneException;

    /**
     * Pauses the server with the specified id, if supported by the underlying IaaS provider
     * 
     * @param id
     *            The server ID to be paused
     * @throws ZoneException
     *             If the server cannot be paused, or the provider does not support pause
     */
    void pauseServer(String id) throws ZoneException;

    /**
     * Prepares the server for resizing
     * 
     * @param server
     *            The server to be resized
     * @param newTemplate
     *            The new template to be applied
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void prepareResize(Server server, Template newTemplate) throws ZoneException;

    /**
     * Processes a Server that has been prepared for a resize
     * 
     * @param server
     *            The server to be resized. Must be in a pending state.
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void processResize(Server server) throws ZoneException;

    /**
     * This method is used to rebuild a server using the exact same template (flavor) and image used to previously
     * construct it.
     * 
     * @param server
     *            The server being rebuilt
     * @throws ZoneException
     */
    void rebuildServer(Server server) throws ZoneException;

    /**
     * This method is used to rebuild a server using a specified snapshot. The snapshot MUST have been created from the
     * server being rebuilt.
     * 
     * @param server
     *            The server being rebuilt
     * @param snapshot
     *            The snapshot image id of this server that is used to rebuild the server.
     * @throws ZoneException
     */
    void rebuildServer(Server server, String snapshot) throws ZoneException;

    /**
     * This method is used to update the state of the mapped hypervisor object
     * 
     * @param hypervisor
     *            The hypervisor object to be refreshed. Only the state of the hypervisor is updated, no other part of
     *            the model is changed.
     * @throws ZoneException
     *             If the hypervisor cannot be refreshed
     */
    void refreshHypervisorState(Hypervisor hypervisor) throws ZoneException;

    /**
     * This method is used to update the status of the mapped hypervisor object
     * 
     * @param hypervisor
     *            The hypervisor object to be refreshed. Only the status of the hypervisor is updated, no other part of
     *            the model is changed.
     * @throws ZoneException
     *             If the hypervisor cannot be refreshed
     */
    void refreshHypervisorStatus(Hypervisor hypervisor) throws ZoneException;

    /**
     * This method is used to update the status of the mapped server object
     * 
     * @param server
     *            The server object to be refreshed. Only the status of the server is updated, no other part of the
     *            model is changed.
     * @throws ZoneException
     *             If the server cannot be refreshed
     */
    void refreshServerStatus(Server server) throws ZoneException;

    /**
     * This method releases the floating IP address from the server and returns it to the pool. The address must have
     * been a floating ip address and it must be assigned to the specified server.
     * 
     * @param server
     *            The server that the address is assigned to
     * @param assignedAddress
     *            The floating ip address to be released and returned to the pool
     * @throws ZoneException
     *             If the ip address cannot be released
     */
    void releaseIpAddress(Server server, String assignedAddress) throws ZoneException;

    /**
     * This method releases the floating IP address from the server and returns it to the pool. The address must have
     * been a floating ip address and it must be assigned to the specified server.
     * 
     * @param serverId
     *            The id of the server that the address is assigned to
     * @param assignedAddress
     *            The floating ip address to be released and returned to the pool
     * @throws ZoneException
     *             If the ip address cannot be released
     */
    void releaseIpAddress(String serverId, String assignedAddress) throws ZoneException;

    /**
     * Resumes the indicated server
     * 
     * @param server
     *            The server to be resumed
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void resumeServer(Server server) throws ZoneException;

    /**
     * Resumes the indicated server
     * 
     * @param id
     *            The id of the server to be resumed
     * @throws ZoneException
     *             If the server is in an invalid state or it does not exist.
     */
    void resumeServer(String id) throws ZoneException;

    /**
     * Starts the indicated server
     * 
     * @param server
     *            The server to be started
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void startServer(Server server) throws ZoneException;

    /**
     * Starts the indicated server
     * 
     * @param id
     *            The id of the server to be started
     * @throws ZoneException
     *             If the server is in an invalid state or it does not exist.
     */
    void startServer(String id) throws ZoneException;

    /**
     * Stops the indicated server
     * 
     * @param server
     *            The server to be stopped
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void stopServer(Server server) throws ZoneException;

    /**
     * Stops the indicated server
     * 
     * @param id
     *            The id of the server to be stopped
     * @throws ZoneException
     *             If the server is in an invalid state or it does not exist.
     */
    void stopServer(String id) throws ZoneException;

    /**
     * Suspends the indicated server
     * 
     * @param server
     *            The server to be suspended
     * @throws ZoneException
     *             If the server is in an invalid state, does not exist, or the server object is disconnected.
     */
    void suspendServer(Server server) throws ZoneException;

    /**
     * Suspends the indicated server
     * 
     * @param id
     *            The id of the server to be suspended
     * @throws ZoneException
     *             If the server is in an invalid state or it does not exist.
     */
    void suspendServer(String id) throws ZoneException;

    /**
     * Un-Pauses the server, if supported by the underlying IaaS provider
     * 
     * @param server
     *            The server to be un-paused
     * @throws ZoneException
     *             If the server was not paused, or the provider does not support pause/unpause
     */
    void unpauseServer(Server server) throws ZoneException;

    /**
     * Un-Pauses the server with the specified id, if supported by the underlying IaaS provider
     * 
     * @param id
     *            The server ID to be un-paused
     * @throws ZoneException
     *             If the server was not paused, or the provider does not support pause/unpause
     */
    void unpauseServer(String id) throws ZoneException;

    /**
     * This method obtains a list of ports (NICs) associated with the server and returns them to the caller.
     * 
     * @param server
     *            The server to be queried
     * @return The list of ports (virtual NICs) connected to the server.
     * @throws ZoneException
     *             If the server is null or invalid, if the context is closed, or if the context has not been
     *             authenticated, or if the authentication has expired
     */
    List<Port> getPorts(Server server) throws ZoneException;

    /**
     * This method can be used to attach a virtual NIC (port) to a specific server. The subnet associated with the port
     * provides the connectivity to the server through the port.
     * 
     * @param server
     *            The server to attach the port to
     * @param port
     *            The port to be attached
     * @throws ZoneException
     *             If the port cannot be attached
     */
    void attachPort(Server server, Port port) throws ZoneException;

    /**
     * This method is used to detach a specific port from a server, disconnecting it from the associated subnet.
     * 
     * @param server
     *            The server from which the port will be detached
     * @param port
     *            The port to be detached
     * @throws ZoneException
     *             If the port cannot be detached
     */
    void detachPort(Server server, Port port) throws ZoneException;

    /**
     * This is a standardized method to wait for the state of the server to change to one of the allowed states.
     * <p>
     * This method will block the caller and not return until either:
     * <ul>
     * <li>The server provided has been found to be in one of the provided states on a sample</li>
     * <lI>The server does not enter any of the provided states within the specified time out period (in seconds). In
     * this case, an exception is thrown.</li>
     * </ul>
     * </p>
     * <p>
     * The caller provides the sampling interval, in SECONDS, and the timeout (also in SECONDS). This sets the frequency
     * of checking of the state to determine if the server has changed to one of the listed states. If the state does
     * not enter one of the allowed or expected states within the timeout period, then the method throws an exception.
     * </p>
     * <p>
     * The caller must also provide the server object to be checked. The server object must be a connected server
     * object. Using a disconnected server object is an error and will result in an exception being thrown.
     * </p>
     * <p>
     * The caller lastly provides a variable list of allowed server states. This is a variable argument list that allows
     * for one or more states to be listed. If the server is found to be in any one of these states on a sample
     * interval, then the method returns and no exception is thrown.
     * </p>
     * 
     * @param pollInterval
     *            The interval, in seconds, to check the server state and see if the server state has changed.
     * @param timeout
     *            The total time, in seconds, that the method will block the caller and check the server state. This
     *            value MUST be greater than or equal to the poll interval.
     * @param server
     *            The server to be checked
     * @param status
     *            The variable list of at least one status value(s) that are allowed or expected. If the server is found
     *            to be in any of these states on a poll interval, the method completes normally and returns to the
     *            caller.
     * @throws TimeoutException
     *             If the server state does not change to one of the allowed states within the timeout period.
     * @throws NotNavigableException
     *             If the server object provided is not connected.
     * @throws InvalidRequestException
     *             If the arguments are null or invalid. This includes the case where the timeout is less than the
     *             interval.
     * @throws ContextClosedException
     *             If the context connected to the server is closed and cannot be used.
     * @throws ZoneException
     *             If anything unexpected happens
     */
    void waitForStateChange(int pollInterval, int timeout, Server server, Server.Status... status)
        throws TimeoutException, NotNavigableException, InvalidRequestException, ContextClosedException, ZoneException;

    /**
     * This method is used to reboot given server.
     * @param server
     * @param rebootType
     * @throws ZoneException
     */
    void rebootServer(Server server, String rebootType) throws ZoneException;
    
    
    
	/**
	 *  This method is used to reboot given server id.
     * 
	 * @param serverId
	 * @param rebootType
	 * @throws ZoneException
	 */
    void rebootServer(String serverId, String rebootType) throws ZoneException;
}
