/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class ConnectedLoadBalancerTest extends AbstractConnectedTests {

    @Test
    public void testConnectedLoadBalancer() {
        ConnectedLoadBalancer balancer = new ConnectedLoadBalancer(context);
        balancer.setCreatedBy("bg6954");
        Date creDate = new Date();
        balancer.setCreatedDate(creDate);
        balancer.setId("id");
        balancer.setName("name");
        balancer.setSubnetId("subnetid");
        balancer.setUpdatedBy("bhanu");
        Date upDate = new Date();
        balancer.setUpdatedDate(upDate);
        balancer.setDeletedBy("ramesh");
        Date delDate = new Date();
        balancer.setDeletedDate(delDate);

        assertTrue(!balancer.isAdminStateUp());
        assertTrue(balancer.isConnected());
        balancer.getLbMethod();
        balancer.getProtocol();
        balancer.getStatus();

        assertNotNull(balancer);
        assertEquals("id", balancer.getId());
        assertEquals("name", balancer.getName());
        assertEquals("subnetid", balancer.getSubnetId());
        assertEquals("bg6954", balancer.getCreatedBy());
        assertEquals("bhanu", balancer.getUpdatedBy());
        assertEquals("ramesh", balancer.getDeletedBy());
        assertEquals(delDate, balancer.getDeletedDate());
        assertEquals(creDate, balancer.getCreatedDate());
        assertEquals(upDate, balancer.getUpdatedDate());

    }

}
