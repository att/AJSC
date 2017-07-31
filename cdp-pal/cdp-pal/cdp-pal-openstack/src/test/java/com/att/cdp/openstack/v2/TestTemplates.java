/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Template;

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

public class TestTemplates extends AbstractTestCase {

    /**
     * Verifies that we can list the existing templates on a provider. This test requires that the provider actually has
     * templates installed.
     * 
     * @throws ZoneException
     */
    @Test
    @Ignore
    public void listTemplates() throws ZoneException {
        Context context = connect();
        ComputeService service = context.getComputeService();

        List<Template> templates = service.getTemplates();
        assertNotNull(templates);
        assertFalse(templates.isEmpty());
        for (Template template : templates) {
            System.out.println(template.toString());
        }
    }
}
