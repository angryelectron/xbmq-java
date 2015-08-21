/*
 * XbmqProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Send a data packet contained in an MQTT message to an XBee device.
 * @author abythell
 */
public class XBeeDataMessage implements XBeeMessage {

    private final Xbmq xbmq;
    
    public XBeeDataMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Check if this object can handle this message.
     * @param topic The topic to check.
     * @return True if the message can be handled.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(XbmqTopic.DATASUBTOPIC);
    }

    /**
     * Send a data packet to an XBee device.
     * @param rxd
     * @param message message 
     * @throws XBeeException if the data cannot be sent.
     */
    @Override
    public void transmit(RemoteXBeeDevice rxd, MqttMessage message) throws XBeeException {        
        if (message.getPayload().length > 0) {
            xbmq.getXBee().sendData(rxd, message.getPayload());
        }
    }

    @Override
    public void publish() throws MqttException {
        /**
         * Any response will be published by XbmqDataReceiveListener.
         */
    }
}
