/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.angryelectron.xmbq.message.XBeeDiscoveryMessage;
import com.angryelectron.xmbq.message.XBeeAtMessage;
import com.angryelectron.xmbq.message.XBeeDataMessage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XbmqMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable thrwbl) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        if (XBeeDataMessage.isDataTopic(topic)) {
            XBeeDataMessage message = new XBeeDataMessage(topic, mm);
            message.send();
        } else if (XBeeAtMessage.isAtTopic(topic)) {
            XBeeAtMessage message = new XBeeAtMessage(topic, mm);
            message.send();
        } else if (XBeeDiscoveryMessage.isDiscoveryTopic(topic)) {
            XBeeDiscoveryMessage message = new XBeeDiscoveryMessage(topic, mm);
            message.send();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
