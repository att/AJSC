/**
 * Copyright (C) 2017, AT&T Inc. All rights reserved. Proprietary materials, property of AT&T. For internal use only,
 * not for disclosure to parties outside of AT&T or its affiliates.
 */

package com.att.cdp.zones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A hypervisor represents a hypervisor or virtual machine monitor (VMM) whether it is computer software, firmware, or
 * hardware, that creates and runs virtual machines.
 * 
 * @author <a href= "mailto:ry303t@att.com?subject=com.att.cdp.zones.model.Hypervisor"> Ryan Young</a>
 * @since Jan 10, 2017
 * @version $Id$
 */
@JsonRootName("hypervisor")
public class Hypervisor extends ModelObject {

    /**
     * The state of the hypervisor. This state is a composite view of the hypervisor. Different implementations may
     * report state differently, and may report multiple state values of various sub-systems. The API implementation
     * must aggregate these state values into a single state defined in this enumeration, and which represents the
     * hypervisor definition and/or instance (if the hypervisor is running).
     * 
     * @since Jan 06, 2017
     * @version $Id$
     */

    public enum State {

        /**
         * The hypervisor is up.
         */
        UP,

        /**
         * The hypervisor is down.
         */
        DOWN,

        /**
         * The state of the hypervisor is unknown or not reported.
         */
        UNKNOWN;
    }

    /**
     * The status of the hypervisor. This status is a composite view of the hypervisor. Different implementations may
     * report status differently, and may report multiple status values of various sub-systems. The API implementation
     * must aggregate these status values into a single status defined in this enumeration, and which represents the
     * hypervisor definition and/or instance (if the hypervisor is running).
     */
    public enum Status {

        /**
         * The hypervisor is enabled.
         */
        ENABLED,

        /**
         * The hypervisor is disabled.
         */
        DISABLED,

        /**
         * The hypervisor status is unknown or unavailable.
         */
        UNKNOWN;
    }

    /**
     * The cpu Architecture
     */
    private Architecture cpuArchitecture;

    /**
     * The number of tasks the hypervisor is responsible for.
     */
    private String currentWorkload;

    /**
     * The free disk remaining in this hypervisor(in GB)
     */
    private String diskFree;

    /**
     * The actual disk in this hypervisor(in GB)
     */
    private String diskSize;

    /**
     * The IP address of the real machine of this hypervisor, if known
     */
    private String hostIp;

    /**
     * The name of the hypervisor host.
     */
    private String hostName;

    /**
     * The unique identification of this hypervisor definition
     */
    private String id;

    /**
     * The free RAM in this hypervisor(in MB)
     */
    private String memoryFree;

    /**
     * The amount of RAM in this hypervisor(in MB)
     */
    private String memorySize;

    /**
     * The number of running vms on this hypervisor
     */
    private String runningVMs;

    /**
     * The serial version id of this class
     */
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * The state of the hypervisor.
     */
    private State state;

    /**
     * The status of the hypervisor.
     */
    private Status status;

    /**
     * The type of the hypervisor.
     */
    private HypervisorType type;

    /**
     * The version of the hypervisor.
     */
    private String version;

    /**
     * A default no-arg constructor
     */
    public Hypervisor() {
        state = State.UNKNOWN;
        status = Status.UNKNOWN;
    }

