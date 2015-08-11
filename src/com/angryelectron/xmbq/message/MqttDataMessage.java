/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.models.XBeeMessage;

/**
 *
 * @author abythell
 */
public class MqttDataMessage extends MqttBaseMessage {
    
    private static final String PUBTOPIC = "dataOut";        
    private static final String SUBTOPIC = "dataIn";
    
    public MqttDataMessage(XBeeMessage message) {        
        this.message.setPayload(message.getData());
        this.address = message.getDevice().get64BitAddress();        
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