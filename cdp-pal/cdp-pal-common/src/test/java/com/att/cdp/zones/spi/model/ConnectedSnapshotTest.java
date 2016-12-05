/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.*;

import org.junit.Test;


public class ConnectedSnapshotTest {
	
	ConnectedSnapshot thisCS = new ConnectedSnapshot(null);

	@Test
	public void testIsConnected() {
		assertNotNull(thisCS.isConnected());
	}

	@Test
	public void testSetId() {
		thisCS.setId("myid");
	}

	@Test
	public void testSetStatus() {
		thisCS.setStatus(null);
	}

	@Test
	public void testSetVolumeId() {
		thisCS.setVolumeId("myvolume");
	}

}
