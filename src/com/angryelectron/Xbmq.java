/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron;

import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class Xbmq {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws XBeeException, MqttException {

        //TODO: load these settings from command line or properties file
        int baud = 9600;
        String port = "/dev/ttyUSB0";        
        String broker = "tcp://iot.eclipse.org:1883";
        String rootTopic = "ziptrek";
        
        XbmqConfig config = new XbmqConfig();
        config.openXBee(port, baud);
        config.openMqtt(broker, rootTopic);
        
        /*
        XmbqSampleListener sampleListener = new XmbqSampleListener(config);
        XbmqDataListener dataListener = new XmbqDataListener(config);
        config.getXBee().addDataListener(dataListener);
        config.getXBee().addIOSampleListener(sampleListener);
        
        XmbqCallback callback = new XbmqCallback(config);
        config.getMqttClient().setCallback(callback);
        */
        
    }
    
}
