/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqDataReceiveListener;
import com.angryelectron.xbmq.XbmqMessage;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws com.digi.xbee.api.exceptions.XBeeException
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public static void main(String[] args) throws XBeeException, MqttException {

        Xbmq xbmq = Xbmq.getInstance();
        xbmq.connect(9600, "/dev/ttyUSB0", "tcp://iot.eclipse.org:1883", "zttest");
        
        XBeeDevice xbee = xbmq.getXBee();
        xbee.addDataListener(new XbmqDataReceiveListener());
        xbee.addIOSampleListener(new XbmqSampleReceiveListener());
        
        MqttClient mqtt = xbmq.getMqttClient();
        mqtt.setCallback(new XbmqCallback());
        
    }
    
}
