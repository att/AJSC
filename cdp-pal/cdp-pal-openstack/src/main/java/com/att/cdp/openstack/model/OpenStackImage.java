/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.Date;
import java.util.HashMap;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedImage;

/**
 * This class implements an OpenStack specific implementation of the Image abstract object.
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class OpenStackImage extends ConnectedImage {
    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * This is a constant 2-dimension array of status codes reported by OpenStack to the status codes that we report in
     * the abstraction
     */
    @SuppressWarnings("nls")
    private static final String[][] STATUS_MAP = {
        {
            "active", Status.ACTIVE.toString()
        }, {
            "pending", Status.PENDING.toString()
        }, {
            "accepted", Status.PENDING.toString()
        }, {
            "saving", Status.PENDING.toString()
        }, {
            "rejected", Status.ERROR.toString()
        }, {
            "deleted", Status.DELETED.toString()
        },
    };

    /**
     * This is a constant 2-dimension array of the openstack image type constants to the enumerated value in the
     * abstraction and is used to set the enumerated value.
     */
    @SuppressWarnings("nls")
    private static final String[][] IMAGE_TYPE_MAP = {
        {
            null, Type.BASIC.toString()
        }, {
            "snapshot", Type.SNAPSHOT.toString()
        }
    };

    /**
     * @param context
     *            The open stack context we are servicing
     * @param osImage
     *            The open stack server object we are representing
     */
    @SuppressWarnings("nls")
    public OpenStackImage(Context context, com.woorea.openstack.nova.model.Image osImage) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("createdAt", "dateCreated");
        dictionary.put("deletedAt", "dateDeleted");
        dictionary.put("id", "id");
        dictionary.put("minDisk", "minimumDisk");
        dictionary.put("minRam", "minimumMemory");
        dictionary.put("name", "name");
        dictionary.put("owner", "owner");
        dictionary.put("properties", "metadata");
        dictionary.put("metadata", "metadata");
        dictionary.put("size", "size");
        // dictionary.put("status", "status");
        dictionary.put("updatedAt", "dateUpdated");
        dictionary.put("uri", "url");
        ObjectMapper.map(osImage, this, dictionary);

        // get data from
        if (osImage.getMetadata() != null) {
            setArchitecture(osImage.getMetadata().get("architecture"));
            setOsType(osImage.getMetadata().get("os_distro"));
        } else {
            setArchitecture(null);
            setOsType(null);
        }

        setStatus(Status.INDETERMINATE);
        for (int index = 0; index < STATUS_MAP.length; index++) {
            if (osImage.getStatus() != null && osImage.getStatus().toLowerCase().startsWith(STATUS_MAP[index][0])) {
                setStatus(Status.valueOf(STATUS_MAP[index][1]));
                break;
            }
        }

        setImageType(Image.Type.BASIC);
    }

    /**
     * This constructor creates the abstract mapping object from a Glance image object.
     * 
     * @param context
     *            The context we are servicing
     * @param osImage
     *            The glance image object
     */
    @SuppressWarnings("nls")
    public OpenStackImage(Context context, com.woorea.openstack.glance.model.Image osImage) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();

        dictionary.put("id", "id");
        dictionary.put("uri", "uri");
        dictionary.put("name", "name");
        dictionary.put("size", "size");
        // dictionary.put("createdAt", "createdDate");
        // dictionary.put("updatedAt", "updatedDate");
        // dictionary.put("deletedAt", "deletedDate");
        dictionary.put("minRam", "minimumMemory");
        dictionary.put("minDisk", "minimumDisk");
        dictionary.put("owner", "owner");
        dictionary.put("properties", "metadata");
        dictionary.put("metadata", "metadata");
        dictionary.put("instanceUUID", "instanceId");
        ObjectMapper.map(osImage, this, dictionary);

        // get data from
        // if (osImage.getMetadata() != null) {
        // setArchitecture(osImage.getMetadata().get("architecture"));
        // setOsType(osImage.getMetadata().get("os_distro"));
        // } else {
        setArchitecture(null);
        setOsType(null);
        // }

        if (osImage.getCreatedAt() != null) {
            setCreatedDate(osImage.getCreatedAt().getTime());
        } else {
            setCreatedDate(new Date());
        }

        if (osImage.getUpdatedAt() != null) {
            setUpdatedDate(osImage.getUpdatedAt().getTime());
        } else {
            setCreatedDate(null);
        }

        if (osImage.getDeletedAt() != null) {
            setDeletedDate(osImage.getDeletedAt().getTime());
        } else {
            setDeletedDate(null);
        }

        setStatus(Status.INDETERMINATE);
        for (int index = 0; index < STATUS_MAP.length; index++) {
            if (osImage.getStatus() != null && osImage.getStatus().toLowerCase().startsWith(STATUS_MAP[index][0])) {
                setStatus(Status.valueOf(STATUS_MAP[index][1]));
                break;
            }
        }

        setImageType(Type.BASIC);
        String osType = osImage.getImageType();
        for (String[] entry : IMAGE_TYPE_MAP) {
            if (osType == null) {
                if (entry[0] == null) {
                    setImageType(Type.valueOf(entry[1]));
                    break;
                }
            } else if (osType.equalsIgnoreCase(entry[0])) {
                setImageType(Type.valueOf(entry[1]));
                break;
            }
        }
    }
}
