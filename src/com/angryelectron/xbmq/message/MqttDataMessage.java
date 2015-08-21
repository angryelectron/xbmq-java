/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Publish XBee data packets as MQTT messages. 
 */
public class MqttDataMessage {
    
    private final Xbmq xbmq;
    
    public MqttDataMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    /**
     * Publish XBee data to MQTT topic.
     * @param message The data to be published.
     * @throws MqttException if the message cannot be published.
     */
    public void send(XBeeMessage message) throws MqttException {
        MqttMessage m = new MqttMessage(message.getData());
        XBee64BitAddress address = message.getDevice().get64BitAddress();
        String topic = xbmq.getTopics().pubData(address.toString());
        xbmq.publishMqtt(topic, m);
    }
    
}