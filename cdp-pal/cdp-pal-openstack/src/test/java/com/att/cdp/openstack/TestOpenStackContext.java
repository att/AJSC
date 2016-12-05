/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.connectors.GlanceConnector;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.connectors.QuantumConnector;
import com.att.cdp.openstack.v2.OpenStackComputeService;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.StackService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Tenant;

public class TestOpenStackContext extends AbstractTestCase {

    /**
     * Test that calling "connectionOnly" with a null url returns null
     */
    @Test
    public void testConnectionOnlyWithNull() {
        assertNull(OpenStackContext.connectionOnly(null));
    }

    /**
     * Test that the connectionOnly method strips extra path and query parameters correctly
     */
    @SuppressWarnings("nls")
    @Test
    public void testConnectionOnlyWithValidURLs() {
        String url1 = "http://one.server:9000";
        String url2 = "https://another.server.noport";
        String url3 = "https://server.with.port:9000";

        assertEquals(url1, OpenStackContext.connectionOnly(url1));
        assertEquals(url1, OpenStackContext.connectionOnly(url1 + "/path/path"));
        assertEquals(url1, OpenStackContext.connectionOnly(url1 + "?query=value"));
        assertEquals(url2, OpenStackContext.connectionOnly(url2));
        assertEquals(url2, OpenStackContext.connectionOnly(url2 + "/path/path"));
        assertEquals(url2, OpenStackContext.connectionOnly(url2 + "?query=value"));
        assertEquals(url3, OpenStackContext.connectionOnly(url3));
        assertEquals(url3, OpenStackContext.connectionOnly(url3 + "/path/path"));
        assertEquals(url3, OpenStackContext.connectionOnly(url3 + "?query=value"));
    }

    /**
     * Test that the connectionOnly method handles an invalid URL correctly
     */
    @SuppressWarnings("nls")
    @Test
    public void testConnectionOnlyInvalidURL() {
        String url1 = "a.bogus.url";

        assertEquals(url1, OpenStackContext.connectionOnly(url1));
        assertEquals(url1, OpenStackContext.connectionOnly("  " + url1 + "  "));
    }

    /**
     * Checks that the allocated floating ip address cache is working correctly.
     * 
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("nls")
    @Test
    public void testAllocateFloatingIP() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
        IllegalAccessException {
        String testIp = "192.168.1.10";
        Class<OpenStackContext> clazz = OpenStackContext.class;
        Field field = clazz.getDeclaredField("allocatedFloatingIPs");
        field.setAccessible(true);
        OpenStackContext context = new OpenStackContext(new OpenStackProvider(), getProperties(), null);

        @SuppressWarnings("unchecked")
        Set<String> set = (Set<String>) field.get(context);
        assertNotNull(set);
        assertTrue(set.isEmpty());

        assertTrue(context.allocateFloatingIP(testIp));

        assertFalse(set.isEmpty());
        assertFalse(context.allocateFloatingIP(testIp));

        context.deallocateFloatingIP(testIp);
        assertTrue(set.isEmpty());
    }

    /**
     * Test that we can update a property after creation of the provider context
     * 
     * @throws IOException
     */
    @SuppressWarnings("nls")
    @Test
    public void testUpdateProperty() throws IOException {
        try (OpenStackContext context = new OpenStackContext(new OpenStackProvider(), getProperties(), null)) {
            assertFalse(context.getProperties().containsKey("DummyProperty"));
            context.updateProperty("DummyProperty", "DummyValue");
            assertTrue(context.getProperties().containsKey("DummyProperty"));
            assertEquals("DummyValue", context.getProperties().get("DummyProperty"));
        }
    }

    /**
     * Verify that we can retrieve the service catalog
     * 
     * @throws IOException
     */
    @Test
    public void testGetServiceCatalog() throws IOException {
        try (OpenStackContext context = new OpenStackContext(new OpenStackProvider(), getProperties(), null)) {
            ServiceCatalog catalog = context.getServiceCatalog();
            assertNotNull(catalog);
        }
    }

    /**
     * Test that we can login and out of the provider, and access the services once logged in (so we dont have to
     * repeatedly login/out)
     * 
     * @throws ZoneException
     */
    @Test
    @Ignore
    public void testLoginLogout() throws ZoneException {
        OpenStackContext context = login();

        /*
         * make sure we can retreive all of the services
         */
        ComputeService computeService = context.getComputeService();
        assertNotNull(computeService);

        IdentityService identityService = context.getIdentityService();
        assertNotNull(identityService);

        ImageService imageService = context.getImageService();
        assertNotNull(imageService);

        NetworkService networkService = context.getNetworkService();
        assertNotNull(networkService);

        VolumeService volumeService = context.getVolumeService();
        assertNotNull(volumeService);

        SnapshotService snapshotService = context.getSnapshotService();
        assertNotNull(snapshotService);

        StackService stackService = context.getStackService();
        assertNotNull(stackService);
        /*
         * Services are locally cached by the provider context. If we get them again, we should get the same exact
         * object. Check that too.
         */
        assertTrue(computeService == context.getComputeService());
        assertTrue(identityService == context.getIdentityService());
        assertTrue(imageService == context.getImageService());
        assertTrue(networkService == context.getNetworkService());
        assertTrue(volumeService == context.getVolumeService());
        assertTrue(snapshotService == context.getSnapshotService());
        assertTrue(stackService == context.getStackService());

        /*
         * Test that we can also obtain the connectors
         */
        GlanceConnector glanceConnector = context.getGlanceConnector();
        assertNotNull(glanceConnector);
        NovaConnector novaConnector = context.getNovaConnector();
        assertNotNull(novaConnector);
        QuantumConnector quantumConnector = context.getQuantumConnector();
        assertNotNull(quantumConnector);

        /*
         * Connectors are locally cached, check that
         */
        assertTrue(glanceConnector == context.getGlanceConnector());
        assertTrue(novaConnector == context.getNovaConnector());
        assertTrue(quantumConnector == context.getQuantumConnector());

        /*
         * Now, check that we can obtain the tenant as well
         */
        Tenant tenant = context.getTenant();
        assertNotNull(tenant);

        assertEquals(tenant.getName(), context.getTenantName());

        assertEquals(tenant.getId(), context.getTenantId());

        logout(context);
    }

    /**
     * @throws ZoneException
     */
    @SuppressWarnings("nls")
    @Test
    @Ignore
    public void testKeepAlive() throws ZoneException {
        OpenStackContext context = login();
        OpenStackComputeService compute = (OpenStackComputeService) context.getComputeService();
        List<Server> ext = compute.getServers();
        System.out.println("Exts: " + ext.size());
    }
}
