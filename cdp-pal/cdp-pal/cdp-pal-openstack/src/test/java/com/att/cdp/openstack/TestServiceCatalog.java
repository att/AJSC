/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.stub.WiremockStub;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

/**
 * This class is used to test that the service catalog can be processed correctly from the OpenStack provider.
 * <p>
 * This test should not be run as a normal part of the build. It's success depends on the accessibility to a suitable
 * OpenStack provider, proper credentials, and other environmental configurations that are not likely to be present on
 * the build system. This is a developer-supported and developer-used test only, and is not part of the product
 * certification test suite!
 * </p>
 * 
 * @since Jan 20, 2015
 * @version $Id$
 */
public class TestServiceCatalog extends AbstractTestCase {
	@ClassRule
	public static WireMockClassRule  wireMockRule = new WireMockClassRule(wireMockConfig().bindAddress("127.0.0.1").port(8089).httpsPort(5000));
	
	@Rule
	public WireMockClassRule instanceRule = wireMockRule;

	static{
		   System.setProperty("mock", "com/att/cdp/mock-test.properties");
		   WiremockStub.disableSslVerification();
		}
	
	@BeforeClass
	public static void beforeClazz(){
		WiremockStub.contextStubLogin();
	}
	 
    /**
     * Obtains the service catalog and then ensures that the core services that we need for cdp are resolved.
     * 
     * @throws ZoneException
     *             If anything fails
     */
    @Test
    @Ignore
    public void testServiceCatalog() throws ZoneException {
    	OpenStackContext context = login();
        ServiceCatalog catalog = context.getServiceCatalog();

        assertNotNull(catalog);
        Map<String, ServiceEntry> services = catalog.getServices();
        assertNotNull(services);

        System.out.println(catalog.toString());

        assertFalse(services.isEmpty());
        assertTrue(services.containsKey("compute"));
        assertTrue(services.containsKey("image"));
        assertTrue(services.containsKey("identity"));
        assertTrue(services.containsKey("volume"));
        assertTrue(services.containsKey("network"));

        Map<String, SupportedVersion> versions = catalog.getResolvedVersionMap();
        System.out.println("Supported versions: " + versions.toString());
        assertNotNull(versions);
//        assertTrue(versions.containsKey("compute"));
//        assertNotNull(versions.get("compute"));
//       // assertTrue(versions.containsKey("image"));
//        assertNotNull(versions.get("image"));
        assertTrue(versions.containsKey("identity"));
        assertNotNull(versions.get("identity"));
//        assertTrue(versions.containsKey("volume"));
//        assertNotNull(versions.get("volume"));
//        assertTrue(versions.containsKey("network"));
//        assertNotNull(versions.get("network"));

        Map<String, String> urls = catalog.getResolvedURLMap();
        System.out.println("URLs: " + urls.toString());
        assertNotNull(urls);
        assertTrue(urls.containsKey("identity"));
        assertNotNull(urls.get("identity"));
        assertTrue(catalog.isServiceAvailable("identity"));
        assertFalse(catalog.isServiceAvailable("fake"));
        assertNotNull(catalog.resolvePackageNode("identity"));
        assertNull(catalog.resolvePackageNode("fake"));
        logout(context);
    }
    
