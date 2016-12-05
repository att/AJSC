/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class TestConnectedNetwork extends AbstractConnectedTests {
    /**
     * Verify the object is not null and test the setter and getter methods
     */
    @Test
    public void testConnectedNetwork() {
        ConnectedNetwork network = new ConnectedNetwork(context);
        network.setId("id");
        network.setName("name");
        network.setStatus("status");
        network.setType("type");
        assertNotNull(network);
        assertEquals("id", network.getId());
        assertEquals("name", network.getName());
        assertEquals("status", network.getStatus());
        assertEquals("type", network.getType());
    }

}
