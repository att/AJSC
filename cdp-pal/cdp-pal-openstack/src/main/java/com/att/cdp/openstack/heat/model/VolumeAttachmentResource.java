/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.Map;

import com.att.cdp.pal.util.ObjectHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since Jun 19, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeAttachmentResource extends Resource {

    /**
     * The ID of the server to which the volume attaches.
     */
    @JsonProperty("instance_uuid")
    private Scalar instanceUUID;

    /**
     * The location where the volume is exposed on the instance. This assignment may not be honored and it is advised
     * that the path /dev/disk/by-id/virtio-<VolumeId> be used instead.
     */
    @JsonProperty("mountpoint")
    private Scalar mountPoint;

    /**
     * The ID of the volume to be attached.
     */
    @JsonProperty("volume-id")
    private Scalar volumeId;

    /**
     * Default no-arg constructor
     */
    public VolumeAttachmentResource() {

    }

    /**
     * @return the value of instanceUUID
     */
    public Scalar getInstanceUUID() {
        return instanceUUID;
    }

    /**
     * @param instanceUUID
     *            the value for instanceUUID
     */
    public void setInstanceUUID(Scalar instanceUUID) {
        this.instanceUUID = instanceUUID;
    }

    /**
     * @return the value of mountPoint
     */
    public Scalar getMountPoint() {
        return mountPoint;
    }

    /**
     * @param mountPoint
     *            the value for mountPoint
     */
    public void setMountPoint(Scalar mountPoint) {
        this.mountPoint = mountPoint;
    }

    /**
     * @return the value of volumeId
     */
    public Scalar getVolumeId() {
        return volumeId;
    }

    /**
     * @param volumeId
     *            the value for volumeId
     */
    public void setVolumeId(Scalar volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = (instanceUUID == null ? 0 : instanceUUID.hashCode());
        hash += (mountPoint == null ? 0 : mountPoint.hashCode());
        hash += (volumeId == null ? 0 : volumeId.hashCode());

        return hash;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, instance[%s], mount[%s], volume[%s]", super.toString(), instanceUUID, mountPoint,
            volumeId);
    }
}
