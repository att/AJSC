/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.util.List;

import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.Provider;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.model.Volume;

/**
 * @since Oct 7, 2013
 * @version $Id$
 */

public abstract class AbstractVolume extends AbstractService implements VolumeService {

    /**
     * Create the abstract volume service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractVolume(Context context) {
        super(context);
    }
    
    @Override
    public Volume createVolume(Volume template, Server server) throws ZoneException{
    	Volume vol = this.createVolume(template);    	
    	return vol;
    }
    
  
    @Override
    public void  destroyVolume(String id, Server server) throws ZoneException{
    	this.destroyVolume(id);   	
    	
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
     * This is a default implementation that is correct for all providers other than VMware. The VMware provider
     * overrides this method. Any other providers that want to implement this method may also override it if needed.
     * 
     * @see com.att.cdp.zones.VolumeService#destroySnapshot(java.lang.String, com.att.cdp.zones.model.Server)
     */
    @Override
    public void destroySnapshot(String id, Server server) throws ZoneException {
        this.destroySnapshot(id);
    }

    /**
     * This is a default implementation that is correct for all providers other than VMware. The VMware provider
     * overrides this method. Any other providers that want to implement this method may also override it if needed.
     * 
     * @see com.att.cdp.zones.VolumeService#createSnapshot(com.att.cdp.zones.model.Snapshot,
     *      com.att.cdp.zones.model.Server)
     */
    @Override
    public Snapshot createSnapshot(Snapshot template, Server server) throws ZoneException {
        return this.createSnapshot(template);
    }

    /**
     * This is a default implementation that is correct for all providers other than VMware. The VMware provider
     * overrides this method. Any other providers that want to implement this method may also override it if needed.
     * 
     *    updateVolume method is used to update the size of the volume (only increase)
     */
    @Override
    public Volume updateVolume(Volume template, Server server) throws ZoneException{
    	Context context = getContext();
        Provider provider = context.getProvider();
        throw new NotSupportedException(String.format(
                "Provider %s does not support updateVolume using a volume object as a model", provider.getName()));
    	
    }
    /**
     * This is a default implementation that is correct for all providers other than VMware. The VMware provider
     * overrides this method. Any other providers that want to implement this method may also override it if needed.
     *    
     *    getVolumes methods gives a list of volumes for a particular model
     */
    
    @Override
    public List<Volume> getVolumes(Server server) throws ZoneException{
    	Context context = getContext();
        Provider provider = context.getProvider();
        throw new NotSupportedException(String.format(
                "Provider %s does not support getVolumebyServer Id using  a model", provider.getName()));
    	
    	
    }
    
}
