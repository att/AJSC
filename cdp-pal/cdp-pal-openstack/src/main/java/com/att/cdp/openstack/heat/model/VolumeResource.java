/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * @since Jun 19, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeResource extends Resource {

    /**
     * The availability zone in which the volume will be created.
     */
    @JsonProperty("availability_zone")
    private Scalar availabilityZone;

    /**
     * If specified, the backup to create the volume from.
     */
    @JsonProperty("backup_id")
    private Scalar backupId;

    /**
     * A description of the volume.
     */
    private Scalar description;

    /**
     * If specified, the name or ID of the image to create the volume from.
     */
    private Scalar image;

    /**
     * A name used to distinguish the volume.
     */
    private Scalar name;

    /**
     * Enables or disables read-only access mode of volume.
     */
    @JsonProperty("read_only")
    private Scalar readOnly;

    /**
     * Arbitrary key-value pairs specified by the client to help the Cinder scheduler creating a volume.
     */
    @JsonProperty("scheduler_hints")
    private Map<String, Scalar> schedulerHints;

    /**
     * The size of the volume in GB. On update only increase in size is supported.
     */
    private Scalar size;

    /**
     * If specified, the snapshot to create the volume from.
     */
    @JsonProperty("snapshot_id")
    private Scalar snapshotId;

    /**
     * If specified, the volume to use as source.
     */
    @JsonProperty("source_volid")
    private Scalar sourceVolid;

    /**
     * If specified, the type of volume to use, mapping to a specific backend.
     */
    @JsonProperty("volume_type")
    private Scalar volumeType;

    /**
     * DEPRECATED since 2014.1 (Icehouse) - Use property image.
     */
    private Scalar imageRef;

    /**
     * Default no-arg constructor
     */
    public VolumeResource() {

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = (name == null ? 0 : name.hashCode());
        hash += (availabilityZone == null ? 0 : availabilityZone.hashCode());
        hash += (image == null ? 0 : image.hashCode());

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
        // TODO Auto-generated method stub
        return super.toString();
    }

    /**
     * @return the value of availabilityZone
     */
    public Scalar getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * @param availabilityZone
     *            the value for availabilityZone
     */
    public void setAvailabilityZone(Scalar availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * @return the value of backupId
     */
    public Scalar getBackupId() {
        return backupId;
    }

    /**
     * @param backupId
     *            the value for backupId
     */
    public void setBackupId(Scalar backupId) {
        this.backupId = backupId;
    }

    /**
     * @return the value of description
     */
    public Scalar getDescription() {
        return description;
    }

    /**
     * @param description
     *            the value for description
     */
    public void setDescription(Scalar description) {
        this.description = description;
    }

    /**
     * @return the value of image
     */
    public Scalar getImage() {
        return image;
    }

    /**
     * @param image
     *            the value for image
     */
    public void setImage(Scalar image) {
        this.image = image;
    }

    /**
     * @return the value of name
     */
    public Scalar getName() {
        return name;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(Scalar name) {
        this.name = name;
    }

    /**
     * @return the value of readOnly
     */
    public Scalar isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly
     *            the value for readOnly
     */
    public void setReadOnly(Scalar readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return the value of schedulerHints
     */
    public Map<String, Scalar> getSchedulerHints() {
        return schedulerHints;
    }

    /**
     * @param schedulerHints
     *            the value for schedulerHints
     */
    public void setSchedulerHints(Map<String, Scalar> schedulerHints) {
        this.schedulerHints = schedulerHints;
    }

    /**
     * @return the value of size
     */
    public Scalar getSize() {
        return size;
    }

    /**
     * @param size
     *            the value for size
     */
    public void setSize(Scalar size) {
        this.size = size;
    }

    /**
     * @return the value of snapshotId
     */
    public Scalar getSnapshotId() {
        return snapshotId;
    }

    /**
     * @param snapshotId
     *            the value for snapshotId
     */
    public void setSnapshotId(Scalar snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * @return the value of sourceVolid
     */
    public Scalar getSourceVolid() {
        return sourceVolid;
    }

    /**
     * @param sourceVolid
     *            the value for sourceVolid
     */
    public void setSourceVolid(Scalar sourceVolid) {
        this.sourceVolid = sourceVolid;
    }

    /**
     * @return the value of volumeType
     */
    public Scalar getVolumeType() {
        return volumeType;
    }

    /**
     * @param volumeType
     *            the value for volumeType
     */
    public void setVolumeType(Scalar volumeType) {
        this.volumeType = volumeType;
    }

    /**
     * @return the value of imageRef
     */
    public Scalar getImageRef() {
        return imageRef;
    }

    /**
     * @param imageRef
     *            the value for imageRef
     */
    public void setImageRef(Scalar imageRef) {
        this.imageRef = imageRef;
    }
}
