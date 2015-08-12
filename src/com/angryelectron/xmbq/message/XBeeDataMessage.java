/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XBeeDataMessage {

    private final MqttMessage message;
    private final String topic;
    private final Xbmq xbmq;
    private final XBeeDevice xbee;

    public XBeeDataMessage(String topic, MqttMessage mm) {
        this.xbmq = Xbmq.getInstance();
        this.xbee = xbmq.getXBee();
        this.topic = topic;
        this.message = mm;
    }

    public static boolean isDataTopic(String topic) {
        return topic.contains(MqttDataMessage.SUBTOPIC);
    }

    public void send() throws XBeeException {
        Pattern pattern = Pattern.compile(xbmq.getRootTopic()
                + "\\/[0-9a-fA-F]{16}\\/([0-9a-fA-F]{16})\\/" + MqttDataMessage.SUBTOPIC);
        Matcher matcher = pattern.matcher(this.topic);
        if (matcher.find()) {
        XBee64BitAddress address = new XBee64BitAddress(matcher.group(1));
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
            xbee.sendData(rxd, message.getPayload());
        } else {
            throw new XBeeException("Invalid topic: " + topic);
        }

    }
}
