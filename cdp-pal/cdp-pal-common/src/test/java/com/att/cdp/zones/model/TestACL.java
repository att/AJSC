/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.junit.Test;

public class TestACL {
    ACL acl = new ACL();
    ACL aclTest = new ACL("");

    /**
     * Verify the ACL object is not null and test the getter methods
     */
    @Test
    public void testACL() {

        assertNotNull(acl);
        assertNotNull(aclTest);

        acl.setName("name");
        String aclId = acl.getId();
        String desc = acl.getDescription();
        assertEquals("name", acl.getName());
        assertEquals(aclId, acl.getId());
        assertEquals(desc, acl.getDescription());

    }

    /**
     * Obtain the list of Rules and verify it is not null
     */
    @Test
    public void testGetRules() {
        List<Rule> rules = acl.getRules();
        assertNotNull(rules);
    }
}
