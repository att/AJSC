/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.model.OpenStackServer;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Server.Status;
import com.att.cdp.zones.model.ServerBootSource;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.Volume;

/**
 * Test the compute service with an actual openstack tenant. The AbstractTestCase loads connectivity information from a
 * property file that it uses to connect to the test tenant.
 * 
 * @since Oct 14, 2015
 * @version $Id$
 */

public class TestComputeService extends AbstractTestCase {

    /**
     * The class of the server object implementation
     */
    private static Class<OpenStackServer> serverClass;

    private static Field volumeAttachmentsProcessedField;

    private static Field imagesProcessedField;

    private static Field networksProcessedField;

    @SuppressWarnings("nls")
    private static final String TEST_SERVER_NAME = "TestVNF";

    @SuppressWarnings("nls")
    private static final String TEST_SERVER_UUID = "f888f89f-096b-421e-ba36-34f714071551";

    @SuppressWarnings("nls")
    private static final String TEST_NETWORK_NAME = "Network2192";

    @SuppressWarnings("nls")
    private static final String TEST_NETWORK_UUID = "516867f3-49d6-4a5c-b7c0-731f386dc07a";

    /**
     * @throws NoSuchFieldException
     *             If there is an error getting a field
     * @throws SecurityException
     *             If the access of the field is not allowed by a security manager
     */
    @SuppressWarnings("nls")
    @BeforeClass
    public static void initialize() throws NoSuchFieldException, SecurityException {
        serverClass = OpenStackServer.class;

        volumeAttachmentsProcessedField = serverClass.getDeclaredField("volumeAttachmentsProcessed");
        volumeAttachmentsProcessedField.setAccessible(true);
        imagesProcessedField = serverClass.getDeclaredField("imagesProcessed");
        imagesProcessedField.setAccessible(true);
        networksProcessedField = serverClass.getDeclaredField("networksProcessed");
        networksProcessedField.setAccessible(true);
    }

    /**
     * @throws ZoneException
     *             If something goes horribly wrong
     */
    @Ignore
    @Test
    public void testComputeService() throws ZoneException {
        Context context = connect();
        ComputeService computeService = context.getComputeService();
        List<Server> servers = computeService.getServers();
        for (Server server : servers) {
            computeService.getServers(server.getName());
            computeService.getServer(server.getId());
            computeService.getAttachments(server);
            computeService.getAttachments(server.getId());
            computeService.getConsoleOutput(server);
        }

        List<Template> templates = computeService.getTemplates();
        for (Template template : templates) {
            computeService.getTemplate(template.getId());
        }

        List<ACL> accessControlLists = computeService.getAccessControlLists();
        for (ACL acl : accessControlLists) {
            computeService.getAccessControlList(acl.getId());
        }
    }

    /**
     * This test case is designed to simply list the existing servers.
     * 
     * @throws ZoneException
     *             If something goes horribly wrong
     */
    @SuppressWarnings("nls")
    @Ignore
    @Test
    public void testListServers() throws ZoneException {
        Context context = connect();
        ComputeService computeService = context.getComputeService();
        List<Server> servers = computeService.getServers();
        for (Server server : servers) {
            System.out.println(server.toString());
            if (server.getImage() == null) {
                // Map<String, String> attachments = server.getAttachments();
                Map<String, Volume> attachments = server.getVolumes();
                System.out.println("+++No image, volume attachments are: " + attachments.toString());
            }
        }
    }

