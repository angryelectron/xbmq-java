/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author abythell
 */
public class XbmqConfig {
    private static final String propertiesFile = "xbmq.properties";
    private final Properties properties = new Properties();
        
    public XbmqConfig() {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, 
                    "xbmq.properties not found - using defaults.");
        }
    }
    
    public String getXBeePort() {
        String port = properties.getProperty("port", "/dev/ttyUSB0");
        return port.trim();
    }
    
    public Integer getXBeeBaud() {
        String baud = properties.getProperty("baud", "9600");
        return Integer.parseInt(baud.trim());
    }
    
    public String getRootTopic() {
        String topic = properties.getProperty("rootTopic", "testRootTopic");
        return topic.trim();
    }
    
    public String getBroker() {
        String broker = properties.getProperty("broker", "tcp://test.mosquitto.org:1883");
        return broker.trim();
    }
}
