/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.message.XBeeISMessage;
import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOSample;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XBeeISMessageTest {
    
    private Xbmq xbmq;    
    private RemoteXBeeDevice rxb;        
    private XBeeISMessage message;
    private IOSample sample;
    
    public XBeeISMessageTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        xbmq = mock(Xbmq.class);        
        rxb = mock(RemoteXBeeDevice.class);
        sample = mock(IOSample.class);        
        when(rxb.readIOSample()).thenReturn(sample);        
        message = new XBeeISMessage(xbmq);
    }
       
    @Test
    public void testTransmit() throws Exception {                
            MqttMessage msg = new MqttMessage();        
            message.transmit(rxb, msg);  
            verify(rxb).readIOSample();
    }
    
    @Test
    public void testPublish() throws MqttException {
        XbmqSampleReceiveListener listener = mock(XbmqSampleReceiveListener.class);
        when(xbmq.sampleListenerFactory()).thenReturn(listener);
        message.publish();
        verify(listener).ioSampleReceived((RemoteXBeeDevice)anyObject(), (IOSample) anyObject());
    }
                            
    @Test
    public void testSubscribesTo() {
        assertTrue(message.subscribesTo("root/ABCDABCDABCDABCD/ioUpdate"));
        assertFalse(message.subscribesTo("root/ABCDABCDABCDABCD/badTopic"));
    }
       
}
