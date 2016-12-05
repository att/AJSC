/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.att.cdp.AbstractTestCase;

/**
 * @since Jan 22, 2015
 * @version $Id$
 */

public class TestServiceEndpoint extends AbstractTestCase {

    /**
     * Tests that the ctor creates the object correctly
     */
    @Test
    public void testConstruction() {
        ServiceEndpoint e1 = new ServiceEndpoint("public", "admin", "internal", "region");

        assertEquals("public", e1.getPublicUrl());
        assertEquals("admin", e1.getAdminUrl());
        assertEquals("internal", e1.getInternalUrl());
        assertEquals("region", e1.getRegion());
    }

    /**
     * Tests the equals method for proper operation
     */
    @Test
    public void testEquals() {
        ServiceEndpoint e1 = new ServiceEndpoint("public", "admin", "internal", "region");
        ServiceEndpoint e2 = new ServiceEndpoint("public2", "admin", "internal", "region");
        ServiceEndpoint e3 = new ServiceEndpoint("public", "admin3", "internal", "region");
        ServiceEndpoint e4 = new ServiceEndpoint("public", "admin", "internal4", "region");
        ServiceEndpoint e5 = new ServiceEndpoint("public", "admin", "internal", "region5");
        ServiceEndpoint e6 = new ServiceEndpoint("public", "admin", "internal", "region");

        assertFalse(e1.equals(e2));
        assertFalse(e1.equals(e3));
        assertFalse(e1.equals(e4));
        assertFalse(e1.equals(e5));

        assertTrue(e1.equals(e6));
        assertTrue(e6.equals(e1));
    }

}
