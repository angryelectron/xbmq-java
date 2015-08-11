/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class Xbmq {
    
    private XBeeDevice xbee;
    private MqttAsyncClient mqtt;
    private String rootTopic;
    private String gatewayId;
    
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
        mqtt = new MqttAsyncClient(broker, getGatewayId()); 
        mqtt.connect().waitForCompletion();
    }
    
    public void connect() throws XBeeException, MqttException {
        XbmqConfig config = new XbmqConfig();
        connect(config.getXBeeBaud(), config.getXBeePort(), config.getBroker(), config.getRootTopic());
    }
    
    /**
     * Get Mqtt Client ID.
     * @return The MAC address of the XBee radio attached to the gateway.
     */
    public String getGatewayId() {
        if (xbee == null) {
            return XBee64BitAddress.UNKNOWN_ADDRESS.toString();
        }
        if ((gatewayId == null) || gatewayId.isEmpty()) {
            gatewayId = xbee.get64BitAddress().toString();
        }
        return gatewayId;
    }
    
    public XBeeDevice getXBee() {        
        return xbee;
    }
    
    public MqttAsyncClient getMqttClient() {              
        return mqtt;
    }
    
    public String getRootTopic() {
        if (rootTopic == null) {
            return "";
        } else {
            return rootTopic;
        }
    }

}
