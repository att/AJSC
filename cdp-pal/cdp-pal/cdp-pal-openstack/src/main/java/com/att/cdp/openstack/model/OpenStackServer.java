/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.ServerBootSource;
import com.att.cdp.zones.model.Volume;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedServer;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class represents the OpenStack implementation of the {@link Server} object.
 * <p>
 * OpenStack presents us with multiple status values. It reports the status of the server, the virtual machine, and the
 * power status. These values mean different things and overlap. They also provide different perspectives into the
 * status of the server definition as well as the instance that may (or may not) be running.
 * </p>
 * <p>
 * We need to map these status values into the API server lifecycle state model. This means that we need to interpret
 * these status values in a specific order, and apply the meanings to the appropriate state in the lifecycle. To do
 * this, we will first set the abstraction state of the server to PENDING. We then examine the task state to see if
 * there is any adjustment needed to change the state from PENDING to any other final state. This is needed because in
 * earlier versions of OpenStack, the state model was very confused, and they reported some final states as task states.
 * Later versions of OpenStack are working to correct this, so the exact meanings of the states has and is changing from
 * release to release.
 * </p>
 * <p>
 * Once the task state has been examined and used to update the server state (if needed), we then move on to the VM
 * state. This is the final state reported by OpenStack. However, we only process this state if the server state is NOT
 * pending, meaning that no task state indicates some task in process.
 * </p>
 * </p>
 * <p>
 * The meanings of the different status values are:
 * </p>
 * <h2>vmStatus</h2>
 * <p>
 * The VM state is the condition of the virtual machine AFTER any pending operations have completed. It is unchanged
 * until after any operations are completed, and will be the last state to change.
 * </p>
 * <dl>
 * <dt>ACTIVE</dt>
 * <dd>VM is running</dd>
 * <dt>BUILDING/INITIALIZED</dt>
 * <dd>VM is being constructed and only exists as a definition currently</dd>
 * <dt>PAUSED</dt>
 * <dd>The VM has been paused</dd>
 * <dt>SUSPENDED</dt>
 * <dd>The VM has been suspended to disk and is not currently running</dd>
 * <dt>STOPPED</dt>
 * <dd>The VM has been stopped</dd>
 * <dt>RESCUED</dt>
 * <dd>A rescue image is running with the original VM image</dd>
 * <dt>RESIZED</dt>
 * <dd>a VM with the new size is active.</dd>
 * <dt>SOFT_DELETED</dt>
 * <dd>VM is marked as deleted but the disk images still exist</dd>
 * <dt>DELETED/HARD_DELETED</dt>
 * <dd>VM is permanently deleted</dd>
 * <dt>ERROR</dt>
 * <dd>VM is not running and not runnable. Some unrecoverable error happened. Only delete is allowed to be called on the
 * VM.</dd>
 * </dl>
 * <h2>powerStatus</h2>
 * <dl>
 * <dt>0</dt>
 * <dd>Indeterminate</dd>
 * <dt>1</dt>
 * <dd>Running</dd>
 * <dt>3</dt>
 * <dd>Paused</dd>
 * <dt>4</dt>
 * <dd>Shutdown</dd>
 * <dt>6</dt>
 * <dd>Crashed</dd>
 * <dt>7</dt>
 * <dd>Suspended</dd>
 * </dl>
 * <h2>Task state/status</h2>
 * <dl>
 * <dt>ACTIVE</dt>
 * <dd>The definition is active, but the server may/may not be running</dd>
 * <dt>BUILD</dt>
 * <dd>The definition is being built</dd>
 * <dt>REBUILD</dt>
 * <dd>The definition is being rebuilt</dd>
 * <dt>RESIZE</dt>
 * <dd>The definition is being resized</dd>
 * <dt>VERIFY_RESIZE</dt>
 * <dd>The resize is awaiting verification</dd>
 * <dt>ERROR</dt>
 * <dd>The definition cannot be used (error)</dd>
 * </dl>
 * <p>
 * The process used to determine the server state as reflected by the API lifecycle is to first map the definition
 * status. This provides some indication if further mappings are needed. If the resulting state is READY, then we will
 * apply the mappings from the vmState. If the resulting mapping is NOT READY, then we stop with the value from the
 * status.
 * </p>
 * <p>
 * If the vmState results in STOPPED, we further check the power state. That is because the power state can tell us if
 * the vm crashed (the reason it is stopped). If it crashed, we report that as an ERROR state. Otherwise we retain the
 * mapping of stopped to "READY".
 * </p>
 * <p>
 * The model object allows the user to navigate to and reference the related objects. However, these relationships are
 * not populated until they are requested. Therefore, the model object implementation must track the fact that either
 * the relationships have been or have not been discovered, and delay doing that until a method that requires that
 * information is called. Once the relationships have been discovered, they are not requested from the provider again,
 * unless the service is called to refresh all of the model (which discards the object and builds a new one). This is a
 * "lazy" traversal of the transitive relationships.
 * </p>
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class OpenStackServer extends ConnectedServer {

    /**
     * This is a map of the server definition status values used by OpenStack to the API server status values.
     */
    //@formatter:off 
    @SuppressWarnings("nls")
    private static final String[][] TASK_STATE_STATUS_MAP = {
        {"ACTIVE",              Status.READY.toString()}, 
        {"BLOCK_DEVICE_MAPPING",Status.PENDING.toString()}, 
        {"BLOCK-DEVICE-MAPPING",Status.PENDING.toString()}, 
        {"BUILDING",            Status.PENDING.toString()}, 
        {"DELETING",            Status.PENDING.toString()}, 
        {"ERROR",               Status.ERROR.toString()}, 
        {"IMAGE_BACKINGUP",     Status.PENDING.toString()}, 
        {"IMAGE-BACKINGUP",     Status.PENDING.toString()}, 
        {"IMAGE_SNAPSHOTTING",  Status.PENDING.toString()} ,
        {"IMAGE-SNAPSHOTTING",  Status.PENDING.toString()} ,
        {"NETWORKING",          Status.PENDING.toString()}, 
        {"PAUSED",              Status.PAUSED.toString()}, 
        {"PAUSING",             Status.PENDING.toString()}, 
        {"POWERING_OFF",        Status.PENDING.toString()}, 
        {"POWERING-OFF",        Status.PENDING.toString()}, 
        {"POWERING_ON",         Status.PENDING.toString()}, 
        {"POWERING-ON",         Status.PENDING.toString()}, 
        {"REBOOTING",           Status.PENDING.toString()}, 
        {"REBUILDING",          Status.PENDING.toString()}, 
        {"REBUILD_SPAWNING",    Status.PENDING.toString()}, 
        {"REBUILD-SPAWNING",    Status.PENDING.toString()}, 
        {"RESCUING",            Status.PENDING.toString()}, 
        {"RESIZING",            Status.PENDING.toString()}, 
        {"RESIZE_CONFIRMING",   Status.PENDING.toString()}, 
        {"RESIZE-CONFIRMING",   Status.PENDING.toString()}, 
        {"RESIZE_FINISH",       Status.PENDING.toString()}, 
        {"RESIZE-FINISH",       Status.PENDING.toString()}, 
        {"RESIZE_MIGRATED",     Status.PENDING.toString()}, 
        {"RESIZE-MIGRATED",     Status.PENDING.toString()}, 
        {"RESIZE_MIGRATING",    Status.PENDING.toString()}, 
        {"RESIZE-MIGRATING",    Status.PENDING.toString()}, 
        {"RESIZE_PREP",         Status.PENDING.toString()}, 
        {"RESIZE-PREP",         Status.PENDING.toString()}, 
        {"RESIZE_REVERTING",    Status.PENDING.toString()}, 
        {"RESIZE-REVERTING",    Status.PENDING.toString()}, 
        {"RESUMING",            Status.PENDING.toString()}, 
        {"SCHEDULING",          Status.PENDING.toString()}, 
        {"SHUTOFF",             Status.READY.toString()}, 
        {"SPAWNING",            Status.PENDING.toString()}, 
        {"STARTING",            Status.PENDING.toString()}, 
        {"STOPPING",            Status.PENDING.toString()}, 
        {"SUSPENDED",           Status.SUSPENDED.toString()},
        {"SUSPENDING",          Status.PENDING.toString()}, 
        {"UPDATING_PASSWORD",   Status.PENDING.toString()}, 
        {"UPDATING-PASSWORD",   Status.PENDING.toString()}, 
        {"UNPAUSING",           Status.PENDING.toString()}, 
        {"UNRESCUING",          Status.PENDING.toString()}, 
        {"VERIFY_RESIZE",       Status.READY.toString()}, 
        {"VERIFY-RESIZE",       Status.READY.toString()}, 
        {null,                  Status.READY.toString()}, 
        {"",                    Status.READY.toString()}
    };

    /**
     * This is a map of the vm_state status values used by OpenStack for which we want to adjust the status. We're only
     * including the values here that are used to CHANGE the mapped definition status above. If the first phase mapping
     * (task state) did not result in READY, then that state is used as-is. If it did, then the VM_STATE is used to set
     * the final state.
     */
    @SuppressWarnings("nls")
    private static final String[][] VM_STATUS_MAP = {
        {"ACTIVE",              Status.RUNNING.toString()},
        {"RESCUED",             Status.RUNNING.toString()}, 
        {"INITIALIZED",         Status.PENDING.toString()}, 
        {"PAUSED",              Status.PAUSED.toString()}, 
        {"SUSPENDED",           Status.SUSPENDED.toString()}, 
        {"STOPPED",             Status.READY.toString()}, 
        {"DELETED",             Status.DELETED.toString()}, 
        {"SOFT_DELETED",        Status.DELETED.toString()}, 
        {"SOFT-DELETED",        Status.DELETED.toString()}, 
        {"HARD_DELETED",        Status.DELETED.toString()}, 
        {"HARD-DELETED",        Status.DELETED.toString()}, 
        {"ERROR",               Status.ERROR.toString()}
    };
    //@formatter:on 

    /**
     * The serial version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * A reference to the Nova server model that we can use to lazy load relationships, if needed.
     */
    private com.woorea.openstack.nova.model.Server novaModel;

    /**
     * This indicates if the volume attachments have been processed or not.
     */
    private AtomicBoolean volumeAttachmentsProcessed = new AtomicBoolean(false);

    /**
     * This indicates if the hypervisor attachment have been processed or not.
     */
    private AtomicBoolean hypervisorAttachmentProcessed = new AtomicBoolean(false);

    /**
     * This indicates if the images have been processed or not.
     */
    private AtomicBoolean imagesProcessed = new AtomicBoolean(false);

    /**
     * This indicates if the networks have been processed or not.
     */
    private AtomicBoolean networksProcessed = new AtomicBoolean(false);

    /**
     * @param context
     *            The open stack context we are servicing
     * @param server
     *            The open stack server object we are representing
     * @throws ZoneException
     *             If the server cant be mapped
     */
    @SuppressWarnings("nls")
    public OpenStackServer(Context context, com.woorea.openstack.nova.model.Server server) throws ZoneException {
        super(context);

        novaModel = server;

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        dictionary.put("created", "created");
        dictionary.put("updated", "updated");
        dictionary.put("hostId", "hostId");
        dictionary.put("userId", "userId");
        dictionary.put("instanceName", "instanceName");
        dictionary.put("keyName", "keyName");
        dictionary.put("diskConfig", "diskConfig");
        dictionary.put("configDrive", "drive");
        dictionary.put("availabilityZone", "availabilityZone");
        dictionary.put("launchedAt", "started");
        dictionary.put("terminatedAt", "stopped");

        ObjectMapper.map(server, this, dictionary);
        mapServerStatus(server);

        /*
         * And do the same for the template
         */
        com.woorea.openstack.nova.model.Flavor flavor = server.getFlavor();
        if (flavor != null) {
            setTemplate(new OpenStackTemplate(context, flavor));
        }

        /*
         * If there is a fault, then lets attach it too
         */
        com.woorea.openstack.nova.model.Server.Fault osFault = server.getFault();
        if (osFault != null) {
            OpenStackFault fault = new OpenStackFault(context, osFault);
            setFault(fault);
        }
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#attachVolume(com.att.cdp.zones.model.Volume, java.lang.String)
     */
    @Override
    public void attachVolume(Volume volume, String device) throws ZoneException {
        loadVolumeAttachments(getContext());
        super.attachVolume(volume, device);
        getVolumes().put(device, volume);
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#detachVolume(java.lang.String)
     */
    @Override
    public void detachVolume(String device) throws ZoneException {
        loadVolumeAttachments(getContext());
        super.detachVolume(device);
        getVolumes().remove(device);
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#getAttachments()
     */
    @Override
    public Map<String, String> getAttachments() throws ZoneException {
        loadVolumeAttachments(getContext());
        Map<String, String> map = new HashMap<>();

        for (String device : getVolumes().keySet()) {
            Volume volume = getVolumes().get(device);
            map.put(device, volume.getId());
        }
        // return super.getAttachments();
        return map;
    }

    /**
     * @see com.att.cdp.zones.model.Server#getVolumes()
     */
    @Override
    public Map<String, Volume> getVolumes() {
        try {
            loadVolumeAttachments(getContext());
        } catch (ZoneException e) {
            Logger logger = ConfigurationFactory.getConfiguration().getApplicationLogger();
            logger.error(EELFResourceManager.format(e));
        }
        return super.getVolumes();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getHypervisor()
     */
    @Override
    public Hypervisor getHypervisor() {
        try {
            loadHypervisorAttachment(getContext());
        } catch (ZoneException e) {
            Logger logger = ConfigurationFactory.getConfiguration().getApplicationLogger();
            logger.error(EELFResourceManager.format(e));
        }
        return super.getHypervisor();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getBootSource()
     */
    @Override
    public ServerBootSource getBootSource() {
        loadImageAndBootSource(getContext());
        return super.getBootSource();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getImage()
     */
    @Override
    public String getImage() {
        loadImageAndBootSource(getContext());
        return super.getImage();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getSnapshots()
     */
    @Override
    public List<Image> getSnapshots() {
        loadImageAndBootSource(getContext());
        return super.getSnapshots();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getFixedAddresses()
     */
    @Override
    public List<String> getFixedAddresses() {
        loadNetworks(getContext());
        return super.getFixedAddresses();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getFloatingAddresses()
     */
    @Override
    public List<String> getFloatingAddresses() {
        loadNetworks(getContext());
        return super.getFloatingAddresses();
    }

    /**
     * @see com.att.cdp.zones.model.Server#getNetworks()
     */
    @Override
    public List<Network> getNetworks() {
        loadNetworks(getContext());
        return super.getNetworks();
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#assignIpAddressFromPool(java.lang.String)
     */
    @Override
    public String assignIpAddressFromPool(String pool) throws ZoneException {
        loadNetworks(getContext());
        String ip = super.assignIpAddressFromPool(pool);
        super.getFloatingAddresses().add(ip);
        return ip;
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#getFixedIpsViaInterfaces()
     */
    @Override
    public List<String> getFixedIpsViaInterfaces() throws ZoneException {
        loadNetworks(getContext());
        List<String> addrs = super.getFixedIpsViaInterfaces();
        List<String> same = new ArrayList<>(addrs);
        List<String> diff = new ArrayList<>(addrs);
        if (same.retainAll(super.getFixedAddresses())) {
            diff.removeAll(same);
            super.getFixedAddresses().addAll(diff);
        }
        return addrs;
    }

    /**
     * @see com.att.cdp.zones.spi.model.ConnectedServer#releaseIpAddress(java.lang.String)
     */
    @Override
    public void releaseIpAddress(String address) throws ZoneException {
        super.releaseIpAddress(address);
        super.getFloatingAddresses().remove(address);
    }

    /**
     * This method is called to load the volume attachments, if they have not already been loaded. If they have been
     * loaded, then the call is ignored.
     * 
     * @param context
     *            The context that represents the connection we are servicing
     * @throws ZoneException
     *             If the attachments cannot be obtained, or if a volume cannot be listed, or a volume does not exist
     */
    private void loadVolumeAttachments(Context context) throws ZoneException {
        if (volumeAttachmentsProcessed.compareAndSet(false, true)) {
            VolumeService volumeService = context.getVolumeService();
            ComputeService computeService = context.getComputeService();
            Map<String, String> attachments = computeService.getAttachments(getId());
            for (Entry<String, String> entry : attachments.entrySet()) {
                Volume volume = volumeService.getVolume(entry.getValue());
                getVolumes().put(entry.getKey(), volume);
            }
        }
    }

    /**
     * This method is called to load the hypervisor attachment, if it has not already been loaded. If it has been
     * loaded, then the call is ignored.
     * 
     * @param context
     *            The context that represents the connection we are servicing
     * @throws ZoneException
     *             If the attachments cannot be obtained, or if a hypervisor cannot be listed, or a hypervisor does not
     *             exist
     */
    private void loadHypervisorAttachment(Context context) throws ZoneException {
        if (hypervisorAttachmentProcessed.compareAndSet(false, true)) {
            ComputeService computeService = context.getComputeService();
            List<Hypervisor> hypervisors = computeService.getHypervisors();

            if (this.novaModel.getHypervisorHostname() != null && !this.novaModel.getHypervisorHostname().isEmpty()) {
                String hypervisorName = this.novaModel.getHypervisorHostname();
                for (Hypervisor h : hypervisors) {
                    if (h.getHostName().equals(hypervisorName)) {
                        this.setHypervisor(h);
                        return;
                    }
                }
            }
        }
    }

    /**
     * This method loads the actual image object that the server was booted from, and also determines if there are any
     * snapshots of that image created from this server. If there are, it populates the snapshot list as well and sorts
     * that list into chronological order, most recent first.
     * 
     * @param context
     *            The context we are servicing
     */
    @SuppressWarnings("nls")
    private void loadImageAndBootSource(Context context) {
        if (imagesProcessed.compareAndSet(false, true)) {
            /*
             * Now, lookup the image and attach it to the server
             */
            Image bootImage = null;
            if (novaModel.getImage() != null) {
                try {
                    String serverImageId = novaModel.getImage().getId();
                    
                    LOG.info(new Date().toString()+":serverImageId(from novaModel)  :"+serverImageId);
                    ImageService imageService = context.getImageService();
                    List<Image> images = imageService.listImages();
                    LOG.info(new Date().toString()+":"+images);
                    for (Image image : images) {
                        if (image.getId().equals(serverImageId)) {
                        	 bootImage = image;
                            LOG.info(new Date().toString()+": bootImage :"+image.getId());
                        }
                        if (image.getImageType().equals(Image.Type.SNAPSHOT) && getId().equals(image.getInstanceId())) {
                        	 LOG.info(new Date().toString()+": Snapshot Image  :"+image.getId());
                            getSnapshots().add(image);
                            bootImage = image;
                           
                        }
                       
                    }
                } catch (Exception e) {
                    LOG.error(String.format("Unexpected exception %s retrieving images for server %s", e.getClass()
                        .getSimpleName(), getId()));
                    LOG.error(EELFResourceManager.format(e));
                }
            }

            /*
             * Sort the snapshots into chronological order (newest first) if any snapshots exist
             */
            if (!getSnapshots().isEmpty()) {
                Collections.sort(getSnapshots(), new Comparator<Image>() {
                    @Override
                    public int compare(Image o1, Image o2) {
                        if (o2.getCreatedDate().before(o1.getCreatedDate())) {
                            return -1;
                        } else if (o2.getCreatedDate().after(o1.getCreatedDate())) {
                            return 1;
                        }

                        return 0;
                    }
                });
            }

            if (bootImage == null) {
                setBootSource(ServerBootSource.UNKNOWN);
                setImage(null);
            } 
            else if(volumeAttachmentsProcessed.get()){
                setBootSource(ServerBootSource.VOLUME);
                setImage(null);
            }
            else {
                if (bootImage.getImageType().equals(Image.Type.SNAPSHOT)) {
                    setBootSource(ServerBootSource.SNAPSHOT);
                } else{
                setBootSource(ServerBootSource.IMAGE);
                }
                setImage(bootImage);
            }
        }
    }

    /**
     * This method lazily loads the networks and ip addresses of the server
     * 
     * @param context
     *            The context we are servicing
     */
    @SuppressWarnings("nls")
    private void loadNetworks(Context context) {
        if (networksProcessed.compareAndSet(false, true)) {
            try {
                NetworkService netService = context.getNetworkService();
                com.woorea.openstack.nova.model.Server.Addresses addresses = novaModel.getAddresses();
                if (addresses != null) {
                    for (Map.Entry<String, List<com.woorea.openstack.nova.model.Server.Addresses.Address>> entry : addresses
                        .getAddresses().entrySet()) {
                        String netName = entry.getKey();
                        try {
                            List<Network> nets = netService.getNetworksByName(netName);
                            if (!nets.isEmpty()) {
                                getNetworks().add(nets.get(0));
                            }
                            for (com.woorea.openstack.nova.model.Server.Addresses.Address osAddr : entry.getValue()) {
                                String type = osAddr.getType();

                                if (type != null) {
                                    if (type.equalsIgnoreCase("fixed")) {
                                        getFixedAddresses().add(osAddr.getAddr());
                                    } else {
                                        getFloatingAddresses().add(osAddr.getAddr());
                                    }
                                }
                            }
                        } catch (ZoneException e) {
                            LOG.error(EELFResourceManager.format(e));
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(String.format("Unexpected exception %s retrieving addresses for server %s", e.getClass()
                    .getSimpleName(), getId()));
                LOG.error(EELFResourceManager.format(e));
            }
        }
    }

    /**
     * This method is used to map the OpenStack server object status to the abstract server status definitions.
     * 
     * @param server
     *            The nova server to be examined to map the status
     */
    public void mapServerStatus(com.woorea.openstack.nova.model.Server server) {

        /*
         * Refresh the cached nova model with the new server object
         */
    	LOG.debug("OpenStackServer.mapServerStatus : in");
        novaModel = server;

        /*
         * Handle the task state mappings. There are several status' that OpenStack reports. It includes a server status
         * (of the definition, aka the task state), the power state, and the vm state. We will initially map the task
         * status, then adjust it (if it resulted in READY) based on the VM state, and finally we'll check the power
         * state to see if it indicates the server has crashed.
         */
        setStatus(Status.PENDING);
        String taskState = server.getTaskState(); // getStatus();
        if (taskState != null) {
            taskState = taskState.toUpperCase();
        }
        for (int index = 0; index < TASK_STATE_STATUS_MAP.length; index++) {
            String sourceState = TASK_STATE_STATUS_MAP[index][0];
            String targetState = TASK_STATE_STATUS_MAP[index][1];

            if (taskState == null) {
                if (sourceState == null) {
                    /*
                     * If the VM's task state AND the source state from the mappings table are both null, then set the
                     * abstract servers state to the target state from the table.
                     */
                    setStatus(Status.valueOf(targetState));
                    break;
                }
            } else {
                if (sourceState != null) {
                    if (sourceState.length() == 0) {
                        if (taskState.length() == 0) {
                            /*
                             * If the VM's task state and the mapping source state are not null AND they are both zero
                             * length (empty strings), then set the abstract servers state to the target state specified
                             * in the table.
                             */
                            setStatus(Status.valueOf(targetState));
                            break;
                        }
                    } else {
                        if (taskState.startsWith(sourceState)) {
                            /*
                             * If the VM's task state and the mapping source state are not null AND they are both
                             * non-zero length (not empty strings), AND the task state value starts with the value
                             * specified as the source state in the mapping table, then set the abstract servers state
                             * to the target state specified in the table.
                             */
                            setStatus(Status.valueOf(targetState));
                            break;
                        }
                    }
                }
            }

            /*
             * Any other condition just iterates the mapping table to find the next possible match. Whenever there is a
             * match based on the above conditions, the logic breaks out of this loop and sets the state of the VM as
             * set by the target state in the table.
             */
        } // end for

        /*
         * Only continue adjusting the status if we resulted in a READY status. Older versions of OpenStack reported
         * terminal states as part of the task state, and that caused confusion. With newer version of OpenStack, the
         * transient state (operations underway) is reported as task state, and the terminal (final) state of the VM is
         * reported in VM_STATE. If no operations are underway, then we map that to a READY state which we will need to
         * adjust based on the terminal state. If any operations are underway, we map that to PENDING, which we will
         * leave as is. Older versions of OpenStack are also supported using this approach, but the mapping tables must
         * have entries to accommodate their confused state reporting.
         */
        if (Status.READY.equals(getStatus())) {
            for (int index = 0; index < VM_STATUS_MAP.length; index++) {
                String vmState = server.getVmState().toUpperCase();
                if (vmState.startsWith(VM_STATUS_MAP[index][0])) {
                    setStatus(Status.valueOf(VM_STATUS_MAP[index][1]));
                    break;
                }
            }

            /*
             * If it is still ready state, then see if the reason it has stopped was a crash
             */
            if (Status.READY.equals(getStatus())) {
                if ("6".equals(server.getPowerState())) {
                    setStatus(Status.ERROR);
                }
            }
        }
    }
}
