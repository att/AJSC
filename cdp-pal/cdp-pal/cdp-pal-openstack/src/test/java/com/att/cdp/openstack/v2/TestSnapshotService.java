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
import com.att.cdp.zones.Context;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.model.Volume;

public class TestSnapshotService extends AbstractTestCase {
	/**
     * @throws ZoneException
     */
    @Test  
    @Ignore
    public void testSnapshots() throws ZoneException {
        Context context = connect();
        SnapshotService service = context.getSnapshotService();
        List<Snapshot> snapshots = service.getSnapshots();

        assertNotNull(snapshots);
        for (Snapshot snapshot : snapshots) {
          	assertNotNull(service.getSnapshot(snapshot.getId()));
        	assertNotNull(service.getSnapshots(snapshot.getName()));
        }
    }
    
    @Test(expected=Exception.class)
    @Ignore
    public void testSnapshot() throws ZoneException {
    	  Context context = connect();
          SnapshotService service = context.getSnapshotService();
          List<Snapshot> snapshots = service.getSnapshots();
          service.getSnapshot("Test");;
          service.getSnapshots("Test");
        
    }
    
    @Test  
    @Ignore
    public void listSnapshot() throws ZoneException {
        Context context = connect();
        SnapshotService service = context.getSnapshotService();
        String id="";
        Snapshot snap = service.getSnapshot(id);
       
        assertNotNull(snap);
      
         
    }
    
    
    @Test    
    @Ignore
    public void CreateSnapshot() throws ZoneException {
        Context context = connect();
        Snapshot temp = new Snapshot();  
        temp.setName("Snapshots");
        temp.setDescription("Test");
        temp.setVolumeId("42d6-8a23-f357bf767f61");
                                     
        SnapshotService service = context.getSnapshotService();
        Snapshot snap=service.createSnapshot(temp);

        assertNotNull(snap);
    
    }
    
    @Test  
    @Ignore
    public void DeleteSnapshot() throws ZoneException {
        Context context = connect();
       String id="9128-20ffac0c7ccc";
                                     
        SnapshotService service = context.getSnapshotService();
        service.destroySnapshot(id);     
       
        }
}
