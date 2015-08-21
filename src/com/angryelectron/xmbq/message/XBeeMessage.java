/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public interface XBeeMessage {
    public void transmit(RemoteXBeeDevice rxb, MqttMessage mm) throws XBeeException;
    public boolean subscribesTo(String topic);
    public void publish() throws MqttException;
}
