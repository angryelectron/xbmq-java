/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqDiscoveryListener;
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

    public void send() throws XBeeException {
        XBeeNetwork network = xbee.getNetwork();
        network.setDiscoveryTimeout(15000);
        network.addDiscoveryListener(new XbmqDiscoveryListener(format));
        network.startDiscoveryProcess();
    }

}
