/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedSnapshot;

/**
 * @since Mar 24, 2015
 * @version $Id$
 */
public class OpenStackSnapshot extends ConnectedSnapshot {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * This is a simple 2-dimensional array of strings, where the first dimension is the OpenStack status code and the
     * second dimension is the SnapshotStatus code. Since openStack uses compound codes for error (e.g., error_deleting,
     * error_restoring, etc), the codes are only checked for equivalence for the length of the code (i.e., starts with
     * checking).
     */
    @SuppressWarnings("nls")
    private static final String[][] STATUS_TRANSLATE = { { "available", Status.AVAILABLE.toString() },
        { "creating", Status.CREATING.toString() }, { "deleting", Status.DELETING.toString() },
        { "error", Status.ERROR.toString() }, { "error_deleting", Status.ERROR.toString() } };

    /**
     * Create an open stack snapshot object
     * 
     * @param context
     *            The context we are servicing
     * @param snapshot
     *            The open stack snapshot object we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackSnapshot(Context context, com.woorea.openstack.nova.model.Snapshot snapshot) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        String status = snapshot.getStatus();
        if (status != null) {
            for (int i = 0; i < STATUS_TRANSLATE.length; i++) {
                if (status.startsWith(STATUS_TRANSLATE[i][0])) {
                    setStatus(Status.valueOf(STATUS_TRANSLATE[i][1]));
                    break;
                }
            }
        } else {
            setStatus(Status.ERROR);
        }
        dictionary.put("description", "description");
        dictionary.put("volumeId", "volumeId");
        dictionary.put("size", "size");
        ObjectMapper.map(snapshot, this, dictionary);

        String value = snapshot.getCreatedAt();
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
     * @param context
     *            The context we are servicing
     * @param snapshot
     *            The snapshot we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackSnapshot(Context context, com.woorea.openstack.cinder.model.Snapshot snapshot) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        String status = snapshot.getStatus();
        if (status != null) {
            for (int i = 0; i < STATUS_TRANSLATE.length; i++) {
                if (status.startsWith(STATUS_TRANSLATE[i][0])) {
                    setStatus(Status.valueOf(STATUS_TRANSLATE[i][1]));
                    break;
                }
            }
        } else {
            setStatus(Status.ERROR);
        }
        dictionary.put("description", "description");
        dictionary.put("volumeId", "volumeId");
        dictionary.put("size", "size");
        ObjectMapper.map(snapshot, this, dictionary);

        String value = snapshot.getCreatedAt();
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
     * This method can be called to allow direct manipulation of the model objects and allow the snapshot to be destroyed
     * 
     * @throws ZoneException
     *             - If the Snapshot is not navigable
     * @see com.att.cdp.zones.model.Snapshot#delete()
     */
    @Override
    public void delete() throws ZoneException {
        Context context = getContext();
        // context.getSnapshotService().destroySnapshot(getId());
        context.getVolumeService().destroySnapshot(getId());
    }
}
