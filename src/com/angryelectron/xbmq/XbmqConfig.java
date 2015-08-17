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
    String propertiesFile = "xbmq.properties";
    final Properties properties = new Properties();
    
    final String PORT = "port";
    final String BAUD = "baud";
    final String TOPIC = "rootTopic";
    final String BROKER = "broker";
    
    final String PORT_DEFAULT = "/dev/ttyUSB0";
    final String BAUD_DEFAULT = "9600";
    final String TOPIC_DEFAULT = "";
    final String BROKER_DEFAULT = "tcp://test.mosquitto.org:1883";
    
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
     * Constructor.  Uses alternate properties file for testing.
     * @param propertiesFile 
     */
    XbmqConfig(String propertiesFile) {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, 
                    propertiesFile + " not found - using defaults.");
        }
        
    }
            
    /**
     * Get the name of the serial port connected to the local XBee device.
     * @return Port name or "/dev/ttyUSB0" if property not set.
     */
    public String getXBeePort() {
        String port = properties.getProperty(PORT, PORT_DEFAULT);
        return port.trim();
    }
    
    /**
     * Get the baud rate of the serial port connected to the local XBee device.
     * @return Baud rate or 9600 if property not set.
     */
    public Integer getXBeeBaud() {
        String baud = properties.getProperty(BAUD, BAUD_DEFAULT);
        return Integer.parseInt(baud.trim());
    }
    
    /**
     * Get the root topic which will be prepended to all XBMQ topics.
     * @return Root topic name or blank if property not set.
     */
    public String getRootTopic() {
        String topic = properties.getProperty(TOPIC, TOPIC_DEFAULT);
        return topic.trim();
    }
    
    /**
     * Get the URL of the MQTT broker.
     * @return Broker url or "tcp://test.mosquitto.org:1883" if property not set.
     */
    public String getBroker() {
        String broker = properties.getProperty(BROKER, BROKER_DEFAULT);
        return broker.trim();
    }
}
