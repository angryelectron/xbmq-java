/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.message.XBeeDataMessage;
import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XBeeDataMessageTest {
    
    private Xbmq xbmq;
    private XBeeDevice xbee;
    private RemoteXBeeDevice rxb;        
    private XBeeDataMessage message;
    
    public XBeeDataMessageTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        xbmq = mock(Xbmq.class);
        xbee = mock(XBeeDevice.class);
        rxb = mock(RemoteXBeeDevice.class);
        when(xbmq.getXBee()).thenReturn(xbee);        
        message = new XBeeDataMessage(xbmq);
    }

    @Test
    public void testSend() throws Exception {
        byte[] testData = "testData".getBytes();
        MqttMessage msg = new MqttMessage(testData);        
        message.transmit(rxb, msg);                
        verify(xbee).sendData(rxb, testData);                                
    }
    
    @Test
    public void testDontSendEmptyMessage() throws Exception {
        byte[] testData = "".getBytes();
        MqttMessage msg = new MqttMessage(testData);        
        message.transmit(rxb, msg);                
        verify(xbee, never()).sendData(rxb, testData);                                
    }
    
    @Test
    public void testSubscribesTo() {
        assertTrue(message.subscribesTo("root/ABCDABCDABCDABCD/1234567812345678/dataIn"));
        assertFalse(message.subscribesTo("root/ABCDABCDABCDABCD/1234567812345678/badTopic"));
    }

    
    
}
