/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.LoadBalancer;
import com.att.cdp.zones.model.LoadBalancerHealthMonitor;
import com.att.cdp.zones.model.LoadBalancerMember;
import com.att.cdp.zones.model.LoadBalancerPool;
import com.att.cdp.zones.model.LoadBalancerListener;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Router;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Subnet;

/**
 * This interface represents the Network services that are available from the cloud service provider.
 * <p>
 * This abstraction of services is based on a virtualized hardware model. In other words, servers can have any number of
 * NIC (Network Interface Cards) installed, each with one or more assigned IP address. Each NIC is connected to one and
 * only one subnet. Subnets are part of a network, and a network may contain any number of subnets.
 * </p>
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */
public interface NetworkService extends Service {

    /**
     * Assigns the ip address to the specified server, if the IP address is available and can be assigned.
     * 
     * @param server
     *            The server that we are assigning the ip address to
     * @param address
     *            The address we are assigning
     * @link {com.att.cdp.zones.model.Port#Port()} object to set the ip address. Each port may have different IP
     *       addresses assigned. Calling this method will assign the IP address to the server (essentially the same on
     *       all ports).
     * @throws ZoneException
     *             If the ip address cannot be assigned
     */
    void assignIpAddress(Server server, String address) throws ZoneException;

    /**
     * Assigns the ip address to the specified server, if the IP address is available and can be assigned.
     * 
     * @param serverId
     *            The id of the server that we are assigning the ip address to
     * @param address
     *            The address we are assigning {@link Port} object to set the ip address. Each port may have different
     *            IP addresses assigned. Calling this method will assign the IP address to the server (essentially the
     *            same on all ports).
     * @throws ZoneException
     *             If the ip address cannot be assigned
     */
    void assignIpAddress(String serverId, String address) throws ZoneException;

    /**
     * Assigns the next available IP address from the first, or only, tenant floating IP address pool to the indicated
     * server.
     * 
     * @param server
     *            The server to be assigned the next IP address
     * @return The ip address that was assigned
     * @throws ZoneException
     *             If the IP address cannot be assigned
     */
    String assignIpAddressFromPool(Server server) throws ZoneException;

    /**
     * Assigns the next available IP address from the tenant floating IP address pool to the indicated server.
     * 
     * @param server
     *            The server to be assigned the next IP address
     * @param pool
     *            The name of the pool to have the IP address reserved from
     * @return The ip address that was assigned
     * @throws ZoneException
     *             If the IP address cannot be assigned
     */
    String assignIpAddressFromPool(Server server, String pool) throws ZoneException;

    /**
     * Assigns the next available IP address from the tenant floating IP address pool to the indicated server.
     * 
     * @param serverId
     *            The id of the server to be assigned the next IP address
     * @param pool
     *            The name of the pool to have the IP address reserved from
     * @return The ip address that was assigned
     * @throws ZoneException
     *             If the IP address cannot be assigned
     */
    String assignIpAddressFromPool(String serverId, String pool) throws ZoneException;

    /**
     * Create a LoadBalancer using the supplied LoadBalancer object as the pattern
     * 
     * @param loadBalancer
     *            The LoadBalancer to create. The LoadBalancer must contain the following attributes in order to be
     *            legal for creating a new host:
     *            <ul>
     *            <li>The name of the loadBalancer</li>
     *            <li>The id of the subnet the loadBalancer is part of</li>
     *            <li>The load balancing method</li>
     *            <li>The load balancer protocol</li>
     *            </ul>
     * @return A reference to the connected loadBalancer. The template loadBalancer (the argument passed) remains
     *         disconnected. The user is encouraged to use the referenced returned from this method for any further
     *         operations on the loadBalancer.
     * @throws ZoneException
     *             If the loadBalancer cannot be created
     */
    LoadBalancer createLoadBalancer(LoadBalancer loadBalancer) throws ZoneException;

    /**
     * Create a network using the supplied network object as the pattern
     * 
     * @param network
     *            The network to create. The network must contain the following attributes in order to be legal for
     *            creating a new host:
     *            <ul>
     *            <li>The name of the network</li>
     *            </ul>
     * @return A reference to the connected network. The template network (the argument passed) remains disconnected.
     *         The user is encouraged to use the referenced returned from this method for any further operations on the
     *         network.
     * @throws ZoneException
     *             If the network cannot be created
     */
    Network createNetwork(Network network) throws ZoneException;

