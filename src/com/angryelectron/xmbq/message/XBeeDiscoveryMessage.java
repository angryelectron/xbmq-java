/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.listener.XbmqDiscoveryListener;
import com.angryelectron.xmbq.message.MqttDiscoveryMessage.Format;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XBeeDiscoveryMessage {

    private final String topic;
    private final Xbmq xbmq;
    private final XBeeDevice xbee;
    private Format format;

    public XBeeDiscoveryMessage(String topic, MqttMessage mm) {
        this.topic = topic;
        this.xbmq = Xbmq.getInstance();
        this.xbee = xbmq.getXBee();
        String fmt = new String(mm.getPayload(), StandardCharsets.UTF_8).toUpperCase();
        try {
            this.format = Format.valueOf(fmt);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, fmt + ": invalid format, using default.");
            this.format = Format.JSON;
        }
    }

    public static boolean isDiscoveryTopic(String topic) {
        return topic.contains(MqttDiscoveryMessage.SUBTOPIC);
    }

    /**
     * Discover all remote XBee devices on the same network as the local XBee
     * device.
     *
     * Note from XBee docs: "Note that DigiMesh/DigiPoint devices are blocked
     * until the discovery time configured (NT parameter) has elapsed, so if you
     * try to get/set any parameter during the discovery process you will
     * receive a timeout exception."
     *
     * To avoid timeouts during the discovery process, block until discovery is
     * complete. Any MQTT messages that arrive while discovery is running will
     * be queued by the MQTT framework.
     *
     * Even though this is blocking, and synchronous discovery is possible, the
     * discovered devices are collected and published asynchronously by
     * {@link com.angryelectron.xbmq.listener.XbmqDiscoveryListener}.
     *
     * @see <a href="https://docs.digi.com/display/XBJLIB/Discover+the+network">
     * https://docs.digi.com/display/XBJLIB/Discover+the+network</a>
     * @throws XBeeException
     */
    public void send() throws XBeeException {
        XbmqDiscoveryListener listener = new XbmqDiscoveryListener(format);
        XBeeNetwork network = xbee.getNetwork();
        if (!network.isDiscoveryRunning()) {
            network.setDiscoveryTimeout(15000);
            network.addDiscoveryListener(listener);
            network.startDiscoveryProcess();
        }
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
