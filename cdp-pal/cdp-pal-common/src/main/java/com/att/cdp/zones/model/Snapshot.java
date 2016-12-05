/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A Snapshot represents an image of a Snapshot which is made available across the provider tenant.
 * 
 * @since Mar 24, 2015
 * @version $Id$
 */
public class Snapshot extends ModelObject {

    /**
     * This enumeration is used to indicate the status of any snapshots that may exist. Each provider may have its own
     * values and ways to indicate status, but the mapping to and from the RIME api uses these enumerated statuses.
     * 
     * @since Mar 24, 2015
     * @version $Id$
     */
    public enum Status {

        /**
         * The snapshot is ready to be used. OpenStack calls this state "available"
         */
        AVAILABLE,

        /**
         * The snapshot is being created and is not ready yet. OpenStack calls this state "creating"
         */
        CREATING,

        /**
         * The snapshot is being deleted. OpenStack reports this as "deleting"
         */
        DELETING,

        /**
         * The snapshot is in an error state. OpenStack appends a sub-state to "error" such as "error-deleting" which we
         * ignore. We just report those as error.
         */
        ERROR,
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * An optional description of the snapshot
     */
    private String description;

    /**
     * The ID of the snapshot, how it is uniquely identified
     */
    private String id;

    /**
     * The name of the snapshot. Note, this does not have to be unique
     */
    private String name;

    /**
     * The size of the snapshot
     */
    private Integer size;

    /**
     * The status of the snapshot
     */
    private Status status;

    /**
     * The id of the volume the snapshot is based from
     */
    private String volumeId;

    /**
     * Creates a disconnected model of a Snapshot that can be initialized using the setters
     */
    public Snapshot() {
    }

    /**
     * This constructor creates a disconnected model snapshot object that can be used to create a Snapshot.
     * 
     * @param name
     *            The name of the snapshot
     * @param volumeId
     *            The id of the volume to snapshot
     */
    public Snapshot(String name, String volumeId) {
        this.name = name;
        this.volumeId = volumeId;
    }

    /**
     * This constructor creates a disconnected model snapshot object that can be used to create a Snapshot.
     * 
     * @param name
     *            The name of the snapshot
     * @param volumeId
     *            The id of the volume to snapshot
     * @param description
     *            The description of the snapshot
     */
    public Snapshot(String name, String volumeId, String description) {
        this.name = name;
        this.volumeId = volumeId;
        this.description = description;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Snapshot(Context context) {
        super(context);
    }

    /**
     * This method can be called to allow direct manipulation of the model objects and allow the snapshot to be
     * destroyed
     * 
     * @throws ZoneException
     *             If the Snapshot is not navigable
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Snapshot other = (Snapshot) obj;
        return id.equals(other.id);
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
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return The tenant that owns this Snapshot
     * @throws ZoneException
     *             If the Snapshot is not navigable
     */
    public Tenant getTenant() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * JavaBean accessor to obtain the value of volumeId
     * 
     * @return the volumeId value
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * This method can be called to refresh the snapshot from the provider
     * 
     * @throws ZoneException
     *             If the snapshot cannot be refreshed
     */
    public void refresh() throws ZoneException {
        notConnectedError();
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
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param volumeId
     *            the value for volumeId
     */
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Snapshot: id(%s), name(%s), desc(%s), created(%s), status(%s), size(%d), volume(%s)", id,
            name, description, getCreatedDate().toString(), status, size, volumeId);
    }
}
