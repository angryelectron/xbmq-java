/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron;

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
    private final XbmqConfig config;
    
    
    XbmqMessage(XbmqConfig config) {
        this.config = config;
    }
    
    
    
    /**
     * Publish an MQTT message.
     */
    void publish() {
        //config.getMqttClient().publish(replyTopic, null);
    }
}
