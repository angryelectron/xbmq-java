/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class MqttIOMessage implements MqttBaseMessage {
        
    static final String PUBTOPIC = "io";    
    static final String SUBTOPIC = "ioUpdate";    
    private IOLine line;
    
    public void send(XBee64BitAddress address, IOLine line, Integer value) throws MqttException {                
        MqttMessage message = new MqttMessage(value.toString().getBytes());        
        message.setRetained(true);
        this.line = line;
        String topic = getPublishTopic(address);
        XbmqUtils.publishMqtt(topic, message);
    }
                
    /**     
     * Note: while the enum is DIO0_AD, the name is DIO0/AD.  Slashes must be
     * replaced (with underscores) since the slash is the MQTT topic separator.
     
     * @return Reply topic.
     */
    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(XbmqUtils.getDeviceTopic(address));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(line.getName().replaceAll(MqttTopic.TOPIC_LEVEL_SEPARATOR, "_"));        
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
