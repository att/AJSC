/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cdp.zones.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.zones.Context;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This is the base class for all model objects. This base class allows the caller to examine common characteristics of
 * the model objects as a whole.
 * 
 * @since May 18, 2014
 * @version $Id$
 */

public abstract class ModelObject implements Serializable {

    /**
     * 
     */
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    /**
     * The user id that created the object
     */
    private String createdBy;

    /**
     * The date/time the stack was created
     */
    private Date createdDate;

    /**
     * The user that deleted this object (if tracked)
     */
    private String deletedBy;

    /**
     * Date deleted (for any objects that track this)
     */
    private Date deletedDate;

    /**
     * The user id that last updated the object
     */
    private String updatedBy;

    /**
     * The date that the object was last updated
     */
    private Date updatedDate;

    /**
     * The context that we are servicing, IF ANY
     */
    @JsonIgnore
    private Context context;

    /**
     * Constructs the model object and initializes it's state
     */
    @SuppressWarnings("nls")
    public ModelObject() {
        createdDate = new Date();
        createdBy = "";
        deletedDate = null;
        deletedBy = "";
        updatedDate = null;
        updatedBy = "";
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected ModelObject(Context context) {
        this();
        this.context = context;
    }

    /**
     * JavaBean accessor to obtain the value of createdBy
     * 
     * @return the createdBy value
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @return the value of createdDate
     */
    public Date getCreatedDate() {
        return createdDate == null ? new Date() : createdDate;
    }

    /**
     * JavaBean accessor to obtain the value of deletedBy
     * 
     * @return the deletedBy value
     */
    public String getDeletedBy() {
        return deletedBy;
    }

    /**
     * JavaBean accessor to obtain the value of deletedDate
     * 
     * @return the deletedDate value
     */
    public Date getDeletedDate() {
        return deletedDate;
    }

    /**
     * JavaBean accessor to obtain the value of updatedBy
     * 
     * @return the updatedBy value
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * JavaBean accessor to obtain the value of updatedDate
     * 
     * @return the updatedDate value
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @return the value of context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Any classes that simply implement the data model are by default never connected to a context. When an object is
     * returned from the appropriate service implementation, that object will be a "connected" model object.
     * 
     * @return true if the model object is connected, and false if they are not connected.
     */
    public boolean isConnected() {
        return context != null;
    }

    /**
     * Standard JavaBean mutator method to set the value of createdBy
     * 
     * @param createdBy
     *            the value to be set into createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Standard JavaBean mutator method to set the value of createdDate
     * 
     * @param createdDate
     *            the value to be set into createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Standard JavaBean mutator method to set the value of deletedBy
     * 
     * @param deletedBy
     *            the value to be set into deletedBy
     */
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * Standard JavaBean mutator method to set the value of deletedDate
     * 
     * @param deletedDate
     *            the value to be set into deletedDate
     */
    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    /**
     * Standard JavaBean mutator method to set the value of updatedBy
     * 
     * @param updatedBy
     *            the value to be set into updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Standard JavaBean mutator method to set the value of updatedDate
     * 
     * @param updatedDate
     *            the value to be set into updatedDate
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return The tenant that owns this ACL
     * @throws ZoneException
     *             If model navigation was attempted but the model object is not connected
     */
    @JsonIgnore
    public Tenant getTenant() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }
        return context.getTenant();
    }

    /**
     * JavaBean accessor to obtain the value of tenantId
     * 
     * @return the tenantId value
     * @throws ZoneException
     *             If model navigation was attempted but the model object is not connected
     */
    public String getTenantId() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }
        return context.getTenant().getId();
    }

    /**
     * This is a general helper method that is called whenever the model object or its descendant is called to perform
     * model navigation but the model object is not connected to a context. This ensures that a consistent message is
     * always produced and could allow for standardized behaviors for diagnostics if needed.
     * 
     * @throws ZoneException
     *             If model navigation was attempted but the model object is not connected
     */
    public void notConnectedError() throws ZoneException {
        throw new NotNavigableException(EELFResourceManager.format(Msg.NOT_NAVIGABLE));
    }
}
