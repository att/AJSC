/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * @since Apr 28, 2015
 * @version $Id$
 */
public class Firewall extends ModelObject {

    enum Status {
        ACTIVE, BUILD, DOWN, ERROR, PENDING_CREATE, PENDING_DELETE, PENDING_UPDATE
    }

    private static final long serialVersionUID = 1L;

    /**
     * Administrative state of the firewall. If false (down), firewall does not forward packets and will drop all
     * traffic to/from VMs behind the firewall.
     */
    private boolean adminStateUp;

    /**
     * Indicates that the firewall has been audited. This should reset to false after any changes to the firewall and
     * must be manually set to true.
     */
    private boolean audited;

    /**
     * Human readable description for the firewall.
     */
    private String description;

    /**
     * The unique identifier for the firewall object.
     */
    private String id;

    /**
     * Human readable name for the firewall. Does not have to be unique.
     */
    private String name;

    /**
     * The ordered list of firewall rules.
     */
    private List<FirewallRule> rules;

    /**
     * Specified if the firewall resource is visible to tenants other than the owner
     */
    private boolean shared;

    /**
     * Indicates whether firewall resource is currently operational.
     */
    private Status status;

    /**
     * Creates the firewall model
     */
    public Firewall() {
        this.status = Status.BUILD;
    }

    /**
     * Creates the firewall model with the specified name.
     * 
     * @param name
     *            The name of the firewall
     */
    public Firewall(String name) {
        this();
        this.name = name;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Firewall(Context context) {
        super(context);
        this.status = Status.BUILD;
    }

    /**
     * Adds a FirewallRule to the end of the rules list. This rule will be the last rule processed and may not be
     * reached if another rule preempts it
     * 
     * @param rule
     *            The FirewallRule to add to the list
     */
    public void addRule(FirewallRule rule) {
        rules.add(rule);
        this.audited = false;
    }

    /**
     * This method can be called to allow direct manipulation of the model objects and allow the firewall to be deleted
     * 
     * @throws ZoneException
     *             If the Volume is not navigable
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * @return The description of the firewall
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The id of the firewall
     */
    public String getId() {
        return id;
    }

    /**
     * @return The name of the firewall
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a FirewallRule to the end of the rules list. This rule will be the last rule processed and may not be
     * reached if another rule preempts it
     * 
     * @param rule
     *            The FirewallRule to add to the list
     * @param index
     *            The index
     */
    public void insertRuleAt(FirewallRule rule, int index) {
        rules.add(index, rule);
        this.audited = false;
    }

    /**
     * @return True if the firewall is up and filtering incoming packets. False if the firewall is down and wil drop all
     *         incoming packets.
     */
    public boolean isAdminStateUp() {
        return adminStateUp;
    }

    /**
     * @return True if the firewall has been audited False if the firewall has not been audited
     */
    public boolean isAudited() {
        return audited;
    }

    /**
     * @return True if the firewall is visible to users other than the tenant owner False if the firewall is only
     *         visible to the tenant owner
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * @return A list of FirewallRules for this firewall
     */
    public List<FirewallRule> listRules() {
        return rules;
    }

    /**
     * @param description
     *            The new description of the firewall
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the value of rules
     */
    public List<FirewallRule> getRules() {
        return rules;
    }

    /**
     * @return the value of status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param adminStateUp
     *            the value for adminStateUp
     */
    public void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    /**
     * @param audited
     *            the value for audited
     */
    public void setAudited(boolean audited) {
        this.audited = audited;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param rules
     *            the value for rules
     */
    public void setRules(List<FirewallRule> rules) {
        this.rules = rules;
    }

    /**
     * @param shared
     *            the value for shared
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
