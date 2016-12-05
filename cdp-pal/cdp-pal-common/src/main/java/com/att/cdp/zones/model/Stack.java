/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.StackService;

/**
 * This class represents a stack created on the provider by its orchestration service, if one exists.
 * 
 * @since Jan 16, 2015
 * @version $Id$
 */
public class Stack extends ModelObject {

    /**
     * The state of a stack object
     * 
     * @since Jan 26, 2015
     * @version $Id$
     */
    public enum Status {
        /**
         * The stack is active and usable
         */
        ACTIVE,

        /**
         * The stack no longer exists and has been deleted.
         */
        DELETED,

        /**
         * The stack creation has failed. The stack is unusable.
         */
        FAILED,

        /**
         * The operation requested on the stack is in progress. If a create had been requested, then the create is being
         * performed and has not yet completed or failed.
         */
        IN_PROGRESS,

        /**
         * The stack state cannot be determined. The provider returned an indication of status that we did not
         * understand.
         */
        INDETERMINATE,

        /**
         * The stack exists, but it is currently suspended and not functional
         */
        SUSPENDED;
    }

    /**
     * Serial version number for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The list of load balancers defined or referenced as part of the stack
     */
    private List<LoadBalancer> balancers = new ArrayList<>();
    
    /**
     * The list of load balancer pools defined or referenced as part of the stack
     */
    private List<LoadBalancerPool> loadBalancerPools = new ArrayList<>();
    
    /**
     * The list of load balancer listeners defined or referenced as part of the stack
     */
    private List<LoadBalancerListener> loadBalancerListeners = new ArrayList<>();
    

    /**
     * An optional description of this stack
     */
    private String description;

    /**
     * Any fault information associated with this stack
     */
    private Fault fault;

    /**
     * The list of firewalls that are part of or referenced by the stack
     */
    private List<Firewall> firewalls = new ArrayList<>();

    /**
     * The id of the stack
     */
    private String id;

    /**
     * The list of keypairs created or referenced by the stack
     */
    private List<KeyPair> keyPairs = new ArrayList<>();

    /**
     * The name of the stack
     */
    private String name;

    /**
     * The list of networks that are associated with the stack
     */
    private List<Network> networks = new ArrayList<>();

    /**
     * An optional collection of named parameters and their value used when the stack was instantiated, if used at all.
     */
    private Map<String, String> parameters = new HashMap<>();

    /**
     * The list of ports created or referenced by the stack
     */
    private List<Port> ports = new ArrayList<>();

    /**
     * The list of servers that are part of the stack
     */
    private List<Server> servers = new ArrayList<>();

    /**
     * An object that we can use as a mutex lock to protect the state of the object for fields that are not objects that
     * we can synchronize on.
     */
    private Object stateLock = new Object();

    /**
     * The state of the stack
     */
    private Status status = Status.INDETERMINATE;

    /**
     * The list of subnets defined or referenced by the stack
     */
    private List<Subnet> subnets = new ArrayList<>();

    /**
     * The list of volumes that are part of the stack
     */
    private List<Volume> volumes = new ArrayList<>();

