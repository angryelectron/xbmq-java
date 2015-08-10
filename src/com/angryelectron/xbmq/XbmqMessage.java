/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XbmqMessage {
    
    private String requestTopic;
    private String requestMessage;
    private String requestId;
    private String replyTopic;
    private String replyMessage;
    private final static Xbmq xbmq = Xbmq.getInstance();

    private static final String SEPARATOR = "/";
    private static final String IOTOPIC = "io";
    
    public String getRequestTopic() {
        return requestTopic;
    }

    public static XbmqMessage createIOResponse(XBee64BitAddress address, IOLine line, Integer value) {
        XbmqMessage message = new XbmqMessage();
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(SEPARATOR);
        builder.append(xbmq.getClientId());
        builder.append(SEPARATOR);
        builder.append(address);
        builder.append(SEPARATOR);
        builder.append(IOTOPIC);
        builder.append(SEPARATOR);
        builder.append(line.getName());
        builder.append(SEPARATOR);
        
        message.replyTopic = builder.toString();
        message.replyMessage = value.toString();
        
        return message;
    }
    
    /**
     * Publish an MQTT message.
     */
    public void publish() throws MqttException {
        MqttMessage msg = new MqttMessage(replyMessage.getBytes());
        xbmq.getMqttClient().publish(replyTopic, msg);
    }
}
