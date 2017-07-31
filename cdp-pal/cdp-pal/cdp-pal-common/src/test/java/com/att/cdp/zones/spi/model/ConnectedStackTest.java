/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class ConnectedStackTest extends AbstractConnectedTests {

    @Test
    public void testConnectedStack() {
        ConnectedStack stack = new ConnectedStack(context);

        stack.setCreatedBy("bg6954");
        Date creDate = new Date();
        stack.setCreatedDate(creDate);
        stack.setId("id");
        stack.setName("name");
        stack.setUpdatedBy("bhanu");
        Date upDate = new Date();
        stack.setUpdatedDate(upDate);
        stack.setDeletedBy("ramesh");
        Date delDate = new Date();
        stack.setDeletedDate(delDate);
        stack.setDescription("desc");

        assertTrue(stack.isConnected());
        assertNotNull(stack);
        assertEquals("id", stack.getId());
        assertEquals("name", stack.getName());
        assertEquals("desc", stack.getDescription());
        assertEquals("bg6954", stack.getCreatedBy());
        assertEquals("bhanu", stack.getUpdatedBy());
        assertEquals("ramesh", stack.getDeletedBy());
        assertEquals(delDate, stack.getDeletedDate());
        assertEquals(creDate, stack.getCreatedDate());
        assertEquals(upDate, stack.getUpdatedDate());
    }

}
