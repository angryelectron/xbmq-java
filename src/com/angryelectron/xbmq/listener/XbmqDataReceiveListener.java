/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq.listener;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.message.MqttDataMessage;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Listen for unsolicited data packets from the XBee network and publish
 * them as MQTT messages. 
 */
public class XbmqDataReceiveListener implements IDataReceiveListener {

    private final Xbmq xbmq;
    
    public XbmqDataReceiveListener(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Called when a data packet is received.
     * @param xbm Message containing data and sender address.
     */
    @Override
    public void dataReceived(XBeeMessage xbm) {
        MqttDataMessage message = new MqttDataMessage(xbmq);
        try {
            message.send(xbm);
        } catch (MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
        
    }       
}
