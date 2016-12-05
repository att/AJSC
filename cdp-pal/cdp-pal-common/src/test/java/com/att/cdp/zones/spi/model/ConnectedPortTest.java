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
import com.att.cdp.zones.model.Port;

/**
 * @since May 18, 2016
 * @version $Id$
 */

public class ConnectedPortTest extends AbstractConnectedTests {

    /**
     * 
     */
    @Test
    public void testConnectedPort() {
        ConnectedPort port = new ConnectedPort(context);
        port.setCreatedBy("bg6954");
        Date creDate = new Date();
        port.setCreatedDate(creDate);
        port.setUpdatedBy("bhanu");
        Date upDate = new Date();
        port.setUpdatedDate(upDate);
        port.setDeletedBy("ramesh");
        Date delDate = new Date();
        port.setDeletedDate(delDate);
        port.setSubnetId("netid");
        port.setMacAddr("macaddr");
        port.setId("portId");
        port.setPortState(Port.Status.ONLINE);

        assertTrue(port.isConnected());
        assertNotNull(port);
        assertEquals("bg6954", port.getCreatedBy());
        assertEquals("bhanu", port.getUpdatedBy());
        assertEquals("ramesh", port.getDeletedBy());
        assertEquals(delDate, port.getDeletedDate());
        assertEquals(creDate, port.getCreatedDate());
        assertEquals(upDate, port.getUpdatedDate());

        assertEquals("netid", port.getSubnetId());
        assertEquals("macaddr", port.getMacAddr());
        assertEquals("portId", port.getId());
        assertEquals(Port.Status.ONLINE, port.getPortState());
    }

}
