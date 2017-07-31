/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.stub.WiremockStub;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkCapabilities;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Volume;
import com.woorea.openstack.cinder.model.VolumeForCreate;

public class TestVolumeService extends AbstractTestCase {
	/**
     * @throws ZoneException
     */
    @Test    
    @Ignore
    public void listVolumes() throws ZoneException {
        Context context = connect();
        VolumeService service = context.getVolumeService();
        List<Volume> volumes = service.getVolumes();

        assertNotNull(volumes);
        for (Volume volume : volumes) {
        	assertNotNull(service.getVolume(volume.getId()));
        	assertNotNull(service.getVolumes(volume.getName()));
            if (volume.getStatus().equals(Volume.Status.ERROR)) {
                System.out.printf("%-12s: %s\n", volume.getName(), volume.getId());
            }
        }
        
    }
    
    @Test  
    @Ignore
    public void listVolume() throws ZoneException {
        Context context = connect();
        VolumeService service = context.getVolumeService();
        String id="15afc83f-557c-42d6-8a23-f357bf767f61";
        Volume volumes = service.getVolume(id);
       
        assertNotNull(volumes);
        System.out.println("::DONE::");
         
    }
    
      
    @Test
    @Ignore
    public void CreateVolume() throws ZoneException {
        Context context = connect();
        Volume template = new Volume();              
        template.setSize(Integer.valueOf(2));
        template.setName("Volume");
        
        VolumeService service = context.getVolumeService();
        Volume vol=service.createVolume(template);

        assertNotNull(vol);
    
    }
    
    @Test   
    @Ignore
    public void DeleteVolume() throws ZoneException {
       Context context = connect();
       String id="";                                     
       VolumeService service = context.getVolumeService();
        service.destroyVolume(id);          
        }
    
    @Test
    @Ignore
    public void networkCapabilities() throws ZoneException {
    	WiremockStub.contextLogin();
        Context context = connect();
        NetworkCapabilities networkCapabilities = new OpenStackNetworkCapabilities(context);
        networkCapabilities.hasIPv4();
        networkCapabilities.hasIPv4();
        networkCapabilities.hasCIDR();
        networkCapabilities.hasPrivateSubnet();
        networkCapabilities.hasGateway();
        networkCapabilities.hasFWaaS();
        networkCapabilities.hasFirewallProtocol();
        networkCapabilities.hasFirewallSourcePort();
        networkCapabilities.hasFirewallDestPort();
        networkCapabilities.hasFirewallSourceHost();
        networkCapabilities.hasLBaaS();
        networkCapabilities.hasPingMonitor();
        networkCapabilities.hasConnectMonitor();
        networkCapabilities.hasHttpMonitor();
        networkCapabilities.hasHttpsMonitor();
      

    }
}
