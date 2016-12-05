/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.model.KeyPair;

/**
 * 
 * @since May 1, 2015
 * @version $Id$
 */

public class TestKeyPairs extends AbstractTestCase {

    private static final String KEYPAIR_NAME = "UnitTestKeyPair";

    /**
     * Test the creation and deletion of a key pair
     * 
     * @throws ZoneException
     *             If the connection to the provider fails
     */
    @Test
    @Ignore
    public void testCreateAndDeleteKeypair() throws ZoneException {
        Context context = connect();

        IdentityService service = context.getIdentityService();

        KeyPair model = new KeyPair(KEYPAIR_NAME, null);
        KeyPair actual = service.createKeyPair(model);
        assertNotNull(actual);
        assertEquals(model.getName(), actual.getName());
        assertNotNull(actual.getFingerprint());
        assertNotNull(actual.getPrivateKey());
        assertNotNull(actual.getPublicKey());

        service.deleteKeyPair(actual);
        context.logout();
    }

    /**
     * @throws ZoneException
     */
    @Test
    @Ignore
    public void listKeyPairs() throws ZoneException {
        Context context = connect();

        IdentityService service = context.getIdentityService();

        List<KeyPair> kps = service.getKeyPairs();
        assertNotNull(kps);
        for (KeyPair kp : kps) {
            if (kp.getName().equals(KEYPAIR_NAME)) {
                fail("KeyPair should not have existed");
            }
        }

        context.logout();
    }
}
