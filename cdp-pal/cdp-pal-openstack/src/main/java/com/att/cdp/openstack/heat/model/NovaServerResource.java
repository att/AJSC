/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * Defines the mapping of a OS::Nova::Server resource in a Heat template
 * 
 * @since Jun 22, 2015
 * @version $Id$
 */
public class NovaServerResource extends Resource {

    /**
     * The administrator password for the server.
     */
    @JsonProperty(value = "admin_pass")
    private Scalar adminPass;

    /**
     * Note DEPRECATED since 2014.1 (Icehouse). Name of the administrative user to use on the server. The default
     * cloud-init user set up for each image (e.g. “ubuntu” for Ubuntu 12.04+, “fedora” for Fedora 19+ and “cloud-user”
     * for CentOS/RHEL 6.5).
     */
    @JsonProperty(value = "admin_user")
    private Scalar adminUser;

    /**
     * Name of the availability zone for server placement.
     */
    @JsonProperty(value = "availability_zone")
    private Scalar availabilityZone;

    /**
     * If True, enable config drive on the server.
     */
    @JsonProperty(value = "config_drive")
    private Scalar configDrive;

    /**
     * Control how the disk is partitioned when the server is created.
     */
    private Scalar diskConfig;

    /**
     * The ID or name of the flavor to boot onto.
     */
    private Scalar flavor;

    /**
     * Policy on how to apply a flavor update; either by requesting a server resize or by replacing the entire server.
     */
    @JsonProperty(value = "flavor_update_policy")
    private Scalar flavorUpdatePolicy;

    /**
     * The ID or name of the image to boot with.
     */
    private Scalar image;

    /**
     * Policy on how to apply an image-id update; either by requesting a server rebuild or by replacing the entire
     * server
     */
    @JsonProperty(value = "image_update_policy")
    private Scalar imageUpdatePolicy;

    /**
     * Name of keypair to inject into the server.
     */
    @JsonProperty(value = "key_name")
    private Scalar keyName;

    /**
     * Server name.
     */
    private Scalar name;

    /**
     * An ordered list of nics to be added to this server, with information about connected networks, fixed ips, port
     * etc.
     */
    private List<NIC> networks;

    /**
     * A map of files to create/overwrite on the server upon boot. Keys are file names and values are the file contents.
     */
    private Map<String, Scalar> personality;

    /**
     * A UUID for the set of servers being requested.
     */
    @JsonProperty(value = "reservation_id")
    private Scalar reservationId;

    /**
     * Arbitrary key-value pairs specified by the client to help boot a server.
     */
    @JsonProperty(value = "scheduler_hints")
    private Map<String, Scalar> schedulerHints;

    /**
     * List of security group names or IDs. Cannot be used if neutron ports are associated with this server; assign
     * security groups to the ports instead.
     */
    @JsonProperty(value = "security_groups")
    private List<Scalar> securityGroups;

    /**
     * How the server should receive the metadata required for software configuration. POLL_SERVER_CFN will allow calls
     * to the cfn API action DescribeStackResource authenticated with the provided keypair. POLL_SERVER_HEAT will allow
     * calls to the Heat API resource-show using the provided keystone credentials. POLL_TEMP_URL will create and
     * populate a Swift TempURL with metadata for polling.
     */
    @JsonProperty(value = "software_config_transport")
    private Scalar softwareConfigTransport;

    /**
     * User data script to be executed by cloud-init.
     */
    @JsonProperty(value = "user_data")
    private Scalar userData;

    /**
     * How the user_data should be formatted for the server. For HEAT_CFNTOOLS, the user_data is bundled as part of the
     * heat-cfntools cloud-init boot configuration data. For RAW the user_data is passed to Nova unmodified. For
     * SOFTWARE_CONFIG user_data is bundled as part of the software config data, and metadata is derived from any
     * associated SoftwareDeployment resources.
     */
    @JsonProperty(value = "user_data_format")
    private Scalar userDataFormat;

    /**
     * Construct the resource
     */
    public NovaServerResource() {
        super();
    }

    /**
     * @see com.att.cdp.openstack.heat.model.Resource#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @return the value of adminPass
     */
    public Scalar getAdminPass() {
        return adminPass;
    }

    /**
     * @return the value of adminUser
     */
    public Scalar getAdminUser() {
        return adminUser;
    }

