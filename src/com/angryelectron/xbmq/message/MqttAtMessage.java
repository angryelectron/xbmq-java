/*
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Build and publish an MQTT message from an XBee AT command.
 */
public class MqttAtMessage {

    private final Xbmq xbmq;

    /**
     * Constructor.
     *
     * @param xbmq
     */
    public MqttAtMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }

    /**
     * Format and publish the AT response as an MQTT message.
     *
     * @param address The address of the device that sent the response.
     * @param command The command that initiated the response.
     * @param value The value of the response.
     * @throws MqttException if the message cannot be published.
     */
    public void send(XBee64BitAddress address, String command, String value) throws MqttException {
        MqttMessage message = new MqttMessage((command + "=" + value).getBytes());
        String topic = xbmq.getTopics().atResponse(address.toString());
        xbmq.publishMqtt(topic, message);
    }

}
