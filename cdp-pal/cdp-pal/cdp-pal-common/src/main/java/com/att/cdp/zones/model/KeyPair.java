/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */

public class KeyPair extends ModelObject {

    /**
     * Serial number
     */
    protected static final long serialVersionUID = 1L;

    /**
     * The "fingerprint" (digest) of the key pair, used to ensure that no tampering has been performed.
     */
    protected String fingerprint;

    /**
     * The name of the key pair
     */
    protected String name;

    /**
     * The private key of the pair
     */
    protected String privateKey;

    /**
     * The public key of the pair
     */
    protected String publicKey;

    /**
     * A user id associated with the key pair. The appears to be unused in OpenStack
     */
    protected String userId;

    /**
     * Requests to create a key pair with just the name. The provider will generate the public key and the PEM file
     * contents.
     * 
     * @param name
     *            The name of the key-pair
     */
    public KeyPair(String name) {
        this.name = name;
    }

    /**
     * Requests to create a key pair with the name and the public key. The provider will create the PEM file and return
     * it for this key-pair.
     * 
     * @param name
     *            The name of the key-pair
     * @param publicKey
     *            The public key to be used.
     */
    public KeyPair(String name, String publicKey) {
        this(name);
        this.publicKey = publicKey;
    }

    /**
     * private default constructor allows only the connected key-pair and implementation classes use
     */
    protected KeyPair() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected KeyPair(Context context) {
        super(context);
    }

    /**
     * Deletes the key-pair from the tenant
     * 
     * @throws ZoneException
     *             If the KeyPair object is not connected to a context
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        KeyPair other = (KeyPair) obj;
        return name.equals(other.getName());
    }

    /**
     * JavaBean accessor to obtain the value of fingerprint
     * 
     * @return the fingerprint value
     * @throws ZoneException
     *             If the model object is not connected
     */
    public String getFingerprint() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }

        return fingerprint;
    }

    /**
     * Returns the name of the key-pair
     * 
     * @return the name of the key pair
     */
    public String getName() {
        return name;
    }

    /**
     * JavaBean accessor to obtain the value of privateKey
     * 
     * @return the privateKey value
     * @throws ZoneException
     *             If the model object is not connected
     */
    public String getPrivateKey() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }
        return privateKey;
    }

    /**
     * Returns the public key for the pair
     * 
     * @return The public key for this key-pair
     * @throws ZoneException
     *             If the KeyPair object is not connected to a context
     */
    public String getPublicKey() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }
        return publicKey;
    }

    /**
     * JavaBean accessor to obtain the value of userId
     * 
     * @return the userId value
     * @throws ZoneException
     *             If the model object is not connected
     */
    public String getUserId() throws ZoneException {
        if (!isConnected()) {
            notConnectedError();
        }

        return userId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @param fingerprint
     *            the value for fingerprint
     */
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param privateKey
     *            the value for privateKey
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @param publicKey
     *            the value for publicKey
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * @param userId
     *            the value for userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("KeyPair: name(%s), public key(%s), fingerprint(%s)", name, publicKey, fingerprint);
    }

}
