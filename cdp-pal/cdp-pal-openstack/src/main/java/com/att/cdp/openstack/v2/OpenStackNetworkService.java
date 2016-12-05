/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.connectors.QuantumConnector;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.openstack.model.OpenStackLoadBalancerHealthMonitor;
import com.att.cdp.openstack.model.OpenStackLoadBalancerMember;
import com.att.cdp.openstack.model.OpenStackLoadBalancerPool;
import com.att.cdp.openstack.model.OpenStackLoadBalancerVIP;
import com.att.cdp.openstack.model.OpenStackNetwork;
import com.att.cdp.openstack.model.OpenStackPort;
import com.att.cdp.openstack.model.OpenStackRouter;
import com.att.cdp.openstack.model.OpenStackSubnet;
import com.att.cdp.openstack.util.ExceptionMapper;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkMetadata;
import com.att.cdp.zones.model.LoadBalancer;
import com.att.cdp.zones.model.LoadBalancerHealthMonitor;
import com.att.cdp.zones.model.LoadBalancerListener;
import com.att.cdp.zones.model.LoadBalancerMember;
import com.att.cdp.zones.model.LoadBalancerPool;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Route;
import com.att.cdp.zones.model.Router;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Subnet;
import com.att.cdp.zones.spi.AbstractNetwork;
import com.att.cdp.zones.spi.RequestState;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.base.client.OpenStackConnectException;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.api.ExtensionsResource;
import com.woorea.openstack.nova.api.extensions.FloatingIpsExtension;
import com.woorea.openstack.nova.model.Extension;
import com.woorea.openstack.nova.model.FixedIp;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.FloatingIps;
import com.woorea.openstack.nova.model.InterfaceAttachment;
import com.woorea.openstack.nova.model.InterfaceAttachments;
import com.woorea.openstack.nova.model.Server.Addresses;
import com.woorea.openstack.nova.model.Server.Addresses.Address;
import com.woorea.openstack.nova.model.Servers;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.api.LoadBalancerResource;
import com.woorea.openstack.quantum.api.NetworksResource;
import com.woorea.openstack.quantum.api.PortsResource;
import com.woorea.openstack.quantum.api.RoutersResource;
import com.woorea.openstack.quantum.api.SubnetsResource;
import com.woorea.openstack.quantum.api.VLANResource;
import com.woorea.openstack.quantum.model.Subnet.IpVersion;

/**
 * @since Oct 17, 2013
 * @version $Id$
 */
public class OpenStackNetworkService extends AbstractNetwork {

    @SuppressWarnings("nls")
    private static final String FIREWALL_EXTENSION = "fwaas";

    @SuppressWarnings("nls")
    private static final String LOAD_BALANCER_EXTENSION = "lbaas";

    @SuppressWarnings("nls")
    private static final String QUOTA_EXTENSION = "quotas";

    @SuppressWarnings("nls")
    private static final String ROUTER_EXTENSION = "router";

    @SuppressWarnings("nls")
    private static final String SECURITY_GROUP_EXTENSION = "security-group";

    @SuppressWarnings("nls")
    private static final String VPN_EXTENSION = "vpnaas";

    /**
     * The OpenStack nova connector
     */
    private NovaConnector novaConnector;

    /**
     * The OpenStack quantum connector
     */
    private QuantumConnector quantumConnector;

