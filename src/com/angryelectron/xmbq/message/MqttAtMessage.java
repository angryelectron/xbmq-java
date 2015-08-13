/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class MqttAtMessage implements MqttBaseMessage {
    
    public static String PUBTOPIC = "atOut";
    public static String SUBTOPIC = "atIn";
            
    public void send(XBee64BitAddress address, String command, String value) throws MqttException {
        MqttMessage message = new MqttMessage((command + "=" + value).getBytes());        
        XbmqUtils.publishMqtt(getPublishTopic(address), message);
    }

    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(XbmqUtils.getDeviceTopic(address));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }

    @Override
    public String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder(XbmqUtils.getGatewayTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(MqttTopic.SINGLE_LEVEL_WILDCARD);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(SUBTOPIC);        
        return builder.toString();
    }
    
}
