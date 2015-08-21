/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.message.XBeeAtMessage;
import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XBeeAtMessageTest {
    
    private Xbmq xbmq;
    private RemoteXBeeDevice rxb;        
    private XBeeAtMessage message;
    
    public XBeeAtMessageTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        xbmq = mock(Xbmq.class);
        rxb = mock(RemoteXBeeDevice.class);
        when(rxb.getParameter(anyString())).thenReturn("value".getBytes());
        message = new XBeeAtMessage(xbmq);
    }

    @Test
    public void testSendSetParameter() throws Exception {
        MqttMessage msg = new MqttMessage("D0=4".getBytes());        
        message.transmit(rxb, msg);
        verify(rxb).setParameter("D0", "4".getBytes());
    }
    
    @Test(expected = XBeeException.class)
    public void testSendSetParameterBlank() throws Exception {
        MqttMessage msg = new MqttMessage("".getBytes());        
        message.transmit(rxb, msg);        
    }
    
    @Test
    public void testSendGetParameter() throws Exception {                
        MqttMessage msg = new MqttMessage("D0".getBytes());                                
        message.transmit(rxb, msg);
        verify(rxb).getParameter("D0");        
    }
    
    @Test(expected = XBeeException.class)
    public void testSendEmptyMessage() throws Exception {
        MqttMessage msg = new MqttMessage("".getBytes());
        message.transmit(rxb, msg);        
    }
    
    @Test(expected = XBeeException.class)
    public void testSendNotAnATCommand() throws Exception {
        MqttMessage msg = new MqttMessage("XX".getBytes());
        message.transmit(rxb, msg);        
    }
    
    @Test
    public void testSendGetAllValidATCommands() throws Exception {
        for (String parameter : message.atCommands) {
            MqttMessage msg = new MqttMessage(parameter.getBytes());
            message.transmit(rxb, msg);
            if (parameter.equals("NI")) {
                verify(rxb).getNodeID();
            } else {
                verify(rxb).getParameter(parameter);
            }
        }
    }
    
    @Test
    public void testSendSetAllValidATCommands() throws Exception {
        for (String parameter : message.atCommands) {
            String cmd = parameter + "=value";
            MqttMessage msg = new MqttMessage(cmd.getBytes());            
            message.transmit(rxb, msg);
            if (parameter.equals("NI")) {
                verify(rxb).setNodeID("value");
            } else {
                verify(rxb).setParameter(parameter, "value".getBytes());
            }
        }
    }
    
    @Test
    public void testSendExecuteATCommands() throws Exception {
        for (String parameter : message.executionCommands) {
            MqttMessage msg = new MqttMessage(parameter.getBytes());            
            message.transmit(rxb, msg);
            verify(rxb).executeParameter(parameter);
        }
    }
    
    @Test(expected = XBeeException.class)
    public void testTryToSetExecuteCommand() throws Exception {
        String cmd = message.executionCommands.iterator().next() + "=value";
        MqttMessage msg = new MqttMessage(cmd.getBytes());
        message.transmit(rxb,msg);
    }
    
    @Test(expected = XBeeException.class)
    public void testNDSpecialHandling() throws Exception {
        MqttMessage msg = new MqttMessage("ND".getBytes());
        message.transmit(rxb,msg);
        verify(rxb, never()).getParameter("ND");
        verify(rxb, never()).executeParameter("ND");
    }
    
    @Test(expected = XBeeException.class)
    public void testISSpecialHandling() throws Exception {
        MqttMessage msg = new MqttMessage("IS".getBytes());
        message.transmit(rxb,msg);
        verify(rxb, never()).getParameter("IS");
        verify(rxb, never()).executeParameter("IS");
    }
    
    @Test
    public void testPublishNullResultNoException() throws MqttException {    
        message.publish();        
    }
    
    @Test
    public void testSubscribesTo() {
        assertTrue(message.subscribesTo("root/ABCDABCDABCDABCD/1234567812345678/atIn"));
        assertFalse(message.subscribesTo("root/ABCDABCDABCDABCD/1234567812345678/badTopic"));
    }

    
    
}
