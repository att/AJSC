/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response.Status;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.openstack.model.OpenStackACL;
import com.att.cdp.openstack.model.OpenStackFault;
import com.att.cdp.openstack.model.OpenStackPort;
import com.att.cdp.openstack.model.OpenStackRule;
import com.att.cdp.openstack.model.OpenStackServer;
import com.att.cdp.openstack.model.OpenStackTemplate;
import com.att.cdp.openstack.util.ExceptionMapper;
import com.att.cdp.pal.util.StringHelper;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Rule;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Subnet;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.VirtualInterface;
import com.att.cdp.zones.model.Volume;
import com.att.cdp.zones.spi.AbstractCompute;
import com.att.cdp.zones.spi.RequestState;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedServer;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.base.client.OpenStackConnectException;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.nova.api.ServersResource;
import com.woorea.openstack.nova.api.ServersResource.Boot;
import com.woorea.openstack.nova.model.Extension;
import com.woorea.openstack.nova.model.InterfaceAttachment;
import com.woorea.openstack.nova.model.InterfaceAttachmentForCreate;
import com.woorea.openstack.nova.model.InterfaceAttachments;
import com.woorea.openstack.nova.model.NetworkForCreate;
import com.woorea.openstack.nova.model.SecurityGroup;
import com.woorea.openstack.nova.model.SecurityGroupForCreate;
import com.woorea.openstack.nova.model.ServerAction.Rebuild;
import com.woorea.openstack.nova.model.ServerForCreate;
import com.woorea.openstack.nova.model.Servers;
import com.woorea.openstack.nova.model.VolumeAttachment;

/**
 * @since Sep 24, 2013
 * @version $Id$
 */

/**
 * @since May 2, 2014
 * @version $Id$
 */
public class OpenStackComputeService extends AbstractCompute {

	/**
	 * The Nova connector object.
	 */
	private NovaConnector nova;

