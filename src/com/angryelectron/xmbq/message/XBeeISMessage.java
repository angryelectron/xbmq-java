/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqUtils;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Fetch the status of all enabled IO lines from an XBee device.  
 */
public class XBeeISMessage implements XBeeMessage {
        
    /**
     * Request an IOSample from an XBee Device.  Results are published
     * to the 'io' topic.
     * @param topic topic
     * @param mm ignored.  Can be blank/empty.
     * @throws Exception if sample cannot be obtained or published.
     */
    @Override
    public void send(String topic, MqttMessage mm) throws Exception {
        XBeeDevice xbee = Xbmq.getInstance().getXBee();
        XBee64BitAddress address = XbmqUtils.getAddressFromTopic(topic);
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        IOSample sample = rxd.readIOSample();
        
        /**
         * Use the unsolicited IO listener to process the result.        
         */
        XbmqSampleReceiveListener listener = new XbmqSampleReceiveListener();
        listener.ioSampleReceived(rxd, sample);
    }        

    /**
     * Check if this class can handle an MQTT message.
     * @param topic The topic to check.
     * @return true if this class can handle the message.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttIOMessage.SUBTOPIC);
    }    
        
}
