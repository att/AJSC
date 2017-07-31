/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.stub.WiremockStub;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Volume;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

/**
 * This test is used to test the volume service support.
 * <p>
 * This test should not be run as a normal part of the build. It's success depends on the accessibility to a suitable
 * OpenStack provider, proper credentials, and other environmental configurations that are not likely to be present on
 * the build system. This is a developer-supported and developer-used test only, and is not part of the product
 * certification test suite!
 * </p>
 * 
 * @since Jan 26, 2015
 * @version $Id$
 */

public class TestVolumeService extends AbstractTestCase {
	@ClassRule
	public static WireMockClassRule  wireMockRule = new WireMockClassRule(wireMockConfig().bindAddress("127.0.0.1").port(8089).httpsPort(5000));
	
	@Rule
	public WireMockClassRule instanceRule = wireMockRule;

	static{
		   System.setProperty("mock", "com/att/cdp/mock-test.properties");
		   WiremockStub.disableSslVerification();
		}
	
	@BeforeClass
	public static void beforeClazz(){
		WiremockStub.contextLogin();
	}
    /**
     * @throws ZoneException
     */
    @Test
    @Ignore
    public void listVolumes() throws ZoneException {
        Context context = connect();
        VolumeService service = context.getVolumeService();
        List<Volume> volumes = service.getVolumes();

        assertNotNull(volumes);
        for (Volume volume : volumes) {
          	assertNotNull(service.getVolume(volume.getId()));
        	assertNotNull(service.getVolumes(volume.getName()));
            if (volume.getStatus().equals(Volume.Status.ERROR)) {
                System.out.printf("%-12s: %s\n", volume.getName(), volume.getId());
            }
        }
    }
}
