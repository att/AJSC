/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.att.cdp.zones.spi.RequestState;

/**
 * This test case tests the thread-local request state class used to track the state of the current request being
 * processed so that the exception mapper can produce good diagnostics.
 * 
 * @since Jan 6, 2015
 * @version $Id$
 */
public class TestRequestState {

    /**
     * Test that we can set/obtain request state
     */
    @Test
    public void testGetStateMap() {
        RequestState.clear();
        Map<String, Object> map = RequestState.getState();
        assertNotNull(map);
        assertTrue(map.isEmpty());

        RequestState.put("Test", "Value");
        map = RequestState.getState();
        assertNotNull(map);
        assertTrue(map.containsKey("Test"));

        RequestState.clear();
        map = RequestState.getState();
        assertNotNull(map);
        assertFalse(map.containsKey("Test"));
    }

    /**
     * Create two threads with different state and verify that they are set correctly in each thread
     */
    @Test
    public void testThreads() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("r1", "r1");
        TestRequest r1 = new TestRequest(map);

        map = new HashMap<String, Object>();
        map.put("r2", "r2");
        TestRequest r2 = new TestRequest(map);
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);

        t1.start();
        t2.start();

        try {
            Thread.sleep(250L);
        } catch (InterruptedException e) {
            // ignore
        }

        synchronized (r1) {
            r1.notify();
        }

        synchronized (r2) {
            r2.notify();
        }

    }

    /**
     * This class is used to create multiple threads and to test that the request state is unique by thread.
     * 
     * @since Jan 6, 2015
     * @version $Id$
     */
    public class TestRequest implements Runnable {

        private Map<String, Object> map;
        private String key;
        private String value;

        public TestRequest(Map<String, Object> map) {
            this.map = map;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                RequestState.put(entry.getKey(), entry.getValue());
            }
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Map<String, Object> state = RequestState.getState();
            assertNotNull(state);
            assertTrue(state.size() == map.size());
            for (String key : state.keySet()) {
                assertTrue(map.containsKey(key));
                assertEquals(state.get(key), map.get(key));
            }
        }
    }
}