    /**
     * Create a Subnet using the supplied Subnet object as the pattern
     * 
     * @param subnet
     *            The subnet to create. The Subnet must contain the following attributes in order to be legal for
     *            creating a new host:
     *            <ul>
     *            <li>The name of the subnet</li>
     *            <li>The id of the network the subnet is part of</li>
     *            <li>The routing (CIDR)</li>
     *            </ul>
     * @return A reference to the connected subnet. The template subnet (the argument passed) remains disconnected. The
     *         user is encouraged to use the referenced returned from this method for any further operations on the
     *         subnet.
     * @throws ZoneException
     *             If the subnet cannot be created
     */
    Subnet createSubnet(Subnet subnet) throws ZoneException;

    /**
     * Delete the specified network using it's id.
     * 
     * @param network
     *            The network to be deleted.
     * @throws ZoneException
     *             If the network does not exist or cannot be deleted for some reason.
     */
    void deleteNetwork(Network network) throws ZoneException;

    /**
     * Delete the specified subnet using it's id.
     * 
     * @param subnet
     *            The subnet to be deleted.
     * @throws ZoneException
     *             If the network does not exist or cannot be deleted for some reason.
     */
    void deleteSubnet(Subnet subnet) throws ZoneException;

    /**
     * This method allows the caller to obtain a list of the available IP addresses from the Floating IP address pool.
     * 
     * @param pool
     *            The name of the pool to be processed
     * @return The list of available IP Addresses that can be used to assign a floating IP address to a server.
     * @throws ZoneException
     *             If the service is not available or the pool cannot be obtained
     */
    List<String> getAvailableAddresses(String pool) throws ZoneException;

    /**
     * Returns a list of the available floating IP pools that can be used to obtain a IP address
     * 
     * @return The list of floating IP address pools
     * @throws ZoneException
     *             If the pools cannot be obtained
     */
    List<String> getFloatingIpPools() throws ZoneException;

    /**
     * @param serverId
     * @return List ports
     * @throws ZoneException
     * @deprecated use {@link #getPorts()} to obtain this information
     */
    @Deprecated
    List<Port> getInterfaces(String serverId) throws ZoneException;

    /**
     * This method return network metadata indicating the capabilities of the network provider
     * 
     * @return NetworkMetadata
     * @throws ZoneException
     *             if the metadata cannot be obtained for some reason
     */
    NetworkMetadata getMetadata() throws ZoneException;

    /**
     * Returns the detailed information about a single network definition
     * 
     * @param id
     *            The id of the network definition
     * @return The detailed network object, or null if not found
     * @throws ZoneException
     *             If the network cannot accessed
     */
    Network getNetworkById(String id) throws ZoneException;

    /**
     * Returns a list of all networks defined in the tenant.
     * 
     * @return A list of all networks defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<Network> getNetworks() throws ZoneException;

    /**
     * Returns the detailed information about network definitions that match the name provided
     * 
     * @param name
     *            The name of the network definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the network cannot accessed
     */
    List<Network> getNetworksByName(String name) throws ZoneException;

    /**
     * @return A list of virtual NIC (ports) that exist on this network.
     * @throws ZoneException
     */
    List<Port> getPorts() throws ZoneException;

    /**
     * Creates a port on the specified subnet and network.
     * 
     * @param subnet
     *            The subnet that the port is associated with
     * @return The port that has been created
     * @throws ZoneException
     *             If the port cannot be created.
     */
    Port createPort(Subnet subnet) throws ZoneException;

    /**
     * Creates a port on the specified subnet using the supplied port as a model. This allows the caller to specify
     * attributes such as the fixed ip addresses, mac address, or other characteristics of the port that may be desired.
     * 
     * @param subnet
     *            The subnet to connect the port to
     * @param model
     *            The model port object that supplies the desired characteristics for the new connected port
     * @return The connected port that will be assigned to the server
     * @throws ZoneException
     *             If the port cannot be created for some reason.
     */
    Port createPort(Subnet subnet, Port model) throws ZoneException;

    /**
     * Deletes a port (that is not being used)
     * 
     * @param port
     *            The port to be deleted
     * @throws ZoneException
     *             If the port is attached to any server, or if the port does not exist, or if there is any error
     *             deleting the port on the provider.
     */
    void deletePort(Port port) throws ZoneException;

    /**
     * This method is used to retrieve a specific port by it's ID.
     * 
     * @param id
     *            The port ID to be obtained
     * @return The port if it exists.
     * @throws ZoneException
     *             If the port cannot be found, or if the ID is not a valid ID (such as null).
     */
    Port getPort(String id) throws ZoneException;

