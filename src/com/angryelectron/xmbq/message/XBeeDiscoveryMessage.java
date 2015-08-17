/*
 * XbmqProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.listener.XbmqDiscoveryListener;
import com.angryelectron.xmbq.message.MqttDiscoveryMessage.Format;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeProtocol;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Discover the XBee network attached to the gateway.
 */
public class XBeeDiscoveryMessage implements XBeeMessage {

    private final Xbmq xbmq;
    
    public XBeeDiscoveryMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Check if an MQTT message can be handled by this class.
     *
     * @param topic The topic to check.
     * @return true if this class can handle this MQTT message.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttDiscoveryMessage.SUBTOPIC);
    }

    /**
     * Discover all remote XBee devices on the same network as the local XBee
     * device.
     *
     * @param topic topic
     * @param message The desired response format. If an unknown format is
     * specified, JSON will be used.
     * @see <a href="https://docs.digi.com/display/XBJLIB/Discover+the+network">
     * https://docs.digi.com/display/XBJLIB/Discover+the+network</a>
     * @throws XBeeException
     */
    @Override
    public void send(String topic, MqttMessage message) throws Exception {
        XBeeDevice xbee = xbmq.getXBee();

        /**
         * Determine response format.
         */
        Format format;
        String fmt = new String(message.getPayload(), StandardCharsets.UTF_8).toUpperCase();
        try {
            format = Format.valueOf(fmt);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, fmt + ": invalid format, using default.");
            format = Format.JSON;
        }
        XbmqDiscoveryListener listener = new XbmqDiscoveryListener(xbmq, format);

        /**
         * Start async discovery.
         */
        XBeeNetwork network = xbee.getNetwork();
        network.setDiscoveryTimeout(15000);
        network.addDiscoveryListener(listener);
        network.startDiscoveryProcess();

        /**
         * From XBee docs: "Note that DigiMesh/DigiPoint devices are blocked
         * until the discovery time configured (NT parameter) has elapsed, so if
         * you try to get/set any parameter during the discovery process you
         * will receive a timeout exception".
         *
         * So if this type of device is in use, block until discovery completes.
         * This will cause the MQTT framework to queue new incoming requests and
         * process them only once this method is finished, thereby avoiding
         * concurrent XBee access.
         *
         */
        XBeeProtocol protocol = xbee.getXBeeProtocol();
        if (protocol.equals(XBeeProtocol.DIGI_MESH) || protocol.equals(XBeeProtocol.DIGI_POINT)) {
            while (network.isDiscoveryRunning()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    //ignore
                }
            }
            network.removeDiscoveryListener(listener);
        }
    }
}