    /**
     * @return the value of availabilityZone
     */
    public Scalar getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * @return the value of configDrive
     */
    public Scalar getConfigDrive() {
        return configDrive;
    }

    /**
     * @return the value of diskConfig
     */
    public Scalar getDiskConfig() {
        return diskConfig;
    }

    /**
     * @return the value of flavor
     */
    public Scalar getFlavor() {
        return flavor;
    }

    /**
     * @return the value of flavorUpdatePolicy
     */
    public Scalar getFlavorUpdatePolicy() {
        return flavorUpdatePolicy;
    }

    /**
     * @return the value of image
     */
    public Scalar getImage() {
        return image;
    }

    /**
     * @return the value of imageUpdatePolicy
     */
    public Scalar getImageUpdatePolicy() {
        return imageUpdatePolicy;
    }

    /**
     * @return the value of keyName
     */
    public Scalar getKeyName() {
        return keyName;
    }

    /**
     * @return the value of name
     */
    public Scalar getName() {
        return name;
    }

    /**
     * @return the value of networks
     */
    public List<NIC> getNetworks() {
        return networks;
    }

    /**
     * @return the value of personality
     */
    public Map<String, Scalar> getPersonality() {
        return personality;
    }

    /**
     * @return the value of reservationId
     */
    public Scalar getReservationId() {
        return reservationId;
    }

    /**
     * @return the value of schedulerHints
     */
    public Map<String, Scalar> getSchedulerHints() {
        return schedulerHints;
    }

    /**
     * @return the value of securityGroups
     */
    public List<Scalar> getSecurityGroups() {
        return securityGroups;
    }

    /**
     * @return the value of softwareConfigTransport
     */
    public Scalar getSoftwareConfigTransport() {
        return softwareConfigTransport;
    }

    /**
     * @return the value of userData
     */
    public Scalar getUserData() {
        return userData;
    }

    /**
     * @return the value of userDataFormat
     */
    public Scalar getUserDataFormat() {
        return userDataFormat;
    }

    /**
     * @see com.att.cdp.openstack.heat.model.Resource#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += adminPass == null ? 0 : adminPass.hashCode();
        hash += adminUser == null ? 0 : adminUser.hashCode();
        hash += availabilityZone == null ? 0 : availabilityZone.hashCode();
        hash += configDrive == null ? 0 : configDrive.hashCode();
        hash += flavor == null ? 0 : flavor.hashCode();
        hash += flavorUpdatePolicy == null ? 0 : flavorUpdatePolicy.hashCode();
        hash += image == null ? 0 : image.hashCode();
        hash += imageUpdatePolicy == null ? 0 : imageUpdatePolicy.hashCode();
        hash += keyName == null ? 0 : keyName.hashCode();
        hash += name == null ? 0 : name.hashCode();
        hash += networks == null ? 0 : networks.hashCode();
        hash += personality == null ? 0 : personality.hashCode();
        hash += reservationId == null ? 0 : reservationId.hashCode();
        hash += schedulerHints == null ? 0 : schedulerHints.hashCode();
        hash += securityGroups == null ? 0 : securityGroups.hashCode();
        hash += softwareConfigTransport == null ? 0 : softwareConfigTransport.hashCode();
        hash += userData == null ? 0 : userData.hashCode();
        hash += userDataFormat == null ? 0 : userDataFormat.hashCode();

        return hash;
    }

    /**
     * @param adminPass
     *            the value for adminPass
     */
    public void setAdminPass(Scalar adminPass) {
        this.adminPass = adminPass;
    }

    /**
     * @param adminUser
     *            the value for adminUser
     */
    public void setAdminUser(Scalar adminUser) {
        this.adminUser = adminUser;
    }

