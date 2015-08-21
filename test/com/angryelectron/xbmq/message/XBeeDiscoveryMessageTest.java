/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.message.XBeeDiscoveryMessage;
import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.listener.XbmqDiscoveryListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.models.XBeeProtocol;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XBeeDiscoveryMessageTest {
    
    private Xbmq xbmq;
    private XBeeDevice xbee;
    private RemoteXBeeDevice rxb;        
    private XBeeDiscoveryMessage message;
    private XBeeNetwork network;
    
    public XBeeDiscoveryMessageTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        xbmq = mock(Xbmq.class);
        xbee = mock(XBeeDevice.class);
        network = mock(XBeeNetwork.class);
        rxb = mock(RemoteXBeeDevice.class);
        
        when(xbmq.getXBee()).thenReturn(xbee);        
        when(xbee.getNetwork()).thenReturn(network);
        when(xbee.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
        message = new XBeeDiscoveryMessage(xbmq);
    }
       
    @Test
    public void testTransmit() throws Exception {        
        String[] formats = {"JSON", "json", "Json", "CSV", "XML", "", "lskdjf"};        
        for (String format : formats) {
            MqttMessage msg = new MqttMessage(format.getBytes());        
            message.transmit(rxb, msg);
        }
        verify(network, times(formats.length)).addDiscoveryListener((XbmqDiscoveryListener) anyObject());
        verify(network, times(formats.length)).startDiscoveryProcess();        
        verify(network, never()).isDiscoveryRunning();
        verify(network, never()).removeDiscoveryListener((XbmqDiscoveryListener) anyObject());
    }
    
    @Test
    public void testTransmitDigiMesh() throws Exception {        
        when(xbee.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
        String[] formats = {"JSON", "json", "Json", "CSV", "XML", "", "lskdjf"};        
        for (String format : formats) {
            MqttMessage msg = new MqttMessage(format.getBytes());        
            message.transmit(rxb, msg);
        }
        verify(network, times(formats.length)).addDiscoveryListener((XbmqDiscoveryListener) anyObject());
        verify(network, times(formats.length)).startDiscoveryProcess();        
        verify(network, times(formats.length)).isDiscoveryRunning();
        verify(network, times(formats.length)).removeDiscoveryListener((XbmqDiscoveryListener) anyObject());
    }
                    
    @Test
    public void testSubscribesTo() {
        assertTrue(message.subscribesTo("root/ABCDABCDABCDABCD/discoveryRequest"));
        assertFalse(message.subscribesTo("root/ABCDABCDABCDABCD/badTopic"));
    }
       
}
