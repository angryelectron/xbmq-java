/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * XBee message interface.  Incoming Mqtt messages are parsed and executed
 * by implementations of this class.
 */
public interface XBeeMessage {

    /**
     * Transmit an Mqtt message to the XBee network.
     * @param rxb The XBee that is to receive the message.
     * @param mm The Mqtt message to parse and send.
     * @throws XBeeException if the message cannot be processed.
     */
    public void transmit(RemoteXBeeDevice rxb, MqttMessage mm) throws XBeeException;

    /**
     * Check if this implementation can handle messages from a topic.
     * @param topic The topic to check.
     * @return True if this implementation can handle messages from this topic.
     */
    public boolean subscribesTo(String topic);

    /**
     * Publish the results of an XBee command as an Mqtt message.
     * @throws MqttException 
     */
    public void publish() throws MqttException;
}
