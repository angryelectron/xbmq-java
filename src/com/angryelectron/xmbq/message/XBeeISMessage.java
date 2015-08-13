/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqUtils;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XBeeISMessage {

    private final MqttMessage message;
    private final String topic;
    private final Xbmq xbmq;
    private final XBeeDevice xbee;    

    public XBeeISMessage(String topic, MqttMessage mm) {
        this.topic = topic;
        this.message = mm;
        this.xbmq = Xbmq.getInstance();
        this.xbee = xbmq.getXBee();
    }

    public void send() throws XBeeException, MqttException {
        /**
         * Extract address from topic.
         */
        Pattern pattern = Pattern.compile(xbmq.getRootTopic()
                + "\\/[0-9a-fA-F]{16}\\/([0-9a-fA-F]{16})\\/" + MqttIOMessage.SUBTOPIC);
        Matcher matcher = pattern.matcher(this.topic);
        if (!matcher.find()) {
            throw new XBeeException("Invalid topic: " + topic);
        }
        XBee64BitAddress address = new XBee64BitAddress(matcher.group(1));

        /**
         * Send command to remote XBee
         */
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        IOSample sample = rxd.readIOSample();
        
        /**
         * Use the unsolicited IO listener to process the result.        
         */
        XbmqSampleReceiveListener listener = new XbmqSampleReceiveListener();
        listener.ioSampleReceived(rxd, sample);
    }        

    public static boolean isISTopic(String topic) {
        return topic.contains(MqttIOMessage.SUBTOPIC);
    }    
    
}
