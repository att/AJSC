/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;

public class TestImage {
    private Map<String, PersistentObject> metadata;

    @Test
    public void testImageFeatures() {
        Image img = new Image();
        assertNotNull(img);

        /*
         * try{ Tenant tenant; tenant = img.getTenant(); assertNotNull(tenant); }catch (NotNavigableException e) {
         * e.printStackTrace(); fail("We were supposed to get the tenants!"); }catch (RimeException e) {
         * e.printStackTrace(); fail("We were supposed to get an exception!"); } metadata = img.getMetadata();
         * assertNotNull(metadata);
         */

    }

}
