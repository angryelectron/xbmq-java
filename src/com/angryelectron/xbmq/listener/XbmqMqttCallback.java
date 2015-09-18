/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.listener;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.message.XBeeAtMessage;
import com.angryelectron.xbmq.message.XBeeDiscoveryMessage;
import com.angryelectron.xbmq.message.XBeeDataMessage;
import com.angryelectron.xbmq.message.XBeeISMessage;
import com.angryelectron.xbmq.message.XBeeMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Listen and process messages from subscribed MQTT topics.
 */
public class XbmqMqttCallback implements MqttCallback {

    final private Xbmq xbmq;
    private final ArrayList<XBeeMessage> messageTypes;

    /**
     * Constructor. Configures a list of all known incoming message types.
     *
     * @param xbmq
     */
    public XbmqMqttCallback(Xbmq xbmq) {
        this.xbmq = xbmq;
        this.messageTypes = new ArrayList<>(
                Arrays.asList(
                        new XBeeAtMessage(xbmq),
                        new XBeeDataMessage(xbmq),
                        new XBeeDiscoveryMessage(xbmq),
                        new XBeeISMessage(xbmq)
                ));
    }

    /**
     * Called when the connection to the MQTT broker is lost.  Will try to 
     * reconnect every 5 seconds forever.
     *
     * @param thrwbl ?
     */
    @Override
    public void connectionLost(Throwable thrwbl) {
        boolean connected = false;
        Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
        while (!connected) {
            try {
                xbmq.connectMqtt();
                connected = true;
                Logger.getLogger(this.getClass()).log(Level.INFO, "Connection restored");
            } catch (MqttException ex) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
            }
        }
    }

    /**
     * Handle messages from subscribed topics.
     *
     *
     *
     * @param topic MQTT topic
     * @param mm MQTT message
     * @throws Exception if things go very wrong.
     */
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        RemoteXBeeDevice device;

        /**
         * Get device address. If the request is for the gateway, pass null as
         * the address.
         */
        try {
            XBee64BitAddress address = new XBee64BitAddress(XbmqTopic.parseAddress(topic));
            device = new RemoteXBeeDevice(xbmq.getXBee(), address);
        } catch (IllegalArgumentException ex) {
            device = null;
        }

        try {
            for (XBeeMessage m : messageTypes) {
                if (m.subscribesTo(topic)) {
                    m.transmit(device, mm);
                    m.publish();
                }
            }
        } catch (Exception ex) {
            /**
             * Use such a broad exception since, "If an implementation of this
             * method throws an exception, then the client will be shutdown"
             * (from Paho javadocs).
             */
            //TODO: need to report errors to MQTT client
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
    }

    /**
     * Called when an MQTT message has completed delivery.
     *
     * @param imdt Token.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        /**
         * OK to ignore this since we are using Qos=0.
         */
    }

}
