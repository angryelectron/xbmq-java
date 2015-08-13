/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.listener;

import com.angryelectron.xmbq.message.XBeeDiscoveryMessage;
import com.angryelectron.xmbq.message.XBeeAtMessage;
import com.angryelectron.xmbq.message.XBeeDataMessage;
import com.digi.xbee.api.exceptions.XBeeException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
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

    /**
     * Handle messages from subscribed topics.
     *
     * Note from docs: "If an implementation of this method throws an exception,
     * then the client will be shutdown".
     *
     * @param topic
     * @param mm
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        try {
            if (XBeeDataMessage.isDataTopic(topic)) {
                Logger.getLogger(this.getClass()).log(Level.DEBUG, "Received dataIn message.");
                XBeeDataMessage message = new XBeeDataMessage(topic, mm);
                message.send();
            } else if (XBeeAtMessage.isAtTopic(topic)) {
                Logger.getLogger(this.getClass()).log(Level.DEBUG, "Received atIn message.");
                XBeeAtMessage message = new XBeeAtMessage(topic, mm);
                message.send();
            } else if (XBeeDiscoveryMessage.isDiscoveryTopic(topic)) {
                Logger.getLogger(this.getClass()).log(Level.DEBUG, "Received discoveryRequest message.");
                XBeeDiscoveryMessage message = new XBeeDiscoveryMessage(topic, mm);
                message.send();
            }
        } catch (XBeeException | MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