	/**
	 * Create the OpenStack compute service implementation for the specified
	 * context
	 * 
	 * @param context
	 *            The context that we are providing the services for
	 */
	public OpenStackComputeService(Context context) {
		super(context);
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#abortResize(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void abortResize(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			OpenStackResponse request = nova.getClient().servers()
					.revertResize(server.getId()).request();
			if (request != null
					&& request.getStatus() != Status.ACCEPTED.getStatusCode()) {
				throw new ZoneException(request.getEntity(String.class));
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#addACLRule(java.lang.String,
	 *      com.att.cdp.zones.model.Rule)
	 */
	@SuppressWarnings("nls")
	@Override
	public Rule addACLRule(String aclId, Rule rule) throws ZoneException {
		checkArg(aclId, "aclId");
		checkArg(rule, "rule");

		connect();
		Context context = getContext();
		trackRequest(new State(RequestState.SERVICE, "Compute"), new State(
				RequestState.SERVICE_URL, nova.getEndpoint()));

		try {
			SecurityGroup group = nova.getClient().securityGroups()
					.showSecurityGroup(aclId).execute();
			if (group != null) {
				com.woorea.openstack.nova.model.SecurityGroup.Rule createdRule = nova
						.getClient()
						.securityGroups()
						.createSecurityGroupRule(aclId,
								rule.getProtocol().toString(),
								rule.getFromPort(), rule.getToPort(),
								rule.getSourceIpRange()).execute();
				return new OpenStackRule(context, createdRule);
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

		return null;
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#assignIpAddress(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void assignIpAddress(Server server, String address)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(address, "address");
		assignIpAddress(server.getId(), address);
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#assignIpAddress(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void assignIpAddress(String serverId, String address)
			throws ZoneException {
		checkArg(serverId, "serverId");
		checkArg(address, "address");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.IPADDRESS, address);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().associateFloatingIp(serverId, address)
					.execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#associateACL(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void associateACL(String serverId, String aclName)
			throws ZoneException {
		checkArg(serverId, "serverId");
		checkArg(aclName, "aclName");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers()
					.associateSecurityGroup(serverId, aclName).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Attach a volume to a specified server. The volume is then attached to the
	 * server for it to use when the server is started.
	 * 
	 * @param server
	 *            The server we are manipulating
	 * @param volume
	 *            The volume we wish to attach to the server
	 * @param deviceName
	 *            the name of the device presented to the server
	 * @throws ZoneException
	 *             If the volume cannot be attached for some reason
	 * @see com.att.cdp.zones.ComputeService#attachVolume(com.att.cdp.zones.model.Server,
	 *      com.att.cdp.zones.model.Volume, java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void attachVolume(Server server, Volume volume, String deviceName)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(volume, "volume");
		//1811 release deviceName can be left as null for auto assignment by OpenStack. 
		//Hence validating the deviceName only if its not null
		if(deviceName != null){
			checkArg(deviceName, "deviceName");
		
			if (!checkDeviceName(deviceName)) {
					throw new InvalidRequestException(EELFResourceManager.format(
							OSMsg.PAL_OS_INVALID_DEVICE_NAME, deviceName));
			}
		}
		

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server);
		RequestState.put(RequestState.VOLUME, volume.getId());
		RequestState.put(RequestState.DEVICE, volume.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		/*
		 * Get the list of existing attachments and check to see if the device
		 * name is already used
		 */
		Map<String, String> attachments = getAttachments(server);
		if(deviceName != null){
			if (attachments.containsKey(deviceName)) {
				throw new InvalidRequestException(EELFResourceManager.format(
						OSMsg.PAL_OS_INVALID_DEVICE_NAME, deviceName));
			}
		}

		/*
		 * Check the server status to see if it is correct for attempting the
		 * attachment
		 */
		server.refreshStatus();
		Server.Status status = server.getStatus();
		RequestState.put(RequestState.STATUS, status.toString());
		if (status.equals(Server.Status.RUNNING)
				|| status.equals(Server.Status.READY)) {

			try {
				nova.getClient()
						.servers()
						.attachVolume(server.getId(), volume.getId(),
								deviceName).execute();
			} catch (OpenStackBaseException ex) {
				ExceptionMapper.mapException(ex);
			}
		} else {
			throw new ZoneException(EELFResourceManager.format(
					OSMsg.PAL_OS_INVALID_SERVER_STATE, server.getName(), server
							.getId().toString(), status.toString(),
					EELFResourceManager.asList(Server.Status.READY.toString(),
							Server.Status.RUNNING.toString())));
		}
	}

	/**
	 * This method is a helper method that checks the name verification to
	 * ensure that a device name is syntactically valid, and that it does not
	 * specify the a or be disk drive devices. That is because OpenStack
	 * implicitly uses the /dev/sda and /dev/sdb devices, and there are errors
	 * that occur but are not reflected back to the api if we attempt to use
	 * those devices.
	 * 
	 * @param name
	 *            The device name to be checked
	 * @return True if the device name is syntactically correct and does not
	 *         conflict with known device names
	 */
	@SuppressWarnings("nls")
	private static boolean checkDeviceName(String name) {

		if (name == null) {
			return false;
		}
		Pattern pattern = Pattern
				.compile("/dev/(?:(?:sd)|(?:hd)|(?:vd))(.)(/*)?");
		Matcher matcher = pattern.matcher(name);

		if (!matcher.matches()) {
			return false;
		}

		if (matcher.group(1).equals("a")) {
			return false;
		}

		return true;
	}

	/**
	 * This is a helper method used to construct the Nova service object and
	 * setup the environment to access the OpenStack compute service (Nova).
	 * 
	 * @throws NotLoggedInException
	 *             If the user is not logged in
	 * @throws ContextClosedException
	 *             If the user attempts an operation after the context is closed
	 */
	private void connect() throws NotLoggedInException, ContextClosedException {
		checkLogin();
		checkOpen();
		OpenStackContext osContext = (OpenStackContext) getContext();

		nova = osContext.getNovaConnector();
		osContext.refreshIfStale(nova);
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#createAccessControlList(com.att.cdp.zones.model.ACL)
	 */
	@SuppressWarnings("nls")
	@Override
	public ACL createAccessControlList(ACL model) throws ZoneException {
		checkArg(model, "model");
		checkArg(model.getName(), "name");
		checkArg(model.getDescription(), "description");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		SecurityGroupForCreate create = new SecurityGroupForCreate();
		create.setName(model.getName());
		create.setDescription(model.getDescription());

		try {
			SecurityGroup group = nova.getClient().securityGroups()
					.createSecurityGroup(create).execute();
			return new OpenStackACL(context, group);
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
		return null;
	}

	/**
	 * Create a server using the supplied server object as the pattern
	 * 
	 * @param model
	 *            The server to create. The model object must contain the
	 *            following attributes in order to be legal for creating a new
	 *            server:
	 *            <ul>
	 *            <li>The name of the server</li>
	 *            <li>The id of the template to be used</li>
	 *            <li>The id of the image to be used</li>
	 *            </ul>
	 *            In addition, the model object may also contain a list of ACL's
	 *            that are to be assigned.
	 * @return A reference to the connected server. The template server (the
	 *         argument passed) remains disconnected. The user is encouraged to
	 *         use the referenced returned from this method for any further
	 *         operations on the server.
	 * @throws ZoneException
	 *             If the server cannot be created
	 * @see com.att.cdp.zones.ComputeService#createServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public Server createServer(Server model) throws ZoneException {
		checkArg(model, "model");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, model.getName());
		RequestState.put(RequestState.IMAGE, model.getImage());
		RequestState.put(RequestState.TEMPLATE, model.getTemplate());
		RequestState.put(RequestState.KEYPAIR, model.getKeyName());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			HashMap<String, String> dictionary = new HashMap<>();
			dictionary.put("name", "name");
			dictionary.put("image", "imageRef");
			dictionary.put("template", "flavorRef");
			dictionary.put("availabilityZone", "availabilityZone");

			ServerForCreate create = new ServerForCreate();
			ObjectMapper.map(model, create, dictionary);

			/*
			 * If an ACL list is present, then use the specified ACL models to
			 * request the appropriate security groups
			 */
			if (model.getAccessControl() != null) {
				for (ACL acl : model.getAccessControl()) {
					ServerForCreate.SecurityGroup group = new ServerForCreate.SecurityGroup(
							acl.getName());
					create.getSecurityGroups().add(group);
				}
			}

			/*
			 * If the key pair name is specified in the server object, set that
			 * on the request
			 */
			if (model.getKeyName() != null) {
				create.setKeyName(model.getKeyName());
			}

			if (model.getAccessControl() != null) {
				for (ACL entry : model.getAccessControl()) {
					create.getSecurityGroups().add(
							new ServerForCreate.SecurityGroup(entry.getName()));
				}
			} else {
				create.getSecurityGroups().add(
						new ServerForCreate.SecurityGroup("default"));
			}

			/*
			 * Process ports if they are specified. Fall-back to the deprecated
			 * network associations if ports are not specified.
			 */
			com.woorea.openstack.nova.model.Server osServer = null;
			List<NetworkForCreate> networks = new ArrayList<>();
			NetworkService networkService = context.getNetworkService();
			if (model.getPorts() != null && !model.getPorts().isEmpty()) {
				for (Port port : model.getPorts()) {
					/*
					 * See if the port is connected (exists) or is disconnected
					 * (it is a model). If disconnected, then we have to create
					 * the port first.
					 */
					if (!port.isConnected()) {
						Subnet subnet = networkService.getSubnetById(port
								.getSubnetId());
						port = networkService.createPort(subnet);
					}

					NetworkForCreate newNet = new NetworkForCreate();
					newNet.setPort(port.getId());
					networks.add(newNet);
				}
				create.setNetworks(networks);
			} else if (!model.getNetworks().isEmpty()) {
				for (Network networkModel : model.getNetworks()) {
					List<Network> networkList = networkService
							.getNetworksByName(networkModel.getName());
					if (networkList.isEmpty()) {
						throw new ZoneException(String.format(
								"Network %s cannot be found",
								networkModel.getName()));
					}
					Network network = networkList.get(0);
					NetworkForCreate newNet = new NetworkForCreate();
					newNet.setId(network.getId());
					networks.add(newNet);
				}
				create.setNetworks(networks);
			}

			ServersResource resource = nova.getClient().servers();
			Boot boot = resource.boot(create);
			osServer = boot.execute();
			com.woorea.openstack.nova.model.Server.Fault osFault = osServer
					.getFault();

			ConnectedServer server = (ConnectedServer) getServer(osServer
					.getId());
			if (osFault != null) {
				server.setFault(new OpenStackFault(context, osFault));
			}
			return server;
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

		return null; // for the compiler
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#createServerSnapshot(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void createServerSnapshot(Server server, String name)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(name, "name");
		throw new ZoneException("Not Yet Implemented");
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#deleteAccessControlList(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void deleteAccessControlList(String id) throws ZoneException {
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().securityGroups().deleteSecurityGroup(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#deleteACLRule(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void deleteACLRule(Rule rule) throws ZoneException {
		checkArg(rule, "rule");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().securityGroups()
					.deleteSecurityGroupRule(rule.getId()).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Delete the specified server using it's id.
	 * 
	 * @param server
	 *            The server to be deleted.
	 * @throws ZoneException
	 *             If the server does not exist or cannot be deleted for some
	 *             reason.
	 * @see com.att.cdp.zones.ComputeService#deleteServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void deleteServer(Server server) throws ZoneException {
		checkArg(server, "server");
		deleteServer(server.getId());
	}

	/**
	 * Delete the specified server using it's id.
	 * 
	 * @param serverId
	 *            The server to be deleted.
	 * @throws ZoneException
	 *             If the server does not exist or cannot be deleted for some
	 *             reason.
	 * @see com.att.cdp.zones.ComputeService#deleteServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void deleteServer(String serverId) throws ZoneException {
		checkArg(serverId, "serverId");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			Server server = getServer(serverId);
			Server.Status status = server.getStatus();
			RequestState.put(RequestState.STATUS, status.toString());
			if (status.equals(Server.Status.RUNNING)
					|| status.equals(Server.Status.READY)
					|| status.equals(Server.Status.ERROR)) {

				List<Port> ports = server.getPorts();
				for (Port port : ports) {
					port.delete();
				}

				nova.getClient().servers().delete(serverId).execute();
			} else {
				throw new ZoneException(EELFResourceManager.format(
						OSMsg.PAL_OS_INVALID_SERVER_STATE, server.getName(),
						server.getId(), status.toString(), StringHelper
								.asList(Arrays.asList(
										Server.Status.READY.toString(),
										Server.Status.RUNNING.toString(),
										Server.Status.ERROR.toString()))));
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Detaches the volume from the specified server.
	 * 
	 * @param server
	 *            The server to be manipulated
	 * @param deviceName
	 *            The device to be detached
	 * @throws ZoneException
	 *             If the volume cannot be detached for some reason
	 * @see com.att.cdp.zones.ComputeService#detachVolume(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void detachVolume(Server server, String deviceName)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(deviceName, "deviceName");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getName());
		RequestState.put(RequestState.DEVICE, deviceName);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		server.refreshStatus();
		Server.Status status = server.getStatus();
		RequestState.put(RequestState.STATUS, status.toString());
		if (status.equals(Server.Status.RUNNING)
				|| status.equals(Server.Status.READY)) {
			try {
				Map<String, String> attachments = getAttachments(server);
				for (Map.Entry<String, String> entry : attachments.entrySet()) {
					if (entry.getKey().equals(deviceName)) {
						nova.getClient().servers()
								.detachVolume(server.getId(), entry.getValue())
								.execute();
					}
					break;
				}
			} catch (OpenStackBaseException ex) {
				ExceptionMapper.mapException(ex);
			}
		} else {
			throw new ZoneException(EELFResourceManager.format(
					OSMsg.PAL_OS_INVALID_SERVER_STATE, server.getName(), server
							.getId().toString(), status.toString(),
					StringHelper.asList(Arrays.asList(
							Server.Status.READY.toString(),
							Server.Status.RUNNING.toString()))));
		}
	}

	/**
	 * Detaches the volume from the specified server.
	 * 
	 * @param server
	 *            The server to be manipulated
	 * @param volume
	 *            The volume to be detached
	 * @throws ZoneException
	 *             - If the volume cannot be detached for some reason
	 * @see com.att.cdp.zones.ComputeService#detachVolume(com.att.cdp.zones.model.Server,
	 *      com.att.cdp.zones.model.Volume)
	 */
	@SuppressWarnings("nls")
	@Override
	public void detachVolume(Server server, Volume volume) throws ZoneException {
		checkArg(server, "server");
		checkArg(volume, "volume");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getName());
		RequestState.put(RequestState.VOLUME, volume.getName());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		server.refreshStatus();
		Server.Status status = server.getStatus();
		RequestState.put(RequestState.STATUS, status.toString());
		if (status.equals(Server.Status.RUNNING)
				|| status.equals(Server.Status.READY)) {
			try {
				nova.getClient().servers()
						.detachVolume(server.getId(), volume.getId()).execute();
			} catch (OpenStackBaseException ex) {
				ExceptionMapper.mapException(ex);
			}
		} else {
			throw new ZoneException(EELFResourceManager.format(
					OSMsg.PAL_OS_INVALID_SERVER_STATE, server.getName(), server
							.getId(), status.toString(), StringHelper
							.asList(Arrays.asList(
									Server.Status.READY.toString(),
									Server.Status.RUNNING.toString()))));
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#disassociateACL(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void disassociateACL(String serverId, String aclName)
			throws ZoneException {
		checkArg(serverId, "serverId");
		checkArg(aclName, "aclName");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers()
					.disassociateSecurityGroup(serverId, aclName).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#executeCommand(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@Override
	public void executeCommand(Server server, String command)
			throws ZoneException {
		// NO-OP
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#getAccessControlList(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public ACL getAccessControlList(String id) throws ZoneException {
		checkArg(id, "id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			SecurityGroup group = nova.getClient().securityGroups()
					.showSecurityGroup(id).execute();
			return new OpenStackACL(context, group);
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
		return null;
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#getAccessControlLists()
	 */
	@Override
	public List<ACL> getAccessControlLists() throws ZoneException {
		connect();
		Context context = getContext();
		ArrayList<ACL> list = new ArrayList<>();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			for (SecurityGroup group : nova.getClient().securityGroups()
					.listSecurityGroups().execute()) {
				list.add(new OpenStackACL(context, group));
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

		return list;
	}

	/**
	 * Returns a map of the volume attachments for the specified server. The key
	 * of the map is the device name for the volume attachment. This is the
	 * device identification which the server will "see".
	 * 
	 * @param server
	 *            The server for which we wish to obtain all attachments (if
	 *            any).
	 * @return A map of the volume attachments, or an empty map if there are
	 *         none. The map is keyed by the device name used to attach the
	 *         volume. The value of the entry is the ID of the volume attached
	 *         at that device.
	 * @throws ZoneException
	 *             - If the attachments cannot be obtained
	 * @see com.att.cdp.zones.ComputeService#getAttachments(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public Map<String, String> getAttachments(Server server)
			throws ZoneException {
		checkArg(server, "server");
		return getAttachments(server.getId());
	}

	/**
	 * Returns a map of the volume attachments for the specified server. The key
	 * of the map is the device name for the volume attachment. This is the
	 * device identification which the server will "see".
	 * 
	 * @param id
	 *            The server ID for which we wish to obtain all attachments (if
	 *            any).
	 * @return A map of the volume attachments, or an empty map if there are
	 *         none. The map is keyed by the device name used to attach the
	 *         volume. The value of the entry is the ID of the volume attached
	 *         at that device.
	 * @throws ZoneException
	 *             - If the attachments cannot be obtained
	 * @see com.att.cdp.zones.ComputeService#getAttachments(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public Map<String, String> getAttachments(String id) throws ZoneException {
		checkArg(id, "id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		HashMap<String, String> map = new HashMap<>();

		try {
			for (VolumeAttachment attachment : nova.getClient().servers()
					.listVolumeAttachments(id).execute()) {
				RequestState.put(RequestState.VOLUME, attachment.getVolumeId());
				if (attachment.getDevice() != null) {
					RequestState.put(RequestState.DEVICE,
							attachment.getDevice());
					map.put(attachment.getDevice(), attachment.getVolumeId());
				} else {
					System.err.println("No device found for " + attachment);
				}
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		} catch (Exception e) {
			throw new ZoneException("Error obtaining volume attachments.", e);
		}

		return map;
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#getConsoleOutput(com.att.cdp.zones.model.Server)
	 */
	@Override
	public List<String> getConsoleOutput(Server server) throws ZoneException {
		// NO-OP
		return null;
	}

	/**
	 * Returns the set of extensions loaded, if any
	 * 
	 * @return The list of extensions installed, if any
	 * @throws ZoneException
	 *             If anything fails
	 */
	public List<String> getExtensions() throws ZoneException {
		connect();
		Context context = getContext();
		ArrayList<String> extensions = new ArrayList<>();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			for (Extension extension : nova.getClient().extensions().list(true)
					.execute()) {
				extensions.add(extension.getName());
			}
		} catch (OpenStackBaseException e) {
			if (e instanceof OpenStackResponseException) {
				OpenStackResponseException osre = (OpenStackResponseException) e;
				if (osre.getStatus() != 404) {
					ExceptionMapper.mapException(e);
				}
			} else {
				ExceptionMapper.mapException(e);
			}
		}

		return extensions;
	}

	/**
	 * Returns the indicated host using the specified identification token
	 * 
	 * @param id
	 *            The identification of the server
	 * @return The server
	 * @throws ZoneException
	 *             - If the host cannot be found
	 * @see com.att.cdp.zones.ComputeService#getServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public Server getServer(String id) throws ZoneException {
		checkArg(id, "id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			com.woorea.openstack.nova.model.Server s = nova.getClient()
					.servers().show(id).execute();
			return new OpenStackServer(context, s);
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

		return null; // for the compiler
	}

	/**
	 * Obtain a list of servers from the compute service.
	 * 
	 * @return The list of servers that are defined.
	 * @throws ZoneException
	 *             - If any of the following conditions are true:
	 *             <ul>
	 *             <li>the user has not successfully logged in to the provider</li>
	 *             <li>the context has been closed and this service is requested
	 *             </li>
	 *             <li>the current user does not have the rights to perform this
	 *             operation</li>
	 *             <li>the user and/or credentials are not valid</li>
	 *             </ul>
	 * @see com.att.cdp.zones.ComputeService#getServers()
	 */
	@Override
	public List<Server> getServers() throws ZoneException {
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		ArrayList<Server> list = new ArrayList<>();
		try {
			com.woorea.openstack.nova.model.Servers servers = nova.getClient()
					.servers().list(true).execute();
			if (servers != null && servers.getList() != null) {
				for (com.woorea.openstack.nova.model.Server s : servers
						.getList()) {
					list.add(new OpenStackServer(context, s));
				}
			}
		} catch (OpenStackBaseException e) {
			ExceptionMapper.mapException(e);
		}

		return list;
	}

	/**
	 * Returns the list of servers that match the name pattern supplied.
	 * 
	 * @param name
	 *            A regular expression that can be used to filter server names.
	 *            A string that is suitable to use in the Java
	 *            <code>String.matches()</code> method.
	 * @return The server
	 * @throws ZoneException
	 *             If the host cannot be found
	 * @see java.lang.String#matches(String)
	 * @see com.att.cdp.zones.ComputeService#getServers(java.lang.String)
	 */
	@Override
	public List<Server> getServers(String name) throws ZoneException {
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());
		RequestState.put(RequestState.SERVER, name);

		ArrayList<Server> list = new ArrayList<>();
		try {
			com.woorea.openstack.nova.model.Servers servers = nova.getClient()
					.servers().list(true).execute();
			for (com.woorea.openstack.nova.model.Server s : servers.getList()) {
				if (name != null) {
					if (s.getName().matches(name)) {
						list.add(new OpenStackServer(context, s));
					}
				} else {
					list.add(new OpenStackServer(context, s));
				}
			}
		} catch (OpenStackBaseException e) {
			ExceptionMapper.mapException(e);
		}

		return list;
	}

	/**
	 * Obtains the template specified by the provided id
	 * 
	 * @see com.att.cdp.zones.ComputeService#getTemplate(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public Template getTemplate(String id) throws ZoneException {
		checkArg(id, "id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());
		RequestState.put(RequestState.TEMPLATE, id);

		try {
			return new OpenStackTemplate(context, nova.getClient().flavors()
					.show(id).execute());
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		} catch (Exception e) {
			throw new ResourceNotFoundException(e);
		}
		return null; // for the compiler
	}

	/**
	 * This method returns a list of templates that are available.
	 * <p>
	 * A template represents a definition of a hardware environment that is used
	 * to create an image. This includes number of cpu's, amount of memory, etc.
	 * </p>
	 * 
	 * @return A list of available templates
	 * @throws ZoneException
	 *             If the templates cannot be listed
	 * @see com.att.cdp.zones.ComputeService#getTemplates()
	 */
	@Override
	public List<Template> getTemplates() throws ZoneException {
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		ArrayList<Template> list = new ArrayList<>();
		try {
			com.woorea.openstack.nova.model.Flavors flavors = nova.getClient()
					.flavors().list(true).execute();
			for (com.woorea.openstack.nova.model.Flavor f : flavors.getList()) {
				Template template = new OpenStackTemplate(context, f);
				list.add(template);
			}
		} catch (OpenStackBaseException e) {
			ExceptionMapper.mapException(e);
		}

		return list;
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#prepareResize(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void prepareResize(Server server, Template newTemplate)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");
		checkArg(newTemplate, "template");
		checkArg(newTemplate.getId(), "template id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers()
					.resize(server.getId(), newTemplate.getId(), null)
					.execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#processResize(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void processResize(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().confirmResize(server.getId()).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

	}

	/**
	 * @see com.att.cdp.zones.ComputeService#releaseIpAddress(com.att.cdp.zones.model.Server,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void releaseIpAddress(Server server, String address)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(address, "address");
		releaseIpAddress(server.getId(), address);
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#releaseIpAddress(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void releaseIpAddress(String serverId, String address)
			throws ZoneException {
		checkArg(serverId, "serverId");
		checkArg(address, "address");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.IPADDRESS, address);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers()
					.disassociateFloatingIp(serverId, address).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#resumeServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void resumeServer(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");
		resumeServer(server.getId());
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#resumeServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void resumeServer(String id) throws ZoneException {
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().resume(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Starts the indicated server
	 * 
	 * @param server
	 *            The server to be started
	 * @throws ZoneException
	 *             - If the server is in an invalid state, does not exist, or
	 *             the server object is disconnected.
	 * @see com.att.cdp.zones.ComputeService#startServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void startServer(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");

		startServer(server.getId());
	}

	/**
	 * Starts the indicated server
	 * 
	 * @param id
	 *            The id of the server to be started
	 * @throws ZoneException
	 *             If the server is in an invalid state or it does not exist.
	 * @see com.att.cdp.zones.ComputeService#startServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void startServer(String id) throws ZoneException {
		checkOpen();
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().start(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Stops the indicated server
	 * 
	 * @param server
	 *            The server to be stopped
	 * @throws ZoneException
	 *             If the server is in an invalid state, does not exist, or the
	 *             server object is disconnected.
	 * @see com.att.cdp.zones.ComputeService#stopServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void stopServer(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");
		stopServer(server.getId());
	}

	/**
	 * Stops the indicated server
	 * 
	 * @param id
	 *            The id of the server to be stopped
	 * @throws ZoneException
	 *             If the server is in an invalid state or it does not exist.
	 * @see com.att.cdp.zones.ComputeService#stopServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void stopServer(String id) throws ZoneException {
		checkArg(id, "id");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().stop(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#suspendServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void suspendServer(Server server) throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");
		suspendServer(server.getId());
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#suspendServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void suspendServer(String id) throws ZoneException {
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().suspend(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * This method returns a list of OS extended network attributes for the
	 * supplied tenant.
	 * <p>
	 * This lists networks that are available to the tenant. The information in
	 * the network list includes extended network attributes.
	 * </p>
	 * 
	 * @return A list of networks and their extended attributes
	 * @throws ZoneException
	 *             If the networks and extended attributes cannot be listed
	 * @see com.att.cdp.zones.ComputeService#getExtendedNetworks()
	 */
	@Override
	public List<Network> getExtendedNetworks() throws ZoneException {

		List<Network> list = new ArrayList<>();
		return list;
	}

	/**
	 * This method returns a list of OS Virtual Interfaces for a specified
	 * server instance.
	 * <p>
	 * This includes the ID for the virtual interface as well as the associated
	 * mac address.
	 * </p>
	 * 
	 * @return A list of virtual interfaces
	 * @throws ZoneException
	 *             If the virtual interfaces cannot be listed
	 * @see com.att.cdp.zones.ComputeService#getVirtualInterfaces(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public List<VirtualInterface> getVirtualInterfaces(String id)
			throws ZoneException {
		checkArg(id, "id");

		ArrayList<VirtualInterface> list = new ArrayList<>();
		return list;
	}

	/**
	 * Rebuilds the server with the exact same image that it was currently built
	 * from.
	 * 
	 * @see com.att.cdp.zones.ComputeService#rebuildServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void rebuildServer(Server server) throws ZoneException {
		checkArg(server, "server");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		Rebuild rebuild = new Rebuild();
		rebuild.setImageRef(server.getImage());
		try {
			nova.getClient().servers().rebuild(server.getId(), rebuild)
					.execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * Rebuilds the server from a supplied snapshot
	 * 
	 * @param server
	 *            The server to be re-built
	 * @param snapshot
	 *            The snapshot or image id to be used to rebuild the server
	 * @throws ZoneException
	 *             If the server cannot be rebuilt
	 */
	@SuppressWarnings("nls")
	@Override
	public void rebuildServer(Server server, String snapshot)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(snapshot, "snapshot");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		Rebuild rebuild = new Rebuild();
		rebuild.setImageRef(snapshot);
		try {
			nova.getClient().servers().rebuild(server.getId(), rebuild)
					.execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#pauseServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void pauseServer(String id) throws ZoneException {
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().pause(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#pauseServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void pauseServer(Server server) throws ZoneException {
		checkArg(server, "server");
		pauseServer(server.getId());
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#unpauseServer(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public void unpauseServer(String id) throws ZoneException {
		checkArg(id, "id");
		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, id);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().unpause(id).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#unpauseServer(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public void unpauseServer(Server server) throws ZoneException {
		checkArg(server, "server");
		unpauseServer(server.getId());
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#findAllServersUsingKey(java.lang.String)
	 */
	@SuppressWarnings("nls")
	@Override
	public List<String> findAllServersUsingKey(String keyPair)
			throws ZoneException {
		checkArg(keyPair, "keyPair");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.KEYPAIR, keyPair);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		List<String> users = new ArrayList<>();
		try {
			Servers servers = nova.getClient().servers().list(false).execute();
			for (com.woorea.openstack.nova.model.Server server : servers
					.getList()) {
				if (keyPair.equals(server.getKeyName())) {
					users.add(server.getId());
				}
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}

		return users;
	}

	@SuppressWarnings("nls")
	@Override
	public void refreshServerStatus(Server server) throws ZoneException {
		checkArg(server, "server");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, server);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			((OpenStackServer) server).mapServerStatus(nova.getClient()
					.servers().show(server.getId()).execute());
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#migrateServer(java.lang.String)
	 */
	@Override
	public void migrateServer(String serverId) throws ZoneException {
		checkArg(serverId, "serverId");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().migrate(serverId).execute();
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#moveServer(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void moveServer(String serverId, String targetHostId)
			throws ZoneException {
		checkArg(serverId, "serverId");

		connect();
		Context context = getContext();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			if (targetHostId != null && targetHostId.length() > 0) {
				nova.getClient().servers().evacuate(serverId, targetHostId)
						.execute();

			} else {
				nova.getClient().servers().evacuate(serverId).execute();
			}
		} catch (OpenStackBaseException ex) {
			ExceptionMapper.mapException(ex);
		}
	}

	/**
	 * @see com.att.cdp.zones.Service#getURL()
	 */
	@Override
	public String getURL() {
		return nova.getEndpoint();
	}

	/**
	 * Gets the ports connected to a specific server
	 * 
	 * @throws ZoneException
	 *             If the server is null or invalid, if the context is closed,
	 *             or if the context has not been authenticated, or if the
	 *             authentication has expired
	 * @see com.att.cdp.zones.ComputeService#getPorts(com.att.cdp.zones.model.Server)
	 */
	@SuppressWarnings("nls")
	@Override
	public List<Port> getPorts(Server server) throws ZoneException {
		checkArg(server, "server");
		connect();

		trackRequest();
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		List<Port> list = new ArrayList<>();
		try {
			InterfaceAttachments attachments = nova.getClient().servers()
					.listInterfaceAttachments(server.getId()).execute();
			for (InterfaceAttachment attachment : attachments.getList()) {
				OpenStackPort port = new OpenStackPort(getContext(), attachment);
				list.add(port);
			}
		} catch (OpenStackConnectException | OpenStackResponseException e) {
			ExceptionMapper.mapException(e);
		}

		return list;
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#attachPort(com.att.cdp.zones.model.Server,
	 *      com.att.cdp.zones.model.Port)
	 */
	@Override
	public void attachPort(Server server, Port port) throws ZoneException {
		checkArg(server, "server");
		checkArg(port, "port");
		connect();

		trackRequest();
		RequestState.put(RequestState.PORT, port.getId());
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		InterfaceAttachmentForCreate iafc = new InterfaceAttachmentForCreate();
		iafc.setPortId(port.getId());
		try {
			nova.getClient().servers()
					.createInterfaceAttachment(server.getId(), iafc).execute();
		} catch (OpenStackConnectException | OpenStackResponseException e) {
			ExceptionMapper.mapException(e);
		}
	}

	/**
	 * @see com.att.cdp.zones.ComputeService#detachPort(com.att.cdp.zones.model.Server,
	 *      com.att.cdp.zones.model.Port)
	 */
	@Override
	public void detachPort(Server server, Port port) throws ZoneException {
		checkArg(server, "server");
		checkArg(port, "port");
		connect();

		trackRequest();
		RequestState.put(RequestState.PORT, port.getId());
		RequestState.put(RequestState.SERVER, server.getId());
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers()
					.detachInterfaceAttachment(server.getId(), port.getId())
					.execute();
		} catch (OpenStackConnectException | OpenStackResponseException e) {
			ExceptionMapper.mapException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.att.cdp.zones.ComputeService#rebootServer(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void rebootServer(String serverId, String rebootType)
			throws ZoneException {
		
		if (!("HARD".equals(rebootType) ||"SOFT".equals(rebootType))) {
			throw new InvalidRequestException(EELFResourceManager.format(
					OSMsg.PAL_OS_INVALID_REBOOT_TYPE, rebootType));
		}
		
		checkArg(serverId, "serverId");

		connect();

		trackRequest();
		RequestState.put(RequestState.SERVER, serverId);
		RequestState.put(RequestState.SERVICE, "Compute");
		RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

		try {
			nova.getClient().servers().reboot(serverId, rebootType).execute();
		} catch (OpenStackConnectException | OpenStackResponseException e) {

			ExceptionMapper.mapException(e);
		}

	}

	@Override
	public void rebootServer(Server server, String rebootType)
			throws ZoneException {
		checkArg(server, "server");
		checkArg(server.getId(), "server id");
		rebootServer(server.getId(), rebootType);

	}
}
