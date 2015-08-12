/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqDataReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

/**
 *
 * @author abythell
 */
public class XbmqDataReceiveListenerTest {
    
    Xbmq xbmq = Xbmq.getInstance();
    
    public XbmqDataReceiveListenerTest() throws XBeeException, MqttException {
        xbmq.connect();
    }

    @Test
    public void testDataReceived() {        
        RemoteXBeeDevice rxbd = new RemoteXBeeDevice(xbmq.getXBee(), XBee64BitAddress.UNKNOWN_ADDRESS);
        byte[] fakeData = new byte[]{0x0A, 0x0B, 0x0C};
        XBeeMessage xbm = new XBeeMessage(rxbd, fakeData);
        XbmqDataReceiveListener listener = new XbmqDataReceiveListener();
        listener.dataReceived(xbm);        
    }
    
}
