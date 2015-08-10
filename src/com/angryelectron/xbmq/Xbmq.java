/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class Xbmq {
    
    private XBeeDevice xbee;
    private MqttClient mqtt;
    private String rootTopic;
    private String clientId;
    
    private Xbmq(){}
     
    private static class SingletonHelper{
        private static final Xbmq INSTANCE = new Xbmq();
    }
     
    public static Xbmq getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    public void connect(int baud, String port, String broker, String rootTopic) throws XBeeException, MqttException {   
        /**
         * Open XBee first so we can pass the 64-bit address as the client ID
         * to MQTT.
         */
        xbee = new XBeeDevice(port, baud);
        xbee.open();
        this.rootTopic = rootTopic;        
        mqtt = new MqttClient(broker, getClientId()); 
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
    
    public XBeeDevice getXBee() {
        return xbee;
    }
    
    public MqttClient getMqttClient() {
        return mqtt;
    }
    
    public String getRootTopic() {
        return this.rootTopic;
    }

}
