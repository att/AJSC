/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * @since Apr 16, 2015
 * @version $Id$
 */
public class TestConnectedVolume extends AbstractConnectedTests {
    /**
     * Verify the object is not null and test the getter/setter methods
     * 
     * @throws ZoneException
     *             if anything fails
     */
    @Test
    public void testCreateConnectedVolume() throws ZoneException {
        ConnectedVolume volume = new ConnectedVolume(context);
        Calendar cal = new GregorianCalendar();
        volume.setId("id");
        volume.setName("name");
        volume.setVolumeType("volumeType");
        volume.setCreatedDate(cal.getTime());
        assertNotNull(volume);
        assertTrue(volume.isConnected());
        assertEquals("id", volume.getId());
        assertEquals("name", volume.getName());
        assertEquals("volumeType", volume.getVolumeType());
        assertEquals(cal.getTime(), volume.getCreatedDate());
        
        // Refresh and getTenant are not valid and should not be attempted without a context
    }

}