    /**
     * @return a list of available routers
     * @throws ZoneException
     *             if the list of available routers cannot be obtained for some reason.
     */
    List<Router> getRouters() throws ZoneException;

    /**
     * Obtains the specified Subnet and returns the definition to the caller, if it exists.
     * 
     * @param id
     *            The id of the subnet
     * @return The subnet if it exists, or null if it does not exist
     * @throws ZoneException
     *             If the network service cannot be used for some reason or there was an error.
     */
    Subnet getSubnetById(String id) throws ZoneException;

    /**
     * Returns a list of all subnets defined in the tenant.
     * 
     * @return A list of all subnets defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<Subnet> getSubnets() throws ZoneException;

    /**
     * Returns the detailed information about subnet definitions that match the name provided
     * 
     * @param name
     *            The name of the subnet definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the subnet cannot accessed
     */
    List<Subnet> getSubnetsByName(String name) throws ZoneException;

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
     * Returns a list of all loadBalancerListeners defined in the tenant.
     * 
     * @return A list of all loadBalancerListeners defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<LoadBalancerListener> getLoadBalancerListeners() throws ZoneException;

    /**
     * Create a LoadBalancerListener using the supplied LoadBalancerListener object as the pattern
     * 
     * @param loadBalancerListener
     *            The LoadBalancerListener to create. The LoadBalancerListener must contain the following attributes in
     *            order to be legal for creating a new LoadBalancerListener:
     *            <ul>
     *            <li>The id of the tenant who owns the Listener</li>
     *            <li>The protocol of the listener address. A valid value is TCP, HTTP, or HTTPS</li>
     *            <li>The port on which to listen to client traffic that is associated with the listener address</li>
     *            </ul>
     * @return A reference to the connected loadBalancerListener. The template loadBalancerListener (the argument
     *         passed) remains disconnected. The user is encouraged to use the referenced returned from this method for
     *         any further operations on the loadBalancerListener.
     * @throws ZoneException
     *             If the loadBalancerListener cannot be created
     */
    LoadBalancerListener createLoadBalancerListener(LoadBalancerListener loadBalancerListener) throws ZoneException;

    /**
     * Obtains the specified loadBalancerListener and returns the definition to the caller, if it exists.
     * 
     * @param id
     *            The id of the loadBalancerListener
     * @return The loadBalancerListener if it exists, or null if it does not exist
     * @throws ZoneException
     *             If the network service cannot be used for some reason or there was an error.
     */
    LoadBalancerListener getLoadBalancerListenerById(String id) throws ZoneException;

    /**
     * Returns the detailed information about load balancer listener definitions that match the name provided
     * 
     * @param name
     *            The name of the load balancer listener definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the service cannot accessed
     */
    List<LoadBalancerListener> getLoadBalancerListenerByName(String name) throws ZoneException;

    /**
     * Update a LoadBalancerListener using the supplied loadBalancerListener object as the pattern
     * 
     * @param loadBalancerListener
     *            The loadBalancerListener to update. The loadBalancerListener must contain the following attributes in
     *            order to be legal for updating loadBalancerListener:
     *            <ul>
     *            <li>The UUID for the listener</li>
     *            </ul>
     * @return A reference to the connected loadBalancerListener. The template loadBalancerListener (the argument
     *         passed) remains disconnected. The user is encouraged to use the referenced returned from this method for
     *         any further operations on the loadBalancerListener.
     * @throws ZoneException
     *             If the loadBalancerListener cannot be updated
     */
    LoadBalancerListener updateLoadBalancerListener(LoadBalancerListener loadBalancerListener) throws ZoneException;

    /**
     * Delete the specified loadBalancerListener using it's id.
     * 
     * @param loadBalancerListener
     *            The loadBalancerListener id to be deleted.
     * @throws ZoneException
     *             If the loadBalancerListener does not exist or cannot be deleted for some reason.
     */
    void deleteLoadBalancerListener(LoadBalancerListener loadBalancerListener) throws ZoneException;

