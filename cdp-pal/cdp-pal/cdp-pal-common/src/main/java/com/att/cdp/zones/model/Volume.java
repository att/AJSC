/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A Volume represents a disk device which is attached to the server and made available to the instance when the server
 * is started. A volume may be initialized using an Image.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public class Volume extends ModelObject {

    /**
     * This enumeration is used to indicate the status of any volumes that may exist. Each provider may have its own
     * values and ways to indicate status, but the mapping to and from the RIME api uses these enumerated statuses.
     * 
     * @since Oct 14, 2013
     * @version $Id$
     */
    public enum Status {

        /**
         * The volume is currently being actively used. OpenStack calls this state "in-use"
         */
        ACTIVE,

        /**
         * The volume is being attached to a server but the operation has not yet been completed. OpenStack reports this
         * as "attaching"
         */
        ATTACHING,

        /**
         * The volume is being created and is not ready yet. OpenStack calls this state "creating"
         */
        CREATING,

        /**
         * The volume is being deleted but the operation has not yet completed. OpenStack reports this as "deleting"
         */
        DELETING,

        /**
         * The volume is being detached from a server but the operation has not yet been completed. OpenStack reports
         * this as "detaching".
         */
        DETACHING,

        /**
         * The volume is in an error state. OpenStack appends several sub-states to "error" such as "error-restoring" or
         * "error-deleting" which we ignore. We just report those as error.
         */
        ERROR,

        /**
         * The status of the volume cannot be determined. This can be caused by an error or invalid state. The volume
         * should not be used in this state.
         */
        INDETERMINATE,

        /**
         * The volume is being initialized but the operation has not been completed yet. OpenStack reports this as
         * "uploading"
         */
        INITIALIZING,

        /**
         * The volume is "available" for use, but is currently not in use by any servers. OpenStack calls this state
         * "available"
         */
        READY;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The availability zone of the volume
     */
    private String availabilityZone;

    /**
     * An optional description of the volume
     */
    private String description;

    /**
     * The ID of the volume, how it is uniquely identified
     */
    private String id;

    /**
     * the name of the volume. Note, this does not have to be unique
     */
    private String name;

    /**
     * The size of the volume
     */
    private Integer size;

    /**
     * The id of the snapshot that the volume is created from
     */
    private String snapshotId;

    /**
     * A list of snapshots that have been created from this volume, if any
     */
    private List<Snapshot> snapshots = new ArrayList<>();

    /**
     * The status of the volume
     */
    private Status status;

    /**
     * The volume type
     */
    private String volumeType;
    
    /**
     * The unitNumber
     */
    private String unitNumber;
    
    /**
     * The mountpoint for which the volume would be used. This field is used to spread the volumes across multiple data stores.
     */

    private String mount;
  
	/**
     * Creates a disconnected model of a Volume that can be initialized using the setters
     */
    public Volume() {
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Volume(Context context) {
        super(context);
    }

    /**
     * This constructor creates a disconnected model volume object that can be used to create a volume.
     * 
     * @param name
     *            The name of the volume
     * @param description
     *            The description of the volume
     * @param size
     *            The size, in GB of the requested volume
     */
    public Volume(String name, String description, Integer size) {
        this.name = name;
        this.description = description;
        this.size = size;
    }
    
    /**
     * This constructor creates a disconnected model volume object that can be used to create a volume.
     * 
     * @param name
     *            The name of the volume
     * @param description
     *            The description of the volume
     * @param size
     *            The size, in GB of the requested volume
     * @param mount
     * 			  The mount point of volume.
     */
    public Volume(String name, String description, Integer size, String mount) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.mount=mount;
    }

    /**
     * This method can be called to allow direct manipulation of the model objects and allow the volume to be destroyed
     * 
     * @throws ZoneException
     *             If the Volume is not navigable
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Volume other = (Volume) obj;

        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of availability zone
     * 
     * @return the availabilityZone value
     */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * JavaBean accessor to obtain the value of description
     * 
     * @return the description value
     */
    public String getDescription() {
        return description;
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of name
     * 
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * JavaBean accessor to obtain the value of size
     * 
     * @return the size value
     */
    public Integer getSize() {
        return size;
    }

    /**
     * JavaBean accessor to obtain the value of snapshotId
     * 
     * @return the snapshotId value
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * @return The list of snapshots created from this volume, if any. If no snapshots exist, an empty list is returned.
     */
    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public Status getStatus() {
        return status;
    }

    /**
     * JavaBean accessor to obtain the value of volumeType
     * 
     * @return the volumeType value
     */
    public String getVolumeType() {
        return volumeType;
    }
    
    /**
     * JavaBean accessor to obtain the value of volume unitNumber
     * 
     * @return the unitNumber value
     */
    public String getUnitNumber() {
  		return unitNumber;
  	}

    /**
     * Standard JavaBean mutator method to set the value of volume unitNumber
     * 
     * @param unitNumber
     *            the value to be set into unitNumber
     */
  	public void setUnitNumber(String unitNumber) {
  		this.unitNumber = unitNumber;
  	}
  	
  	

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * This method can be called to refresh the volume from the provider
     * 
     * @throws ZoneException
     *             If the volume cannot be refreshed
     */
    public void refresh() throws ZoneException {
        notConnectedError();
    }

    /**
     * @param availabilityZone
     *            the value for availabilityZone
     */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * Standard JavaBean mutator method to set the value of availabilityZone
     * 
     * @param availabilityZone
     *            the value to be set into availabilityZone
     */
    public void setAvailabiltyZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * Standard JavaBean mutator method to set the value of description
     * 
     * @param description
     *            the value to be set into description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Standard JavaBean mutator method to set the value of name
     * 
     * @param name
     *            the value to be set into name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Standard JavaBean mutator method to set the value of size
     * 
     * @param size
     *            the value to be set into size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Standard JavaBean mutator method to set the value of snapshotId
     * 
     * @param snapshotId
     *            the value to be set into snpashotId
     */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param volumeType
     *            the value for volumeType
     */
    public void setVolumeType(String volumeType) {
        this.volumeType = volumeType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Volume: id(%s), name(%s), desc(%s), created(%s), status(%s), size(%d), type(%s)", id,
            name, description, getCreatedDate().toString(), status, size, volumeType);
    }

	/**
	 
	 * @return the mount
	 */
	public String getMount() {
		return mount;
	}

	/**
	 * @param mount the mount to set
	 */
	public void setMount(String mount) {
		this.mount = mount;
	}
}
