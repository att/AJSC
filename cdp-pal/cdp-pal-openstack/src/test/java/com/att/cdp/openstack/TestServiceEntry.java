/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.att.cdp.AbstractTestCase;

/**
 * @since Jan 22, 2015
 * @version $Id$
 */

public class TestServiceEntry extends AbstractTestCase {

    /**
     * Verifies that the ctor sets the fields correctly
     */
    @Test
    public void testConstruction() {
        ServiceEntry entry = new ServiceEntry("name", "type");

        assertEquals("name", entry.getName());
        assertEquals("type", entry.getType());
    }

    /**
     * Verifies that the endpoints list is maintained correctly
     */
    @Test
    public void testAddEndpoints() {
        ServiceEntry entry = new ServiceEntry("name", "type");
        ServiceEndpoint e1 = new ServiceEndpoint("public1", "admin1", "internal1", "region1");
        ServiceEndpoint e2 = new ServiceEndpoint("public2", "admin2", "internal2", "region2");
        ServiceEndpoint e3 = new ServiceEndpoint("public3", "admin3", "internal3", "region3");

        entry.addEndpoint(e1);
        entry.addEndpoint(e2);

        List<ServiceEndpoint> list = entry.getEndpoints();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.contains(e1));
        assertTrue(list.contains(e2));
        assertFalse(list.contains(e3));
    }
}
