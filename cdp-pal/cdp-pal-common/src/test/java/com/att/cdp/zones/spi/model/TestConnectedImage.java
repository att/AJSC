/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class TestConnectedImage extends AbstractConnectedTests {
    /**
     * This test ensures that we can load the test image and its setter methods
     */
    @Test
    public void testConnectedImage() {
        ConnectedImage img = new ConnectedImage(context);
        Calendar cal = new GregorianCalendar();
        Integer num = new Integer(5);
        Long size = new Long(5);
        img.setId("id");
        img.setName("name");
        img.setOwner("owner");
        img.setUri("uri");
        img.setCreatedDate(cal.getTime());
        img.setDeletedDate(cal.getTime());
        img.setUpdatedDate(cal.getTime());
        img.setMinimumDisk(num);
        img.setMinimumDisk(num);
        img.setSize(size);
        assertNotNull(img);
        assertEquals("id", img.getId());
        assertEquals("name", img.getName());
        assertEquals("owner", img.getOwner());
        assertEquals("uri", img.getUri());
        assertEquals(cal.getTime(), img.getCreatedDate());
        assertEquals(cal.getTime(), img.getDeletedDate());
        assertEquals(cal.getTime(), img.getUpdatedDate());
        assertEquals(num, img.getMinimumDisk());
        assertEquals(num, img.getMinimumDisk());
        assertEquals(size, img.getSize());

    }

}
