/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.glance.Glance;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.nova.Nova;

public class TestNovaConnector extends AbstractTestCase {

    @Test
    @Ignore
    public void testGlanceConnector() throws ZoneException {
        OpenStackContext context = login();

        NovaConnector connector = context.getNovaConnector();
        assertNotNull(connector);

        Access access = connector.getAccess();
        assertNotNull(access);

        Nova client = connector.getClient();
        assertNotNull(client);

        logout(context);
    }
}
