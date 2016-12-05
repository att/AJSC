/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class TestConnectedACL extends AbstractConnectedTests {
    /**
     * Test the basic ACL features
     */
    @Test
    public void testConnectedACL() {
        ConnectedACL acl = new ConnectedACL(context);
        acl.setDescription("desc");
        acl.setId("id");
        acl.setName("name");
        assertNotNull(acl);
        assertEquals("desc", acl.getDescription());
        assertEquals("id", acl.getId());
        assertEquals("name", acl.getName());
    }

}
