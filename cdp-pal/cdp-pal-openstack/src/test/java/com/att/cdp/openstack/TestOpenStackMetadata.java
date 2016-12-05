/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.ProviderMetadata;

public class TestOpenStackMetadata extends AbstractTestCase {

    @Test
    @Ignore
    public void testMetadata() throws ZoneException {
        OpenStackContext context = login();

        ProviderMetadata md = context.getProviderMetadata();
        assertNotNull(md);

        assertNotNull(md.getClientVersion());
        assertNotNull(md.getComputeVersion());
        assertNotNull(md.getIdentityVersion());
        assertNotNull(md.getImageVersion());
        assertNotNull(md.getNetworkVersion());
        assertNotNull(md.getObjectVersion());
        assertNotNull(md.getStackVersion());
        assertNotNull(md.getVolumeVersion());

        assertEquals("OpenStackProvider", md.getProviderName());
        assertTrue(md.isComputeSupported());
        assertTrue(md.isIdentitySupported());
        assertTrue(md.isImageSupported());
        assertTrue(md.isNetworkSupported());
        assertFalse(md.isStartServerSupported());
        assertFalse(md.isStopServerSupported());

        logout(context);
    }
}
