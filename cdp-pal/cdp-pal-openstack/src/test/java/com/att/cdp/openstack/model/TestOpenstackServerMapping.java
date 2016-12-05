/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.Server.Addresses;

/**
 * @since Nov 25, 2015
 * @version $Id$
 */

public class TestOpenstackServerMapping {

    private ObjectMapper om;
    private String json;

    @Before
    public void setup() throws IOException {
        om = new ObjectMapper();
        om.configure(Feature.WRAP_ROOT_VALUE, true);
        om.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        StringBuilder builder = new StringBuilder();
        try (InputStream is = this.getClass().getResourceAsStream("test.json")) {
            byte[] buffer = new byte[4096];
            int amtRead = is.read(buffer, 0, buffer.length);
            while (amtRead != -1) {
                builder.append(new String(buffer, 0, amtRead, "UTF-8"));
                amtRead = is.read(buffer, 0, buffer.length);
            }

            json = builder.toString();
        }

        assertNotNull(json);
    }

    @After
    public void teardown() {
        om = null;
    }

    @Test
    public void testMapJsonToServer() throws JsonParseException, JsonMappingException, IOException {
        Server server = om.readValue(json, Server.class);
        assertNotNull(server);

        Server.Addresses addresses = server.getAddresses();
        assertNotNull(addresses);

        Map<String, List<Server.Addresses.Address>> map = addresses.getAddresses();
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());

        List<Server.Addresses.Address> list = map.get("VLAN_OVERLAY_422");
        assertNotNull(list);
        assertFalse(list.isEmpty());

        Server.Addresses.Address entry = list.get(0);
        assertNotNull(entry);
        assertEquals("135.144.122.121", entry.getAddr());

    }
}
