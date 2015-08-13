/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.listener;

import com.angryelectron.xmbq.message.MqttDataMessage;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class XbmqDataReceiveListener implements IDataReceiveListener {

    @Override
    public void dataReceived(XBeeMessage xbm) {
        MqttDataMessage message = new MqttDataMessage();
        try {
            message.send(xbm);
        } catch (MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
        
    }       
}
