/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.test;

import java.util.List;
import java.util.Map;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.model.Network;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Rule;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Template;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.model.VirtualInterface;
import com.att.cdp.zones.model.Volume;
import com.att.cdp.zones.spi.AbstractCompute;
import com.att.cdp.zones.spi.AbstractContext;

/**
 * @since Oct 29, 2015
 * @version $Id$
 */
public class DummyCompute extends AbstractCompute {

    /**
     * Create the identity service implementation
     * 
     * @param context
     *            The context object we are servicing
     */
    public DummyCompute(AbstractContext context) {
        super(context);
    }

    @Override
    public void assignIpAddress(Server server, String address) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void assignIpAddress(String serverId, String address) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void attachVolume(Server server, Volume volume, String deviceName) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public Server createServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createServerSnapshot(Server server, String name) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteServer(String serverId) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void detachVolume(Server server, String deviceName) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void detachVolume(Server server, Volume volume) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#executeCommand(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @Override
    public void executeCommand(Server server, String command) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ACL> getAccessControlLists() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getAttachments(Server server) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getAttachments(String id) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.ComputeService#getConsoleOutput(com.att.cdp.zones.model.Server)
     */
    @Override
    public List<String> getConsoleOutput(Server server) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Server getServer(String id) throws ZoneException {
        // TODO Auto-generated method stub
        return new DummyServer(getContext());
    }

    @Override
    public List<Server> getServers() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Server> getServers(String name) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Template getTemplate(String string) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Template> getTemplates() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tenant getTenant() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<VirtualInterface> getVirtualInterfaces(String id) {
        return null;
    }

    @Override
    public List<Network> getExtendedNetworks() {
        return null;
    }

    @Override
    public void releaseIpAddress(Server server, String assignedAddress) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void releaseIpAddress(String serverId, String assignedAddress) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void resumeServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void resumeServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void suspendServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void suspendServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void prepareResize(Server server, Template newTemplate) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void processResize(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public Rule addACLRule(String aclId, Rule rule) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void associateACL(String serverId, String aclName) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#createAccessControlList(com.att.cdp.zones.model.ACL)
     */
    @Override
    public ACL createAccessControlList(ACL model) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAccessControlList(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteACLRule(Rule rule) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void disassociateACL(String serverId, String aclName) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public ACL getAccessControlList(String id) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void rebuildServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub
    }

    /**
     * @see com.att.cdp.zones.ComputeService#rebuildServer(com.att.cdp.zones.model.Server, java.lang.String)
     */
    @Override
    public void rebuildServer(Server server, String snapshot) throws ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void pauseServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void pauseServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unpauseServer(String id) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unpauseServer(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> findAllServersUsingKey(String keyPair) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void refreshServerStatus(Server server) throws ZoneException {
        // TODO Auto-generated method stub
    }

    /**
     * @see com.att.cdp.zones.ComputeService#migrateServer(java.lang.String)
     */
    @Override
    public void migrateServer(String serverId) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#moveServer(java.lang.String, java.lang.String)
     */
    @Override
    public void moveServer(String serverId, String targetHostId) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return "";
    }

    /**
     * @see com.att.cdp.zones.ComputeService#abortResize(com.att.cdp.zones.model.Server)
     */
    @Override
    public void abortResize(Server server) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#getPorts(com.att.cdp.zones.model.Server)
     */
    @Override
    public List<Port> getPorts(Server server) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.ComputeService#attachPort(com.att.cdp.zones.model.Server, com.att.cdp.zones.model.Port)
     */
    @Override
    public void attachPort(Server server, Port port) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#detachPort(com.att.cdp.zones.model.Server, com.att.cdp.zones.model.Port)
     */
    @Override
    public void detachPort(Server server, Port port) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.ComputeService#getHypervisor(java.lang.String)
     */
    @Override
    public Hypervisor getHypervisor(String id) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.ComputeService#getHypervisors()
     */
    @Override
    public List<Hypervisor> getHypervisors() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.ComputeService#getHypervisors(java.lang.String)
     */
    @Override
    public List<Hypervisor> getHypervisors(String name) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.ComputeService#refreshHypervisorStatus(com.att.cdp.zones.model.Hypervisor)
     */
    @Override
    public void refreshHypervisorStatus(Hypervisor hypervisor) throws ZoneException {
        // TODO Auto-generated method stub

    }
}
