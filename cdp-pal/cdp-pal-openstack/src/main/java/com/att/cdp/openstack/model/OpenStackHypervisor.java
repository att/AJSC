/**
 * Copyright (C) 2017, AT&T Inc. All rights reserved. Proprietary materials, property of AT&T. For internal use only,
 * not for disclosure to parties outside of AT&T or its affiliates.
 */

package com.att.cdp.openstack.model;

import java.util.HashMap;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.model.HypervisorType;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedHypervisor;

/**
 * This class represents the OpenStack implementation of the {@link Hypervisor} object.
 * <p>
 * The model object allows the user to navigate to and reference the related objects. However, these relationships are
 * not populated until they are requested. Therefore, the model object implementation must track the fact that either
 * the relationships have been or have not been discovered, and delay doing that until a method that requires that
 * information is called. Once the relationships have been discovered, they are not requested from the provider again,
 * unless the service is called to refresh all of the model (which discards the object and builds a new one). This is a
 * "lazy" traversal of the transitive relationships.
 * </p>
 * 
 * @author <a href= "mailto:ry303t@att.com?subject=com.att.cdp.openstack.model.OpenStackHypervisor"> Ryan Young</a>
 * @since Jan 10, 2017
 * @version $Id$
 */
public class OpenStackHypervisor extends ConnectedHypervisor {
    /**
     * This is a map of the hypervisor state values used by OpenStack to the API hypervisor state values.
     */
    @SuppressWarnings("nls")
    private static final String[][] HYPERVISOR_STATE_MAP = { { "UP", State.UP.toString() },
        { "DOWN", State.DOWN.toString() }, };

    /**
     * This is a map of the hypervisor status values used by OpenStack to the API hypervisor status values.
     */
    @SuppressWarnings("nls")
    private static final String[][] HYPERVISOR_STATUS_MAP = { { "ENABLED", Status.ENABLED.toString() },
        { "DISABLED", Status.DISABLED.toString() } };

    // @formatter:on

    /**
     * The serial version id
     */
    private static final long serialVersionUID = 1L;

    /**
     * A reference to the Nova hypervisor model that we can use to lazy load relationships, if needed.
     */
    private com.woorea.openstack.nova.model.Hypervisor novaModel;

    /**
     * @param context
     *            The open stack context we are servicing
     * @param hypervisor
     *            The open stack hypervisor object we are representing
     * @throws ZoneException
     *             If the hypervisor cannot be mapped
     */
    @SuppressWarnings("nls")
    public OpenStackHypervisor(Context context, com.woorea.openstack.nova.model.Hypervisor hypervisor)
                    throws ZoneException {
        super(context);

        novaModel = hypervisor;

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("currentWorkload", "currentWorkload");
        dictionary.put("freeDiskGb", "diskFree");
        dictionary.put("freeRamMb", "memoryFree");
        dictionary.put("hostIp", "hostIp");
        dictionary.put("hypervisorHostname", "hostName");
        dictionary.put("hypervisorVersion", "version");
        dictionary.put("id", "id");
        dictionary.put("localGb", "diskSize");
        dictionary.put("memoryMb", "memorySize");
        dictionary.put("runningVms", "runningVMs");

        ObjectMapper.map(hypervisor, this, dictionary);
        mapHypervisorStatus(hypervisor);
        mapHypervisorState(hypervisor);
        // mapHypervisorArchitecture(hypervisor);
        mapHypervisorType(hypervisor);
    }

    /**
     * This method is used to map the OpenStack hypervisor state to the abstract hypervisor state definitions.
     * 
     * @param hypervisor
     *            The nova hypervisor to be examined to map the state
     */
    public void mapHypervisorState(com.woorea.openstack.nova.model.Hypervisor hypervisor) {

        /*
         * Refresh the cached nova model with the new hypervisor object
         */
        if (hypervisor.getState() != null && !hypervisor.getState().isEmpty()) {
            String hypervisorState = hypervisor.getState().toUpperCase();
            for (int index = 0; index < HYPERVISOR_STATE_MAP.length; index++) {
                if (hypervisorState.startsWith(HYPERVISOR_STATE_MAP[index][0])) {
                    setState(State.valueOf(HYPERVISOR_STATE_MAP[index][1]));
                    break;
                }
            }
        }
    }

    /**
     * This method is used to map the OpenStack hypervisor status to the abstract hypervisor status definitions.
     * 
     * @param hypervisor
     *            The nova hypervisor to be examined to map the status
     */
    public void mapHypervisorStatus(com.woorea.openstack.nova.model.Hypervisor hypervisor) {

        /*
         * Refresh the cached nova model with the new hypervisor object
         */
        if (hypervisor.getStatus() != null && !hypervisor.getStatus().isEmpty()) {
            String hypervisorStatus = hypervisor.getStatus().toUpperCase();
            for (int index = 0; index < HYPERVISOR_STATUS_MAP.length; index++) {
                if (hypervisorStatus.startsWith(HYPERVISOR_STATUS_MAP[index][0])) {
                    setStatus(Status.valueOf(HYPERVISOR_STATUS_MAP[index][1]));
                    break;
                }
            }
        }
    }

    /**
     * This method is used to map the OpenStack hypervisor cpu architecture to the abstract hypervisor architecture
     * definitions.
     * 
     * @param hypervisor
     *            The nova hypervisor to be examined to map the architecture
     */
    public void mapHypervisorArchitecture(com.woorea.openstack.nova.model.Hypervisor hypervisor) {

        /*
         * Refresh the cached nova model with the new hypervisor object
         */
        /*
         * if (hypervisor.getCpuInfo() != null && !hypervisor.getCpuInfo().isEmpty()) { Map<String, String> cpuInfo =
         * hypervisor.getCpuInfo(); // TODO: remove hard coded string String arch = cpuInfo.get("arch"); if (arch !=
         * null) { try { setCpuArchitecture(Architecture.valueOf(arch.toUpperCase())); } catch (IllegalArgumentException
         * e) { setType(null); } } }
         */
    }

    /**
     * This method is used to map the OpenStack hypervisor type to the abstract hypervisor type definitions.
     * 
     * @param hypervisor
     *            The nova hypervisor to be examined to map the hypervisor type
     */
    public void mapHypervisorType(com.woorea.openstack.nova.model.Hypervisor hypervisor) {

        /*
         * Refresh the cached nova model with the new hypervisor object
         */
        String type = hypervisor.getHypervisorType();
        if (type != null) {
            try {
                setType(HypervisorType.valueOf(type));
            } catch (IllegalArgumentException e) {
                setType(null);
            }
        }
    }
}