    /**
     * Returns a list of all loadBalancerHealthMonitors defined in the tenant.
     * 
     * @return A list of all loadBalancerHealthMonitors defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<LoadBalancerHealthMonitor> getLoadBalancerHealthMonitors() throws ZoneException;

    /**
     * Create a LoadBalancerHealthMonitor using the supplied loadBalancerHealthMonitor object as the pattern
     * 
     * @param loadBalancerHealthMonitor
     *            The LoadBalancerHealthMonitor to create. The LoadBalancerHealthMonitor must contain the following
     *            attributes in order to be legal for creating a new loadBalancerHealthMonitor:
     *            <ul>
     *            <li>The id of the tenant who owns the monitor</li>
     *            <li>The type of probe, which is PING, TCP, HTTP, or HTTPS, that is sent by the load balancer to verify
     *            the member state</li>
     *            <li>The time, in seconds, between sending probes to members
     *            <li>
     *            <li>Time in seconds to timeout each probe
     *            <li>
     *            <li>Maximum consecutive health probe tries</li>
     *            </ul>
     * @return A reference to the connected loadBalancerHealthMonitor. The template loadBalancerHealthMonitor (the
     *         argument passed) remains disconnected. The user is encouraged to use the referenced returned from this
     *         method for any further operations on the loadBalancerHealthMonitor.
     * @throws ZoneException
     *             If the loadBalancerHealthMonitor cannot be created
     */
    LoadBalancerHealthMonitor createLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor)
        throws ZoneException;

    /**
     * Returns the detailed information about loadBalancerHealthMonitor definition that match the id provided
     * 
     * @param id
     *            The id of the loadBalancerHealthMonitor definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the loadBalancerHealthMonitor cannot accessed
     */
    LoadBalancerHealthMonitor getLoadBalancerHealthMonitorById(String id) throws ZoneException;

    /**
     * Update a LoadBalancerHealthMonitor using the supplied LoadBalancerHealthMonitor object as the pattern
     * 
     * @param loadBalancerHealthMonitor
     *            The LoadBalancerHealthMonitor to update. The LoadBalancerHealthMonitor must contain the following
     *            attributes in order to be legal for updating LoadBalancerHealthMonitor:
     *            <ul>
     *            <li>The time, in seconds, between sending probes to members</li>
     *            <li>The HTTP path of the request sent by the monitor to test the health of a member</li>
     *            </ul>
     * @return A reference to the connected loadBalancerHealthMonitor. The template loadBalancerHealthMonitor (the
     *         argument passed) remains disconnected. The user is encouraged to use the referenced returned from this
     *         method for any further operations on the loadBalancerHealthMonitor.
     * @throws ZoneException
     *             If the loadBalancerHealthMonitor cannot be updated
     */
    LoadBalancerHealthMonitor updateLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor)
        throws ZoneException;

    /**
     * Delete the specified loadBalancerHealthMonitor using it's id.
     * 
     * @param loadBalancerHealthMonitor
     *            The loadBalancerHealthMonitor id to be deleted.
     * @throws ZoneException
     *             If the loadBalancerHealthMonitor does not exist or cannot be deleted for some reason.
     */
    void deleteLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor) throws ZoneException;

    /**
     * Returns a list of all loadBalancerPools defined in the tenant.
     * 
     * @return A list of all loadBalancerPools defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<LoadBalancerPool> getLoadBalancerPools() throws ZoneException;

    /**
     * Create a LoadBalancerPool using the supplied LoadBalancerPool object as the pattern
     * 
     * @param loadBalancerPool
     *            The LoadBalancerPool to create. The loadBalancerPool must contain the following attributes in order to
     *            be legal for creating a new host:
     *            <ul>
     *            <li>The id of the tenant who owns the pool</li>
     *            <li>The protocol of the pool. A valid value is TCP, HTTP, or HTTPS</li>
     *            <li>The load-balancer algorithm, which is round-robin, least-connections, and so on</li>
     *            </ul>
     * @return A reference to the connected loadBalancerPool. The template loadBalancerPool (the argument passed)
     *         remains disconnected. The user is encouraged to use the referenced returned from this method for any
     *         further operations on the loadBalancerPool.
     * @throws ZoneException
     *             If the loadBalancerPool cannot be created
     */
    LoadBalancerPool createLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException;

    /**
     * Returns the detailed information about loadBalancerPool definitions that match the id provided
     * 
     * @param id
     *            The id of the loadBalancerPool definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the loadBalancerPool cannot accessed
     */
    LoadBalancerPool getLoadBalancerPoolById(String id) throws ZoneException;

    /**
     * Returns the detailed information about load balancer pool definitions that match the name provided
     * 
     * @param name
     *            The name of the load balancer pool definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the service cannot accessed
     */
    List<LoadBalancerPool> getLoadBalancerPoolByName(String name) throws ZoneException;

    /**
     * Update a LoadBalancerPool using the supplied LoadBalancerPool object as the pattern
     * 
     * @param loadBalancerPool
     *            The LoadBalancerPool to update. The LoadBalancerPool must contain the following attributes in order to
     *            be legal for updating:
     *            <ul>
     *            <li>The id of the pool</li>
     *            </ul>
     * @return A reference to the connected loadBalancerPool. The template loadBalancerPool (the argument passed)
     *         remains disconnected. The user is encouraged to use the referenced returned from this method for any
     *         further operations on the loadBalancerPool.
     * @throws ZoneException
     *             If the loadBalancerPool cannot be updated
     */
    LoadBalancerPool updateLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException;

    /**
     * Delete the specified loadBalancerPool using it's id.
     * 
     * @param loadBalancerPool
     *            The loadBalancerPool to be deleted.
     * @throws ZoneException
     *             If the loadBalancerPool does not exist or cannot be deleted for some reason.
     */
    void deleteLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException;

    /**
     * Returns a list of all loadBalancerMembers defined in the tenant.
     * 
     * @return A list of all loadBalancerMembers defined
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<LoadBalancerMember> getLoadBalancerMembers() throws ZoneException;

    /**
     * Create a LoadBalancerMember using the supplied LoadBalancerMember object as the pattern
     * 
     * @param loadBalancerMember
     *            The LoadBalancerMember to create. The LoadBalancerMember must contain the following attributes in
     *            order to be legal for creating a new host:
     *            <ul>
     *            <li>The id of the tenant who owns the member</li>
     *            <li>The IP address of the member</li>
     *            <li>The port on which the application is hosted</li>
     *            <li>Subnet in which to access this member</li>
     *            </ul>
     * @return A reference to the connected loadBalancerMember. The template loadBalancerMember (the argument passed)
     *         remains disconnected. The user is encouraged to use the referenced returned from this method for any
     *         further operations on the loadBalancerMember.
     * @throws ZoneException
     *             If the loadBalancerMember cannot be created
     */
    LoadBalancerMember createLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException;

    /**
     * Returns the detailed information about loadBalancerMember definitions that match the id provided
     * 
     * @param id
     *            The id of the loadBalancerMember definition
     * @return List containing matching definitions
     * @throws ZoneException
     *             If the loadBalancerMember cannot accessed
     */
    LoadBalancerMember getLoadBalancerMemberById(String id) throws ZoneException;

    /**
     * Update LoadBalancerMember using the supplied LoadBalancerMember object as the pattern
     * 
     * @param loadBalancerMember
     *            The loadBalancerMember to update. The loadBalancerMember must contain the following attributes in
     *            order to be legal for creating a new host:
     *            <ul>
     *            <li>The id of the tenant who owns the member</li>
     *            </ul>
     * @return A reference to the connected loadBalancerMember. The template loadBalancerMember (the argument passed)
     *         remains disconnected. The user is encouraged to use the referenced returned from this method for any
     *         further operations on the loadBalancer.
     * @throws ZoneException
     *             If the loadBalancerMember cannot be created
     */
    LoadBalancerMember updateLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException;

    /**
     * Delete the specified loadBalancerMember using it's id.
     * 
     * @param loadBalancerMember
     *            The loadBalancerMember to be deleted.
     * @throws ZoneException
     *             If the loadBalancerMember does not exist or cannot be deleted for some reason.
     */
    void deleteLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException;

    /**
     * Associates a health monitor with a specified pool. .
     * 
     * @param poolId
     *            The UUID for the pool
     * @param healthMonitorId
     *            The heath monitor id
     * @throws ZoneException
     *             If cannot be associated
     */
    void associateLoadBalancerHealthMonitorWithPool(String poolId, String healthMonitorId) throws ZoneException;

    /**
     * Disassociate a health monitor with a specified pool. .
     * 
     * @param poolId
     *            The UUID for the pool
     * @param healthMonitorId
     *            The heath monitor id
     * @throws ZoneException
     *             If cannot be disassociated
     */
    void disassociateLoadBalancerHealthMonitorWithPool(String poolId, String healthMonitorId) throws ZoneException;

    /**
     * Returns a list of all VLANs
     * 
     * @return A list of all VLANs
     * @throws ZoneException
     *             If the list cannot be obtained or the service is not supported
     */
    List<Network> getVLANTransparency() throws ZoneException;

}
