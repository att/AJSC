/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Subnet;

/**
 * @since May 17, 2016
 * @version $Id$
 */
public class TestComputeService extends AbstractTestCase {

	/**
	 * Test that we can list all ports on some server
	 * 
	 * @throws ZoneException
	 *             if anything fails
	 */
	@Test
	@Ignore
	public void testListPorts() throws ZoneException {
		Context context = connect();
		ComputeService service = context.getComputeService();

		List<Server> servers = service.getServers();
		assertNotNull(servers);
		assertFalse(servers.isEmpty());

		for (Server server : servers) {
			List<Port> ports = service.getPorts(server);
			assertNotNull(ports);
			assertFalse(ports.isEmpty());

			List<Port> serverPorts = server.getPorts();
			assertNotNull(serverPorts);
			assertFalse(serverPorts.isEmpty());

			assertTrue(ports.containsAll(serverPorts));

			// System.out.printf("Server %s has %d ports\n", server.getName(),
			// ports.size());
		}
	}

	/**
	 * Test the attach and detach of a port
	 * 
	 * @throws ZoneException
	 *             if the test fails
	 */
	@SuppressWarnings("nls")
	@Test
	@Ignore
	public void testAttachAndDetachPorts() throws ZoneException {
		Context context = connect();
		ComputeService compute = context.getComputeService();
		NetworkService network = context.getNetworkService();

		/*
		 * Get a list of servers. Make sure that at least one server exists.
		 * We'll just use the first one we find.
		 */
		List<Server> servers = compute.getServers();
		assertNotNull(servers);
		assertFalse(servers.isEmpty());
		Server server = servers.get(0);

		//

		/*
		 * Now, get the ports attached to that server. Make sure there is at
		 * least one port. We'll use the first one we find.
		 */
		List<Port> ports = server.getPorts();
		assertNotNull(ports);
		assertFalse(ports.isEmpty());

		/*
		 * Use the first port to model the creation of a new port. We'll use the
		 * same subnet.
		 */
		Port modelPort = ports.get(0);
		Subnet subnet = network.getSubnetById(modelPort.getSubnetId());
		Port newPort = network.createPort(subnet);
		assertNotNull(newPort);
		assertNotNull(newPort.getMacAddr());
		assertNotNull(newPort.getId());

		/*
		 * Save the new port's ID. We'll use that to test that it was actually
		 * attached and detached
		 */
		String id = newPort.getId();

		/*
		 * Attach the port to the server. Then list all the ports and make sure
		 * the new port is included
		 */
		server.attachPort(newPort);
		ports = server.getPorts();
		boolean found = false;
		for (Port p : ports) {
			if (p.getId().equals(id)) {
				found = true;
				break;
			}
		}
		if (!found) {
			fail("Port is not listed as attached to the server");
		}

		/*
		 * Now detach the port and make sure that it is NOT listed
		 */
		server.detachPort(newPort);
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// ignore
		}
		ports = server.getPorts();
		found = false;
		for (Port p : ports) {
			if (p.getId().equals(id)) {
				found = true;
				break;
			}
		}
		if (found) {
			fail("Port is still listed as attached to the server");
		}

		/*
		 * Finally, delete the new port
		 */
		network.deletePort(newPort);
	}

	@Ignore
	@Test
	public void testDeletePort() throws ZoneException {
		String[] ids = { "0e1ae02a-f34e-4208-a031-ba251c9d6538",
				"7235a241-a5d1-473c-97e2-f3115e91d878",
				"791b9689-7f19-469a-9759-154018315d4b",
				"958bbf73-7559-439d-bbad-94e67fc07263",
				"b43449bc-a427-4838-a0ab-4fd544533677",
				"b94deec4-fdcc-41d9-ae93-8d0f746011d2" };

		Context context = connect();
		ComputeService compute = context.getComputeService();
		NetworkService network = context.getNetworkService();

		for (String id : ids) {
			Port port = network.getPort(id);
			if (port != null) {
				port.delete();
			}
		}
	}

	@Ignore
	@Test(expected = InvalidRequestException.class) 
	public void testInvalidRebootType() throws ZoneException {

		Context context = connect();
		ComputeService compute = context.getComputeService();
		// NetworkService network = context.getNetworkService();
		List<Server> ids = compute.getServers();

		

			for (Server id : ids) {
				compute.rebootServer(id, "SOFT1");
			}
		
	}
	
	@Ignore	
	public void testSoftRebootService() throws ZoneException {

		Context context = connect();
		ComputeService compute = context.getComputeService();
		// NetworkService network = context.getNetworkService();
		List<Server> servers = compute.getServers();
		Server server = servers.get(0);
		compute.rebootServer(server, "SOFT");
		assertTrue(server.getStatus().equals("REBOOT"));
		
	}
	
	@Ignore	
	public void testHardRebootService() throws ZoneException {

		Context context = connect();
		ComputeService compute = context.getComputeService();
		// NetworkService network = context.getNetworkService();
		List<Server> servers = compute.getServers();
		Server server = servers.get(0);
		compute.rebootServer(server, "HARD");
		assertTrue(server.getStatus().equals("HARD_REBOOT"));
		
	}
}
