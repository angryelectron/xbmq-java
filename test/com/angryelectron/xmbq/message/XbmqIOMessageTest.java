/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

/**
 *
 * @author abythell
 */
public class XbmqIOMessageTest {
            
    public XbmqIOMessageTest() throws XBeeException, MqttException {
        Xbmq.getInstance().connect();
    }

    @Test
    public void testPublishIOMessage() throws MqttException {                        
        MqttIOMessage message = new MqttIOMessage(XBee64BitAddress.BROADCAST_ADDRESS, IOLine.DIO0_AD0, 0);            
        message.send();        
    }
        
}