    /**
     * Obtains the service catalog and then ensures that the core services that we need for cdp are resolved.
     * 
     * @throws ZoneException
     *             If anything fails
     */
    @Ignore
    @Test
    public void testServiceCatalogIntegration() throws ZoneException {
    	OpenStackContext context = login();
        ServiceCatalog catalog = context.getServiceCatalog();

        assertNotNull(catalog);
        Map<String, ServiceEntry> services = catalog.getServices();
        assertNotNull(services);

        System.out.println(catalog.toString());

        assertFalse(services.isEmpty());
        assertTrue(services.containsKey("compute"));
        assertTrue(services.containsKey("image"));
        assertTrue(services.containsKey("identity"));
        assertTrue(services.containsKey("volume"));
        assertTrue(services.containsKey("network"));

        Map<String, SupportedVersion> versions = catalog.getResolvedVersionMap();
        System.out.println("Supported versions: " + versions.toString());
        assertNotNull(versions);
//        assertTrue(versions.containsKey("compute"));
//        assertNotNull(versions.get("compute"));
//       // assertTrue(versions.containsKey("image"));
//        assertNotNull(versions.get("image"));
        assertTrue(versions.containsKey("identity"));
        assertNotNull(versions.get("identity"));
//        assertTrue(versions.containsKey("volume"));
//        assertNotNull(versions.get("volume"));
//        assertTrue(versions.containsKey("network"));
//        assertNotNull(versions.get("network"));

        Map<String, String> urls = catalog.getResolvedURLMap();
        System.out.println("URLs: " + urls.toString());
        assertNotNull(urls);
        assertTrue(urls.containsKey("compute"));
        assertNotNull(urls.get("compute"));
        assertTrue(urls.containsKey("image"));
        assertNotNull(urls.get("image"));
        assertTrue(urls.containsKey("identity"));
        assertNotNull(urls.get("identity"));
        assertTrue(urls.containsKey("volume"));
        assertNotNull(urls.get("volume"));
        assertTrue(urls.containsKey("network"));
        assertNotNull(urls.get("network"));

        assertTrue(catalog.isServiceAvailable("compute"));
        assertTrue(catalog.isServiceAvailable("image"));
        assertTrue(catalog.isServiceAvailable("identity"));
        assertTrue(catalog.isServiceAvailable("volume"));
        assertTrue(catalog.isServiceAvailable("network"));
        assertFalse(catalog.isServiceAvailable("fake"));

        assertNotNull(catalog.resolvePackageNode("compute"));
        assertNotNull(catalog.resolvePackageNode("image"));
        assertNotNull(catalog.resolvePackageNode("identity"));
        assertNotNull(catalog.resolvePackageNode("volume"));
        assertNotNull(catalog.resolvePackageNode("network"));
        assertNull(catalog.resolvePackageNode("fake"));

        logout(context);
    }
    
    @Test
    public void testConnectionOnly(){
           // test basic url
           String url = "http://simplestack:9000/identity/v2.0";             
           String value = ServiceCatalog.connectionOnly(url);         
           assertEquals("http://simplestack:9000", value);
           
           //test url with tenant id
           url = "http://simplestack:9000/v2.0/tenant_id";            
           value = ServiceCatalog.connectionOnly(url);          
           assertEquals("http://simplestack:9000", value);      

           //test url with tenant id
           url = "http://simplestack:9000/compute/v2.1/0a06842a-4ec4-4918-b046-399f6b38f5f9/servers/0a06842a-4ec4-4918-b046-399f6b38f5f9/action";             
           value = ServiceCatalog.connectionOnly(url);          
           assertEquals("http://simplestack:9000", value);            
           
           //test "identity" url with proxy host 
           url = "http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne/identity/v3";            
           value = ServiceCatalog.connectionOnly(url);          
           assertEquals("http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne",value);
           
           //test "compute" url with tenant id
           url = "http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne/compute/v2.1/0a06842a-4ec4-4918-b046-399f6b38f5f9/servers/0a06842a-4ec4-4918-b046-399f6b38f5f9/action";             
           value = ServiceCatalog.connectionOnly(url);          
           assertEquals("http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne", value);
           
         //test "compute" url with tenant id
           url = "http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne/v2.1/0a06842a-4ec4-4918-b046-399f6b38f5f9/servers/0a06842a-4ec4-4918-b046-399f6b38f5f9/compute/";             
           value = ServiceCatalog.connectionOnly(url);          
           assertEquals("http://10.0.14.1:9005/api/multicloud-titanium_cloud/v0/pod25_RegionOne/v2.1/0a06842a-4ec4-4918-b046-399f6b38f5f9/servers/0a06842a-4ec4-4918-b046-399f6b38f5f9", value);
    }
}
