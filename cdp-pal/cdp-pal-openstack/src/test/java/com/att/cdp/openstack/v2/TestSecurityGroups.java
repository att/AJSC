/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.model.Rule;
import com.att.cdp.zones.model.Server;
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

public class TestSecurityGroups extends AbstractTestCase {

    @Test
    @Ignore
    public void testAssociations() throws ZoneException {
        Context context = connect();
        ComputeService service = context.getComputeService();
        
        String name = "sm974v";
        String server_id = "7d5b3322-6a00-45cd-ae8f-95d985c4148b";
        
        Server server = service.getServer(server_id);
        
        
        
        service.associateACL(server_id, name);
        server.refresh();
        
        
        
        service.disassociateACL(server_id, name);
        server.refresh();
        
        
        
    }
    
    
    @Test
    @Ignore
    public void testAll() throws ZoneException {
        Context context = connect();
        ComputeService service = context.getComputeService();
        
        String aclName = "Unit Test ACL";
        
        try {
            ACL template = new ACL();
            template.setName(aclName);
            template.setDescription(aclName);
            ACL acl = service.createAccessControlList(template);
            assertNotNull(acl);
            assertTrue(acl.getRules().isEmpty());
            
            Rule rule1 = new Rule(Rule.PROTOCOL.TCP, 80, 80, "0.0.0.0/0");
            Rule rule2 = new Rule(Rule.PROTOCOL.UDP, 60000, 60010, "0.0.0.0/0");
            
            rule1 = service.addACLRule(acl.getId(), rule1);
            assertNotNull(rule1.getId());
            rule2 = service.addACLRule(acl.getId(), rule2);
            assertNotNull(rule2.getId());
            
            acl = service.getAccessControlList(acl.getId());
            assertEquals(2, acl.getRules().size());

            assertEquals(Rule.PROTOCOL.TCP, acl.getRules().get(0).getProtocol());
            assertEquals(Rule.PROTOCOL.UDP, acl.getRules().get(1).getProtocol());
            
            service.deleteACLRule(rule1);
            
            acl = service.getAccessControlList(acl.getId());
            assertEquals(1, acl.getRules().size());
            assertEquals(Rule.PROTOCOL.UDP, acl.getRules().get(0).getProtocol());
            
            service.deleteACLRule(rule2);
            
            acl = service.getAccessControlList(acl.getId());
            assertTrue(acl.getRules().isEmpty());
            
            service.deleteAccessControlList(acl.getId());
            
            try {
                service.getAccessControlList(acl.getId());
                fail("Failed to delete the ACL");
            } catch (ZoneException ze) {
                // Successfully deleted the ACL
            }
            
        } catch(ZoneException ze) {
            ze.printStackTrace();
            fail();
        }
    }
}
