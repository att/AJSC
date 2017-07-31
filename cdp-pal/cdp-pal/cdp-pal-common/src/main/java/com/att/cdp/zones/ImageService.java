/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Image;

/**
 * This interface represents the implementation of the Image service of the cloud service provider. Image services are
 * used to manage images that can be deployed to compute resources.
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */

public interface ImageService extends Service {

    /**
     * @param string
     *            The id of the image desired
     * @return The image
     * @throws ZoneException
     *             If the image cannot be obtained
     */
    Image getImage(String string) throws ZoneException;

    /**
     * This method is used to list all of the images that are available within the context.
     * 
     * @return The list of images or an empty list if there are none available.
     * @throws ZoneException
     *             If the service fails.
     */
    List<Image> listImages() throws ZoneException;

    /**
     * This method is used to list all of the images where the name of the image matches the supplied pattern.
     * 
     * @param pattern
     *            The pattern (regular expression) that is compared to the image name to select it for the returned
     *            list. If the pattern is null, then the method acts identically to {@link #listImages()}.
     * @return The list of images that match the specified name pattern
     * @throws ZoneException
     *             If anything fails
     */
    List<Image> listImages(String pattern) throws ZoneException;

    /**
     * This method is used to get the images where the name of the image exactly matches the supplied name.
     * 
     * @param name
     *            The name of the image we are trying to get
     * @return The image of the specified name
     * @throws ZoneException
     */
    Image getImageByName(String name) throws ZoneException;
    
	/**
	 * This is a standardized method to wait for the state of the image to
	 * change to one of the allowed states.
	 * <p>
	 * This method will block the caller and not return until either:
	 * <ul>
	 * <li>The image provided has been found to be in one of the provided
	 * states on a sample</li>
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
	 * The caller must also provide the image object to be checked. The image
	 * object must be a connected image object. Using a disconnected image
	 * object is an error and will result in an exception being thrown.
	 * </p>
	 * <p>
	 * The caller lastly provides a variable list of allowed image states. This
	 * is a variable argument list that allows for one or more states to be
	 * listed. If the image is found to be in any one of these states on a
	 * sample interval, then the method returns and no exception is thrown.
	 * </p>
	 * 
	 * @param pollInterval
	 *            The interval, in seconds, to check the image state and see if
	 *            the image state has changed.
	 * @param timeout
	 *            The total time, in seconds, that the method will block the
	 *            caller and check the image state. This value MUST be greater
	 *            than or equal to the poll interval.
	 * @param image
	 *            The image to be checked
	 * @param status
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
	 *             If the context connected to the image is closed and cannot
	 *             be used.
	 * @throws ZoneException
	 *             If anything unexpected happens
	 */
	void waitForStateChange(int pollInterval, int timeout, Image image, Image.Status... status)
			throws TimeoutException, NotNavigableException, InvalidRequestException, ContextClosedException,
			ZoneException;

}
