/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class XbmqConfig {
    private XBeeDevice xbee;
    private MqttClient mqtt;
    private String rootTopic;
    private String clientId;
      
    void openXBee(String port, int baud) throws XBeeException {
        xbee = new XBeeDevice(port, baud);
        xbee.open();
    }
    
    void openMqtt(String broker, String rootTopic) throws MqttException {
        this.rootTopic = rootTopic;        
        getClientId();
        mqtt = new MqttClient(broker, clientId); 
        mqtt.connect();
    }
            
    /**
     * Get Mqtt Client ID.
     * @return The MAC address of the XBee radio attached to the gateway.
     */
    String getClientId() {
        if ((clientId == null) || clientId.isEmpty()) {
            clientId = xbee.get64BitAddress().toString();
        }
        return clientId;
    }
    
    XBeeDevice getXBee() {
        return xbee;
    }
    
    MqttClient getMqttClient() {
        return mqtt;
    }
    
    XbmqMessage newMessage() {
        return new XbmqMessage(this);
    }
}
