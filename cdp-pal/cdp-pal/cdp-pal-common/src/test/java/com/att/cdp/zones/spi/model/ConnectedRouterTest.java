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

public class ConnectedRouterTest extends AbstractConnectedTests {

    @Test
    public void testConnectedRouter() {
        ConnectedRouter router = new ConnectedRouter(context);
        router.setCreatedBy("bg6954");
        Date creDate = new Date();
        router.setCreatedDate(creDate);
        router.setUpdatedBy("bhanu");
        Date upDate = new Date();
        router.setUpdatedDate(upDate);
        router.setDeletedBy("ramesh");
        Date delDate = new Date();
        router.setDeletedDate(delDate);
        router.setId("id");
        router.setExternalNetworkId("enetid");
        router.setName("name");
        router.setStatus("status");

        assertTrue(router.isConnected());
        assertNotNull(router);
        assertEquals("bg6954", router.getCreatedBy());
        assertEquals("bhanu", router.getUpdatedBy());
        assertEquals("ramesh", router.getDeletedBy());
        assertEquals(delDate, router.getDeletedDate());
        assertEquals(creDate, router.getCreatedDate());
        assertEquals(upDate, router.getUpdatedDate());

        assertEquals("id", router.getId());
        assertEquals("enetid", router.getExternalNetworkId());
        assertEquals("name", router.getName());
        assertEquals("status", router.getStatus());
    }

}
