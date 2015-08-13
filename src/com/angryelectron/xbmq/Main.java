/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.angryelectron.xbmq.listener.XbmqDataReceiveListener;
import com.angryelectron.xbmq.listener.XbmqMqttCallback;
import com.angryelectron.xmbq.message.MqttAtMessage;
import com.angryelectron.xmbq.message.MqttDataMessage;
import com.angryelectron.xmbq.message.MqttDiscoveryMessage;
import com.angryelectron.xmbq.message.MqttIOMessage;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Main entry point for the application 
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws com.digi.xbee.api.exceptions.XBeeException
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public static void main(String[] args) throws XBeeException, MqttException {

        /**
         * Connect to XBee and MQTT broker.         
         */
        Xbmq xbmq = Xbmq.getInstance();
        xbmq.connect();

        /**
         * Setup listeners for unsolicited packets from the XBee network.
         */
        XBeeDevice xbee = xbmq.getXBee();
        xbee.addDataListener(new XbmqDataReceiveListener());
        xbee.addIOSampleListener(new XbmqSampleReceiveListener());                
        
        /**
         * Subscribe to topics.
         */
        String[] topics = {
            new MqttDataMessage().getSubscriptionTopic(),   
            new MqttAtMessage().getSubscriptionTopic(),
            new MqttDiscoveryMessage().getSubscriptionTopic(),
            new MqttIOMessage().getSubscriptionTopic()
        };
        int[] qos = {0, 0, 0, 0};
        
        MqttAsyncClient mqtt = xbmq.getMqttClient();
        mqtt.setCallback(new XbmqMqttCallback());
        mqtt.subscribe(topics, qos);
        
        /**
         * Wait for events.
         */

    }

}