    /**
     * @throws ZoneException
     *             If the connection fails or the server cant be found
     * @throws IllegalAccessException
     *             If the fields cant be accessed
     * @throws IllegalArgumentException
     *             If the fields cant be accessed
     */
    @Ignore
    @Test
    public void testGetServerLazy() throws ZoneException, IllegalArgumentException, IllegalAccessException {
        Context context = connect();
        ComputeService computeService = context.getComputeService();
        Server server = computeService.getServer(TEST_SERVER_UUID);

        /*
         * Make sure that we have not obtained any of the transitive dependencies yet
         */
        assertNotNull(server);
        AtomicBoolean ab = (AtomicBoolean) volumeAttachmentsProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        ab = (AtomicBoolean) imagesProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        ab = (AtomicBoolean) networksProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        /*
         * Now, get the network and ensure that it is correct
         */
        List<Network> networks = server.getNetworks();
        assertNotNull(networks);
        assertFalse(networks.isEmpty());
        boolean found = false;
        for (Network network : networks) {
            if (network.getName().equals(TEST_NETWORK_NAME)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        ab = (AtomicBoolean) networksProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

        ab = (AtomicBoolean) volumeAttachmentsProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        ab = (AtomicBoolean) imagesProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        /*
         * Now, determine the boot source and image, this will cause the images to be processed
         */
        ServerBootSource bs = server.getBootSource();
        assertNotNull(bs);
        assertTrue(bs.equals(ServerBootSource.IMAGE) || bs.equals(ServerBootSource.SNAPSHOT));

        String image = server.getImage();
        assertNotNull(image);

        ab = (AtomicBoolean) networksProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

        ab = (AtomicBoolean) imagesProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

        ab = (AtomicBoolean) volumeAttachmentsProcessedField.get(server);
        assertNotNull(ab);
        assertFalse(ab.get());

        /*
         * Now, lets see if there are any volume attachments. This will load the volumes transitive relationship
         */
        Map<String, Volume> volumes = server.getVolumes();
        assertNotNull(volumes);

        ab = (AtomicBoolean) networksProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

        ab = (AtomicBoolean) imagesProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

        ab = (AtomicBoolean) volumeAttachmentsProcessedField.get(server);
        assertNotNull(ab);
        assertTrue(ab.get());

    }

    /**
     * This test is designed to rebuild a specific server and test that it rebuilds correctly
     * 
     * @throws ZoneException
     *             If something goes horribly wrong
     */
    @SuppressWarnings("nls")
    @Ignore
    @Test
    public void testRebuildServer() throws ZoneException {
        Context context = connect();
        ComputeService computeService = context.getComputeService();

        Server testVNF = computeService.getServer(TEST_SERVER_UUID);
        testVNF.rebuild();
        testVNF.waitForStateChange(1, 600, Server.Status.RUNNING);
    }

    @Ignore
    @Test
    public void testOpenstackMigrate() throws ZoneException {
        // For testing the migrate functionality

        Properties properties = new Properties();
        String vmId = "75dce20c-97f9-454d-abcc-aa904a33df5a";
        properties.put(ContextFactory.PROPERTY_IDENTITY_URL, "http://135.25.246.131:5000");
        properties.put(ContextFactory.PROPERTY_TENANT, "Play");
        // String vmId = "af51c8b9-3df4-4770-846a-457666367b23";
        // properties.put(ContextFactory.PROPERTY_IDENTITY_URL, "http://135.25.69.195:5000");
        // properties.put(ContextFactory.PROPERTY_TENANT, "Trinity");
        OpenStackContext context = (OpenStackContext) ContextFactory.getContext("OpenStackProvider", properties);

        context.login("AppC", "AppC");
        ComputeService computeService = context.getComputeService();
        Server testVNF = computeService.getServer(vmId);

        long t1, t2, t3, t4;
        t1 = System.currentTimeMillis() / 1000;
        computeService.migrateServer(testVNF.getId());

        testVNF.waitForStateChange(1, 600, Status.PENDING);
        t2 = System.currentTimeMillis() / 1000;
        System.out.println("Migrate accepted and calculating prep work");

        try {
            computeService.migrateServer(testVNF.getId());
            fail("Should not be able to migrate a server that is in PENDING");
        } catch (Exception e) {
            // Good
        }
        try {
            computeService.processResize(testVNF);
            fail("Should not be able to confirm a resize on a server that is in PENDING");
        } catch (Exception e) {
            // Good
        }

        testVNF.waitForStateChange(5, 600, Status.READY);
        t3 = System.currentTimeMillis() / 1000;
        System.out.println("Migrate calculations does. Send resizeConfirm to apply");

        computeService.processResize(testVNF);
        testVNF.waitForStateChange(1, 600, Status.RUNNING);
        t4 = System.currentTimeMillis() / 1000;
        // System.out.println("Migrate Complete. Time to: 1) Start checking after migrate() call, 2) Finish checking, 3) Commit migrate");
        // System.out.println(String.format("1) %3ds\n2) %3ds (%03ds)\n3) %3ds (%3ds)", t2 - t1, t3 - t2, t3 - t1,
        // t4 - t3, t4 - t1));

    }
}
