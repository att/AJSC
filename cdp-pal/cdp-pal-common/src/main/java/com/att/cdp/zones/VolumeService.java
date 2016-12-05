/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.model.Volume;

/**
 * This interface defines the volume services that are available from this service provider. This is also known as
 * "block storage", and represents persistent disk space that can be attached to a compute resource.
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */
public interface VolumeService extends Service {

    /**
     * This method is used to create a new volume using the Volume object (disconnected) as the model. When the method
     * returns, a connected model object is returned that should be used for any model navigation desired.
     * 
     * @see Context Context for information about model navigation
     * @param template
     *            The template of the volume to create. This must contain at least a name and size. Other information
     *            will be supplied if not present. Any ID is ignored, and the ID of the created volume is returned in
     *            the connected volume object returned to the caller.
     * @return A <code>Volume</code> object that is connected to the service context and can be used to navigate the
     *         model.
     * @throws ZoneException
     *             If the volume cannot be created for some reason.
     */
    Volume createVolume(Volume template) throws ZoneException;

    /**
     * This method can be called to destroy a volume.
     * 
     * @param id
     *            The id of the volume to be destroyed.
     * @throws ZoneException
     *             If the volume cannot be destroyed.
     */
    void destroyVolume(String id) throws ZoneException;

    /**
     * Returns information about the volume with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the volume that we want to find information about
     * @return The volume if it exists
     * @throws ZoneException
     *             If the volume cannot be listed, or the volume does not exist
     */
    Volume getVolume(String id) throws ZoneException;

    /**
     * Retrieves the list of volumes defined for this service.
     * 
     * @return The list of volumes for this tenant, if any. The list may be empty if there are no volumes defined.
     * @throws ZoneException
     *             If the volume service cannot be accessed.
     */
    List<Volume> getVolumes() throws ZoneException;

    /**
     * Returns a list of volumes that match the supplied name
     * 
     * @param name
     *            The name pattern of the volumes to be located. The name is a regular expression that is suitable for
     *            use in the Java <code>String.matches()</code> method.
     * @return A list (potentially empty) of all volumes that match the specified name pattern
     * @see java.lang.String#matches(String)
     * @throws ZoneException
     *             If the volume service cannot be accessed.
     */
    List<Volume> getVolumes(String name) throws ZoneException;

    /**
     * This method is used to create a new snapshot using the Snapshot object (disconnected) as the model. When the
     * method returns, a connected model object is returned that should be used for any model navigation desired.
     * 
     * @see Context Context for information about model navigation
     * @param template
     *            The template of the snapshot to create. This must contain at least a name and volumeId. Other
     *            information will be supplied if not present. Any ID is ignored, and the ID of the created snapshot is
     *            returned in the connected volume object returned to the caller.
     * @return A <code>Snapshot</code> object that is connected to the service context and can be used to navigate the
     *         model.
     * @throws ZoneException
     *             If the snapshot cannot be created for some reason.
     */
    Snapshot createSnapshot(Snapshot template) throws ZoneException;

    /**
     * This method can be called to destroy a snapshot.
     * 
     * @param id
     *            The id of the snapshot to be destroyed.
     * @throws ZoneException
     *             If the snapshot cannot be destroyed.
     */
    void destroySnapshot(String id) throws ZoneException;

    /**
     * Returns information about the snapshot with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the snapshot that we want to find information about
     * @return The snapshot if it exists
     * @throws ZoneException
     *             If the snapshot cannot be listed, or the snapshot does not exist
     */
    Snapshot getSnapshot(String id) throws ZoneException;

    /**
     * Retrieves the list of snapshots defined for this service.
     * 
     * @return The list of snapshots for this tenant, if any. The list may be empty if there are no snapshots defined.
     * @throws ZoneException
     *             If the snapshot service cannot be accessed.
     */
    List<Snapshot> getSnapshots() throws ZoneException;

    /**
     * Returns a list of snapshots that match the supplied name
     * 
     * @param name
     *            The name pattern of the snapshots to be located. The name is a regular expression that is suitable for
     *            use in the Java <code>String.matches()</code> method.
     * @return A list (potentially empty) of all snapshots that match the specified name pattern
     * @see java.lang.String#matches(String)
     * @throws ZoneException
     *             If the snapshot service cannot be accessed.
     */
    List<Snapshot> getSnapshots(String name) throws ZoneException;

    /**
     * Returns a list of snapshots that were created from a specified volume.
     * 
     * @param id
     *            The id of the volume that the snapshots were created from
     * @return A list of snapshots, or an empty list if no snapshots exist for the specified volume.
     * @throws ZoneException
     *             If the snapshots cannot be obtained
     */
    List<Snapshot> getSnapshotsByVolume(String id) throws ZoneException;
}
