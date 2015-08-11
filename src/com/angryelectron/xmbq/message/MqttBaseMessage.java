/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public abstract class MqttBaseMessage {
        
    XBee64BitAddress address;
    MqttMessage message = new MqttMessage();
    
    static Xbmq xbmq = Xbmq.getInstance();
    final static String SEPARATOR = "/";   
    
    /**
     * Publish an MQTT message.
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public void send() throws MqttException {        
        xbmq.getMqttClient().publish(getPublishTopic(), message, this, new IMqttActionListener(){

            @Override
            public void onSuccess(IMqttToken imt) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onFailure(IMqttToken imt, Throwable thrwbl) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
            }
        
        });
    }
    
    String getPublishTopic() {
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(SEPARATOR);
        builder.append(address);        
        return builder.toString();
    } 
    
    public static String getSubscriptionTopic() {        
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(SEPARATOR);
        builder.append(xbmq.getGatewayId());
        return builder.toString();
    }
        
}
