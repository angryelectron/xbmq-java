/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Gateway configuration and settings.  Reads settings from xbmq.properties.
 * Returns default values if properties file not found. 
 */
public class XbmqConfig {
    private static final String propertiesFile = "xbmq.properties";
    private final Properties properties = new Properties();
    
    /**
     * Constructor.
     */
    public XbmqConfig() {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, 
                    "xbmq.properties not found - using defaults.");
        }
    }
    
    /**
     * Get the name of the serial port connected to the local XBee device.
     * @return Port name or "/dev/ttyUSB0" if property not set.
     */
    public String getXBeePort() {
        String port = properties.getProperty("port", "/dev/ttyUSB0");
        return port.trim();
    }
    
    /**
     * Get the baud rate of the serial port connected to the local XBee device.
     * @return Baud rate or 9600 if property not set.
     */
    public Integer getXBeeBaud() {
        String baud = properties.getProperty("baud", "9600");
        return Integer.parseInt(baud.trim());
    }
    
    /**
     * Get the root topic which will be prepended to all XBMQ topics.
     * @return Root topic name or blank if property not set.
     */
    public String getRootTopic() {
        String topic = properties.getProperty("rootTopic", "");
        return topic.trim();
    }
    
    /**
     * Get the URL of the MQTT broker.
     * @return Broker url or "tcp://test.mosquitto.org:1883" if property not set.
     */
    public String getBroker() {
        String broker = properties.getProperty("broker", "tcp://test.mosquitto.org:1883");
        return broker.trim();
    }
}
