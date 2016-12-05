/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.zones.Context;

/**
 * This class represents a hardware template that defines required resources needed to run a {@link Server}.
 * <p>
 * The mappings to this object from the various providers is as defined here:
 * </p>
 * <table border="1">
 * <tr>
 * <th>OpenStack</th>
 * <th>vmWare Cloud</th>
 * </tr>
 * <tr>
 * <td>Nova model : Flavor</td>
 * <td>??</td>
 * </tr>
 * </table>
 * 
 * @since Sep 26, 2013
 * @version $Id$
 */
public class Template extends ModelObject {

    /**
     * Serial version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of CPUs for this machine
     */
    private Integer cpus;

    /**
     * The amount of disk space (in gigabytes) needed for this machine
     */
    private Integer disk;

    /**
     * True if the machine is enabled and can be used
     */
    private Boolean enabled;

    /**
     * The identification of this machine in the cloud zone
     */
    private String id;

    /**
     * A list of URL links to manage the machine
     */
    private List<String> links = new ArrayList<>();

    /**
     * The name of this machine in the cloud zone
     */
    private String name;

    /**
     * The amount of RAM (in megabytes) for this machine
     */
    private Integer ram;

    /**
     * Default Constructor
     */
    public Template() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Template(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Template other = (Template) obj;
        return other.id.equals(id);
    }

    /**
     * JavaBean accessor to obtain the value of cpus
     * 
     * @return the cpus value
     */
    public Integer getCpus() {
        return cpus;
    }

    /**
     * JavaBean accessor to obtain the value of disk
     * 
     * @return the disk value
     */
    public Integer getDisk() {
        return disk;
    }

    /**
     * @return the value of enabled
     */
    // public Boolean getEnabled() {
    // return enabled;
    // }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of links
     * 
     * @return the links value
     */
    public List<String> getLinks() {
        return links;
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
     * JavaBean accessor to obtain the value of ram
     * 
     * @return the ram value
     */
    public Integer getRam() {
        return ram;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * JavaBean accessor to obtain the value of enabled
     * 
     * @return the enabled value
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * @param cpus
     *            the value for cpus
     */
    public void setCpus(Integer cpus) {
        this.cpus = cpus;
    }

    /**
     * @param disk
     *            the value for disk
     */
    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    /**
     * @param enabled
     *            the value for enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Standard JavaBean mutator method to set the value of id
     * 
     * @param id
     *            the value to be set into id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param links
     *            the value for links
     */
    public void setLinks(List<String> links) {
        this.links = links;
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
     * @param ram
     *            the value for ram
     */
    public void setRam(Integer ram) {
        this.ram = ram;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Template: Id(%s), name(%s), CPUs(%d), RAM(%d), Disk(%d), Enabled(%s)", id, name, cpus,
            ram, disk, enabled);
    }
}