    /**
     * A constructor that takes only a id
     * 
     * @param id
     *            The id of the hypervisor
     */
    public Hypervisor(String id) {
        this();
        this.id = id;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Hypervisor(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Hypervisor other = (Hypervisor) obj;

        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of cpuInfo
     * 
     * @return the cpuArchitecture value
     */
    public Architecture getCpuArchitecture() {
        return cpuArchitecture;
    }

    /**
     * JavaBean accessor to obtain the value of currentWorkload
     * 
     * @return the currentWorkload value
     */
    public String getCurrentWorkload() {
        return currentWorkload;
    }

    /**
     * JavaBean accessor to obtain the value of diskFree
     * 
     * @return the diskFree value
     */
    public String getDiskFree() {
        return diskFree;
    }

    /**
     * JavaBean accessor to obtain the value of diskSize
     * 
     * @return the diskSize value
     */
    public String getDiskSize() {
        return diskSize;
    }

    /**
     * JavaBean accessor to obtain the value of hostIP
     * 
     * @return the hostIP value
     */
    public String getHostIp() {
        return hostIp;
    }

    /**
     * JavaBean accessor to obtain the value of hostName
     * 
     * @return the hostName value
     */
    public String getHostName() {
        return hostName;
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
     * JavaBean accessor to obtain the value of memoryFree
     * 
     * @return the memoryFree value
     */
    public String getMemoryFree() {
        return memoryFree;
    }

    /**
     * JavaBean accessor to obtain the value of memorySize
     * 
     * @return the memorySize value
     */
    public String getMemorySize() {
        return memorySize;
    }

    /**
     * JavaBean accessor to obtain the value of runningVMs
     * 
     * @return the runningVMs value
     */
    public String getRunningVMs() {
        return runningVMs;
    }

    /**
     * JavaBean accessor to obtain the value of State
     * 
     * @return the hypervisor state
     */
    public State getState() {
        return state;
    }

    /**
     * JavaBean accessor to obtain the value of Status
     * 
     * @return the hypervisor status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * JavaBean accessor to obtain the value of type
     * 
     * @return the hypervisor type
     */
    public HypervisorType getType() {
        return type;
    }

    /**
     * JavaBean accessor to obtain the value of version
     * 
     * @return the hypervisor version
     */
    public String getVersion() {
        return version;
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
     * This method should be used instead of {@link #refresh()} to refresh the entire hypervisor model. If it is only
     * needed to refresh the hypervisor status, use {@link #refreshStatus()} instead.
     * <p>
     * This method discards the entire model of the hypervisor, as well as all related objects, and rebuilds the entire
     * model of the hypervisor.
     * </p>
     * 
     * @throws ZoneException
     *             If the hypervisor cannot be refreshed
     */
    public void refreshAll() throws ZoneException {
        notConnectedError();
    }

    /**
     * This method is used to refresh the state of the hypervisor.
     * 
     * @throws ZoneException
     *             If the hypervisor cannot be refreshed
     */
    public void refreshState() throws ZoneException {
        notConnectedError();
    }

    /**
     * This method is used to refresh the status of the hypervisor.
     * 
     * @throws ZoneException
     *             If the hypervisor cannot be refreshed
     */
    public void refreshStatus() throws ZoneException {
        notConnectedError();
    }

    /**
     * Standard JavaBean mutator method to set the value of cpuArchitecture
     * 
     * @param architecture
     *            the value to be set into cpuArchitecture
     */
    public void setCpuArchitecture(Architecture architecture) {
        this.cpuArchitecture = architecture;
    }

    /**
     * Standard JavaBean mutator method to set the value of currentWorkload
     * 
     * @param currentWorkload
     *            the value to be set into currentWorkload
     */
    public void setCurrentWorkload(String currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    /**
     * Standard JavaBean mutator method to set the value of diskFree
     * 
     * @param diskFree
     *            the value to be set into diskFree
     */
    public void setDiskFree(String diskFree) {
        this.diskFree = diskFree;
    }

    /**
     * Standard JavaBean mutator method to set the value of diskSize
     * 
     * @param diskSize
     *            the value to be set into diskSize
     */
    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    /**
     * Standard JavaBean mutator method to set the value of hostIP
     * 
     * @param hostIp
     *            the value to be set into hostIP
     */
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    /**
     * Standard JavaBean mutator method to set the value of hostName
     * 
     * @param hostName
     *            the value to be set into hostName
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Standard JavaBean mutator method to set the value of id
     * 
     * @param id
     *            The hypervisor id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Standard JavaBean mutator method to set the value of memoryFree
     * 
     * @param memoryFree
     *            the value to be set into memoryFree
     */
    public void setMemoryFree(String memoryFree) {
        this.memoryFree = memoryFree;
    }

    /**
     * Standard JavaBean mutator method to set the value of memorySize
     * 
     * @param memorySize
     *            the value to be set into memorySize
     */
    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    /**
     * Standard JavaBean mutator method to set the value of runningVMs
     * 
     * @param runningVMs
     *            the value to be set into runningVMs
     */
    public void setRunningVMs(String runningVMs) {
        this.runningVMs = runningVMs;
    }

    /**
     * @param state
     *            the value for state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param type
     *            the hypervisor type
     */
    public void setType(HypervisorType type) {
        this.type = type;
    }

    /**
     * @param version
     *            the hypervisor version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Hypervisor :" + "id=(" + id + "), hostname=(" + hostName + "), cpuArchitecture=(" + cpuArchitecture
            + "), currentWorkload=(" + currentWorkload + "), diskSize=(" + diskSize + "), diskFree=(" + diskFree
            + "), hostIp=(" + hostIp + "), memorySize=(" + memorySize + "), memoryFree=(" + memoryFree
            + "), runningVMs=(" + runningVMs + "), state=(" + state + "), status=(" + status + "), type=(" + type
            + "), version=(" + version + ")";
    }

}
