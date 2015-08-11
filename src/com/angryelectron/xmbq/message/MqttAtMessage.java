/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import static com.angryelectron.xmbq.message.MqttBaseMessage.SEPARATOR;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class MqttAtMessage extends MqttBaseMessage {
    
    public static String PUBTOPIC = "atOut";
    public static String SUBTOPIC = "atIn";
    
    public MqttAtMessage(XBee64BitAddress address, String command, String value) {
        this.address = address;
        this.message = new MqttMessage((command + "=" + value).getBytes());        
    }
    
    @Override
    String getPublishTopic() {
        StringBuilder builder = new StringBuilder(super.getPublishTopic());        
        builder.append(SEPARATOR);
        builder.append(PUBTOPIC);        
        return builder.toString();
    }
    
    public static String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder(MqttBaseMessage.getSubscriptionTopic());
        builder.append(SEPARATOR);
        builder.append("+");
        builder.append(SEPARATOR);
        builder.append(SUBTOPIC);        
        return builder.toString();
    }
    
}
