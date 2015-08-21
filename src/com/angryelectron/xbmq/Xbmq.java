/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Access to XBee device and MQTT broker.
 */
public class Xbmq {
    
    private final XBeeDevice xbee;
    private final MqttAsyncClient mqtt;
    private String rootTopic = "";  
    private final XbmqTopic topics;
    
    public Xbmq(XBeeDevice xbee, MqttAsyncClient mqtt, String rootTopic) {
        if ((mqtt == null) || (xbee == null)) {
            throw new IllegalArgumentException("XBee and/or Mqtt cannot be null.");
        }
        if (!xbee.isOpen()) {
            throw new IllegalArgumentException("XBee is not open.");
        }
        if ((mqtt.getClientId() == null) || (mqtt.getClientId().isEmpty())) {
            throw new IllegalArgumentException("Mqtt requires clientID.");
        }
        this.xbee = xbee;        
        this.mqtt = mqtt;
        this.rootTopic = rootTopic;
        this.topics = new XbmqTopic(rootTopic, mqtt.getClientId());
    }
                    
    public XbmqTopic getTopics() {
        return topics;
    }
    
    /**
     * Connect to devices and brokers.               
     * @throws MqttException if connection to MQTT broker fails
     */   
    public void connectMqtt() throws MqttException {
        if (mqtt.isConnected()) {
            throw new MqttException(MqttException.REASON_CODE_CLIENT_CONNECTED);
        }
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setWill(topics.pubOnline(), "0".getBytes(), 0, true);         
        options.setCleanSession(true);                
        mqtt.connect(options).waitForCompletion();        
        
        /**
         * Set online status.
         */        
        this.publishMqtt(topics.pubOnline(), new MqttMessage("1".getBytes()));
    }
                        
    public XBee64BitAddress getGatewayAddress() {        
        return new XBee64BitAddress(mqtt.getClientId());        
    }
        
    public XBeeDevice getXBee() {        
        return xbee;
    }
    
    public MqttAsyncClient getMqtt() {              
        return mqtt;
    }
            
    /**
     * Close XBee device and MQTT client.  Blocks until MQTT connection is
     * closed to ensure last will and testament is published.
     * @throws MqttException 
     */
    
    public void disconnect() throws MqttException {
        xbee.close();
        mqtt.disconnect().waitForCompletion();        
    }
                        
    public void publishMqtt(String topic, MqttMessage message) throws MqttException {                
        mqtt.publish(topic, message, null, new IMqttActionListener(){
            
            @Override
            public void onSuccess(IMqttToken imt) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onFailure(IMqttToken imt, Throwable thrwbl) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
            }
        
        });
    }
    
    public XbmqSampleReceiveListener sampleListenerFactory() {
        return new XbmqSampleReceiveListener(this);
    }
                
}
