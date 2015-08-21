/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author abythell
 */
public class XbmqConfigTest {
            
    public XbmqConfigTest() {
    }
        
    @Test
    public void testProperties() {        
        XbmqConfig config = new XbmqConfig();
        assertFalse("missing broker", config.getBroker().isEmpty());
        assertFalse("missing rootTopic", config.getRootTopic().isEmpty());
        assertFalse("missing port", config.getXBeePort().isEmpty());
        assertFalse("missing baud", config.getXBeeBaud() == null);
    }
    
    @Test
    public void testDefaultProperties() {
        String missingPropertiesFile = "/dev/null";
        XbmqConfig config = new XbmqConfig(missingPropertiesFile);
        assertEquals("bad default broker", "tcp://test.mosquitto.org:1883", config.getBroker());
        assertTrue("bad default root topic", config.getRootTopic().isEmpty());
        assertTrue("bad default baud rate", 9600 == config.getXBeeBaud());
        assertEquals("bad default port", "/dev/ttyUSB0", config.getXBeePort());
    }
        
}
