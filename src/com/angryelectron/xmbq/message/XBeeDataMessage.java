/*
 * XbmqProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
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
     * @param topic topic
     * @param message message 
     * @throws XBeeException if the data cannot be sent.
     */
    @Override
    public void send(String topic, MqttMessage message) throws XBeeException {
        XBeeDevice xbee = xbmq.getXBee();
        XBee64BitAddress address = XbmqUtils.getAddressFromTopic(topic);
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        xbee.sendData(rxd, message.getPayload());
    }
}
