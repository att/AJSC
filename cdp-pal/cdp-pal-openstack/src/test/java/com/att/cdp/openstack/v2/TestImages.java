/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.model.Image;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorea.openstack.glance.model.Images;

/**
 * This test is used to test template support.
 * <p>
 * This test should not be run as a normal part of the build. It's success depends on the accessibility to a suitable
 * OpenStack provider, proper credentials, and other environmental configurations that are not likely to be present on
 * the build system. This is a developer-supported and developer-used test only, and is not part of the product
 * certification test suite!
 * </p>
 * 
 * @since Feb 5, 2015
 * @version $Id$
 */

public class TestImages extends AbstractTestCase {

    /**
     * Verifies that we can list the existing templates on a provider. This test requires that the provider actually has
     * templates installed.
     * 
     * @throws ZoneException
     */
    @Ignore
    @Test
    public void listImages() throws ZoneException {
        Context context = connect();
        ImageService service = context.getImageService();

        List<Image> images = service.listImages();

        assertNotNull(images);
        assertFalse(images.isEmpty());
        for (Image image : images) {
            System.out.println(image.toString());
        }
    }

    /**
     * Verifies that we can list the existing templates on a provider. This test requires that the provider actually has
     * templates installed.
     * 
     * @throws ZoneException
     */
    // @Ignore
    @Test
    @Ignore
    public void getImages() throws ZoneException {
        Context context = connect();
        ImageService service = context.getImageService();

        List<Image> images = service.listImages();

        assertNotNull(images);
        assertFalse(images.isEmpty());
        for (Image image : images) {
            service.getImage(image.getId());
        }
    }

    /**
     * Test to deserialize a known bad image
     */
    @Test
    @Ignore
    public void deserializeBadImage() {
        try (InputStream stream = getClass().getResourceAsStream("CEAP.json")) {
            ObjectMapper om = new ObjectMapper();
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            Images images = om.readValue(stream, Images.class);

            assertFalse(images.getList().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

    }
}
