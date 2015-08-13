/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public interface XBeeMessage {
    public void send(String topic, MqttMessage mm) throws Exception;
    public boolean subscribesTo(String topic);
}
