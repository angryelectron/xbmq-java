/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class MqttIOMessage extends MqttBaseMessage {
        
    static final String PUBTOPIC = "io";    
    static final String SUBTOPIC = "ioUpdate";
    private final IOLine line;    
    
    public MqttIOMessage(XBee64BitAddress address, IOLine line, Integer value) {        
        this.address = address;
        this.line = line;
        this.message.setPayload(value.toString().getBytes());        
    }
                
    /**     
     * Note: while the enum is DIO0_AD, the name is DIO0/AD.  Slashes must be
     * replaced (with underscores) since the slash is the MQTT topic separator.
     
     * @return Reply topic.
     */
    @Override
    String getPublishTopic() {
        StringBuilder builder = new StringBuilder(super.getPublishTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(line.getName().replaceAll(MqttTopic.TOPIC_LEVEL_SEPARATOR, "_"));        
        return builder.toString();
    }
    
    public static String getSubscriptionTopic() {        
        StringBuilder builder = new StringBuilder(MqttBaseMessage.getSubscriptionTopic());        
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR); 
        builder.append(MqttTopic.SINGLE_LEVEL_WILDCARD);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR); 
        builder.append(SUBTOPIC);
        return builder.toString();
    }
     
}
