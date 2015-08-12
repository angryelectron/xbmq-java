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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

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
     
    private static final String STATUSTOPIC = "online";
    
    private static class SingletonHelper{
        private static final Xbmq INSTANCE = new Xbmq();
    }
     
    public static Xbmq getInstance(){
        return SingletonHelper.INSTANCE;
    }
            
    public void connect(int baud, String port, String broker, String rootTopic) throws XBeeException, MqttException {   
                        
        this.rootTopic = rootTopic;
        
        /**
         * Open XBee first so we can pass the 64-bit address as the client ID
         * to MQTT.  
         */
        xbee = new XBeeDevice(port, baud);        
        xbee.open();    
        String gateway = getGatewayId();
        
        /**
         * Setup last will and testament.
         */
        StringBuilder topicBuilder = new StringBuilder(rootTopic);
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append(gateway);
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append(STATUSTOPIC);
        String lwtTopic = topicBuilder.toString();

        /**
         * Connect.
         */
        MqttConnectOptions options = new MqttConnectOptions();
        options.setWill(lwtTopic, "0".getBytes(), 0, true);        
        mqtt = new MqttAsyncClient(broker, getGatewayId()); 
        mqtt.connect(options).waitForCompletion();
        
        /**
         * Set online status
         */
        mqtt.publish(lwtTopic, "1".getBytes(), 0, true);
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