    /**
     * Create the service for the supplied context
     * 
     * @param context
     *            The context we are servicing
     */
    public OpenStackNetworkService(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#assignIpAddress(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void assignIpAddress(Server server, String address) throws ZoneException {
        checkArg(server, "server");
        checkArg(address, "address");
        assignIpAddress(server.getId(), address);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#assignIpAddress(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void assignIpAddress(String serverId, String address) throws ZoneException {
        checkArg(serverId, "serverId");
        checkArg(address, "address");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVER, serverId);
        RequestState.put(RequestState.IPADDRESS, address);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        ComputeService compute = context.getComputeService();
        compute.assignIpAddress(serverId, address);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#assignIpAddressFromPool(com.att.cdp.zones.model.Server)
     */
    @SuppressWarnings("nls")
    @Override
    public String assignIpAddressFromPool(Server server) throws ZoneException {
        checkArg(server, "server");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVER, server.getName());
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        String ip;
        try {
            ip = reserveFreeFloatingIPAddress(null);
            if (ip != null) {
                assignIpAddress(server, ip);
                return ip;
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_RESOURCE_UNAVAILABLE, "Floating IP Address",
            context.getProvider().getName()));
    }

    /**
     * @see com.att.cdp.zones.NetworkService#assignIpAddressFromPool(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public String assignIpAddressFromPool(Server server, String pool) throws ZoneException {
        checkArg(server, "server");
        checkArg(pool, "pool");
        return assignIpAddressFromPool(server.getId(), pool);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#assignIpAddressFromPool(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public String assignIpAddressFromPool(String serverId, String pool) throws ZoneException {
        checkArg(serverId, "serverId");
        checkArg(pool, "pool");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVER, serverId);
        RequestState.put(RequestState.POOL, pool);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        Nova client = novaConnector.getClient();
        FloatingIpsExtension extension = client.floatingIps();
        if (extension == null) {
            throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_UNSUPPORTED_OPERATION,
                "getAvailableAddresses", context.getProvider().getName()));
        }

        try {
            String ip = reserveFreeFloatingIPAddress(pool);
            if (ip != null) {
                assignIpAddress(serverId, ip);
                return ip;
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_RESOURCE_UNAVAILABLE, "Floating IP Address",
            context.getProvider().getName()));
    }

    /**
     * @see com.att.cdp.zones.NetworkService#associateLoadBalancerHealthMonitorWithPool(java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void associateLoadBalancerHealthMonitorWithPool(String poolId, String healthMonitorId) throws ZoneException {
        checkArg(poolId, "poolId");
        checkArg(healthMonitorId, "healthMonitorId");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, poolId);
        RequestState.put(RequestState.LOADBALANCERHEALTHMONITOR, healthMonitorId);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.lbaas().Pool().associateMonitor(poolId, healthMonitorId).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * This is a helper method used to construct the Nova service object and setup the environment to access the
     * OpenStack compute service (Nova).
     * 
     * @throws NotLoggedInException
     *             If the user is not logged in
     * @throws ContextClosedException
     *             If the user attempts an operation after the context is closed
     */
    private void connect() throws NotLoggedInException, ContextClosedException {
        checkLogin();
        checkOpen();
        Context context = getContext();
        OpenStackContext osContext = (OpenStackContext) context;
        quantumConnector = osContext.getQuantumConnector();
        novaConnector = osContext.getNovaConnector();
        ((OpenStackContext) context).refreshIfStale(quantumConnector);
        ((OpenStackContext) context).refreshIfStale(novaConnector);
    }

    @SuppressWarnings("nls")
    @Override
    public LoadBalancer createLoadBalancer(LoadBalancer loadBalancer) throws ZoneException {
        throw new ZoneException("Not yet implemented");
    }

    /**
     * @see com.att.cdp.zones.NetworkService#createLoadBalancerHealthMonitor(com.att.cdp.zones.model.LoadBalancerHealthMonitor)
     */
    @Override
    public LoadBalancerHealthMonitor
        createLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor) throws ZoneException {

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor loadBalancerHealthMonitorToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor();

            if (loadBalancerHealthMonitor.getType() != null) {
                loadBalancerHealthMonitorToCreate.setType(loadBalancerHealthMonitor.getType().name());
            }
            loadBalancerHealthMonitorToCreate.setDelay(loadBalancerHealthMonitor.getDelay());
            loadBalancerHealthMonitorToCreate.setTimeout(loadBalancerHealthMonitor.getTimeout());
            loadBalancerHealthMonitorToCreate.setMaxRetries(loadBalancerHealthMonitor.getMaxRetries());
            loadBalancerHealthMonitorToCreate.setUrlPath(loadBalancerHealthMonitor.getUrlPath());
            loadBalancerHealthMonitorToCreate.setExpectedCodes(loadBalancerHealthMonitor.getExpectedCodes());

            com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor openstackLbMonitor =
                client.lbaas().HealthMonitor().create(loadBalancerHealthMonitorToCreate).execute();

            return new OpenStackLoadBalancerHealthMonitor(context, openstackLbMonitor);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#createLoadBalancerListener(com.att.cdp.zones.model.LoadBalancerListener)
     */
    @Override
    public LoadBalancerListener createLoadBalancerListener(LoadBalancerListener listener) throws ZoneException {

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERLISTENER, listener.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerVIP loadBalancerVIPToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerVIP();

            loadBalancerVIPToCreate.setName(listener.getName());
            // TODO Description
            loadBalancerVIPToCreate.setSubnetId(listener.getSubnetId());
            loadBalancerVIPToCreate.setAddress(listener.getIpAddress());
            if (listener.getProtocol() != null) {
                loadBalancerVIPToCreate.setProtocol(listener.getProtocol().name());
            }
            loadBalancerVIPToCreate.setPort(listener.getProtocolPort());
            loadBalancerVIPToCreate.setPoolId(listener.getPoolId());
            // TODO session persistence
            loadBalancerVIPToCreate.setConnectionLimit(listener.getConnectionLimit());
            loadBalancerVIPToCreate.setState(listener.isAdminStateUp());

            com.woorea.openstack.quantum.model.LoadBalancerVIP openstackLbVip =
                client.lbaas().VIP().create(loadBalancerVIPToCreate).execute();

            return new OpenStackLoadBalancerVIP(context, openstackLbVip);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#createLoadBalancerMember(com.att.cdp.zones.model.LoadBalancerMember)
     */
    @Override
    public LoadBalancerMember createLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException {

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerMember loadBalancerMemberToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerMember();

            loadBalancerMemberToCreate.setAddress(loadBalancerMember.getAddress());
            loadBalancerMemberToCreate.setPort(loadBalancerMember.getProtocolPort());
            loadBalancerMemberToCreate.setPoolId(loadBalancerMember.getPoolId());
            loadBalancerMemberToCreate.setWeight(loadBalancerMember.getWeight());
            com.woorea.openstack.quantum.model.LoadBalancerMember openstackLbMember =
                client.lbaas().Member().create(loadBalancerMemberToCreate).execute();

            return new OpenStackLoadBalancerMember(context, openstackLbMember);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#createLoadBalancerPool(com.att.cdp.zones.model.LoadBalancerPool)
     */
    @Override
    public LoadBalancerPool createLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException {

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, loadBalancerPool.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerPool loadBalancerPoolToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerPool();

            loadBalancerPoolToCreate.setName(loadBalancerPool.getName());
            if (loadBalancerPool.getProtocol() != null) {
                loadBalancerPoolToCreate.setProtocol(loadBalancerPool.getProtocol().name());
            }
            loadBalancerPoolToCreate.setSubnetId(loadBalancerPool.getSubnetId());
            if (loadBalancerPool.getLbAlgorithm() != null) {
                loadBalancerPoolToCreate.setMethod(loadBalancerPool.getLbAlgorithm().name());
            }

            com.woorea.openstack.quantum.model.LoadBalancerPool openstackLbPool =
                client.lbaas().Pool().create(loadBalancerPoolToCreate).execute();

            return new OpenStackLoadBalancerPool(context, openstackLbPool);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public Network createNetwork(Network network) throws ZoneException {
        checkArg(network, "network");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.NETWORK, network.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.Network netcreate = new com.woorea.openstack.quantum.model.Network();
            netcreate.setName(network.getName());
            netcreate.setAdminStateUp(true);

            com.woorea.openstack.quantum.model.Network openstackNetwork = client.networks().create(netcreate).execute();
            return new OpenStackNetwork(context, openstackNetwork);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    @Override
    public Subnet createSubnet(Subnet subnet) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SUBNET, subnet.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.Subnet subnetToCreate = new com.woorea.openstack.quantum.model.Subnet();

            subnetToCreate.setName(subnet.getName());
            subnetToCreate.setNetworkId(subnet.getNetwork());
            subnetToCreate.setCidr(subnet.getRouting());
            subnetToCreate.setGw(subnet.getGatewayIp());
            subnetToCreate.setEnableDHCP(subnet.isDhcp());
            subnetToCreate.setDnsNames(subnet.getDns());

            if (subnet.isIpv4()) {
                subnetToCreate.setIpversion(IpVersion.IPV4);
            } else {
                subnetToCreate.setIpversion(IpVersion.IPV6);
            }

            if (!subnet.getHostRoutes().isEmpty()) {
                List<com.woorea.openstack.quantum.model.Route> routesToCreate = new ArrayList<>();

                List<Route> routes = subnet.getHostRoutes();
                for (Route route : routes) {
                    com.woorea.openstack.quantum.model.Route newRoute = new com.woorea.openstack.quantum.model.Route();
                    newRoute.setDestination(route.getDestination());
                    newRoute.setNexthop(route.getNexthop());
                    routesToCreate.add(newRoute);
                }

                subnetToCreate.setHostRoutes(routesToCreate);
            }

            com.woorea.openstack.quantum.model.Subnet openstackSubnet =
                client.subnets().create(subnetToCreate).execute();

            return new OpenStackSubnet(context, openstackSubnet);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#deleteLoadBalancerHealthMonitor(com.att.cdp.zones.model.LoadBalancerHealthMonitor)
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor)
        throws ZoneException {
        checkArg(loadBalancerHealthMonitor, "loadBalancerHealthMonitor");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERHEALTHMONITOR, loadBalancerHealthMonitor.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.lbaas().HealthMonitor().delete(loadBalancerHealthMonitor.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * @see com.att.cdp.zones.NetworkService#deleteLoadBalancerListener(com.att.cdp.zones.model.LoadBalancerListener)
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteLoadBalancerListener(LoadBalancerListener loadBalancerListener) throws ZoneException {
        checkArg(loadBalancerListener, "loadBalancerListener");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERLISTENER, loadBalancerListener.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.lbaas().VIP().delete(loadBalancerListener.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * @see com.att.cdp.zones.NetworkService#deleteLoadBalancerMember(com.att.cdp.zones.model.LoadBalancerMember)
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException {
        checkArg(loadBalancerMember, "loadBalancerMember");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERMEMBER, loadBalancerMember.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.lbaas().Member().delete(loadBalancerMember.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * @see com.att.cdp.zones.NetworkService#deleteLoadBalancerPool(com.att.cdp.zones.model.LoadBalancerPool)
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException {
        checkArg(loadBalancerPool, "loadBalancerPool");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, loadBalancerPool.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.Pool resource = client.lbaas().Pool();
            LoadBalancerResource.Pool.Delete deleRes = resource.delete(loadBalancerPool.getId());
            deleRes.execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * Delete the specified network using it's id.
     * 
     * @param network
     *            The network to be deleted.
     * @throws ZoneException
     *             If the network does not exist or cannot be deleted for some reason.
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteNetwork(Network network) throws ZoneException {
        checkArg(network, "network");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.NETWORK, network.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.networks().delete(network.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
    }

    /**
     * Delete the specified network using it's id.
     * 
     * @param subnet
     *            The subnet to be deleted.
     * @throws ZoneException
     *             If the network does not exist or cannot be deleted for some reason.
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteSubnet(Subnet subnet) throws ZoneException {
        checkArg(subnet, "subnet");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SUBNET, subnet.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.subnets().delete(subnet.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
    }

    /**
     * @see com.att.cdp.zones.NetworkService#disassociateLoadBalancerHealthMonitorWithPool(java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void disassociateLoadBalancerHealthMonitorWithPool(String poolId, String healthMonitorId)
        throws ZoneException {
        checkArg(poolId, "poolId");
        checkArg(healthMonitorId, "healthMonitorId");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, poolId);
        RequestState.put(RequestState.LOADBALANCERHEALTHMONITOR, healthMonitorId);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.lbaas().Pool().disassociateMonitor(poolId, healthMonitorId).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

    }

    /**
     * This method allows the caller to obtain a list of the available IP addresses from the Floating IP address (DNS)
     * pool.
     * 
     * @return The list of available IP Addresses that can be used to assign a floating IP address to a server.
     * @throws ZoneException
     *             If the service is not available or the pool cannot be obtained
     * @see com.att.cdp.zones.NetworkService#getAvailableAddresses(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<String> getAvailableAddresses(String pool) throws ZoneException {
        checkArg(pool, "pool");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.POOL, pool);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        ArrayList<String> addresses = new ArrayList<>();

        FloatingIpsExtension extension = novaConnector.getClient().floatingIps();
        if (extension == null) {
            throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_UNSUPPORTED_OPERATION,
                "getAvailableAddresses", context.getProvider().getName()));
        }

        try {
            FloatingIps ips = extension.list().execute();
            for (FloatingIp ip : ips) {
                if (ip.getPool().equalsIgnoreCase(pool)) {
                    String ipAddress = ip.getIp();
                    addresses.add(ipAddress);
                }
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return addresses;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getFloatingIpPools()
     */
    @SuppressWarnings("nls")
    @Override
    public List<String> getFloatingIpPools() throws ZoneException {
        HashSet<String> pools = new HashSet<>();

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        FloatingIpsExtension extension = novaConnector.getClient().floatingIps();
        if (extension == null) {
            throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_UNSUPPORTED_OPERATION,
                "getAvailableAddresses", context.getProvider().getName()));
        }

        try {
            for (FloatingIp ip : extension.list().execute()) {
                if (!pools.contains(ip.getPool())) {
                    pools.add(ip.getPool());
                }
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return Collections.list(Collections.enumeration(pools));
    }

    /**
     * @throws ZoneException
     * @see com.att.cdp.zones.NetworkService#getInterfaces(java.lang.String)
     */
    @Override
    public List<com.att.cdp.zones.model.Port> getInterfaces(String serverId) throws ZoneException {
        InterfaceAttachments list = null;
        com.att.cdp.zones.model.Port port = null;
        List<com.att.cdp.zones.model.Port> ports = new ArrayList<>();

        connect();
        trackRequest();
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        try {
            list = novaConnector.getClient().servers().listInterfaceAttachments(serverId).execute();
            if (list != null && list.getList() != null && list.getList().size() > 0) {
                for (InterfaceAttachment intf : list.getList()) {
                    port = new com.att.cdp.zones.model.Port();
                    port.setPortState(OpenStackPort.mapState(intf.getPortState()));
                    port.setMacAddr(intf.getMacAddress());
                    port.setId(intf.getPortId());
                    port.setSubnetId(intf.getNetworkId());
                    List<String> addresses = new ArrayList<>();
                    for (FixedIp ip : intf.getFixedIps()) {
                        addresses.add(ip.getIpAddress());
                    }
                    port.setAddresses(addresses);
                    ports.add(port);
                }
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return ports;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerHealthMonitorById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public LoadBalancerHealthMonitor getLoadBalancerHealthMonitorById(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERHEALTHMONITOR, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor n =
                quantumConnector.getClient().lbaas().HealthMonitor().show(id).execute();
            return new OpenStackLoadBalancerHealthMonitor(context, n);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerHealthMonitors()
     */
    @Override
    public List<LoadBalancerHealthMonitor> getLoadBalancerHealthMonitors() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<LoadBalancerHealthMonitor> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.HealthMonitor resource = client.lbaas().HealthMonitor();
            for (com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor loadBalancerHealthMonitor : resource
                .list().execute()) {
                list.add(new OpenStackLoadBalancerHealthMonitor(context, loadBalancerHealthMonitor));
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerListenerById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public LoadBalancerListener getLoadBalancerListenerById(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERLISTENER, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            com.woorea.openstack.quantum.model.LoadBalancerVIP n =
                quantumConnector.getClient().lbaas().VIP().show(id).execute();
            return new OpenStackLoadBalancerVIP(context, n);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerListenerByName(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<LoadBalancerListener> getLoadBalancerListenerByName(String name) throws ZoneException {
        checkArg(name, "name");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERLISTENER, name);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        List<LoadBalancerListener> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.VIP resource = client.lbaas().VIP();
            for (com.woorea.openstack.quantum.model.LoadBalancerVIP loadBalancerVIP : resource.list().execute()) {
                if (loadBalancerVIP.getName().equals(name)) {
                    list.add(new OpenStackLoadBalancerVIP(context, loadBalancerVIP));
                }

            }

        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerListeners()
     */
    @Override
    public List<LoadBalancerListener> getLoadBalancerListeners() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<LoadBalancerListener> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.VIP resource = client.lbaas().VIP();
            for (com.woorea.openstack.quantum.model.LoadBalancerVIP loadBalancerVIP : resource.list().execute()) {
                list.add(new OpenStackLoadBalancerVIP(context, loadBalancerVIP));
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerMemberById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public LoadBalancerMember getLoadBalancerMemberById(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERMEMBER, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            com.woorea.openstack.quantum.model.LoadBalancerMember n =
                quantumConnector.getClient().lbaas().Member().show(id).execute();
            return new OpenStackLoadBalancerMember(context, n);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerMembers()
     */
    @Override
    public List<LoadBalancerMember> getLoadBalancerMembers() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<LoadBalancerMember> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.Member resource = client.lbaas().Member();
            for (com.woorea.openstack.quantum.model.LoadBalancerMember loadBalancerMember : resource.list().execute()) {
                list.add(new OpenStackLoadBalancerMember(context, loadBalancerMember));
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerPoolById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public LoadBalancerPool getLoadBalancerPoolById(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            com.woorea.openstack.quantum.model.LoadBalancerPool n =
                quantumConnector.getClient().lbaas().Pool().show(id).execute();
            return new OpenStackLoadBalancerPool(context, n);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerPoolByName(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<LoadBalancerPool> getLoadBalancerPoolByName(String name) throws ZoneException {
        checkArg(name, "name");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, name);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        List<LoadBalancerPool> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.Pool resource = client.lbaas().Pool();
            for (com.woorea.openstack.quantum.model.LoadBalancerPool loadBalancerPool : resource.list().execute()) {
                if (loadBalancerPool.getName().equals(name)) {
                    list.add(new OpenStackLoadBalancerPool(context, loadBalancerPool));
                }

            }

        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getLoadBalancerPools()
     */
    @Override
    public List<LoadBalancerPool> getLoadBalancerPools() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<LoadBalancerPool> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            LoadBalancerResource.Pool resource = client.lbaas().Pool();
            for (com.woorea.openstack.quantum.model.LoadBalancerPool loadBalancerPool : resource.list().execute()) {
                list.add(new OpenStackLoadBalancerPool(context, loadBalancerPool));
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getMetadata()
     */
    @Override
    public NetworkMetadata getMetadata() throws ZoneException {
        OpenStackNetworkMetadata metadata = new OpenStackNetworkMetadata();
        // get the extensions
        List<Extension> extensions = getNetworkExtensions();
        for (Extension extension : extensions) {
            String alias = extension.getAlias();
            String description = extension.getDescription();
            // should never happen but to be safe
            if (alias == null) {
                continue;
            }
            // check alias extension and fill out metadata appropriately
            switch (alias) {
                case LOAD_BALANCER_EXTENSION:
                    metadata.setLoadBalancerSupported(true);
                    metadata.setLoadBalancerDescription(description);
                    break;
                case SECURITY_GROUP_EXTENSION:
                    metadata.setSecurityGroupSupported(true);
                    metadata.setSecurityGroupDescription(description);
                    break;
                case ROUTER_EXTENSION:
                    metadata.setRouterSupported(true);
                    metadata.setRouterDescription(description);
                    break;
                case FIREWALL_EXTENSION:
                    metadata.setFirewallSupported(true);
                    metadata.setFirewallDescription(description);
                    break;
                case QUOTA_EXTENSION:
                    metadata.setQuotaSupported(true);
                    metadata.setQuotaDescription(description);
                    break;
                case VPN_EXTENSION:
                    metadata.setVpnSupported(true);
                    metadata.setVpnDescription(description);
                    break;
                default:
                    break;
            }
        }
        return metadata;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getNetworkById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public Network getNetworkById(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.NETWORK, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            com.woorea.openstack.quantum.model.Network n = quantumConnector.getClient().networks().show(id).execute();
            return new OpenStackNetwork(context, n);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null; // for the compiler
    }

    /**
     * @return List of extensions
     * @throws ZoneException
     *             If the context cannot be used to obtain the list of extensions
     * @see com.att.cdp.zones.NetworkService#getNetworks()
     */
    @SuppressWarnings("nls")
    public List<Extension> getNetworkExtensions() throws ZoneException {
        connect();
        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<Extension> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            ExtensionsResource res = new ExtensionsResource(client);

            for (com.woorea.openstack.nova.model.Extension ext : res.list(false).execute()) {
                list.add(ext);
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getNetworks()
     */
    @SuppressWarnings("nls")
    @Override
    public List<Network> getNetworks() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<Network> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            NetworksResource resource = client.networks();
            for (com.woorea.openstack.quantum.model.Network net : resource.list().execute()) {
                list.add(new OpenStackNetwork(context, net));
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getNetworksByName(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<Network> getNetworksByName(String name) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.NETWORK, name);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        List<Network> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            NetworksResource resource = client.networks();
            for (com.woorea.openstack.quantum.model.Network net : resource.list().execute()) {
                if (net.getName().equals(name)) {
                    list.add(new OpenStackNetwork(context, net));
                }
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getPorts()
     */
    @SuppressWarnings("nls")
    @Override
    public List<com.att.cdp.zones.model.Port> getPorts() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        List<com.att.cdp.zones.model.Port> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            PortsResource resource = client.ports();
            for (com.woorea.openstack.quantum.model.Port p : resource.list().execute()) {
                list.add(new OpenStackPort(context, p));
            }

        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getPort(java.lang.String)
     */
    @Override
    public Port getPort(String id) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());
        RequestState.put(RequestState.PORT, id);
        com.woorea.openstack.quantum.model.Port p = null;

        try {
            Quantum client = quantumConnector.getClient();
            PortsResource resource = client.ports();
            p = resource.show(id).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        return new OpenStackPort(context, p);
    }

    /**
     * @return List<Router>
     * @throws ZoneException
     *             If the context cannot be used to get the list or routers
     * @see com.att.cdp.zones.NetworkService#getRouters()
     */
    @SuppressWarnings("nls")
    @Override
    public List<Router> getRouters() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<Router> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            RoutersResource resource = client.routers();

            for (com.woorea.openstack.quantum.model.Router openstackRouter : resource.list().execute().getList()) {
                Router r = new OpenStackRouter(context, openstackRouter);
                list.add(r);
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getSubnetById(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public Subnet getSubnetById(String id) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SUBNET, id);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            SubnetsResource resource = client.subnets();
            for (com.woorea.openstack.quantum.model.Subnet net : resource.list().execute()) {
                if (net.getId().equals(id)) {
                    return new OpenStackSubnet(context, net);
                }
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getSubnets()
     */
    @SuppressWarnings("nls")
    @Override
    public List<Subnet> getSubnets() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<Subnet> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            SubnetsResource resource = client.subnets();
            for (com.woorea.openstack.quantum.model.Subnet net : resource.list().execute()) {
                list.add(new OpenStackSubnet(context, net));

            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getSubnetsByName(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public List<Subnet> getSubnetsByName(String name) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SUBNET, name);
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        List<Subnet> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            SubnetsResource resource = client.subnets();
            for (com.woorea.openstack.quantum.model.Subnet net : resource.list().execute()) {
                if (net.getName().equals(name)) {
                    list.add(new OpenStackSubnet(context, net));
                }
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#getVLANTransparency()
     */
    @SuppressWarnings("nls")
    @Override
    public List<Network> getVLANTransparency() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        ArrayList<Network> list = new ArrayList<>();
        try {
            Quantum client = quantumConnector.getClient();
            VLANResource resource = client.vlans();
            for (com.woorea.openstack.quantum.model.Network net : resource.list().execute()) {
                list.add(new OpenStackNetwork(context, net));

            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return list;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#releaseIpAddress(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void releaseIpAddress(Server server, String assignedAddress) throws ZoneException {
        checkArg(server, "server");
        checkArg(assignedAddress, "assignedAddress");
        releaseIpAddress(server.getId(), assignedAddress);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#releaseIpAddress(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void releaseIpAddress(String serverId, String assignedAddress) throws ZoneException {
        checkArg(serverId, "serverId");
        checkArg(assignedAddress, "assignedAddress");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVER, serverId);
        RequestState.put(RequestState.IPADDRESS, assignedAddress);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, novaConnector.getEndpoint());

        ComputeService compute = context.getComputeService();
        compute.releaseIpAddress(serverId, assignedAddress);
        ((OpenStackContext) context).deallocateFloatingIP(assignedAddress);
    }

    /**
     * This method is used o determine which IP addresses in the floating ip address pool specified are free, and to
     * reserve the first one
     * 
     * @param pool
     *            The name of the pool to be searched, or null if we are searching all pools available to the tenant
     * @return The reserved IP address (or null if there are none available in the pool)
     * @throws OpenStackResponseException
     * @throws OpenStackConnectException
     */
    @SuppressWarnings("nls")
    private String reserveFreeFloatingIPAddress(String pool) throws OpenStackConnectException,
        OpenStackResponseException {
        Nova client = novaConnector.getClient();
        HashSet<String> available = new HashSet<>();
        Context context = getContext();

        FloatingIps ips = client.floatingIps().list().execute();
        for (FloatingIp ip : ips.getList()) {
            if (pool == null || pool.equalsIgnoreCase(ip.getPool())) {
                available.add(ip.getIp());
            }
        }

        Servers servers = client.servers().list(true).execute();
        for (com.woorea.openstack.nova.model.Server server : servers.getList()) {
            Addresses allocatedAddresses = server.getAddresses();
            Map<String, List<Address>> addressMap = allocatedAddresses.getAddresses();
            for (Map.Entry<String, List<Address>> entry : addressMap.entrySet()) {
                List<Address> addressList = entry.getValue();
                for (Address address : addressList) {
                    if (address.getType().equalsIgnoreCase("floating")) {
                        available.remove(address.getAddr());
                    }
                }
            }
        }

        if (!available.isEmpty()) {
            Iterator<String> it = available.iterator();
            while (it.hasNext()) {
                String ip = it.next();
                if (((OpenStackContext) context).allocateFloatingIP(ip)) {
                    return ip;
                }
            }
        }

        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#updateLoadBalancerHealthMonitor(com.att.cdp.zones.model.LoadBalancerHealthMonitor)
     */
    @SuppressWarnings("nls")
    @Override
    public LoadBalancerHealthMonitor
        updateLoadBalancerHealthMonitor(LoadBalancerHealthMonitor loadBalancerHealthMonitor) throws ZoneException {

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor loadBalancerHealthMonitorToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor();

            if (loadBalancerHealthMonitor.getType() != null) {
                loadBalancerHealthMonitorToCreate.setType(loadBalancerHealthMonitor.getType().name());
            }
            loadBalancerHealthMonitorToCreate.setDelay(loadBalancerHealthMonitor.getDelay());
            loadBalancerHealthMonitorToCreate.setTimeout(loadBalancerHealthMonitor.getTimeout());
            loadBalancerHealthMonitorToCreate.setMaxRetries(loadBalancerHealthMonitor.getMaxRetries());
            loadBalancerHealthMonitorToCreate.setUrlPath(loadBalancerHealthMonitor.getUrlPath());
            loadBalancerHealthMonitorToCreate.setExpectedCodes(loadBalancerHealthMonitor.getExpectedCodes());

            com.woorea.openstack.quantum.model.LoadBalancerHealthMonitor openstackLbMonitor =
                client.lbaas().HealthMonitor().update(loadBalancerHealthMonitorToCreate).execute();

            return new OpenStackLoadBalancerHealthMonitor(context, openstackLbMonitor);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#updateLoadBalancerListener(com.att.cdp.zones.model.LoadBalancerListener)
     */
    @Override
    public LoadBalancerListener updateLoadBalancerListener(LoadBalancerListener loadBalancerVIP) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERLISTENER, loadBalancerVIP.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerVIP loadBalancerVIPToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerVIP();

            loadBalancerVIPToCreate.setName(loadBalancerVIP.getName());
            // TODO Description
            loadBalancerVIPToCreate.setSubnetId(loadBalancerVIP.getSubnetId());
            loadBalancerVIPToCreate.setAddress(loadBalancerVIP.getIpAddress());
            if (loadBalancerVIP.getProtocol() != null) {
                loadBalancerVIPToCreate.setProtocol(loadBalancerVIP.getProtocol().name());
            }
            loadBalancerVIPToCreate.setPort(loadBalancerVIP.getProtocolPort());
            loadBalancerVIPToCreate.setPoolId(loadBalancerVIP.getPoolId());
            // TODO session persistence
            loadBalancerVIPToCreate.setConnectionLimit(loadBalancerVIP.getConnectionLimit());
            loadBalancerVIPToCreate.setState(loadBalancerVIP.isAdminStateUp());

            com.woorea.openstack.quantum.model.LoadBalancerVIP openstackLbVip =
                client.lbaas().VIP().update(loadBalancerVIPToCreate).execute();

            return new OpenStackLoadBalancerVIP(context, openstackLbVip);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#updateLoadBalancerMember(com.att.cdp.zones.model.LoadBalancerMember)
     */
    @Override
    public LoadBalancerMember updateLoadBalancerMember(LoadBalancerMember loadBalancerMember) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerMember loadBalancerMemberToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerMember();

            loadBalancerMemberToCreate.setAddress(loadBalancerMember.getAddress());
            loadBalancerMemberToCreate.setPort(loadBalancerMember.getProtocolPort());
            loadBalancerMemberToCreate.setSubnetId(loadBalancerMember.getSubnetId());

            com.woorea.openstack.quantum.model.LoadBalancerMember openstackLbMember =
                client.lbaas().Member().update(loadBalancerMemberToCreate).execute();

            return new OpenStackLoadBalancerMember(context, openstackLbMember);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.NetworkService#updateLoadBalancerPool(com.att.cdp.zones.model.LoadBalancerPool)
     */
    @Override
    public LoadBalancerPool updateLoadBalancerPool(LoadBalancerPool loadBalancerPool) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.LOADBALANCERPOOL, loadBalancerPool.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            com.woorea.openstack.quantum.model.LoadBalancerPool loadBalancerPoolToCreate =
                new com.woorea.openstack.quantum.model.LoadBalancerPool();

            loadBalancerPoolToCreate.setName(loadBalancerPool.getName());
            if (loadBalancerPool.getProtocol() != null) {
                loadBalancerPoolToCreate.setProtocol(loadBalancerPool.getProtocol().name());
            }
            loadBalancerPoolToCreate.setSubnetId(loadBalancerPool.getSubnetId());
            if (loadBalancerPool.getLbAlgorithm() != null) {
                loadBalancerPoolToCreate.setMethod(loadBalancerPool.getLbAlgorithm().name());
            }

            com.woorea.openstack.quantum.model.LoadBalancerPool openstackLbPool =
                client.lbaas().Pool().update(loadBalancerPoolToCreate).execute();

            return new OpenStackLoadBalancerPool(context, openstackLbPool);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return null;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return quantumConnector.getEndpoint();
    }

    /**
     * @see com.att.cdp.zones.NetworkService#createPort(com.att.cdp.zones.model.Subnet)
     */
    @Override
    public Port createPort(Subnet subnet) throws ZoneException {
        this.checkArg(subnet, "subnet");
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.PORT, subnet.getName());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        com.woorea.openstack.quantum.model.Port port = new com.woorea.openstack.quantum.model.Port();
        try {
            Quantum client = quantumConnector.getClient();
            port.setNetworkId(subnet.getNetwork());
            // List<com.woorea.openstack.quantum.model.Port.Ip> ips = new ArrayList<>();
            // com.woorea.openstack.quantum.model.Port.Ip ip = new com.woorea.openstack.quantum.model.Port.Ip();
            // ip.setSubnetId(subnet.getId());
            // ips.add(ip);
            // port.setList(ips);
            port = client.ports().create(port).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
        return new OpenStackPort(context, port);
    }

    /**
     * @see com.att.cdp.zones.NetworkService#deletePort(com.att.cdp.zones.model.Port)
     */
    @Override
    public void deletePort(Port port) throws ZoneException {
        this.checkArg(port, "port");
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.PORT, port.getId());
        RequestState.put(RequestState.SERVICE, "Network");
        RequestState.put(RequestState.SERVICE_URL, quantumConnector.getEndpoint());

        try {
            Quantum client = quantumConnector.getClient();
            client.ports().delete(port.getId()).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
    }
}
