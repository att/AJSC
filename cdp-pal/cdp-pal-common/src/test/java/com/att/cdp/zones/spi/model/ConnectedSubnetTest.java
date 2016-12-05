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

public class ConnectedSubnetTest extends AbstractConnectedTests {

    @Test
    public void testConnectedSubnet() {
        ConnectedSubnet subnet = new ConnectedSubnet(context);
        subnet.setCreatedBy("bg6954");
        Date creDate = new Date();
        subnet.setCreatedDate(creDate);
        subnet.setId("id");
        subnet.setName("name");
        subnet.setUpdatedBy("bhanu");
        Date upDate = new Date();
        subnet.setUpdatedDate(upDate);
        subnet.setDeletedBy("ramesh");
        Date delDate = new Date();
        subnet.setDeletedDate(delDate);
        subnet.setIpv4(true);

        assertTrue(subnet.isConnected());
        assertTrue(subnet.isIpv4());

        assertNotNull(subnet);
        assertEquals("id", subnet.getId());
        assertEquals("name", subnet.getName());
        assertEquals("bg6954", subnet.getCreatedBy());
        assertEquals("bhanu", subnet.getUpdatedBy());
        assertEquals("ramesh", subnet.getDeletedBy());
        assertEquals(delDate, subnet.getDeletedDate());
        assertEquals(creDate, subnet.getCreatedDate());
        assertEquals(upDate, subnet.getUpdatedDate());
    }

}
