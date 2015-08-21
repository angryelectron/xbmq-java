/*
 * XbmqProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Fetch the status of all enabled IO lines from an XBee device.  
 */
public class XBeeISMessage implements XBeeMessage {
        
    private final Xbmq xbmq;
    
    public XBeeISMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Request an IOSample from an XBee Device.  Results are published
     * to the 'io' topic.
     * @param rxb
     * @param mm ignored.  Can be blank/empty.
     * @throws com.digi.xbee.api.exceptions.XBeeException
     */
    @Override
    public void transmit(RemoteXBeeDevice rxb, MqttMessage mm) throws XBeeException {        
        IOSample sample = rxb.readIOSample();
        
        /**
         * Use the unsolicited IO listener to process the result.        
         */
        XbmqSampleReceiveListener listener = new XbmqSampleReceiveListener(xbmq);
        listener.ioSampleReceived(rxb, sample);
    }        

    /**
     * Check if this class can handle an MQTT message.
     * @param topic The topic to check.
     * @return true if this class can handle the message.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(XbmqTopic.IOSUBTOPIC);
    }    

    @Override
    public void publish() throws MqttException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
