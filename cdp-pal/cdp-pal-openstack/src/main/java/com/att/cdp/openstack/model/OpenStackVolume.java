/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.CinderConnector;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedVolume;

/**
 * @since Oct 9, 2013
 * @version $Id$
 */
public class OpenStackVolume extends ConnectedVolume {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * This is a simple 2-dimensional array of strings, where the first dimension is the OpenStack status code and the
     * second dimension is the VolumeStatus code. Since openStack uses compound codes for error (e.g., error_deleting,
     * error_restoring, etc), the codes are only checked for equivalence for the length of the code (i.e., starts with
     * checking).
     */
    private static final String[][] STATUS_TRANSLATE = { { "available", Status.READY.toString() },
        { "in-use", Status.ACTIVE.toString() }, { "error", Status.ERROR.toString() },
        { "creating", Status.CREATING.toString() }, { "deleting", Status.DELETING.toString() },
        { "attaching", Status.ATTACHING.toString() }, { "detaching", Status.DETACHING.toString() },
        { "uploading", Status.INITIALIZING.toString() }, };

    /**
     * Create an open stack volume object
     * 
     * @param context
     *            The context we are servicing
     * @param volume
     *            The open stack volume object we are mapping
     */
    
    public OpenStackVolume(Context context, com.woorea.openstack.nova.model.Volume volume) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        String status = volume.getStatus();
        if (status != null) {
            for (int i = 0; i < STATUS_TRANSLATE.length; i++) {
                if (status.startsWith(STATUS_TRANSLATE[i][0])) {
                    setStatus(Status.valueOf(STATUS_TRANSLATE[i][1]));
                    break;
                }
            }
        } else {
            setStatus(Status.INDETERMINATE);
        }
        dictionary.put("description", "description");
        dictionary.put("volumeType", "volumeType");
        dictionary.put("size", "size");
        dictionary.put("snapshotId", "snapshotId");
        dictionary.put("availabilityZone", "availabilityZone");
        ObjectMapper.map(volume, this, dictionary);

        String value = volume.getCreatedAt();
        if (value != null) {
            try {
                Date date = DateFormat.getDateTimeInstance().parse(value);
                setCreatedDate(date);
            } catch (ParseException e) {
                setCreatedDate(new Date());
            }
        }
    }
    
    
    public OpenStackVolume(Context context, com.woorea.openstack.cinder.model.Volume volume) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        String status = volume.getStatus();
        if (status != null) {
            for (int i = 0; i < STATUS_TRANSLATE.length; i++) {
                if (status.startsWith(STATUS_TRANSLATE[i][0])) {
                    setStatus(Status.valueOf(STATUS_TRANSLATE[i][1]));
                    break;
                }
            }
        } else {
            setStatus(Status.INDETERMINATE);
        }
        dictionary.put("description", "description");
        dictionary.put("volumeType", "volumeType");
        dictionary.put("size", "size");
        dictionary.put("snapshotId", "snapshotId");
        dictionary.put("availability_zone", "availability_zone");        
        dictionary.put("status","status");
        dictionary.put("source_volid","source_volid");
        dictionary.put("os_vol_host_attr_host","os_vol_host_attr_host");
        dictionary.put("os_vol_tenant_attr_tenant_id","os_vol_tenant_attr_tenant_id");
        dictionary.put("bootable", "bootable");     
        dictionary.put("volume_type","volume_type");
                
        ObjectMapper.map(volume, this, dictionary);

        String value = volume.getCreatedAt();
        if (value != null) {
            try {
                Date date = DateFormat.getDateTimeInstance().parse(value);
                setCreatedDate(date);
            } catch (ParseException e) {
                setCreatedDate(new Date());
            }
        }
    }

    /**
     * This method is used to locate all of the snapshots created from this volume (if any) and attach them to the model
     * object
     * 
     * @throws ZoneException
     */
    private void associateSnapshots(Context context) throws ZoneException {
        VolumeService service = context.getVolumeService();
        getSnapshots().addAll(service.getSnapshotsByVolume(getId()));
    }

    /**
     * This method can be called to allow direct manipulation of the model objects and allow the volume to be destroyed
     * 
     * @throws ZoneException
     *             - If the Volume is not navigable
     * @see com.att.cdp.zones.model.Volume#delete()
     */
    @Override
    public void delete() throws ZoneException {
        Context context = getContext();
        context.getVolumeService().destroyVolume(getId());
    }
}
