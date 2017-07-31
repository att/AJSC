/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Stack;

/**
 * This service definition is used to specify abstract capabilities that a supplied orchestration engine, if any, may
 * provide within the mapped provider. Not all providers offer orchestration services, so the presence of such a service
 * is deterministic through the provider metadata. Any attempt to call these services when such an orchestration service
 * is not provided will result in an exception.
 * 
 * @since Jan 16, 2015
 * @version $Id$
 */

public interface StackService extends Service {

    /**
     * Obtains a list (unfiltered) of all stacks that exist on the provider and returns them to the caller.
     * 
     * @return A list of all stacks that exist. Note that the list may be empty.
     * @throws ZoneException
     *             If the context is closed, a login has not occurred, or an error happened trying to contact the
     *             provider.
     */
    List<Stack> getStacks() throws ZoneException;

    /**
     * Obtains the detailed information about a specific stack using the provider-specific ID of the stack
     * 
     * @param name
     *            The name of the stack to obtain
     * @param id
     *            The id as the stack is known to the provider
     * @return The detailed stack definition
     * @throws ZoneException
     *             If the stack cannot be obtained
     */
    Stack getStack(String name, String id) throws ZoneException;

    /**
     * Creates a stack from the supplied model object.
     * 
     * @param model
     *            The model that is used to describe the stack to be created.
     * @return The connected stack object that represents this stack.
     * @throws ZoneException
     *             If the stack cannot be created.
     */
    Stack createStack(Stack model) throws ZoneException;

    /**
     * Deletes the indicated stack.
     * 
     * @param stack
     *            The stack to be deleted.
     * @throws NotNavigableException
     *             If the stack object provided to the method is not connected to a context.
     * @throws ResourceNotFoundException
     *             If the stack, or any resource referenced by the stack, could not be found and force was specified as
     *             false.
     * @throws ZoneException
     *             If the stack could not be deleted because of some provider failure.
     */
    void deleteStack(Stack stack) throws NotNavigableException, ResourceNotFoundException, ZoneException;

    /**
     * This method is used to delete a stack by its name and identifier on the provider.
     * 
     * @param providerStackName
     *            The stack name
     * @param providerStackId
     *            The stack id
     * @throws ZoneException
     *             If the stack could not be deleted because of some provider failure.
     */
    void deleteStack(String providerStackName, String providerStackId) throws ZoneException;

    /**
     * This method is used to create a stack using whatever mechanism the underlying provider supports. To use this
     * mechanism, the caller must be aware of the capabilities of the underlying provider, as well as the type of
     * provider.
     * 
     * @param stackName
     *            The name of the stack to be created
     * @param content
     *            The content of whatever specification "language" that the provider supports.
     * @return The stack object created
     * @throws ZoneException
     *             If the stack could not be created for some reason
     */
    Stack createNativeStack(String stackName, String content) throws ZoneException;

}
