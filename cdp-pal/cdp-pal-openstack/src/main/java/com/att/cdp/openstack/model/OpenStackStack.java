/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import com.att.cdp.pal.util.StringHelper;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedStack;
import com.woorea.openstack.heat.model.Stack;

/**
 * @since Jan 26, 2015
 * @version $Id$
 */

public class OpenStackStack extends ConnectedStack {

    /**
     * This is a map of the status values used by OpenStack and the abstraction state that we map that to.
     */
    //@formatter:off 
    @SuppressWarnings("nls")
    private static final String[][] STATUS_MAP = { 
        { "CREATE_FAILED", Status.FAILED.toString() },
        { "DELETE_FAILED", Status.FAILED.toString() }, 
        { "RESUME_FAILED", Status.FAILED.toString() },
        { "SUSPEND_FAILED", Status.FAILED.toString() }, 
        { "UPDATE_FAILED", Status.FAILED.toString() },
        { "CREATE_IN_PROGRESS", Status.IN_PROGRESS.toString() },
        { "DELETE_IN_PROGRESS", Status.IN_PROGRESS.toString() },
        { "RESUME_IN_PROGRESS", Status.IN_PROGRESS.toString() },
        { "SUSPEND_IN_PROGRESS", Status.IN_PROGRESS.toString() },
        { "UPDATE_IN_PROGRESS", Status.IN_PROGRESS.toString() }, 
        { "CREATE_COMPLETE", Status.ACTIVE.toString() },
        { "SUSPEND_COMPLETE", Status.SUSPENDED.toString() }, 
        { "RESUME_COMPLETE", Status.ACTIVE.toString() },
        { "DELETE_COMPLETE", Status.DELETED.toString() }, 
        { "UPDATE_COMPLETE", Status.ACTIVE.toString() }, 
        { "IN_PROGRESS", Status.IN_PROGRESS.toString() }, 
    };
    //@formatter:on 

    // stackStatusReason

    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            the context we are servicing
     * @param osStack
     *            The OpenStack Heat Stack object we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackStack(Context context, Stack osStack) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("stackName", "name");
        dictionary.put("id", "id");
        dictionary.put("description", "description");
        dictionary.put("creationTime", "createdDate");
        dictionary.put("updatedTime", "updatedDate");
        dictionary.put("parameters", "parameters");

        setStatus(Status.INDETERMINATE);
        for (String[] entry : STATUS_MAP) {
            if (entry[0].equalsIgnoreCase(osStack.getStackStatus())) {
                setStatus(Status.valueOf(entry[1]));
                if (getStatus().equals(Status.FAILED)) {
                    OpenStackFault osFault = new OpenStackFault(context);
                    setFault(osFault);
                    osFault.setMessage(osStack.getStackStatusReason());
                    osFault.setCode(getStatus().toString());
                }
                break;
            }
        }

        ObjectMapper.map(osStack, this, dictionary);

        Date date = osStack.getCreationTime();
        if (date != null) {
            setCreatedDate(date);
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        OpenStackStack other = (OpenStackStack) obj;

        return StringHelper.equals(getName(), other.getName())
            && StringHelper.equals(getDescription(), other.getDescription());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {

        DateFormat fmt = DateFormat.getDateTimeInstance();
        return String.format("Stack: %s, created %s by %s %s", getName(), fmt.format(getCreatedDate()), getCreatedBy(),
            getFault() == null ? "" : getFault().toString());
    }

}
