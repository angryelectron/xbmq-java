/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.XbmqTopic.Topic;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Fetch the status of all enabled IO lines from an XBee device in response
 * to an incoming Mqtt message.
 */
public class XBeeISMessage implements XBeeMessage {
        
    private final Xbmq xbmq;
    private IOSample sample;
    private RemoteXBeeDevice rxb;
    
    /**
     * Constructor.
     * @param xbmq 
     */
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
        this.rxb = rxb;
        this.sample = rxb.readIOSample();                
    }        

    /**
     * Check if this class can handle an MQTT message.
     * @param topic The topic to check.
     * @return true if this class can handle the message.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return XbmqTopic.matches(Topic.IOSUBTOPIC, topic);
    }    

    /**
     * Publish IO sample response as Mqtt message(s).
     * @throws MqttException if the message(s) cannot be published.
     */
    @Override
    public void publish() throws MqttException {
        /**
         * Use the unsolicited IO listener to process the result.        
         */
        XbmqSampleReceiveListener listener = xbmq.sampleListenerFactory();
        listener.ioSampleReceived(rxb, sample);
    }
        
}
