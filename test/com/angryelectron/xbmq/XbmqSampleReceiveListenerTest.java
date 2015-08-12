/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

/**
 *
 * @author abythell
 */
public class XbmqSampleReceiveListenerTest {
    
    Xbmq xbmq = Xbmq.getInstance();
    
    public XbmqSampleReceiveListenerTest() throws XBeeException, MqttException {
        xbmq.connect();
    }

    @Test
    public void testIoSampleReceived() {                
        RemoteXBeeDevice rxbd = new RemoteXBeeDevice(xbmq.getXBee(), XBee64BitAddress.UNKNOWN_ADDRESS);
        byte[] fakeIoSample = "7E001492007D33A20040E664D0FFFE020100010400010093C5".getBytes();
        IOSample ios = new IOSample(fakeIoSample);
        XbmqSampleReceiveListener listener = new XbmqSampleReceiveListener();
        listener.ioSampleReceived(rxbd, ios);        
    }
    
}
