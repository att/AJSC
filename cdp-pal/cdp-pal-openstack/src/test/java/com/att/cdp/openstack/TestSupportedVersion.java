/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.att.cdp.AbstractTestCase;

/**
 * @since Jan 22, 2015
 * @version $Id$
 */

public class TestSupportedVersion extends AbstractTestCase {

    /**
     * Tests that the supported version is constructed correctly
     */
    @Test
    public void testConstruction() {
        SupportedVersion v1 = new SupportedVersion("pattern", "package", "url");

        assertEquals("pattern", v1.getPattern());
        assertEquals("package", v1.getPackageNode());
        assertEquals("url", v1.getUrlNode());
    }

    /**
     * Tests that the supported version will match the version string correctly regardless of case and qualified value
     */
    @Test
    public void testPatternMatching() {
        SupportedVersion v1 = new SupportedVersion("V1(\\.[0-9]+)?", "package", "url");

        assertTrue(v1.isMatch("v1"));
        assertTrue(v1.isMatch("v1.0"));
        assertTrue(v1.isMatch("v1.10"));
        assertTrue(v1.isMatch("V1"));
    }
}
