/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestConnectedTemplate extends AbstractConnectedTests {
    /**
     * Verify the object is not null and test the setter/getter methods
     */
    @Test
    public void testCreateConnectedTemplate() {
        ConnectedTemplate template = new ConnectedTemplate(context);
        Integer num = new Integer(5);
        template.setId("id");
        template.setName("name");
        template.setCpus(num);
        template.setDisk(num);
        template.setRam(num);
        template.setEnabled(true);
        assertNotNull(template);
        assertEquals("id", template.getId());
        assertEquals("name", template.getName());
        assertEquals(num, template.getCpus());
        assertEquals(num, template.getDisk());
        assertEquals(num, template.getRam());
        assertTrue(template.isEnabled());

    }

}
