/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A server is the definition of a virtual machine (and also represents the running image of that server) that requires
 * a specific hardware environment (specified by a template), boots from a specific disk image (an Image), is a member
 * of a specific tenant (the Tenant), and may be accessed over one or more subnets connected to ports (NICs).
 * <p>
 * The server class can also be used to create a new server. In that case, the server is instantiated and initialized
 * with a name, template, an image, and optionally an ACL name). The server is then used as a prototype to create the
 * actual server using the specified image, template, and optional ACL.
 * </p>
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
@JsonRootName("server")
public class Server extends ModelObject {

    /**
     * The status of the server. This status is a composite view of the server. Different implementations may report
     * status differently, and may report multiple status values of various sub-systems. The API implementation must
     * aggregate these status values into a single status defined in this enumeration, and which represents the server
     * definition and/or instance (if the server is running).
     * 
     */
    public enum Status {

        /**
         * The server definition has been deleted. Not all implementations may support this state. If this state is
         * reported, the server may not be returned to any other state.
         */
        DELETED,

        /**
         * The server is in an error state and cannot be used. It may be returned to the pending state if there are
         * modifications made, or to the running state if it simply failed and has been restarted.
         */
        ERROR,

        /**
         * The server is being built or rebuilt, or it is in some state of flux and cannot be operated upon. It is not
         * active nor can it currently be used. This state applies to any condition where the server is not usable,
         * possibly because of structural changes being made, such as resizing.
         */
        PENDING,

        /**
         * The server is ready for use but is not currently running anywhere. This state indicates that the definition
         * is valid, it can be manipulated or changed, and it can be started but is currently not running.
         */
        READY,

        /**
         * The server is ready and it is running in at least one instance
         */
        RUNNING,

        /**
         * The server was running but has been suspended. This means that the virtual machine has been swapped to disk
         * and is not currently in a runnable state, but the VM still exists and can be resumed at some point in time.
         * Not all implementations may support suspend/resume.
         */
        SUSPENDED,

        /**
         * The server was running but was paused. A paused server is simply frozen and not dispatchable on the
         * hypervisor. It can be unpaused and continued. It is NOT swapped to disk and technically is still in a
         * runnable state, just not being actively dispatched. This is a variation of suspended that some IaaS providers
         * may offer. Not all implementations may support pause/unpause.
         */
        PAUSED;
    }

