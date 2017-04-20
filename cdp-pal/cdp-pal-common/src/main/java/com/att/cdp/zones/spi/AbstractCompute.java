/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.model.Server;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class is an abstract base class that provides common support for compute service implementations.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public abstract class AbstractCompute extends AbstractService implements ComputeService {

    /**
     * Create the abstract compute service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractCompute(Context context) {
        super(context);
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
     * If the timeout is specified as zero, then no timeout is applied.
     * </p>
     * <p>
     * The caller must also provide the server object to be checked. The server object must be a connected server
     * object. Using a disconnected server object is an error and will result in an exception being thrown.
     * </p>
     * <p>
     * The caller lastly provides a variable list of allowed server states. This is a variable argument list that allows
     * for one or more states to be listed. If the server is found to be in any one of these states on a sample
     * interval, then the method returns and no exception is thrown.
     * </p>
     * 
     * @param pollInterval
     *            The interval, in seconds, to check the server state and see if the server state has changed. This
     *            value must be a positive, non-zero integer.
     * @param timeout
     *            The total time, in seconds, that the method will block the caller and check the server state. This
     *            value MUST be greater than or equal to the poll interval, or zero to indicate that there is no timeout
     *            (not recommended).
     * @param server
     *            The server to be checked
     * @param allowedStates
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
     * @see com.att.cdp.zones.ComputeService#waitForStateChange(int, int, com.att.cdp.zones.model.Server,
     *      com.att.cdp.zones.model.Server.Status[])
     */
    @SuppressWarnings("nls")
    @Override
    public void waitForStateChange(int pollInterval, int timeout, Server server, Server.Status... allowedStates)
        throws TimeoutException, NotNavigableException, InvalidRequestException, ContextClosedException, ZoneException {

        /*
         * Check that the arguments are specified, non-null, and of the correct domain
         */
        checkArg(pollInterval, "pollInterval");
        checkInteger(pollInterval, "pollInterval");
        checkArg(timeout, "timeout");
        checkInteger(timeout, "timeout");
        checkArg(server, "server");
        checkArg(allowedStates, "allowedStates");

        /*
         * Check that the poll interval and timeout are both positive, non-zero values, and that the timeout >= poll
         * interval.
         */
        if (pollInterval <= 0) {
            throw new InvalidRequestException(EELFResourceManager.format(Msg.INVALID_POLL_INTERVAL,
                Integer.toString(pollInterval)));
        }
        if (timeout != 0 && timeout < pollInterval) {
            throw new InvalidRequestException(EELFResourceManager.format(Msg.INVALID_POLL_TIMEOUT,
                Integer.toString(timeout), Integer.toString(pollInterval)));
        }

        /*
         * Make sure that the server is connected
         */
        if (!server.isConnected()) {
            throw new NotNavigableException(EELFResourceManager.format(Msg.NOT_NAVIGABLE));
        }

        /*
         * Accumulate the states and normalize them so they are all unique, just in case the caller includes multiple of
         * the same state. No need doing more work than we have to...
         */
        List<Server.Status> states = new ArrayList<>();
        for (Server.Status allowedState : allowedStates) {
            if (!states.contains(allowedState)) {
                states.add(allowedState);
            }
        }

        /*
         * Compute the time limit for the operation. This is checked after each poll interval is completed.
         */
        long delay = pollInterval * 1000L;
        long limit = System.currentTimeMillis() + (timeout * 1000L);
        boolean found = false;
        outer: do {
            server.refreshStatus();
            for (Server.Status state : states) {
                if (state.equals(server.getStatus())) {
                    found = true;
                    break outer;
                }
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // ignore
            }

        } while (timeout == 0 || System.currentTimeMillis() < limit);

        /*
         * Check to see if we found the server in one of the allowed states?
         */
        if (!found) {
            StringBuilder builder = new StringBuilder("[");
            for (Server.Status state : states) {
                builder.append(state.name());
                builder.append(',');
            }
            builder.replace(builder.length() - 1, builder.length(), "]");

            throw new TimeoutException(EELFResourceManager.format(Msg.SERVER_TIMEOUT, server.getName(),
                Integer.toString(timeout), server.getId(), server.getStatus().name(), builder.toString()));
        }
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method has been deprecated, and therefore need not be implemented by any provider. It will be removed at
     * some point in the future.
     * 
     * @see com.att.cdp.zones.ComputeService#executeCommand(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @Override
    public void executeCommand(Server server, String command) throws ZoneException {
    }

    /**
     * This method has been deprecated, and therefore need not be implemented by any provider. It will be removed at
     * some point in the future.
     * 
     * @see com.att.cdp.zones.ComputeService#getConsoleOutput(com.att.cdp.zones.model.Server)
     */
    @Override
    public List<String> getConsoleOutput(Server server) throws ZoneException {
        return new ArrayList<String>();
    }

    /**
     * This method is not supported by all providers, and therefore need not be implemented by all providers.
     */
    public Hypervisor getHypervisor(String id) throws ZoneException {
        String message =
            EELFResourceManager.format(Msg.UNIMPLEMENTED_OPERATION, "getHypervisor", getContext().getProvider()
                .getName());
        throw new NotSupportedException(message);
    }

    /**
     * This method is not supported by all providers, and therefore need not be implemented by all providers.
     */
    public List<Hypervisor> getHypervisors() throws ZoneException {
        String message =
            EELFResourceManager.format(Msg.UNIMPLEMENTED_OPERATION, "getHypervisors", getContext().getProvider()
                .getName());
        throw new NotSupportedException(message);
    }

    /**
     * This method is not supported by all providers, and therefore need not be implemented by all providers.
     */
    public List<Hypervisor> getHypervisors(String name) throws ZoneException {
        String message =
            EELFResourceManager.format(Msg.UNIMPLEMENTED_OPERATION, "getHypervisor", getContext().getProvider()
                .getName());
        throw new NotSupportedException(message);
    }

    /**
     * This method is not supported by all providers, and therefore need not be implemented by all providers.
     */
    public void refreshHypervisorState(Hypervisor hypervisor) throws ZoneException {
        String message =
            EELFResourceManager.format(Msg.UNIMPLEMENTED_OPERATION, "refreshHypervisorState", getContext()
                .getProvider().getName());
        throw new NotSupportedException(message);
    }

    /**
     * This method is not supported by all providers, and therefore need not be implemented by all providers.
     */
    public void refreshHypervisorStatus(Hypervisor hypervisor) throws ZoneException {
        String message =
            EELFResourceManager.format(Msg.UNIMPLEMENTED_OPERATION, "refreshHypervisorStatus", getContext()
                .getProvider().getName());
        throw new NotSupportedException(message);
    }
}
