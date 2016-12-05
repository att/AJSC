/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.test.DummyProviderContext;

public class TestConnectedServer extends AbstractConnectedTests {
    /**
     * Verify the ConnectedServer object is not null
     */
    @Test
    public void testConnectedServer() {
        ConnectedServer serv = new ConnectedServer(context);
        serv.setId("id");
        serv.setName("name");
        assertNotNull(serv);
        assertEquals("id", serv.getId());
        assertEquals("name", serv.getName());
    }

    /**
     * Verify the tenant object is not null
     */
    /*
     * @SuppressWarnings("unused")
     * @Test public void testGetTenant(){ Tenant tenant = new Tenant(); try{ tenant = context.getTenant();
     * assertNotNull(tenant); Context ctx = tenant.getContext(); }catch(RimeException e){ e.printStackTrace();
     * fail("Exception thrown"); } }
     */

}