    /**
     * The serial version id of this class
     */
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * The list of access control lists (ACL's) assigned to this server to control access.
     */
    private List<ACL> accessControl;

    /**
     * The availability zone of the server
     */
    private String availabilityZone;

    /**
     * The source of the boot image for this server
     */
    private ServerBootSource bootSource;

    /**
	 * 
	 */
    private String diskConfig;

    /**
     * the drive
     */
    private String drive;

    /**
     * The fault descriptor if there is a problem with this server. It will be null if there is no fault.
     */
    private Fault fault;

    /**
     * A list of all of the fixed ip addresses assigned to the server (if any)
     * 
     * @deprecated use {@link Port} to obtain IP addresses for a specific NIC card on a specific subnet
     */
    @Deprecated
    private List<String> fixedAddresses = new ArrayList<>();

    /**
     * A list of all of the floating IP addresses for this server (if any)
     */
    private List<String> floatingAddresses = new ArrayList<>();

    /**
     * The id of the real machine that this VM exists on, if known
     */
    private String hostId;

    /**
     * The hypervisor where the sever is running if available
     */
    private Hypervisor hypervisor;

    /**
     * The unique identification of this server definition
     */
    private String id;

    /**
     * The id of the image used to create this host
     */
    private Image image;

    /**
     * An optional script that can be executed when the server is created.
     */
    private String initScript;

    /**
     * The fully qualified name of the virtual machine
     */
    private String instanceName;

    /**
     * The name of the Key-Pair definition to be used when this server is booted
     */
    private String keyName;

    /**
     * The name of the server. Typically, this is how the server is seen in any consoles or user interfaces.
     */
    private String name;

    /**
     * The list of networks that this server is attached to (virtual NIC interfaces)
     * 
     * @deprecated use {@link #ports} instead.
     */
    @Deprecated
    private List<Network> networks = new ArrayList<>();

    /**
     * The list of ports to be attached to the server when it is created. This list is not used after the creation of
     * the server.
     */
    private List<Port> ports;

    /**
     * A list of snapshots, in chronological order (newest first) that were created from this server, or an empty list
     * if no snapshots exist.
     */
    private List<Image> snapshots = new ArrayList<>();

    /**
     * The timestamp (expressed as a String) when the vm was started. Note, the specific provider may or may not have
     * this information available. If not available, it will be null.
     */
    private String started;

    /**
     * The status of the server. This status reflects the condition of the definition as well as its running state, if
     * any.
     */
    private Status status;

    /**
     * The timestamp (expressed as a String) when the vm was stopped. Note, the specific provider may or may not have
     * this information available. If not available, it will be null.
     */
    private String stopped;

    /**
     * The definition of the vm or hardware platform required
     */
    private Template template;

    /**
     * The user used to log in to this machine, if known
     */
    private String userId;

    /**
     * A mapping of all volumes attached to the server, keyed by the device name.
     */
    private Map<String, Volume> volumes = new HashMap<>();

    /**
     * A default no-arg constructor
     */
    public Server() {
        status = Status.PENDING;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Server(Context context) {
        super(context);
    }

    /**
     * A constructor that takes only a name
     * 
     * @param name
     *            The name of the server
     */
    public Server(String name) {
        this();
        this.name = name;
    }

    /**
     * This constructor can be used to create a Server object suitable for requesting the creation of the host.
     * 
     * @param name
     *            The name of the server to be created
     * @param image
     *            The reference to the Image to be used
     * @param template
     *            The reference to the Template to be used
     */
    public Server(String name, Image image, Template template) {
        this();
        this.name = name;
        this.image = image;
        this.template = template;
    }

    /**
     * Creates a template server object suitable for creating a new server that specifies the name, image, template, and
     * access control list(s) to be associated with the new server.
     * 
     * @param name
     *            The name of the server to be created
     * @param image
     *            The reference to the Image id to be used
     * @param template
     *            The reference to the Template id to be used
     * @param accessControl
     *            The access control list(s) to be assigned to the server
     */
    public Server(String name, Image image, Template template, List<ACL> accessControl) {
        this(name, image, template);
        this.accessControl = accessControl;
    }

    /**
     * This constructor can be used to create a Server object suitable for requesting the creation of the host.
     * 
     * @param name
     *            The name of the server to be created
     * @param imageId
     *            The reference to the Image id to be used
     * @param templateId
     *            The reference to the Template id to be used
     */
    public Server(String name, String imageId, String templateId) {
        this(name);
        this.image = new Image();
        this.image.setId(imageId);
        this.template = new Template();
        this.template.setId(templateId);
    }

    /**
     * Creates a template server object suitable for creating a new server that specifies the name, image, template, and
     * access control list(s) to be associated with the new server.
     * 
     * @param name
     *            The name of the server to be created
     * @param image
     *            The reference to the Image id to be used
     * @param template
     *            The reference to the Template id to be used
     * @param aclName
     *            The name of the access control list that we want to associate with the new server.
     */
    public Server(String name, String image, String template, String aclName) {
        this(name, image, template);
        accessControl.add(new ACL(aclName));
    }

    /**
     * Adds a network to the server
     * 
     * @param network
     *            the name of the network to be attached, if any
     * @deprecated use { #addPort(Port)} instead.
     */
    @Deprecated
    public void addNetwork(Network network) {
        if (!networks.contains(network)) {
            networks.add(network);
        }
    }

    /**
     * This method is called to assign the server the specified IP address
     * 
     * @param ip
     *            An ip address to be assigned
     * @throws ZoneException
     *             If the address cannot be assigned
     */
    public void assignIp(String ip) throws ZoneException {
        notConnectedError();
    }

    /**
     * Assigns an IP address from the first pool of floating ip addresses
     * 
     * @return The assigned IP address
     * @throws ZoneException
     *             If the address cannot be assigned
     */
    public String assignIpAddressFromPool() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Assigns an IP address from the named pool of floating ip addresses
     * 
     * @param pool
     *            The name of the floating IP address pool to reserve the address from
     * @return The assigned IP address
     * @throws ZoneException
     *             If the address cannot be assigned
     */
    public String assignIpAddressFromPool(String pool) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * Attaches a volume to this server
     * 
     * @param volume
     *            The volume to be attached
     * @param device
     *            The symbolic device name (/dev/sdc, for example)
     * @throws ZoneException
     *             If the server cannot be resumed
     */
    public void attachVolume(Volume volume, String device) throws ZoneException {
        notConnectedError();
    }

    /**
     * Requests the creation of a snapshot image of this server
     * 
     * @param name
     *            The name of the snapshot that will be created
     * @throws ZoneException
     *             If the server cannot be snapshot-ed
     */
    public void createSnapshot(String name) throws ZoneException {
        notConnectedError();
    }

    /**
     * Delete this server using it's id.
     * 
     * @throws ZoneException
     *             If the server does not exist or cannot be deleted for some reason.
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * Detaches the indicated volume (specified by the device) from this server
     * 
     * @param device
     *            The device to detach
     * @throws ZoneException
     *             If the volume cannot be detached
     */
    public void detachVolume(String device) throws ZoneException {
        notConnectedError();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Server other = (Server) obj;

        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of accessControl
     * 
     * @return the accessControl value
     */
    public List<ACL> getAccessControl() {
        if (accessControl == null) {
            return null;
        }
        return Collections.unmodifiableList(accessControl);
    }

    /**
     * @return A map of volume attachments to the server. The map is keyed by the device name. The value of the map is
     *         the id of the volume.
     * @throws ZoneException
     *             If the attachments cannot be obtained
     * @deprecated Use {@link #getVolumes()} instead
     */
    @JsonIgnore
    @Deprecated
    public Map<String, String> getAttachments() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * JavaBean accessor to obtain the value of availabilityZone
     * 
     * @return The name of the availability zone for this server
     */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * Returns the source of the boot image for this server.
     * 
     * @return One of the enumerated constants indicating the source of the boot for the server
     */
    public ServerBootSource getBootSource() {
        return bootSource;
    }

    /**
     * JavaBean accessor to obtain the value of diskConfig
     * 
     * @return the diskConfig value
     */
    public String getDiskConfig() {
        return diskConfig;
    }

    /**
     * JavaBean accessor to obtain the value of drive
     * 
     * @return the drive value
     */
    public String getDrive() {
        return drive;
    }

    /**
     * JavaBean accessor to obtain the value of fault
     * 
     * @return the fault value
     */
    public Fault getFault() {
        return fault;
    }

    /**
     * Returns the list of all fixed ip addresses assigned to this server, if any
     * 
     * @return A list of all fixed ip addresses, if any, assigned to this server. If none are assigned, the list will be
     *         empty.
     */
    public List<String> getFixedAddresses() {
        return fixedAddresses;
    }

    /**
     * @return The list of fixed ip addresses
     * @throws ZoneException
     *             If the model object is not connected
     */
    @JsonIgnore
    public List<String> getFixedIpsViaInterfaces() throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * @return the list of floating IP addresses assigned to this server, if any
     */
    public List<String> getFloatingAddresses() {
        return floatingAddresses;
    }

    /**
     * JavaBean accessor to obtain the value of hostId
     * 
     * @return the hostId value
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * JavaBean accessor to obtain the hypervisor for the server
     * 
     * @return the hypervisor
     */
    public Hypervisor getHypervisor() {
        return hypervisor;
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
     * JavaBean accessor to obtain the value of image
     * 
     * @return the image value
     */
    public String getImage() {
        return image == null ? null : image.getId();
    }

    /**
     * JavaBean accessor to obtain the value of initScript
     * 
     * @return the initScript value
     */
    public String getInitScript() {
        return initScript;
    }

    /**
     * JavaBean accessor to obtain the value of instanceName
     * 
     * @return the instanceName value
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * JavaBean accessor to obtain the value of keyName
     * 
     * @return the keyName value
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * JavaBean accessor to obtain the value of machine
     * 
     * @return the machine value
     */
    public Template getMachine() {
        return template;
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
     * Returns the list of network names that the server is to be attached to
     * 
     * @return The list of network names. Note that the list may be empty if not networks have been assigned
     * @deprecated
     */
    @JsonIgnore
    @Deprecated
    public List<Network> getNetworks() {
        return networks;
    }

    /**
     * @return the value of snapshots
     */
    @JsonIgnore
    public List<Image> getSnapshots() {
        return snapshots;
    }

    /**
     * @return the value of started. If null, the provider does not supply this
     */
    public String getStarted() {
        return started;
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
     * @return the value of stopped. If null, the provider does not supply this
     */
    public String getStopped() {
        return stopped;
    }

    /**
     * JavaBean accessor to obtain the value of template
     * 
     * @return the template value
     */
    public String getTemplate() {
        return template == null ? null : template.getId();
    }

    /**
     * JavaBean accessor to obtain the value of userId
     * 
     * @return the userId value
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return The map of all volumes attached to this server keyed by the device name of the attachment.
     */
    @JsonIgnore
    public Map<String, Volume> getVolumes() {
        return volumes;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @JsonIgnore
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @return True if the server has a fault condition, false otherwise
     */
    @JsonIgnore
    public boolean isServerInError() {
        return fault != null;
    }

    /**
     * This method rebuilds the VM using the exact same template (flavor) and image that it was created from. It does
     * not take into consideration if there are any snapshots available or not.
     * 
     * @throws ZoneException
     *             If the rebuild cannot be performed.
     */
    public void rebuild() throws ZoneException {
        notConnectedError();
    }

    /**
     * This method is used to rebuild a VM from a snapshot of the VM. The snapshot must be associated with the VM being
     * rebuilt.
     * 
     * @param snapshot
     *            The snapshot or image id to be used
     * @throws ZoneException
     *             If the rebuild cannot be performed for some reason.
     */
    public void rebuild(String snapshot) throws ZoneException {
        notConnectedError();
    }

    /**
     * This method informs the provider implementation to refresh this server object with the current state as defined
     * on the provider.
     * 
     * @throws ZoneException
     *             If the server cannot be refreshed
     * @deprecated use {@link #refreshStatus()} or {@link #refreshAll()} instead
     */
    @Deprecated
    public void refresh() throws ZoneException {
        refreshStatus();
    }

    /**
     * This method should be used instead of {@link #refresh()} to refresh the entire server model. If it is only needed
     * to refresh the server status, use {@link #refreshStatus()} instead.
     * <p>
     * This method discards the entire model of the server, as well as all related objects, and rebuilds the entire
     * model of the server.
     * </p>
     * 
     * @throws ZoneException
     *             If the server cannot be refreshed
     */
    public void refreshAll() throws ZoneException {
        notConnectedError();
    }

    /**
     * This method is used to refresh the status of the server.
     * 
     * @throws ZoneException
     *             If the server cannot be refreshed
     */
    public void refreshStatus() throws ZoneException {
        notConnectedError();
    }

    /**
     * Releases a floating IP Address back to the pool that owns the address
     * 
     * @param address
     *            The address to be released from this server and returned to the pool
     * @throws ZoneException
     *             If the address cannot be released
     */
    public void releaseIpAddress(String address) throws ZoneException {
        notConnectedError();
    }

    /**
     * Resumes this server, if it is suspended
     * 
     * @throws ZoneException
     *             If the server cannot be resumed
     */
    public void resume() throws ZoneException {
        notConnectedError();
    }

    /**
     * Standard JavaBean mutator method to set the value of accessControl
     * 
     * @param accessControl
     *            the value to be set into accessControl
     */
    public void setAccessControl(List<ACL> accessControl) {
        this.accessControl = accessControl;
    }

    /**
     * Standard JavaBean mutator method to set the value of availabilityZone
     * 
     * @param availabilityZone
     *            The value to be set into availabilityZone
     */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * Sets the boot source value for this server
     * 
     * @param bootSource
     *            The source of the boot image
     */
    public void setBootSource(ServerBootSource bootSource) {
        this.bootSource = bootSource;
    }

    /**
     * @param diskConfig
     *            the value for diskConfig
     */
    public void setDiskConfig(String diskConfig) {
        this.diskConfig = diskConfig;
    }

    /**
     * @param drive
     *            the value for drive
     */
    public void setDrive(String drive) {
        this.drive = drive;
    }

    /**
     * @param fault
     *            the value for fault
     */
    public void setFault(Fault fault) {
        this.fault = fault;
    }

    /**
     * @param fixedAddresses
     *            the value for fixedAddresses
     */
    public void setFixedAddresses(List<String> fixedAddresses) {
        this.fixedAddresses = fixedAddresses;
    }

    /**
     * Standard JavaBean mutator method to set the value of floatingAddresses
     * 
     * @param floatingAddresses
     *            the value for floatingAddresses
     */
    public void setFloatingAddresses(List<String> floatingAddresses) {
        this.floatingAddresses = floatingAddresses;
    }

    /**
     * Standard JavaBean mutator method to set the value of hostId
     * 
     * @param hostId
     *            the value for hostId
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    /**
     * Standard JavaBean mutator method to set the hypervisor
     * 
     * @param hypervisor
     */
    public void setHypervisor(Hypervisor hypervisor) {
        this.hypervisor = hypervisor;
    }

    /**
     * Standard JavaBean mutator method to set the value of server id
     * 
     * @param id
     *            The server id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Standard JavaBean mutator method to set the value of image
     * 
     * @param image
     *            the value to be set into image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Standard JavaBean mutator method to set the value of initScript
     * 
     * @param initScript
     *            the value to be set into initScript
     */
    public void setInitScript(String initScript) {
        this.initScript = initScript;
    }

    /**
     * @param instanceName
     *            the value for instanceName
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * Standard JavaBean mutator method to set the value of keyName
     * 
     * @param keyName
     *            the value to be set into keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
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
     * Replaces the list of networks for this server with a new list
     * 
     * @param networks
     *            the list of networks
     * @deprecated
     */
    @Deprecated
    public void setNetworks(List<Network> networks) {
        if (networks != null) {
            this.networks.clear();
            this.networks.addAll(networks);
        }
    }

    /**
     * @param snapshots
     *            the value for snapshots
     */
    public void setSnapshots(List<Image> snapshots) {
        this.snapshots = snapshots;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Standard JavaBean mutator method to set the value of template
     * 
     * @param template
     *            the value to be set into template
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @param userId
     *            the value for userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Starts the server
     * 
     * @throws ZoneException
     *             If the server cannot be started
     */
    public void start() throws ZoneException {
        notConnectedError();
    }

    /**
     * Stops the server
     * 
     * @throws ZoneException
     *             If the server cannot be stopped
     */
    public void stop() throws ZoneException {
        notConnectedError();
    }

    /**
     * Suspends this server, if it is not already suspended
     * 
     * @throws ZoneException
     *             If the server cannot be suspended
     */
    public void suspend() throws ZoneException {
        notConnectedError();
    }

    /**
     * Pauses the server, if supported by the underlying provider
     * 
     * @throws ZoneException
     *             If the server cannot be paused
     */
    public void pause() throws ZoneException {
        notConnectedError();
    }

    /**
     * UnPauses the server, if supported by the underlying provider
     * 
     * @throws ZoneException
     *             If the server cannot be un-paused
     */
    public void unpause() throws ZoneException {
        notConnectedError();
    }

    /**
     * This method returns the ports attached to the server in the case of an existing server, or the set of ports to be
     * attached to the server in the case of a server being created.
     * <p>
     * Once a server exists, the list of ports is NOT maintained in local state. Obtaining the list of ports of an
     * existing server is done by querying the provider. This is necessary to allow the server to change independently
     * of this abstract data model. It also allows the ports to be obtained lazily, that is, when they are needed.
     * </p>
     * <p>
     * In the case of a server that is used as a model to create a new instance, the ports collection is set to the list
     * of ports to be attached to the server. Once created, the collection is NOT maintained in the instance server
     * object.
     * </p>
     * <p>
     * When the server is connected, this method is overridden and will perform the lookup of the ports from the
     * provider.
     * </p>
     * 
     * @return A list of the virtual NIC (Network Interface Card) ports. Each port represents a connection to a subnet.
     * @throws ZoneException
     *             If the ports cannot be obtained for some reason
     */
    public List<Port> getPorts() throws ZoneException {
        return ports;
    }

    /**
     * This method is used to attach a port to the server.
     * 
     * @param port
     *            The port to be attached
     * @throws ZoneException
     *             If the server is not connected, or if the attachment failed for some reason
     */
    public void attachPort(Port port) throws ZoneException {
        notConnectedError();
    }

    /**
     * This method detaches the specified port from the server
     * 
     * @param port
     *            The port to be detached
     * @throws ZoneException
     *             If the server is not connected, or if the detach failed for some reason
     */
    public void detachPort(Port port) throws ZoneException {
        notConnectedError();
    }

    /**
     * Sets the list of ports to the specified values. This is used to construct a new server with the associated port
     * attachment(s).
     * 
     * @param ports
     *            The list of ports
     */
    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    /**
     * This is a standardized method to wait for the state of the server to change to one of the allowed states.
     * <p>
     * This method will block the caller and not return until either:
     * <ul>
     * <li>The server provided has been found to be in one of the provided states on a sample</li>
     * <lI>The server does not enter any of the provided states within the specified time out period (in seconds). In
     * this case, an exception is thrown.</li>
     * </ul>
     * </p>
     * <p>
     * The caller provides the sampling interval, in SECONDS, and the timeout (also in SECONDS). This sets the frequency
     * of checking of the state to determine if the server has changed to one of the listed states. If the state does
     * not enter one of the allowed or expected states within the timeout period, then the method throws an exception.
     * </p>
     * <p>
     * The server object must be a connected server object. Using a disconnected server object is an error and will
     * result in an exception being thrown.
     * </p>
     * <p>
     * The caller provides a variable list of allowed server states. This is a variable argument list that allows for
     * one or more states to be listed. If the server is found to be in any one of these states on a sample interval,
     * then the method returns and no exception is thrown.
     * </p>
     * 
     * @param pollInterval
     *            The interval, in seconds, to check the server state and see if the server state has changed.
     * @param timeout
     *            The total time, in seconds, that the method will block the caller and check the server state. This
     *            value MUST be greater than or equal to the poll interval.
     * @param status
     *            The variable list of at least one status value(s) that are allowed or expected. If the server is found
     *            to be in any of these states on a poll interval, the method completes normally and returns to the
     *            caller.
     * @throws TimeoutException
     *             If the server state does not change to one of the allowed states within the timeout period.
     * @throws NotNavigableException
     *             If the server object provided is not connected.
     * @throws InvalidRequestException
     *             If the arguments are null or invalid. This includes the case where the timeout is less than the
     *             interval.
     * @throws ContextClosedException
     *             If the context connected to the server is closed and cannot be used.
     * @throws ZoneException
     *             If anything unexpected happens
     */
    public void waitForStateChange(int pollInterval, int timeout, Server.Status... status) throws TimeoutException,
        NotNavigableException, InvalidRequestException, ContextClosedException, ZoneException {

        getContext().getComputeService().waitForStateChange(pollInterval, timeout, this, status);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        DateFormat fmt = DateFormat.getDateTimeInstance();

        // Null dates will throw an NPE in DateFormat.format()
        String created = (getCreatedDate() != null) ? fmt.format(getCreatedDate()) : "null";
        String updated = (getUpdatedDate() != null) ? fmt.format(getUpdatedDate()) : "null";

        if (isServerInError()) {
            return String.format("Server: id(%s), name(%s), created(%s), updated(%s), status(%s),  "
                + "started(%s), stopped(%s), FAULT(%s)", id, name, created, updated, status, started == null
                ? "unknown" : started, stopped == null ? "unknown" : stopped, fault.toString());
        }
        return String.format("Server: id(%s), name(%s), created(%s), updated(%s), status(%s)", id, name, created,
            updated, status);
    }
}
