/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.HeatConnector;
import com.att.cdp.openstack.model.OpenStackStack;
import com.att.cdp.openstack.util.ExceptionMapper;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Stack;
import com.att.cdp.zones.spi.AbstractStack;
import com.att.cdp.zones.spi.RequestState;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.heat.Heat;
import com.woorea.openstack.heat.StackResource;
import com.woorea.openstack.heat.model.CreateStackParam;
import com.woorea.openstack.heat.model.Stacks;

/**
 * @since Jan 23, 2015
 * @version $Id$
 */

public class OpenStackStackService extends AbstractStack {

    /**
     * The connector to use to access heat api
     */
    private HeatConnector connector;

    /**
     * Create the OpenStack orchestration (stack) service
     * 
     * @param context
     *            The context that we are servicing
     */
    public OpenStackStackService(Context context) {
        super(context);
    }

    /**
     * Obtains a list (unfiltered) of all stacks that exist on the provider and returns them to the caller.
     * 
     * @return A list of all stacks that exist. Note that the list may be empty.
     * @throws ZoneException
     *             - If the context is closed, a login has not occurred, or an error happened trying to contact the
     *             provider.
     * @see com.att.cdp.zones.StackService#getStacks()
     */
    @Override
    public List<Stack> getStacks() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Orchestration");
        RequestState.put(RequestState.SERVICE_URL, connector.getEndpoint());

        ArrayList<Stack> list = new ArrayList<>();
        Heat heat = connector.getClient();
        StackResource resource = heat.getStacks();

        try {
            /*
             * Get a list of stacks, then call byName api for each one to get the detail for the stack.
             */
            Stacks osStacks = resource.list().execute();
            if (osStacks != null) {
                for (com.woorea.openstack.heat.model.Stack osStack : osStacks) {
                    list.add(getStack(osStack.getStackName(), osStack.getId()));
                }
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        return list;
    }

    /**
     * @see com.att.cdp.zones.StackService#getStack(java.lang.String, java.lang.String)
     */
    @Override
    public Stack getStack(String name, String id) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Orchestration");
        RequestState.put(RequestState.SERVICE_URL, connector.getEndpoint());

        Heat heat = connector.getClient();
        OpenStackStack stack = null;
        try {
            com.woorea.openstack.heat.model.Stack osStack = heat.getStacks().get(name, id).execute();
            stack = new OpenStackStack(context, osStack);
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        // TODO Parse OpenStack stack and construct the generic stack model

        return stack;
    }

    /**
     * This method is used to request a stack construction using whatever the native capabilities of the provider are.
     * The caller of this method must know the inherent capabilities of the underlying provider they are using in order
     * to use this method. Also, using this method partially violates and nullifies the benefits of the abstraction. The
     * return value from this method will still be the <code>Stack</code> object that is represented in the abstraction.
     * 
     * @throws ZoneException
     *             If there is a problem making the request
     * @see com.att.cdp.zones.StackService#createNativeStack(java.lang.String, java.lang.String)
     */
    @Override
    public Stack createNativeStack(String stackName, String content) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Orchestration");
        RequestState.put(RequestState.SERVICE_URL, connector.getEndpoint());

        Heat heat = connector.getClient();
        StackResource resource = heat.getStacks();
        CreateStackParam model = new CreateStackParam();
        model.setTimeoutMinutes(5);
        model.setStackName(stackName);
        model.setTemplate(content);
        OpenStackStack stack = null;

        try {
            com.woorea.openstack.heat.model.Stack osStack = resource.create(model).execute();

            if (osStack != null) {
                stack = (OpenStackStack) getStack(stackName, osStack.getId());
                // stack = new OpenStackStack(context, osStack);
                // stack.setName(stackName);
            }
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }

        return stack;
    }

    /**
     * @see com.att.cdp.zones.StackService#createStack(com.att.cdp.zones.model.Stack)
     */
    @Override
    public Stack createStack(Stack model) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.StackService#deleteStack(com.att.cdp.zones.model.Stack)
     */
    @Override
    public void deleteStack(Stack stack) throws NotNavigableException, ResourceNotFoundException, ZoneException {

        deleteStack(stack.getName(), stack.getId());
    }

    /**
     * @see com.att.cdp.zones.StackService#deleteStack(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteStack(String stackName, String stackId) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Orchestration");
        RequestState.put(RequestState.SERVICE_URL, connector.getEndpoint());

        Heat heat = connector.getClient();
        try {
            heat.getStacks().delete(stackName, stackId).execute();
        } catch (OpenStackBaseException e) {
            ExceptionMapper.mapException(e);
        }
    }

    /**
     * This is a helper method used to construct the Nova service object and setup the environment to access the
     * OpenStack compute service (Nova).
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
        connector = osContext.getHeatConnector();
        ((OpenStackContext) context).refreshIfStale(connector);
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return connector.getEndpoint();
    }
}
