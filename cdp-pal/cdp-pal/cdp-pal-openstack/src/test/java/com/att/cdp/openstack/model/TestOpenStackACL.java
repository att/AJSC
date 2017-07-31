/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.woorea.openstack.nova.model.SecurityGroup;

public class TestOpenStackACL extends AbstractTestCase {

    @Test
    @Ignore
    public void testCtorACL() throws ZoneException {
        OpenStackContext context = login();

        SecurityGroup group = new SecurityGroup();

        logout(context);
    }

}
