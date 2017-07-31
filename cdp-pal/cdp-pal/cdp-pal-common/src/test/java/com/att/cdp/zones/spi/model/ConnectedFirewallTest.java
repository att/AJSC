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

public class ConnectedFirewallTest extends AbstractConnectedTests {

    /**
     * 
     */
    @Test
    public void testConnectedFirewall() {
        ConnectedFirewall firewall = new ConnectedFirewall(context);
        firewall.setCreatedBy("bg6954");
        Date creDate = new Date();
        firewall.setCreatedDate(creDate);
        firewall.setId("id");
        firewall.setName("name");
        firewall.setUpdatedBy("bhanu");
        Date upDate = new Date();
        firewall.setUpdatedDate(upDate);
        firewall.setDeletedBy("ramesh");
        Date delDate = new Date();
        firewall.setDeletedDate(delDate);

        firewall.setAdminStateUp(true);
        firewall.setAudited(true);
        firewall.setDescription("desc");
        firewall.setShared(true);

        assertTrue(firewall.isAdminStateUp());
        assertTrue(firewall.isConnected());
        assertTrue(firewall.isShared());
        assertTrue(firewall.isAudited());

        assertNotNull(firewall);
        assertEquals("id", firewall.getId());
        assertEquals("name", firewall.getName());
        assertEquals("desc", firewall.getDescription());
        assertEquals("bg6954", firewall.getCreatedBy());
        assertEquals("bhanu", firewall.getUpdatedBy());
        assertEquals("ramesh", firewall.getDeletedBy());
        assertEquals(delDate, firewall.getDeletedDate());
        assertEquals(creDate, firewall.getCreatedDate());
        assertEquals(upDate, firewall.getUpdatedDate());

    }

}
