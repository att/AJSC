/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
  
package com.att.cdp.openstack.v1;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Subnet;

/**
 *
 * @since May 17, 2016
 * @version $Id$
 */
public class TestNetworkService extends AbstractTestCase {

    /**
     * Test that we can list all ports on the network
     * 
     * @throws ZoneException
     *             If the test cannot connect to the provider
     */
    @Test
    @Ignore
    public void testListPorts() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        List<Port> ports = service.getPorts();
        assertNotNull(ports);
        assertFalse(ports.isEmpty());

        Port port = ports.get(0);
        assertNotNull(port.getId());
        assertNotNull(port.getMacAddr());
    }

    /**
     * Tests that we can create and delete a port
     * 
     * @throws ZoneException
     *             If the test cannot connect to the provider
     */
    @Test
    @Ignore
    public void testCreateAndDeletePort() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        List<Subnet> subnets = service.getSubnets();
        assertNotNull(subnets);
        assertFalse(subnets.isEmpty());

        Subnet subnet = subnets.get(0);
        Port port = service.createPort(subnet);
        assertNotNull(port);
        assertEquals(port.getSubnetId(), subnet.getId());
        assertNotNull(port.getMacAddr());
        assertNotNull(port.getId());
        assertNotNull(port.getNetwork());

        service.deletePort(port);
    }
}
