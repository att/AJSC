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
import com.woorea.openstack.cinder.Cinder;
import com.woorea.openstack.keystone.model.Access;


public class TestCinderConnector extends AbstractTestCase {

    @Test
    @Ignore
    public void testCinderConnector() throws ZoneException {
        OpenStackContext context = login();

        CinderConnector connector = context.getCinderConnector();
        assertNotNull(connector);

        Access access = connector.getAccess();
        assertNotNull(access);

        Cinder client = connector.getClient();
        assertNotNull(client);

        logout(context);
    }
}
