/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkMetadata;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.LoadBalancerHealthMonitor;
import com.att.cdp.zones.model.LoadBalancerMember;
import com.att.cdp.zones.model.LoadBalancerPool;
import com.att.cdp.zones.model.LoadBalancerListener;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.ProtocolType;
import com.att.cdp.zones.model.Route;
import com.att.cdp.zones.model.Router;
import com.att.cdp.zones.model.Subnet;
import com.att.cdp.zones.model.LoadBalancerPool.AlgorithmType;


/**
 * This test is used to test NetworkService support.
 * <p>
 * This test should not be run as a normal part of the build. It's success depends on the accessibility to a suitable
 * OpenStack provider, proper credentials, available networks, and other environmental configurations that are not
 * likely to be present on the build system. This is a developer-supported and developer-used test only, and is not part
 * of the product certification test suite!
 * </p>
 * 
 * @since May 1, 2015
 * @version $Id$
 */

public class TestNetworkService extends AbstractTestCase {

    // Ensure the CDPTestNetwork does not already exist on provider. Relies on delete functioning properly
    // comment out to preserve the test networks on provider for debugging
    @Ignore
    @Before
    public final void setUp() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

       /* try {
            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            for (Network network : networks) {
                service.deleteNetwork(network);
            }
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail();
        }*/
    }

    // Ensure the test network has been removed from provider
    // comment out to preserve the test networks on provider for debugging
    @Ignore
    @After
    public final void tearDown() throws ZoneException {

        setUp();
    }

    /**
     * Verifies that we can list the existing networks on a provider. This test requires that the provider actually has
     * networks installed.
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Ignore
    @Test
    public void listNetworks() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            List<Network> networks = service.getNetworks();
            assertNotNull(networks);
            assertFalse(networks.isEmpty());
            for (Network network : networks) {
                System.out.println(network.toString());
                // assertNotNull(service.getNetworksByName(network.getName()));
                // assertNotNull(service.getNetworkById(network.getId()));
            }
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail();
        }
    }
    
    @Test
    @Ignore
    public void getNetworkMetadata() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
           NetworkMetadata metadata = service.getMetadata();
            assertNotNull(metadata);
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail();
        }
    }

    /**
     * Verifies that we can create a network on a provider.
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testCreateNetwork() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        // test network creation
        try {
            Network testNetwork = new Network("CDP_Test_Network");
            service.createNetwork(testNetwork);
            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test network");
        }
    }
    
    /**
     * Verifies that we can get ports
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testGetPorts() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();
        List<Port> ports = service.getPorts();
        assertNotNull(ports);
        assertFalse(ports.isEmpty());
 
    }
    
    /**
     * Verifies that we can get ports
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testGetRouters() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();
        List<Router> routers = service.getRouters();
        assertNotNull(routers);
        assertFalse(routers.isEmpty());
 
    }

    
    /**
     * Verifies that we can get floating IP Pools
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testFloatingIpPools() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();
        List<String> floatingIpPools = service.getFloatingIpPools();
        assertNotNull(floatingIpPools);
        assertFalse(floatingIpPools.isEmpty());
 
    }
    
    
    /**
     * Verifies that we can get floating IP Pools
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testSubnets() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();
        List<Subnet> subnets = service.getSubnets();
        assertNotNull(subnets);
        assertFalse(subnets.isEmpty());
        for (Subnet subnet : subnets) {
        	assertNotNull(service.getSubnetById(subnet.getId()));
        	assertNotNull(service.getSubnetsByName(subnet.getName()));
			
		}
 
    }
    /**
     * Test the ability to delete a network
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void testDeleteNetwork() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // ensure test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            // test network deletion
            for (Network network : networks) {
                if (network.getName().equalsIgnoreCase("CDP_Test_Network")) {
                    service.deleteNetwork(network);
                }
            }

            networks = service.getNetworksByName("CDP_Test_Network");
            assertTrue(networks.isEmpty());

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to delete the test network");
        }

    }

    /**
     * Verifies that we can create a subnet with a specified gateway IP address on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createIPV4SubnetWithGateway() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            Subnet subnetWithGateway = new Subnet();
            subnetWithGateway.setName("CDP_Test_IPV4_Subnet_With_Gateway");
            subnetWithGateway.setNetwork(testNetwork.getId());
            subnetWithGateway.setIpv4(true);
            subnetWithGateway.setRouting("10.10.10.0/24");
            subnetWithGateway.setGatewayIp("10.10.10.1");

            service.createSubnet(subnetWithGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV4_Subnet_With_Gateway");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("10.10.10.0/24"));
                assertTrue(subnet.getGatewayIp().equalsIgnoreCase("10.10.10.1"));
            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the ipv4 test subnet with gateway IP");
        }

    }

    /**
     * Verifies that we can create a subnet with a specified gateway IP address on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createIPV6SubnetWithGateway() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            Subnet ipv6SubnetWithGateway = new Subnet();
            ipv6SubnetWithGateway.setName("CDP_Test_IPV6_Subnet_With_Gateway");
            ipv6SubnetWithGateway.setNetwork(testNetwork.getId());
            ipv6SubnetWithGateway.setIpv4(false);
            ipv6SubnetWithGateway.setRouting("2001:db8:1234::/48");
            ipv6SubnetWithGateway.setGatewayIp("2001:db8:1234::1");

            service.createSubnet(ipv6SubnetWithGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV6_Subnet_With_Gateway");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("2001:db8:1234::/48"));
                assertTrue(subnet.getGatewayIp().equalsIgnoreCase("2001:db8:1234::1"));
            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the ipv6 test subnet with gateway IP");
        }

    }

    /**
     * Verifies that we can create a subnet with no gateway IP address on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createSubnetWithoutGateway() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            // create the subnet
            Subnet subnetWithoutGateway = new Subnet();
            subnetWithoutGateway.setName("CDP_Test_IPV4_Subnet_Without_Gateway");
            subnetWithoutGateway.setNetwork(testNetwork.getId());
            subnetWithoutGateway.setIpv4(true);
            subnetWithoutGateway.setRouting("10.10.20.0/24");
            subnetWithoutGateway.setGatewayIp(null);

            service.createSubnet(subnetWithoutGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV4_Subnet_Without_Gateway");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("10.10.20.0/24"));
                assertTrue(subnet.getGatewayIp() == null);
            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test subnet without gateway IP");
        }

    }

    /**
     * Verifies that we can create a subnet with DHCP disabled on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createSubnetWithDHCPDisabled() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            Subnet subnetWithGateway = new Subnet();
            subnetWithGateway.setName("CDP_Test_IPV4_Subnet_With_DHCP_Disabled");
            subnetWithGateway.setNetwork(testNetwork.getId());
            subnetWithGateway.setIpv4(true);
            subnetWithGateway.setRouting("10.10.30.0/24");
            subnetWithGateway.setGatewayIp("10.10.30.1");
            subnetWithGateway.setDhcp(false);

            service.createSubnet(subnetWithGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV4_Subnet_With_DHCP_Disabled");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("10.10.30.0/24"));
                assertTrue(subnet.getGatewayIp().equalsIgnoreCase("10.10.30.1"));
                assertFalse(subnet.isDhcp());
            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the ipv4 test subnet with DHCP disabled");
        }

    }

    /**
     * Verifies that we can create a subnet with a specified set of host routes on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createSubnetWithHostRoutes() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            Subnet subnetWithGateway = new Subnet();
            subnetWithGateway.setName("CDP_Test_IPV4_Subnet_With_Host_Routes");
            subnetWithGateway.setNetwork(testNetwork.getId());
            subnetWithGateway.setIpv4(true);
            subnetWithGateway.setRouting("10.10.40.0/24");
            subnetWithGateway.setGatewayIp("10.10.40.1");

            List<Route> hostRoutes = new ArrayList<Route>();
            Route newRoute1 = new Route();
            Route newRoute2 = new Route();
            newRoute1.setDestination("10.10.10.0/24");
            newRoute1.setNexthop("10.10.20.1");
            newRoute2.setDestination("10.10.30.0/24");
            newRoute2.setNexthop("10.10.40.1");
            hostRoutes.add(newRoute1);
            hostRoutes.add(newRoute2);
            subnetWithGateway.setHostRoutes(hostRoutes);

            service.createSubnet(subnetWithGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV4_Subnet_With_Host_Routes");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("10.10.40.0/24"));
                assertTrue(subnet.getGatewayIp().equalsIgnoreCase("10.10.40.1"));
                assertFalse(subnet.getHostRoutes().isEmpty());
                assertTrue(subnet.getHostRoutes().contains(newRoute1));
                assertTrue(subnet.getHostRoutes().contains(newRoute2));

            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the ipv4 test subnet with host routes");
        }

    }

    /**
     * Verifies that we can create a subnet with a specified set of DNS name servers on a provider
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Test
    @Ignore
    public void createSubnetWithDNSNameServers() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            // verify test network exists
            verifyTestNetworkHelper();

            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            assertNotNull(networks);
            assertFalse(networks.isEmpty());

            Network testNetwork = networks.get(0);

            Subnet subnetWithGateway = new Subnet();
            subnetWithGateway.setName("CDP_Test_IPV4_Subnet_With_DNS_Name_Servers");
            subnetWithGateway.setNetwork(testNetwork.getId());
            subnetWithGateway.setIpv4(true);
            subnetWithGateway.setRouting("10.10.50.0/24");
            subnetWithGateway.setGatewayIp("10.10.50.1");

            ArrayList<String> dnsNames = new ArrayList<String>();
            dnsNames.add("0.0.0.5");
            dnsNames.add("0.0.0.7");
            subnetWithGateway.setDns(dnsNames);

            service.createSubnet(subnetWithGateway);

            List<Subnet> subnets = service.getSubnetsByName("CDP_Test_IPV4_Subnet_With_DNS_Name_Servers");
            assertFalse(subnets.isEmpty());
            for (Subnet subnet : subnets) {
                assertTrue(subnet.getRouting().equalsIgnoreCase("10.10.50.0/24"));
                assertTrue(subnet.getGatewayIp().equalsIgnoreCase("10.10.50.1"));
                assertTrue(subnet.getDns().contains("0.0.0.5"));
                assertTrue(subnet.getDns().contains("0.0.0.7"));

            }

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the ipv4 test subnet with DNS name servers");
        }

    }

    /**
     * Helper function to create the base test network if it does not already exist. The base test network is needed for
     * all subnet tests This relies on createNetwork functioning properly
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    protected void verifyTestNetworkHelper() throws ZoneException {
        Context context = connect();
        NetworkService service = context.getNetworkService();

        try {
            List<Network> networks = service.getNetworksByName("CDP_Test_Network");
            if (networks.isEmpty()) {
                Network testNetwork = new Network("CDP_Test_Network");
                service.createNetwork(testNetwork);
                networks = service.getNetworksByName("CDP_Test_Network");
            }

            assertNotNull(networks);
            assertFalse(networks.isEmpty());

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test network");
        }
    }
    
  
    
    /**
     * Verifies all load balancer apis
     * 
     * @throws ZoneException
     *             If the connection fails, user is not authorized, or the provider cannot perform the operation.
     */
    @Ignore
    @Test
    public void testLoadBalancer() throws ZoneException {
    	
    	Context context = connect();
        NetworkService service = context.getNetworkService();
        String poolName = "TestNewPool";
        String tenantId = context.getTenant().getId();
        String subnetId = "2de8b5a0-1f7d-4736-8655-874c506e8f1b";
        
        //Step 1: Check if pool exists with this name
        LoadBalancerPool  loadBalancerPool = null;
        try {
	        List<LoadBalancerPool> loadBalancePools = service.getLoadBalancerPoolByName(poolName);
	        assertNotNull(loadBalancePools);
	       if (loadBalancePools.size() == 1) {
	    	   loadBalancerPool = loadBalancePools.get(0);
	       }  
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to find the pool with name: "+ze.getMessage());
        }
        
        //Step 2: If pool exists delete it
        if (loadBalancerPool != null) {
	        try {
	           service.deleteLoadBalancerPool(loadBalancerPool);
	           List<LoadBalancerPool> loadBalancePools = service.getLoadBalancerPoolByName(poolName);
	           assertNotNull(loadBalancePools);
		        assertEquals(loadBalancePools.size(),0);
	        } catch (ZoneException ze) {
	            ze.printStackTrace();
	            fail("Failed to delete the test load balancer pool: "+ze.getMessage());
	        }
        }   
        
        //Step 3: Create a new pool 
        try {
            LoadBalancerPool testlbPool = new LoadBalancerPool();
            testlbPool.setProtocol(ProtocolType.HTTP);
            testlbPool.setName(poolName);
            testlbPool.setSubnetId(subnetId); 
            testlbPool.setLbAlgorithm(AlgorithmType.ROUND_ROBIN);
            loadBalancerPool = service.createLoadBalancerPool(testlbPool); 
            LoadBalancerPool returnedLoadBalancerPool = service.getLoadBalancerPoolById(loadBalancerPool.getId());
            assertNotNull(returnedLoadBalancerPool);
          	assertEquals(returnedLoadBalancerPool.getName(), poolName);
      	} catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test load balancer pool: "+ze.getMessage());
        }
        
        //Step 4:  Create a new load balancer vip and assign to the pool
        LoadBalancerListener loadBalancerListener =  null;
        try {
        	LoadBalancerListener testlbVIP = new LoadBalancerListener();
            testlbVIP.setProtocol(ProtocolType.HTTP);
            testlbVIP.setName("TestNewVip");
            testlbVIP.setPoolId(loadBalancerPool.getId());
            testlbVIP.setSubnetId(subnetId);
            loadBalancerListener = service.createLoadBalancerListener(testlbVIP);
            LoadBalancerListener returnedloadBalancerVIP = service.getLoadBalancerListenerById(loadBalancerListener.getId());
            assertNotNull(returnedloadBalancerVIP);
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test load balancer VIP: "+ze.getMessage());
        }
        
        //Step 5: Fetch load balancer vips
        try {
        	 List<LoadBalancerListener> loadBalanceVIPs = service.getLoadBalancerListeners();
             assertNotNull(loadBalanceVIPs);
             for (LoadBalancerListener loadBalanceVIP : loadBalanceVIPs) {
             	assertNotNull(loadBalanceVIP.getId());
       		
     		}
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to fetch the test load balancer VIPs: "+ze.getMessage());
        }
        
       
        //Step 7: Create load balancer health monitor
        LoadBalancerHealthMonitor loadBalancerHealthMonitor  = null;
        try {
        	LoadBalancerHealthMonitor testlbHM = new LoadBalancerHealthMonitor();
            testlbHM.setType(ProtocolType.PING);
            testlbHM.setMaxRetries(1);
            loadBalancerHealthMonitor = service.createLoadBalancerHealthMonitor(testlbHM); 
            
            LoadBalancerHealthMonitor returnedloadBalancerHealthMonitor = service.getLoadBalancerHealthMonitorById(loadBalancerHealthMonitor.getId());
            assertNotNull(returnedloadBalancerHealthMonitor);
            assertNotNull(returnedloadBalancerHealthMonitor.getId());
      		
            List<LoadBalancerHealthMonitor> loadBalancerHealthMonitors= service.getLoadBalancerHealthMonitors();
            assertNotNull(loadBalancerHealthMonitors);
            for (LoadBalancerHealthMonitor monitor : loadBalancerHealthMonitors) {
            	assertNotNull(monitor.getId());
      		}

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test load balancer health monitor : "+ze.getMessage());
        }
        
        //associate the health monitor to pool
        try {
            
            service.associateLoadBalancerHealthMonitorWithPool(loadBalancerPool.getId(), loadBalancerHealthMonitor.getId());
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to associate health monitor to pool: "+ze.getMessage());
        }
        
        
        
        //Step 9:  Create a new load balancer member and assign to the pool
        LoadBalancerMember loadBalancerMember =  null;
        // test load balancer vip creation
        try {
            LoadBalancerMember testlbMember = new LoadBalancerMember();
            testlbMember.setAddress("135.144.122.19");
            testlbMember.setPoolId(loadBalancerPool.getId());
            loadBalancerMember = service.createLoadBalancerMember(testlbMember); 
            
            LoadBalancerMember returnedLoadBalancerMembers = service.getLoadBalancerMemberById(loadBalancerMember.getId());
            assertNotNull(returnedLoadBalancerMembers);
            
            List<LoadBalancerMember> loadBalancerMembers = service.getLoadBalancerMembers();
            assertNotNull(loadBalancerMembers);
            for (LoadBalancerMember loadBalancerMembe : loadBalancerMembers) {
            	assertNotNull(loadBalancerMembe.getId());
      		
    		}

        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to create the test load balancer member : "+ze.getMessage());
        }
        
        //Step 10: Fetch load balancer member
        try {
        	List<LoadBalancerMember> loadBalanceMembers = service.getLoadBalancerMembers();
            assertNotNull(loadBalanceMembers);
            for (LoadBalancerMember loadBalanceMember : loadBalanceMembers) {
            	assertNotNull(loadBalanceMember.getId());
      		
    		}
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to fetch the test load balancer members: "+ze.getMessage());
        }
        
       
 
        //Step 6: Delete load balancer vip
        try {
            assertNotNull(loadBalancerListener);
            // test network deletion
            service.deleteLoadBalancerListener(loadBalancerListener);
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to delete the test load balancer vip: "+ze.getMessage());
        }
        
        //disassociate the health monitor to pool
        try {
            
            service.disassociateLoadBalancerHealthMonitorWithPool(loadBalancerPool.getId(), loadBalancerHealthMonitor.getId());
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to disassociate health monitor to pool: "+ze.getMessage());
        }
        
       
        //Step 11: Delete load balancer member
        try {
        	assertNotNull(loadBalancerMember);
        	
            // test network deletion
            service.deleteLoadBalancerMember(loadBalancerMember);
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to delete the test load balancer member: "+ze.getMessage());
        }
        
        
        
        
        //Step 12: pool delete 
        if (loadBalancerPool != null) {
	        try {
	           service.deleteLoadBalancerPool(loadBalancerPool);
	           List<LoadBalancerPool> loadBalancePools = service.getLoadBalancerPoolByName(poolName);
		        assertNotNull(loadBalancePools);
	        } catch (ZoneException ze) {
	            ze.printStackTrace();
	            fail("Failed to delete the test load balancer pool: "+ze.getMessage());
	        }
        }  
        
       //Step 8: Delete load balancer health monitor
        try {
            
            LoadBalancerHealthMonitor loadBalancerHM = service.getLoadBalancerHealthMonitorById(loadBalancerHealthMonitor.getId());
            assertNotNull(loadBalancerHM);
          
            // test network deletion
            service.deleteLoadBalancerHealthMonitor(loadBalancerHM);
           
        } catch (ZoneException ze) {
            ze.printStackTrace();
            fail("Failed to delete the test load balancer health montor: "+ze.getMessage());
        }
    }
    
    @Test
    @Ignore
    public void testCreateAndDeletePort() throws ZoneException {

        Context context = connect();
        NetworkService service = context.getNetworkService();
        String poolName = "TestNewPool";
        String tenantId = context.getTenant().getId();
        String networkId = "64e612b4-0a2a-4073-b408-66a3ef27aef7";
        String subnetId = "b124ab37-a285-4b8f-848b-e485c062594c";

        Subnet subnet = service.getSubnetById(subnetId);
        assertNotNull(subnet);
        Port port = service.createPort(subnet);

        assertNotNull(port);

        port.delete();
    }

}
