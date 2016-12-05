/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Snapshot;

/**
 * This interface defines the snapshot services that are available from this service provider.
 * 
 * @since Mar 24, 2015
 * @version $Id$
 * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed as
 *             part of the volume service.
 */
@Deprecated
public interface SnapshotService extends Service {

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
     * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed
     *             as part of the volume service.
     */
    @Deprecated
    Snapshot createSnapshot(Snapshot template) throws ZoneException;

    /**
     * This method can be called to destroy a snapshot.
     * 
     * @param id
     *            The id of the snapshot to be destroyed.
     * @throws ZoneException
     *             If the snapshot cannot be destroyed.
     * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed
     *             as part of the volume service.
     */
    @Deprecated
    void destroySnapshot(String id) throws ZoneException;

    /**
     * Returns information about the snapshot with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the snapshot that we want to find information about
     * @return The snapshot if it exists
     * @throws ZoneException
     *             If the snapshot cannot be listed, or the snapshot does not exist
     * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed
     *             as part of the volume service.
     */
    @Deprecated
    Snapshot getSnapshot(String id) throws ZoneException;

    /**
     * Retrieves the list of snapshots defined for this service.
     * 
     * @return The list of snapshots for this tenant, if any. The list may be empty if there are no snapshots defined.
     * @throws ZoneException
     *             If the snapshot service cannot be accessed.
     * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed
     *             as part of the volume service.
     */
    @Deprecated
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
     * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed
     *             as part of the volume service.
     */
    @Deprecated
    List<Snapshot> getSnapshots(String name) throws ZoneException;
}
