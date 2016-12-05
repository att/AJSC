/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.CinderConnector;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.openstack.model.OpenStackSnapshot;
import com.att.cdp.openstack.util.ExceptionMapper;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.spi.AbstractSnapshot;
import com.att.cdp.zones.spi.RequestState;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.keystone.model.Access;

/**
 * This class implements the OpenStack snapshot service when the version is V1.0, which is actually provided as part of
 * Nova.
 * <p>
 * Later releases of OpenStack move this service out to a new service, named Cinder. The version supported is different
 * (V2.0) so the implementation of SnapshotService is different in that package.
 * </p>
 * 
 * @since Oct 9, 2013
 * @version $Id$
 */
public class OpenStackSnapshotService extends AbstractSnapshot {

    /**
     * The access object used to handle OpenStack authentication
     */
    protected Access access;

    /**
     * The OpenStack identity service implementation
     */
    protected IdentityService identity;

    /**
     * The OpenStack cinder connector object.
     */
    protected CinderConnector cinder;

    /**
     * The URL to the nova service
     */
    protected String cinderUrl;

    /**
     * Create the V2.0 snapshot service. This service uses the Cinder service.
     * 
     * @param context
     *            The context we are servicing
     */
    public OpenStackSnapshotService(Context context) {
        super(context);
    }

    /**
     * This is a helper method used to construct the Cinder service object and setup the environment to access the
     * OpenStack compute service (Cinder).
     * 
     * @throws NotLoggedInException
     *             If the user is not logged in
     * @throws ContextClosedException
     *             If the user attempts an operation after the context is closed
     */
    private void connect() throws NotLoggedInException, ContextClosedException {
        checkLogin();
        checkOpen();
        Context context = getContext();
        OpenStackContext osContext = (OpenStackContext) context;
        cinder = osContext.getCinderConnector();
        ((OpenStackContext) context).refreshIfStale(cinder);
    }

    /**
     * This method is used to create a new snapshot using the Snapshot object (disconnected) as the model. When the
     * method returns, a connected model object is returned that should be used for any model navigation desired.
     * 
     * @param template
     *            The template of the snapshot to create. This must contain at least a name and volumeId. Other
     *            information will be supplied if not present. Any ID is ignored, and the ID of the created snapshot is
     *            returned in the connected snapshot object returned to the caller.
     * @return A Snapshot object that is connected to the service context and can be used to navigate the model.
     * @throws ZoneException
     *             - If the snapshot cannot be created for some reason.
     * @see com.att.cdp.zones.SnapshotService#createSnapshot(com.att.cdp.zones.model.Snapshot)
     */
    @SuppressWarnings("nls")
    @Override
    public Snapshot createSnapshot(Snapshot template) throws ZoneException {
        checkArg(template, "template");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SNAPSHOT, template.getName());
        RequestState.put(RequestState.VOLUME, template.getVolumeId());
        RequestState.put(RequestState.SERVICE, "Volume");
        RequestState.put(RequestState.SERVICE_URL, cinder.getEndpoint());

        try {
            com.woorea.openstack.cinder.model.SnapshotForCreate newSnapshot =
                new com.woorea.openstack.cinder.model.SnapshotForCreate();
            HashMap<String, String> dictionary = new HashMap<>();
            dictionary.put("name", "name");
            dictionary.put("description", "description");
            dictionary.put("size", "size");
            dictionary.put("volumeId", "volumeId");
            ObjectMapper.map(template, newSnapshot, dictionary);

            OpenStackSnapshot snapshot =
                new OpenStackSnapshot(context, cinder.getClient().snapshots().create(newSnapshot).execute());
            return snapshot;
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null; // for the compiler
    }

    /**
     * This method can be called to destroy a snapshot.
     * 
     * @param id
     *            The id of the snapshot to be destroyed.
     * @throws ZoneException
     *             - If the snapshot cannot be destroyed.
     * @see com.att.cdp.zones.SnapshotService#destroySnapshot(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void destroySnapshot(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SNAPSHOT, id);
        RequestState.put(RequestState.SERVICE, "Volume");
        RequestState.put(RequestState.SERVICE_URL, cinder.getEndpoint());

        try {
            cinder.getClient().snapshots().delete(id).execute();
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }
    }

    /**
     * Returns information about the snapshot with the indicated id, if it exists.
     * 
     * @param id
     *            The id of the snapshot that we want to find information about
     * @return The snapshot if it exists
     * @throws ZoneException
     *             - If the snapshot cannot be listed, or the snapshot does not exist
     * @see com.att.cdp.zones.SnapshotService#getSnapshot(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public Snapshot getSnapshot(String id) throws ZoneException {
        checkArg(id, "id");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SNAPSHOT, id);
        RequestState.put(RequestState.SERVICE, "Volume");
        RequestState.put(RequestState.SERVICE_URL, cinder.getEndpoint());

        try {
            com.woorea.openstack.cinder.model.Snapshot snapshot = cinder.getClient().snapshots().show(id).execute();
            if (snapshot == null) {
                throw new ResourceNotFoundException(EELFResourceManager.format(OSMsg.PAL_OS_RESOURCE_NOT_FOUND, "Snapshot",
                    id, context.getProvider().getName()));
            }
            return new OpenStackSnapshot(context, snapshot);
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return null; // for the compiler
    }

    /**
     * Retrieves the list of snapshots defined for this service.
     * 
     * @return The list of snapshots for this tenant, if any. The list may be empty if there are no snapshots defined.
     * @see com.att.cdp.zones.SnapshotService#getSnapshots()
     */
    @Override
    public List<Snapshot> getSnapshots() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Volume");
        RequestState.put(RequestState.SERVICE_URL, cinder.getEndpoint());

        ArrayList<Snapshot> list = new ArrayList<>();
        try {
            com.woorea.openstack.cinder.model.Snapshots snapshots = cinder.getClient().snapshots().list(true).execute();
            for (com.woorea.openstack.cinder.model.Snapshot snap : snapshots) {
                list.add(new OpenStackSnapshot(context, snap));
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return list;
    }

    /**
     * Returns a list of snapshots that match the supplied name
     * 
     * @param name
     *            The name pattern of the snapshots to be located. The name is a regular expression that is suitable for
     *            use in the Java String.matches() method.
     * @return A list (potentially empty) of all snapshots that match the specified name pattern
     * @throws ZoneException
     *             - If the snapshot service cannot be accessed.
     * @see java.lang.String#matches(String)
     * @see com.att.cdp.zones.SnapshotService#getSnapshots(java.lang.String)
     */
    @Override
    public List<Snapshot> getSnapshots(String name) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SNAPSHOT, name);
        RequestState.put(RequestState.SERVICE, "Volume");
        RequestState.put(RequestState.SERVICE_URL, cinder.getEndpoint());

        ArrayList<Snapshot> list = new ArrayList<>();
        try {
            com.woorea.openstack.cinder.model.Snapshots snapshots = cinder.getClient().snapshots().list(true).execute();
            for (com.woorea.openstack.cinder.model.Snapshot snap : snapshots) {
                if (name != null) {
                    if (snap.getName() != null && snap.getName().matches(name)) {
                        list.add(new OpenStackSnapshot(context, snap));
                    }
                } else {
                    list.add(new OpenStackSnapshot(context, snap));
                }

            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return list;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return cinder.getEndpoint();
    }
}
