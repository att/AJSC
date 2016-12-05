/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import org.slf4j.Logger;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.Provider;
import com.att.cdp.zones.Service;
import com.att.cdp.zones.model.Tenant;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class is the base class for all service implementations. It is used to provide common checking and utility
 * methods and common state that all services need to track.
 * 
 * @since Oct 15, 2013
 * @version $Id$
 */

public abstract class AbstractService implements Service {

    /**
     * The logger that we are using for all application logging
     */
    private Logger appLogger;

    /**
     * The framework configuration
     */
    private Configuration configuration;

    /**
     * The context that we are servicing
     */
    private Context context;

    /**
     * The logger that we are using for all security logging
     */
    private Logger securityLogger;

    /**
     * Creates the base class and initializes the context reference
     * 
     * @param context
     *            The reference to the context we are servicing
     */
    public AbstractService(Context context) {
        this.context = context;
        configuration = ConfigurationFactory.getConfiguration();
        appLogger = configuration.getApplicationLogger();
        securityLogger = configuration.getSecurityLogger();

        appLogger.debug(String.format("Creating instance of service [%s]", getClass().getName()));
    }

    /**
     * Checks that the string argument is not null or an empty string
     * 
     * @param value
     *            The value to be checked
     * @param name
     *            The name of the argument for the exception message, if any
     * @throws InvalidRequestException
     *             if the argument value is null, or an empty string
     */
    private void checkArg(String value, String name) throws InvalidRequestException {
        if (value == null || value.trim().length() == 0) {
            throw new InvalidRequestException(EELFResourceManager.format(Msg.INVALID_ARGUMENT_FROM_CLIENT, name, value));
        }
    }

    /**
     * Checks that the reference is not null. If the reference is null, then an exception is thrown, otherwise it
     * returns silently.
     * <p>
     * If the object being referenced is an array, then the array and all items of the array are also checked for null.
     * </p>
     * 
     * @param ref
     *            The reference to be checked
     * @param name
     *            The name of the argument for the exception message, if any
     * @throws InvalidRequestException
     *             If the reference is null;
     */
    protected void checkArg(Object ref, String name) throws InvalidRequestException {
        if (ref == null) {
            throw new InvalidRequestException(EELFResourceManager.format(Msg.NULL_ARGUMENT, name));
        }

        if (ref.getClass().isArray()) {
            Object[] array = (Object[]) ref;
            if (array.length == 0) {
                throw new InvalidRequestException(EELFResourceManager.format(Msg.NULL_ARGUMENT, name));
            }
            for (Object item : array) {
                if (item == null) {
                    throw new InvalidRequestException(EELFResourceManager.format(Msg.NULL_ARGUMENT, name));
                }
            }
        }
    }

    /**
     * Checks the value of the argument to determine if it is a valid representation of an integer.
     * 
     * @param ref
     *            The argument reference to be checked
     * @param name
     *            The name of the argument
     * @throws InvalidRequestException
     *             If the argument is not a valid integer representation
     */
    protected void checkInteger(Object ref, String name) throws InvalidRequestException {
        if (ref != null) {
            try {
                Integer.parseInt(ref.toString());
            } catch (NumberFormatException e) {
                throw new InvalidRequestException(EELFResourceManager.format(Msg.INVALID_INTEGER_ARGUMENT, name));
            }
        }
    }

    /**
     * This method checks to see if the client has successfully logged in to the provider before attempting to make a
     * service request. If the client is logged in, the method returns silently. If not, then an exception is thrown.
     * 
     * @throws NotLoggedInException
     *             If the client is not logged in.
     */
    public void checkLogin() throws NotLoggedInException {
        if (!context.isLoggedIn()) {
            Provider provider = context.getProvider();
            String msg = EELFResourceManager.format(Msg.NOT_LOGGED_INTO_PROVIDER, provider.getName());
            appLogger.error(msg);
            throw new NotLoggedInException(msg);
        }
    }

    /**
     * This method checks to see if the open flag has been cleared (indicating that the context has been closed). If the
     * context has been closed, then an exception is thrown indicating that the context has been closed and cannot be
     * used. If the open flag has not been cleared, indicating that the context is still usable, the method simply
     * returns to the caller.
     * 
     * @throws ContextClosedException
     *             IFF the context has been closed.
     */
    protected void checkOpen() throws ContextClosedException {
        if (!context.isOpen()) {
            Provider provider = context.getProvider();
            String msg = EELFResourceManager.format(Msg.PROVIDER_CONTEXT_IS_CLOSED, provider.getName());
            appLogger.error(msg);
            throw new ContextClosedException(msg);
        }
    }

    /**
     * @see com.att.cdp.zones.Service#getTenant()
     */
    @Override
    public Tenant getTenant() throws ZoneException {
        checkLogin();
        return context.getTenant();
    }

    /**
     * @return the value of context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Initializes the request state for the current requested service.
     * <p>
     * This method is used to track requests made to the various service implementations and to provide additional
     * information for diagnostic purposes. The <code>RequestState</code> class stores the state in thread-local storage
     * and is available to all code on that thread.
     * </p>
     * <p>
     * This method first obtains the stack trace and scans the stack backward for the call to this method. It then backs
     * up one more call and assumes that method is the request that we are "tracking".
     * </p>
     * 
     * @param states
     *            A variable argument list of additional state values that the caller wants to add to the request state
     *            thread-local object to track the context.
     */
    protected void trackRequest(State... states) {
        RequestState.clear();

        for (State state : states) {
            RequestState.put(state.getName(), state.getValue());
        }

        Thread currentThread = Thread.currentThread();
        StackTraceElement[] stack = currentThread.getStackTrace();
        if (stack != null && stack.length > 0) {
            int index = 0;
            StackTraceElement element = null;
            for (; index < stack.length; index++) {
                element = stack[index];
                if ("trackRequest".equals(element.getMethodName())) {  //$NON-NLS-1$
                    break;
                }
            }
            index++;

            if (index < stack.length) {
                element = stack[index];
                RequestState.put(RequestState.METHOD, element.getMethodName());
                RequestState.put(RequestState.CLASS, element.getClassName());
                RequestState.put(RequestState.LINE_NUMBER, Integer.toString(element.getLineNumber()));
                RequestState.put(RequestState.THREAD, currentThread.getName());
                RequestState.put(RequestState.PROVIDER, context.getProvider().getName());
                RequestState.put(RequestState.TENANT, context.getTenantName());
                RequestState.put(RequestState.PRINCIPAL, context.getPrincipal());
            }
        }
    }

    /**
     * @return The application logger
     */
    protected Logger getLogger() {
        return appLogger;
    }

    public class State {
        private String name;
        private String value;

        public State(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