    /**
     * Creates a disconnected model object to define a stack for creation.
     */
    public Stack() {
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Stack(Context context) {
        super(context);
    }

    /**
     * Adds a load balancer to the list of referenced load balancers.
     * 
     * @param balancer
     *            The load balancer to be added. If it already exists, it is not added again.
     */
    public void addBalancer(LoadBalancer balancer) {
        synchronized (balancers) {
            if (!balancers.contains(balancer)) {
                balancers.add(balancer);
            }
        }
    }
    
  

    /**
     * Adds a firewall to the list of referenced firewalls for the creation of the stack
     * 
     * @param firewall
     *            The firewall to be created or referenced. If it already exists, it is not added again.
     */
    public void addFirewall(Firewall firewall) {
        synchronized (firewalls) {
            if (!firewalls.contains(firewall)) {
                firewalls.add(firewall);
            }
        }
    }

    /**
     * Adds a keypair to the stack
     * 
     * @param keyPair
     *            The keypair to be added. If it already exists, it is not added again.
     */
    public void addKeyPair(KeyPair keyPair) {
        synchronized (keyPairs) {
            if (!keyPairs.contains(keyPair)) {
                keyPairs.add(keyPair);
            }
        }
    }

    /**
     * Adds a network to the list of associated networks.
     * 
     * @param network
     *            The network to be added. If it already exists, it is not added again.
     */
    public void addNetwork(Network network) {
        synchronized (networks) {
            if (!networks.contains(network)) {
                networks.add(network);
            }
        }
    }

    /**
     * Adds the indicated port to the set of ports associated with this stack
     * 
     * @param port
     *            The port to be added. If it already exists, it is not added again.
     */
    public void addPort(Port port) {
        synchronized (ports) {
            if (!ports.contains(port)) {
                ports.add(port);
            }
        }
    }

    /**
     * Adds the indicated server to the set of servers that are part of this stack
     * 
     * @param server
     *            The server to be part of the stack. If it already exists, it is not added again.
     */
    public void addServer(Server server) {
        synchronized (servers) {
            if (!servers.contains(server)) {
                servers.add(server);
            }
        }
    }

    /**
     * Adds the indicated subnet to the stack for construction
     * 
     * @param subnet
     *            The subnet to be added. If it already exists, it is not added again
     */
    public void addSubnet(Subnet subnet) {
        synchronized (subnets) {
            if (!subnets.contains(subnet)) {
                subnets.add(subnet);
            }
        }
    }

    /**
     * Adds the volume to the set for construction of the stack
     * 
     * @param volume
     *            The volume to be added. If it already exists, it is not added again.
     */
    public void addVolume(Volume volume) {
        synchronized (volumes) {
            if (!volumes.contains(volume)) {
                volumes.add(volume);
            }
        }
    }

    /**
     * Deletes the current stack from the provider. The stack must be a connected object in order to be deleted using
     * this method.
     * 
     * @param force
     *            True if the deletion is to be forced and the stack is to be removed regardless of the state of the
     *            resources defined in the stack. If true, and the stack references any resources that do not exist, the
     *            stack is still deleted and no errors are indicated.
     * @throws NotNavigableException
     *             If the stack object is not connected to a context.
     * @throws ResourceNotFoundException
     *             If the stack, or any resource referenced by the stack, could not be found and force was specified as
     *             false.
     * @throws NotSupportedException
     *             If the provider
     * @throws ZoneException
     *             If the stack could not be deleted because of some provider failure.
     */
    public void deleteStack(boolean force) throws NotNavigableException, ResourceNotFoundException,
        NotSupportedException, ZoneException {

        notConnectedError();
    }

    /**
     * @return the value of balancers
     */
    public List<LoadBalancer> getBalancers() {
        synchronized (balancers) {
            return balancers;
        }
    }
    
   

    /**
     * JavaBean accessor to obtain the value of description
     * 
     * @return the description value
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the value of fault
     */
    public Fault getFault() {
        synchronized (stateLock) {
            return fault;
        }
    }

    /**
     * @return the value of firewalls
     */
    public List<Firewall> getFirewalls() {
        synchronized (firewalls) {
            return firewalls;
        }
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * @return the value of keyPairs
     */
    public List<KeyPair> getKeyPairs() {
        synchronized (keyPairs) {
            return keyPairs;
        }
    }

    /**
     * @return the value of name
     */
    public String getName() {
        synchronized (stateLock) {
            return name;
        }
    }

    /**
     * @return the value of networks
     */
    public List<Network> getNetworks() {
        synchronized (networks) {
            return networks;
        }
    }

    /**
     * JavaBean accessor to obtain the value of parameters
     * 
     * @return the parameters value
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @return the value of ports
     */
    public List<Port> getPorts() {
        return ports;
    }

    /**
     * @return the value of servers
     */
    public List<Server> getServers() {
        synchronized (servers) {
            return servers;
        }
    }

    /**
     * Provides a wrapper for the service method {@link StackService#getStacks()}
     * 
     * @return A list of stacks on this provider
     * @throws ZoneException
     *             If the model is not connected
     * @see com.att.cdp.zones.StackService#getStacks()
     */
    public List<Stack> getStacks() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * @return the value of stateLock
     */
    public Object getStateLock() {
        return stateLock;
    }

    /**
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the value of subnets
     */
    public List<Subnet> getSubnets() {
        synchronized (subnets) {
            return subnets;
        }
    }

    /**
     * @return the value of volumes
     */
    public List<Volume> getVolumes() {
        synchronized (volumes) {
            return volumes;
        }
    }

    /**
     * This method informs the provider implementation to refresh this stack object with the current state as defined on
     * the provider.
     * 
     * @throws ZoneException
     *             If the stack cannot be refreshed
     */
    public void refresh() throws ZoneException {
        notConnectedError();
    }

    /**
     * Removes a load balancer from the list
     * 
     * @param balancer
     *            The load balancer to be removed
     * @return True if the load balancer existed and was removed, false if it did not exist
     * @throws NotNavigableException
     *             If the model object is not connected to a context
     */
    public boolean removeBalancer(LoadBalancer balancer) throws NotNavigableException {
        synchronized (balancers) {
            return balancers.remove(balancer);
        }

    }

    /**
     * Removes a firewall from the stack
     * 
     * @param firewall
     *            The firewall to be removed
     * @return True if the firewall existed and was removed, false if it did not exist
     */
    public boolean removeFirewall(Firewall firewall) {
        synchronized (firewalls) {
            return firewalls.remove(firewall);
        }
    }

    /**
     * Removes a keypair from the stack
     * 
     * @param keyPair
     *            The keypair to be removed
     * @return True if the load balancer existed and was removed, false if it did not exist
     */
    public boolean removeKeyPair(KeyPair keyPair) {
        synchronized (keyPairs) {
            return keyPairs.remove(keyPair);
        }
    }

    /**
     * Removes the indicated network from the set of associated networks for this stack
     * 
     * @param network
     *            The network to be removed
     * @return True if the network existed and was removed, false if it did not exist
     */
    public boolean removeNetwork(Network network) {
        synchronized (networks) {
            return networks.remove(network);
        }
    }

    /**
     * Removes the indicated port from the associated set of ports
     * 
     * @param port
     *            The port to be removed
     * @return True if the port existed and was removed, false if it did not exist
     */
    public boolean removePort(Port port) {
        synchronized (ports) {
            return ports.remove(port);
        }
    }

    /**
     * Removes the server from the association with the stack
     * 
     * @param server
     *            The server to be removed
     * @return True if the server existed and was removed, false if it did not exist.
     */
    public boolean removeServer(Server server) {
        synchronized (servers) {
            return servers.remove(server);
        }
    }

    /**
     * Removes the indicated volume from the stack
     * 
     * @param volume
     *            The volume to be removed
     * @return True if the volume existed and was removed, false if it did not exist.
     */
    public boolean removeVolume(Volume volume) {
        synchronized (volumes) {
            return volumes.remove(volume);
        }
    }

    /**
     * @param balancers
     *            The value of balancers (of type List<LoadBalancer>)
     */
    public void setBalancers(List<LoadBalancer> balancers) {
        synchronized (balancers) {
            if (balancers != null) {
                for (LoadBalancer balancer : balancers) {
                    if (!this.balancers.contains(balancer)) {
                        this.balancers.add(balancer);
                    }
                }
            }
        }
    }

    /**
     * Standard JavaBean mutator method to set the value of description
     * 
     * @param description
     *            the value to be set into description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param fault
     *            the value for fault
     */
    public void setFault(Fault fault) {
        this.fault = fault;
    }

    /**
     * @param firewalls
     *            The value of firewalls (of type List<Firewall>)
     */
    public void setFirewalls(List<Firewall> firewalls) {
        synchronized (this.firewalls) {
            if (firewalls != null) {
                for (Firewall firewall : firewalls) {
                    if (!this.firewalls.contains(firewall)) {
                        this.firewalls.add(firewall);
                    }
                }
            }
        }
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param keyPairs
     *            The value of keyPairs (of type List<KeyPair>)
     */
    public void setKeyPairs(List<KeyPair> keyPairs) {
        synchronized (this.keyPairs) {
            if (keyPairs != null) {
                for (KeyPair keyPair : keyPairs) {
                    if (!this.keyPairs.contains(keyPair)) {
                        this.keyPairs.add(keyPair);
                    }
                }
            }
        }
    }

    /**
     * @param name
     *            The value of name (of type String)
     */
    public void setName(String name) {
        synchronized (stateLock) {
            this.name = name;
        }
    }

    /**
     * @param networks
     *            The value of networks (of type List<Network>)
     */
    public void setNetworks(List<Network> networks) {
        synchronized (this.networks) {
            if (networks != null) {
                for (Network network : networks) {
                    if (!this.networks.contains(network)) {
                        this.networks.add(network);
                    }
                }
            }
        }
    }

    /**
     * Standard JavaBean mutator method to set the value of parameters
     * 
     * @param parameters
     *            the value to be set into parameters
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param ports
     *            The value of ports (of type List<Port>)
     */
    public void setPorts(List<Port> ports) {
        synchronized (this.ports) {
            if (ports != null) {
                for (Port port : ports) {
                    if (!this.ports.contains(port)) {
                        this.ports.add(port);
                    }
                }
            }
        }
    }

    /**
     * @param servers
     *            The value of servers (of type List<Server>)
     */
    public void setServers(List<Server> servers) {
        synchronized (this.servers) {
            if (servers != null) {
                for (Server server : servers) {
                    if (!this.servers.contains(server)) {
                        this.servers.add(server);
                    }
                }
            }
        }
    }

    /**
     * @param stateLock
     *            the value for stateLock
     */
    public void setStateLock(Object stateLock) {
        this.stateLock = stateLock;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param subnets
     *            The value of subnets (of type List<Subnet>)
     */
    public void setSubnets(List<Subnet> subnets) {
        synchronized (this.subnets) {
            if (subnets != null) {
                for (Subnet subnet : subnets) {
                    if (!this.subnets.contains(subnet)) {
                        this.subnets.add(subnet);
                    }
                }
            }
        }
    }

    /**
     * @param volumes
     *            The value of volumes (of type List<Volume>)
     */
    public void setVolumes(List<Volume> volumes) {
        synchronized (this.volumes) {
            if (volumes != null) {
                for (Volume volume : volumes) {
                    if (!this.volumes.contains(volume)) {
                        this.volumes.add(volume);
                    }
                }
            }
        }
    }
    
    /**
     * Adds a load balancer pool to the list of referenced load balancer pools.
     * Az
     * @param pool
     *            The load balancer pool to be added. If it already exists, it is not added again.
     */
    public void addLoadBalancerPool(LoadBalancerPool loadBalancerPool) {
        synchronized (loadBalancerPools) {
            if (!loadBalancerPools.contains(loadBalancerPool)) {
            	loadBalancerPools.add(loadBalancerPool);
            }
        }
    }
    
    /**
     * Adds a load balancer listener to the list of referenced load balancer listeners.
     * 
     * @param pool
     *            The load balancer listener to be added. If it already exists, it is not added again.
     */
    public void addLoadBalancerListener(LoadBalancerListener loadBalancerListener) {
        synchronized (loadBalancerListeners) {
            if (!loadBalancerListeners.contains(loadBalancerListener)) {
            	loadBalancerListeners.add(loadBalancerListener);
            }
        }
    }
    
    /**
     * @return the value of load balancer pools
     */
    public List<LoadBalancerPool> getLoadBalancerPools() {
        synchronized (loadBalancerPools) {
            return loadBalancerPools;
        }
    }
    
    /**
     * @return the value of load balancer listeners
     */
    public List<LoadBalancerListener> getLoadBalancerListeners() {
        synchronized (loadBalancerListeners) {
            return loadBalancerListeners;
        }
    }
}
