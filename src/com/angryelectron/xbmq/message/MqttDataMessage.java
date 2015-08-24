/*
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
 * Build and publish an Mqtt message from an XBee data packet.
 */
public class MqttDataMessage {

    private final Xbmq xbmq;

    /**
     * Constructor.
     * @param xbmq 
     */
    public MqttDataMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }

    /**
     * Publish XBee data to MQTT topic.
     *
     * @param message The data to be published.
     * @throws MqttException if the message cannot be published.
     */
    public void send(XBeeMessage message) throws MqttException {
        MqttMessage m = new MqttMessage(message.getData());
        XBee64BitAddress address = message.getDevice().get64BitAddress();
        String topic = xbmq.getTopics().dataResponse(address.toString());
        xbmq.publishMqtt(topic, m);
    }

}
