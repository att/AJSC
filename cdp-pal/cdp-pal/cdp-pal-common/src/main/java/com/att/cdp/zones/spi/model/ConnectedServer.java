/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.model.Fault;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.ServerBootSource;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.model.Volume;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */

public class ConnectedServer extends Server {

    protected static final Logger LOG = ConfigurationFactory.getConfiguration().getServerLogger();

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Server</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedServer(Context context) {
        super(context);
        setBootSource(ServerBootSource.UNKNOWN);
    }

    /**
     * @see com.att.cdp.zones.model.Server#assignIp(java.lang.String)
     */
    @Override
    public void assignIp(String address) throws ZoneException {
        Context context = getContext();
        context.getComputeService().assignIpAddress(this, address);
    }

    /**
     * @see com.att.cdp.zones.model.Server#assignIpAddressFromPool()
     */
    @Override
    public String assignIpAddressFromPool() throws ZoneException {
        Context context = getContext();
        List<String> pools = context.getNetworkService().getFloatingIpPools();
        if (pools == null || pools.isEmpty()) {
            throw new InvalidRequestException(EELFResourceManager.format(Msg.NO_FLOATING_IP_POOL));
        }
        return assignIpAddressFromPool(pools.get(0));
    }

    /**
     * @see com.att.cdp.zones.model.Server#assignIpAddressFromPool(java.lang.String)
     */
    @Override
    public String assignIpAddressFromPool(String pool) throws ZoneException {
        Context context = getContext();
        return context.getNetworkService().assignIpAddressFromPool(this, pool);
    }

    /**
     * @see com.att.cdp.zones.model.Server#attachVolume(com.att.cdp.zones.model.Volume, java.lang.String)
     */
    @Override
    public void attachVolume(Volume volume, String device) throws ZoneException {
        Context context = getContext();
        context.getComputeService().attachVolume(this, volume, device);
    }

    /**
	 * Requests the creation of a snapshot image of this server
	 * 
	 * @param name
	 *            The name of the snapshot that will be created
	 * @throws ZoneException
	 *             If the server cannot be snapshot-ed
	 */
    @Override
	public void createSnapshot(String name) throws ZoneException {
		Context context = getContext();
		context.getComputeService().createServerSnapshot(this, name);
	}
    
    /**
     * Delete the specified server using it's id.
     * 
     * @throws ZoneException
     *             If the server does not exist or cannot be deleted for some reason.
     */
    @Override
    public void delete() throws ZoneException {
        Context context = getContext();
        context.getComputeService().deleteServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#detachVolume(java.lang.String)
     */
    @Override
    public void detachVolume(String device) throws ZoneException {
        Context context = getContext();
        context.getComputeService().detachVolume(this, device);
    }

    /**
     * @see com.att.cdp.zones.model.Server#getAttachments()
     */
    @Override
    public Map<String, String> getAttachments() throws ZoneException {
        Context context = getContext();
        return context.getComputeService().getAttachments(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#getFixedIpsViaInterfaces()
     */
    @Override
    public List<String> getFixedIpsViaInterfaces() throws ZoneException {
        Context context = getContext();
        List<String> ipAddrList = null;
        List<com.att.cdp.zones.model.Port> ports = context.getNetworkService().getInterfaces(getId());
        if (ports != null && ports.size() > 0) {
            LOG.info("getFixedIpViaInterfaces(): ports={}", ports);
            ipAddrList = ports.get(0).getAddresses();
        } else {
            LOG.warn("getFixedIpViaInterfaces(): No ports found!");
        }
        return ipAddrList;
    }

    /**
     * @see com.att.cdp.zones.model.Server#refresh()
     */
    @Override
    public void refreshAll() throws ZoneException {
        Context context = getContext();
        Server copy = context.getComputeService().getServer(getId());
        ObjectMapper.map(copy, this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#refreshStatus()
     */
    @Override
    public void refreshStatus() throws ZoneException {
        Context context = getContext();
        context.getComputeService().refreshServerStatus(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#releaseIpAddress(java.lang.String)
     */
    @Override
    public void releaseIpAddress(String address) throws ZoneException {
        Context context = getContext();
        context.getComputeService().releaseIpAddress(this, address);
    }

    /**
     * @see com.att.cdp.zones.model.Server#resume()
     */
    @Override
    public void resume() throws ZoneException {
        Context context = getContext();
        context.getComputeService().resumeServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#rebuild()
     */
    @Override
    public void rebuild() throws ZoneException {
        Context context = getContext();
        context.getComputeService().rebuildServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#rebuild(java.lang.String)
     */
    @Override
    public void rebuild(String snapshot) throws ZoneException {
        Context context = getContext();
        context.getComputeService().rebuildServer(this, snapshot);
    }

    /**
     * @see com.att.cdp.zones.model.Server#stop()
     */
    @Override
    public void stop() throws ZoneException {
        Context context = getContext();
        context.getComputeService().stopServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#start()
     */
    @Override
    public void start() throws ZoneException {
        Context context = getContext();
        context.getComputeService().startServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#suspend()
     */
    @Override
    public void suspend() throws ZoneException {
        Context context = getContext();
        context.getComputeService().suspendServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#pause()
     */
    @Override
    public void pause() throws ZoneException {
        Context context = getContext();
        context.getComputeService().pauseServer(this);
    }

    /**
     * @see com.att.cdp.zones.model.Server#unpause()
     */
    @Override
    public void unpause() throws ZoneException {
        Context context = getContext();
        context.getComputeService().unpauseServer(this);
    }

    /**
     * Override the API class to actually get and return the list of ports from the service provider and to cache them
     * once obtained.
     * 
     * @see com.att.cdp.zones.model.Server#getPorts()
     */
    @Override
    public List<Port> getPorts() throws ZoneException {
        Context context = getContext();
        ComputeService service = context.getComputeService();
        List<Port> ports = service.getPorts(this);
        if (ports == null) {
            ports = new ArrayList<Port>();
        }
        return ports;
    }

    /**
     * @see com.att.cdp.zones.model.Server#attachPort(com.att.cdp.zones.model.Port)
     */
    @Override
    public void attachPort(Port nic) throws ZoneException {
        Context context = getContext();
        ComputeService service = context.getComputeService();
        service.attachPort(this, nic);
    }

    /**
     * @see com.att.cdp.zones.model.Server#detachPort(com.att.cdp.zones.model.Port)
     */
    @Override
    public void detachPort(Port port) throws ZoneException {
        Context context = getContext();
        ComputeService service = context.getComputeService();
        service.detachPort(this, port);
    }
    
    /**
     * This method reboot the given server
     * @param rebootType
     * @throws ZoneException
     */
    public void reboot(String rebootType) throws ZoneException {
        Context context = getContext();
        context.getComputeService().rebootServer(this,rebootType);
    }
    
   
}