    /**
     * @param availabilityZone
     *            the value for availabilityZone
     */
    public void setAvailabilityZone(Scalar availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
     * @param configDrive
     *            the value for configDrive
     */
    public void setConfigDrive(Scalar configDrive) {
        this.configDrive = configDrive;
    }

    /**
     * @param diskConfig
     *            the value for diskConfig
     */
    public void setDiskConfig(Scalar diskConfig) {
        this.diskConfig = diskConfig;
    }

    /**
     * @param flavor
     *            the value for flavor
     */
    public void setFlavor(Scalar flavor) {
        this.flavor = flavor;
    }

    /**
     * @param flavorUpdatePolicy
     *            the value for flavorUpdatePolicy
     */
    public void setFlavorUpdatePolicy(Scalar flavorUpdatePolicy) {
        this.flavorUpdatePolicy = flavorUpdatePolicy;
    }

    /**
     * @param image
     *            the value for image
     */
    public void setImage(Scalar image) {
        this.image = image;
    }

    /**
     * @param imageUpdatePolicy
     *            the value for imageUpdatePolicy
     */
    public void setImageUpdatePolicy(Scalar imageUpdatePolicy) {
        this.imageUpdatePolicy = imageUpdatePolicy;
    }

    /**
     * @param keyName
     *            the value for keyName
     */
    public void setKeyName(Scalar keyName) {
        this.keyName = keyName;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(Scalar name) {
        this.name = name;
    }

    /**
     * @param networks
     *            the value for networks
     */
    public void setNetworks(List<NIC> networks) {
        this.networks = networks;
    }

    /**
     * @param personality
     *            the value for personality
     */
    public void setPersonality(Map<String, Scalar> personality) {
        this.personality = personality;
    }

    /**
     * @param reservationId
     *            the value for reservationId
     */
    public void setReservationId(Scalar reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * @param schedulerHints
     *            the value for schedulerHints
     */
    public void setSchedulerHints(Map<String, Scalar> schedulerHints) {
        this.schedulerHints = schedulerHints;
    }

    /**
     * @param securityGroups
     *            the value for securityGroups
     */
    public void setSecurityGroups(List<Scalar> securityGroups) {
        this.securityGroups = securityGroups;
    }

    /**
     * @param softwareConfigTransport
     *            the value for softwareConfigTransport
     */
    public void setSoftwareConfigTransport(Scalar softwareConfigTransport) {
        this.softwareConfigTransport = softwareConfigTransport;
    }

    /**
     * @param userData
     *            the value for userData
     */
    public void setUserData(Scalar userData) {
        this.userData = userData;
    }

    /**
     * @param userDataFormat
     *            the value for userDataFormat
     */
    public void setUserDataFormat(Scalar userDataFormat) {
        this.userDataFormat = userDataFormat;
    }

    /**
     * @see com.att.cdp.openstack.heat.model.Resource#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, name[%s], flavor[%s], image[%s], networks[%s]", super.toString(), name, flavor,
            image, networks == null ? "null" : networks.toString());
    }

    /**
     * The definition of a block device within the context of the server it is attached to
     */
    public class BlockDevice extends ModelObject {

        /**
         * Integer used for ordering the boot disks.
         */
        @JsonProperty(value = "boot_index")
        private Scalar bootIndex;

        /**
         * Indicate whether the volume should be deleted when the server is terminated.
         */
        @JsonProperty(value = "delete_on_termination")
        private Scalar deleteOnTermination;

        /**
         * A device name where the volume will be attached in the system at /dev/device_name. This value is typically
         * vda.
         */
        @JsonProperty("device_name")
        private Scalar deviceName;

        /**
         * Device type: at the moment we can make distinction only between disk and cdrom.
         */
        @JsonProperty(value = "device_type")
        private Scalar deviceType;

        /**
         * Bus of the device: hypervisor driver chooses a suitable default if omitted.
         */
        @JsonProperty(value = "disk_bus")
        private Scalar diskBus;

        /**
         * The ID of the image to create a volume from.
         */
        @JsonProperty(value = "image_id")
        private Scalar imageId;

        /**
         * The ID of the snapshot to create a volume from.
         */
        @JsonProperty(value = "snapshot_id")
        private Scalar snapshotId;

        /**
         * The size of the swap, in MB.
         */
        @JsonProperty(value = "swap_size")
        private Scalar swapSize;

        /**
         * The ID of the volume to boot from. Only one of volume_id or snapshot_id should be provided.
         */
        @JsonProperty(value = "volume_id")
        private Scalar volumeId;

        /**
         * The size of the volume, in GB. It is safe to leave this blank and have the Compute service infer the size.
         */
        @JsonProperty(value = "volume_size")
        private Scalar volumeSize;

        /**
         * @return the value of bootIndex
         */
        public Scalar getBootIndex() {
            return bootIndex;
        }

        /**
         * @return the value of deleteOnTermination
         */
        public Scalar getDeleteOnTermination() {
            return deleteOnTermination;
        }

        /**
         * @return the value of deviceName
         */
        public Scalar getDeviceName() {
            return deviceName;
        }

        /**
         * @return the value of deviceType
         */
        public Scalar getDeviceType() {
            return deviceType;
        }

        /**
         * @return the value of diskBus
         */
        public Scalar getDiskBus() {
            return diskBus;
        }

        /**
         * @return the value of imageId
         */
        public Scalar getImageId() {
            return imageId;
        }

        /**
         * @return the value of snapshotId
         */
        public Scalar getSnapshotId() {
            return snapshotId;
        }

        /**
         * @return the value of swapSize
         */
        public Scalar getSwapSize() {
            return swapSize;
        }

        /**
         * @return the value of volumeId
         */
        public Scalar getVolumeId() {
            return volumeId;
        }

        /**
         * @return the value of volumeSize
         */
        public Scalar getVolumeSize() {
            return volumeSize;
        }

        /**
         * @param bootIndex
         *            the value for bootIndex
         */
        public void setBootIndex(Scalar bootIndex) {
            this.bootIndex = bootIndex;
        }

        /**
         * @param deleteOnTermination
         *            the value for deleteOnTermination
         */
        public void setDeleteOnTermination(Scalar deleteOnTermination) {
            this.deleteOnTermination = deleteOnTermination;
        }

        /**
         * @param deviceName
         *            the value for deviceName
         */
        public void setDeviceName(Scalar deviceName) {
            this.deviceName = deviceName;
        }

        /**
         * @param deviceType
         *            the value for deviceType
         */
        public void setDeviceType(Scalar deviceType) {
            this.deviceType = deviceType;
        }

        /**
         * @param diskBus
         *            the value for diskBus
         */
        public void setDiskBus(Scalar diskBus) {
            this.diskBus = diskBus;
        }

        /**
         * @param imageId
         *            the value for imageId
         */
        public void setImageId(Scalar imageId) {
            this.imageId = imageId;
        }

        /**
         * @param snapshotId
         *            the value for snapshotId
         */
        public void setSnapshotId(Scalar snapshotId) {
            this.snapshotId = snapshotId;
        }

        /**
         * @param swapSize
         *            the value for swapSize
         */
        public void setSwapSize(Scalar swapSize) {
            this.swapSize = swapSize;
        }

        /**
         * @param volumeId
         *            the value for volumeId
         */
        public void setVolumeId(Scalar volumeId) {
            this.volumeId = volumeId;
        }

        /**
         * @param volumeSize
         *            the value for volumeSize
         */
        public void setVolumeSize(Scalar volumeSize) {
            this.volumeSize = volumeSize;
        }
    }

    /**
     * The definition of a Network Interface Card (NIC) (virtualized) for the server
     */
    public class NIC extends ModelObject {
        /**
         * Fixed IP address to specify for the port created on the requested network.
         */
        @JsonProperty(value = "fixed_ip")
        private Scalar fixedIp;

        /**
         * Name or ID of network to create a port on.
         */
        private Scalar network;

        /**
         * ID of an existing port to associate with this server.
         */
        private Scalar port;

        /**
         * Note DEPRECATED since 2014.1 (Icehouse) - Use property network. ID of network to create a port on.
         */
        private Scalar uuid;

        /**
         * @return the value of fixedIp
         */
        public Scalar getFixedIp() {
            return fixedIp;
        }

        /**
         * @return the value of network
         */
        public Scalar getNetwork() {
            return network;
        }

        /**
         * @return the value of port
         */
        public Scalar getPort() {
            return port;
        }

        /**
         * @return the value of uuid
         */
        public Scalar getUuid() {
            return uuid;
        }

        /**
         * @param fixedIp
         *            the value for fixedIp
         */
        public void setFixedIp(Scalar fixedIp) {
            this.fixedIp = fixedIp;
        }

        /**
         * @param network
         *            the value for network
         */
        public void setNetwork(Scalar network) {
            this.network = network;
        }

        /**
         * @param port
         *            the value for port
         */
        public void setPort(Scalar port) {
            this.port = port;
        }

        /**
         * @param uuid
         *            the value for uuid
         */
        public void setUuid(Scalar uuid) {
            this.uuid = uuid;
        }
    }
}
