/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XbmqTest {
    
    private static final XBee64BitAddress testGatewayAddress = 
            XBee64BitAddress.BROADCAST_ADDRESS;
    XBeeDevice xbee;
    
    /**
     * Setup Mock XBee device.
     */
    @Before
    public void setUp() {
        xbee = mock(XBeeDevice.class);
        when(xbee.get64BitAddress()).thenReturn(testGatewayAddress);
    }
       
    @Test
    public void testConnectWithOptions() throws Exception {
        //TODO: subscribe and check status of 'online' topic.
        String testBroker = "tcp://test.mosquitto.org:1883";
        String testTopic = "testTopic";                
        Xbmq xbmq = new Xbmq(xbee);
        xbmq.connect(testBroker, testTopic);
        assertTrue("xmbq connect (options) failed", xbmq.getMqttClient().isConnected());
        xbmq.disconnect();
        assertFalse("xmbq disconnect (options) failed", xbmq.getMqttClient().isConnected());
    }
    
    @Test
    public void testConnectWithDefaults() throws Exception {        
        Xbmq xbmq = new Xbmq(xbee);
        xbmq.connect();
        assertTrue("xmbq connect (default) failed", xbmq.getMqttClient().isConnected());        
        
        MqttSyncSubscribe sub = new MqttSyncSubscribe(xbmq.getMqttClient());
        byte[] actualPayload = sub.getPayload(xbmq.getLwtTopic(), 10);
        byte[] expectedPayload = new byte[]{'1'};
        
        try {
            assertTrue("will-and-testament not published", Arrays.equals(expectedPayload, actualPayload));
        } finally {        
            xbmq.disconnect();
            assertFalse("xmbq disconnect (default) failed", xbmq.getMqttClient().isConnected());
        }
    }            
    
    @Test
    public void testBytesToString() {
        byte[] testBytes = {0x00, 0x01, 0x7F};
        String expectedResult = "00017f";
        String actualResult = XbmqUtils.bytesToString(testBytes);
        assertEquals("bytesToString failed", expectedResult, actualResult);
    }
    
    @Test
    public void testGetAddressFromTopic() {
        Xbmq xbmq = new Xbmq(xbee);
        
        String topic = "rootTopic/0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = xbmq.getAddressFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("1111111111111111");
        assertEquals("address mismatch", expectedAddress, actualAddress);        
        
        topic = "rootTopic/0000000000000000/badaddress/subtopic";
        actualAddress = xbmq.getAddressFromTopic(topic);
        expectedAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
        assertEquals("invalid address not detetcted", expectedAddress, actualAddress);
    }
    
    @Test
    public void testGetGatewayAddressFromTopic() {
        Xbmq xbmq = new Xbmq(xbee);
        
        String topic = "rootTopic/0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = xbmq.getGatewayFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("0000000000000000");
        assertEquals("address mismatch", expectedAddress, actualAddress);        
        
        topic = "rootTopic/badaddress/1111111111111111/subtopic";
        actualAddress = xbmq.getAddressFromTopic(topic);
        expectedAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
        assertEquals("invalid address not detetcted", expectedAddress, actualAddress);
    }
    
    @Test
    public void testGetDeviceTopic() {
        Xbmq xbmq = new Xbmq(xbee);
        XBee64BitAddress deviceAddress = new XBee64BitAddress("1234567812345678");
        StringBuilder builder = new StringBuilder(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(testGatewayAddress);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(deviceAddress);
        String expectedDeviceTopic = builder.toString();
        String actualDeviceTopic = xbmq.getDeviceTopic(deviceAddress);
        assertEquals("topic mismatch", expectedDeviceTopic, actualDeviceTopic);        
    }
    
    @Test
    public void testGetGatewayTopic() {
        Xbmq xbmq = new Xbmq(xbee);        
        StringBuilder builder = new StringBuilder(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(testGatewayAddress);        
        String expectedDeviceTopic = builder.toString();
        String actualDeviceTopic = xbmq.getGatewayTopic();
        assertEquals("topic mismatch", expectedDeviceTopic, actualDeviceTopic);        
    }
    
    
}
