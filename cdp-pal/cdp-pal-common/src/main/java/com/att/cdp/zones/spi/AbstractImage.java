/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.model.Image;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class is an abstract base class that provides common support for the
 * image service implementations.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public abstract class AbstractImage extends AbstractService implements ImageService {

	/**
	 * Create the abstract compute service implementation for the specified
	 * context
	 * 
	 * @param context
	 *            The context that we are providing the services for
	 */
	public AbstractImage(Context context) {
		super(context);
	}

	/**
	 * This is a standardized method to wait for the state of the image to
	 * change to one of the allowed states.
	 * <p>
	 * This method will block the caller and not return until either:
	 * <ul>
	 * <li>The image provided has been found to be in one of the provided states
	 * on a sample</li>
	 * <lI>The image does not enter any of the provided states within the
	 * specified time out period (in seconds). In this case, an exception is
	 * thrown.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The caller provides the sampling interval, in SECONDS, and the timeout
	 * (also in SECONDS). This sets the frequency of checking of the state to
	 * determine if the image has changed to one of the listed states. If the
	 * state does not enter one of the allowed or expected states within the
	 * timeout period, then the method throws an exception.
	 * </p>
	 * <p>
	 * The image object must be a connected image object. Using a disconnected
	 * image object is an error and will result in an exception being thrown.
	 * </p>
	 * <p>
	 * The caller provides a variable list of allowed image states. This is a
	 * variable argument list that allows for one or more states to be listed.
	 * If the image is found to be in any one of these states on a sample
	 * interval, then the method returns and no exception is thrown.
	 * </p>
	 * 
	 * @param pollInterval
	 *            The interval, in seconds, to check the image state and see if
	 *            the image state has changed.
	 * @param timeout
	 *            The total time, in seconds, that the method will block the
	 *            caller and check the image state. This value MUST be greater
	 *            than or equal to the poll interval.
	 * @param allowedStates
	 *            The variable list of at least one status value(s) that are
	 *            allowed or expected. If the image is found to be in any of
	 *            these states on a poll interval, the method completes normally
	 *            and returns to the caller.
	 * @throws TimeoutException
	 *             If the image state does not change to one of the allowed
	 *             states within the timeout period.
	 * @throws NotNavigableException
	 *             If the image object provided is not connected.
	 * @throws InvalidRequestException
	 *             If the arguments are null or invalid. This includes the case
	 *             where the timeout is less than the interval.
	 * @throws ContextClosedException
	 *             If the context connected to the image is closed and cannot be
	 *             used.
	 * @throws ZoneException
	 *             If anything unexpected happens
	 * @see com.att.cdp.zones.ImageService#waitForStateChange(int, int,
	 *      com.att.cdp.zones.model.Image,
	 *      com.att.cdp.zones.model.Image.Status[])
	 */
	@SuppressWarnings("nls")
	public void waitForStateChange(int pollInterval, int timeout, Image image, Image.Status... allowedStates)
			throws TimeoutException, NotNavigableException, InvalidRequestException, ContextClosedException,
			ZoneException {

		/*
		 * Check that the arguments are specified, non-null, and of the correct
		 * domain
		 */
		checkArg(pollInterval, "pollInterval");
		checkInteger(pollInterval, "pollInterval");
		checkArg(timeout, "timeout");
		checkInteger(timeout, "timeout");
		checkArg(image, "image");
		checkArg(allowedStates, "allowedStates");

		/*
		 * Check that the poll interval and timeout are both positive, non-zero
		 * values, and that the timeout >= poll interval.
		 */
		if (pollInterval <= 0) {
			throw new InvalidRequestException(
					EELFResourceManager.format(Msg.INVALID_POLL_INTERVAL, Integer.toString(pollInterval)));
		}
		if (timeout != 0 && timeout < pollInterval) {
			throw new InvalidRequestException(EELFResourceManager.format(Msg.INVALID_POLL_TIMEOUT,
					Integer.toString(timeout), Integer.toString(pollInterval)));
		}

		/*
		 * Make sure that the image is connected
		 */
		if (!image.isConnected()) {
			throw new NotNavigableException(EELFResourceManager.format(Msg.NOT_NAVIGABLE));
		}

		/*
		 * Accumulate the states and normalize them so they are all unique, just
		 * in case the caller includes multiple of the same state. No need doing
		 * more work than we have to...
		 */
		List<Image.Status> states = new ArrayList<>();
		for (Image.Status allowedState : allowedStates) {
			if (!states.contains(allowedState)) {
				states.add(allowedState);
			}
		}

		/*
		 * Compute the time limit for the operation. This is checked after each
		 * poll interval is completed.
		 */
		long delay = pollInterval * 1000L;
		long limit = System.currentTimeMillis() + (timeout * 1000L);
		boolean found = false;
		outer: do {
			image.refreshAll();
			for (Image.Status state : states) {
				if (state.equals(image.getStatus())) {
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
		 * Check to see if we found the image in one of the allowed states?
		 */
		if (!found) {
			StringBuilder builder = new StringBuilder("[");
			for (Image.Status state : states) {
				builder.append(state.name());
				builder.append(',');
			}
			builder.replace(builder.length() - 1, builder.length(), "]");

			throw new TimeoutException(EELFResourceManager.format(Msg.IMAGE_TIMEOUT, image.getName(), image.getId(),
					Integer.toString(timeout), image.getStatus().name(), builder.toString()));
		}
	}

}
