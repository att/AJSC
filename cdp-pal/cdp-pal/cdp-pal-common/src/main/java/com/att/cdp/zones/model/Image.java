/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.Map;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * An image represents a complete disk copy that can be restored quickly and efficiently to a volume and then used by a
 * server.
 * <p>
 * An image is usually used to supply the operating system disk and operational environment. When a VM is started, the
 * image associated with a volume is restored to that volume and then used to run the VM.
 * </p>
 * 
 * @since Sep 25, 2013
 * @version $Id$
 */
public class Image extends ModelObject {

    /**
     * This enumeration is an embedded status that describes the state of the image
     * 
     * @since Oct 16, 2013
     * @version $Id$
     */
    public enum Status {
        /**
         * The image is an active image that can be operated upon or used.
         */
        ACTIVE,

        /**
         * The image has been deleted and cannot be used.
         */
        DELETED,

        /**
         * The image is in an error state and cannot be used.
         */
        ERROR,

        /**
         * The status of the image cannot be determined
         */
        INDETERMINATE,

        /**
         * The image is currently undergoing some operation and is not active. This state reflects a change being
         * performed on the image and no other operations are allowed while it is in this state.
         */
        PENDING;
    }

    /**
     * This enumeration is used to indicate the type of image that we are representing. Some providers provide the
     * ability to create snapshots of a running instance, and then use the snapshot as an image to create other
     * instances.
     */
    public enum Type {
        /**
         * The image is a boot image created and installed externally or as part of the provider system. It is not a
         * snapshot or other image created from a running system.
         */
        BASIC,

        /**
         * The image is a boot image created by copying another running instance, possibly after configuration may have
         * been performed. There is a linkage between the original image/instance and this image.
         */
        SNAPSHOT;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The architecture of the image
     */
    private String architecture;

    /**
     * The format of the image
     */
    private Format format;

    /**
     * The identifier of this image
     */
    private String id;

    /**
     * The type of image we are representing
     */
    private Type imageType;

    /**
     * If the image is a snapshot, then this field contains the ID of the instance (VM) that the image is a snapshot of.
     */
    private String instanceId;

    /**
     * The metadata of the image
     */
    private Map<String, PersistentObject> metadata;

    /**
     * The minimum amount of disk space needed
     */
    private Integer minimumDisk;

    /**
     * The minimum amount of memory needed
     */
    private Integer minimumMemory;

    /**
     * The name of the image
     */
    private String name;

    /**
     * The operating system of the image
     */
    private String osType;

    /**
     * The owner of the image
     */
    private String owner;

    /**
     * The size, in bytes, of the image
     */
    private Long size;

    /**
     * The status of the image
     */
    private Status status;

    /**
     * The URI that identifies the image
     */
    private String uri;

    /**
     * Default constructor
     */
    public Image() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Image(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        Image other = (Image) obj;
        return id.equals(other.id) && name.equals(other.name);
    }

    /**
     * JavaBean accessor to obtain the value of architecture
     * 
     * @return the architecture that identifies this image
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * JavaBean accessor to obtain the value of format
     * 
     * @return the format value
     */
    public Format getFormat() {
        return format;
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * @return The type of image
     */
    public Type getImageType() {
        return imageType;
    }

    /**
     * @return the value of instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * JavaBean accessor to obtain the value of metadata
     * 
     * @return the metadata value
     */
    public Map<String, PersistentObject> getMetadata() {
        return metadata;
    }

    /**
     * JavaBean accessor to obtain the value of minimumDisk
     * 
     * @return the minimumDisk value
     */
    public Integer getMinimumDisk() {
        return minimumDisk;
    }

    /**
     * JavaBean accessor to obtain the value of minimumMemory
     * 
     * @return the minimumMemory value
     */
    public Integer getMinimumMemory() {
        return minimumMemory;
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
     * JavaBean accessor to obtain the value of osType
     * 
     * @return the osType that identifies this image
     */
    public String getOsType() {
        return osType;
    }

    /**
     * JavaBean accessor to obtain the value of owner
     * 
     * @return the owner value
     */
    public String getOwner() {
        return owner;
    }

    /**
     * JavaBean accessor to obtain the value of size
     * 
     * @return the size value
     */
    public Long getSize() {
        return size;
    }

    /**
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public Status getStatus() {
        return status;
    }

    /**
     * JavaBean accessor to obtain the value of URI
     * 
     * @return the URI that identifies this image
     */
    public String getUri() {
        return uri;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
	 * This method informs the provider implementation to refresh this image
	 * object with the current state as defined on the provider.
	 * 
	 * @throws ZoneException
	 *             If the image cannot be refreshed
	 */
	public void refreshAll() throws ZoneException {
		notConnectedError();
	}
    
    /**
     * @param architecture
     *            the value for architecture
     */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * @param format
     *            the value for format
     */
    public void setFormat(Format format) {
        this.format = format;
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
     * @param imageType
     *            The type of image
     */
    public void setImageType(Type imageType) {
        this.imageType = imageType;
    }

    /**
     * @param instanceId
     *            the value for instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @param metadata
     *            the value for metadata
     */
    public void setMetadata(Map<String, PersistentObject> metadata) {
        this.metadata = metadata;
    }

    /**
     * @param minimumDisk
     *            the value for minimumDisk
     */
    public void setMinimumDisk(Integer minimumDisk) {
        this.minimumDisk = minimumDisk;
    }

    /**
     * @param minimumMemory
     *            the value for minimumMemory
     */
    public void setMinimumMemory(Integer minimumMemory) {
        this.minimumMemory = minimumMemory;
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
     * @param osType
     *            the value for osType
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * @param owner
     *            the value for owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @param size
     *            the value for size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param uri
     *            the value for uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String
            .format(
                "Image: id(%s), name(%s), type(%s), owned(%s), size(%d), status(%s), url(%s), created(%s), snapshot instance(%s)",
                id, name, imageType.toString(), owner, size, status, uri, getCreatedDate(), instanceId);
    }
    
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
	public void waitForStateChange(int pollInterval, int timeout, Image.Status... status) throws TimeoutException,
			NotNavigableException, InvalidRequestException, ContextClosedException, ZoneException {

		getContext().getImageService().waitForStateChange(pollInterval, timeout, this, status);
	}
}
