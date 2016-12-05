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

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

public class TestConnectedKeyPair extends AbstractConnectedTests {
    /**
     * Verify the object is not null
     * 
     * @throws ZoneException
     */
    @Test
    public void testConnectedKeyPair() throws ZoneException {
        ConnectedKeyPair keyPair = new ConnectedKeyPair(context);
        assertNotNull(keyPair);

        keyPair.setCreatedBy("bg6954");
        Date creDate = new Date();
        keyPair.setCreatedDate(creDate);
        keyPair.setUpdatedBy("bhanu");
        Date upDate = new Date();
        keyPair.setUpdatedDate(upDate);
        keyPair.setDeletedBy("ramesh");
        Date delDate = new Date();
        keyPair.setDeletedDate(delDate);

        assertTrue(keyPair.isConnected());
        assertNotNull(keyPair);
        assertEquals("bg6954", keyPair.getCreatedBy());
        assertEquals("bhanu", keyPair.getUpdatedBy());
        assertEquals("ramesh", keyPair.getDeletedBy());
        assertEquals(delDate, keyPair.getDeletedDate());
        assertEquals(creDate, keyPair.getCreatedDate());
        assertEquals(upDate, keyPair.getUpdatedDate());

        keyPair.setPrivateKey("privatekey");
        keyPair.setPublicKey("publickey");
        keyPair.setFingerprint("fingerprint");
        keyPair.setName("name");
        keyPair.setUserId("userId");
        assertEquals(keyPair.getPrivateKey(), "privatekey");
        // @sonar:off
        try {
            assertEquals(keyPair.getPublicKey(), "publickey");
        } catch (ZoneException e) {
            // ignore
        }
        assertEquals(keyPair.getFingerprint(), "fingerprint");
        try {
            keyPair.getTenant();
        } catch (Exception e) {
            // ignore
        }
        assertEquals(keyPair.getUserId(), "userId");

        try {
            keyPair.delete();
        } catch (Exception e) {
            // ignore

        }
        // @sonar:on
    }

}
