/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class XbmqTest {
    
    private static final XBee64BitAddress testGatewayAddress = 
            XBee64BitAddress.BROADCAST_ADDRESS;
    private static XBee64BitAddress testDeviceAddress = new XBee64BitAddress("ABCDABCD12345678");
    private XBeeDevice xbee;
    private MqttAsyncClient mqtt;
    
    /**
     * Setup Mock devices.
     */
    @Before
    public void setUp() {
        xbee = mock(XBeeDevice.class);
        mqtt = mock(MqttAsyncClient.class);
        when(xbee.get64BitAddress()).thenReturn(testGatewayAddress);
        when(xbee.isOpen()).thenReturn(true);
        when(mqtt.getClientId()).thenReturn(testGatewayAddress.toString());
    }
             
    @Test
    public void testConstructor() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequiresValidMqtt() throws Exception {        
        mqtt = null;
        Xbmq xbmq = new Xbmq(xbee, mqtt);        
    }            
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequiresValidXBee() throws Exception {
        xbee = null;
        Xbmq xbmq = new Xbmq(xbee, mqtt);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequiresOpenXBee() throws Exception {
        when(xbee.isOpen()).thenReturn(false);
        Xbmq xbmq = new Xbmq(xbee, mqtt);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequiresClientId() {
        when(mqtt.getClientId()).thenReturn(null);
        Xbmq xbmq = new Xbmq(xbee, mqtt);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorClientIdNotEmpty() {
        when(mqtt.getClientId()).thenReturn("");
        Xbmq xbmq = new Xbmq(xbee, mqtt);
    }
    
    @Test
    public void testRootTopicValidWhenNotSet() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        assert(xbmq.getRootTopic() != null);
    }
    
    @Test
    public void testConnect() throws MqttException {
        IMqttToken token = mock(IMqttToken.class);
        when(mqtt.connect((MqttConnectOptions) anyObject())).thenReturn(token);
        doNothing().when(token).waitForCompletion();        
        
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        xbmq.connectMqtt();
        verify(mqtt).connect((MqttConnectOptions) anyObject());        
    }
    
    @Test(expected = MqttException.class)
    public void testConnectWhenAlreadyConnected() throws MqttException {
        when(mqtt.isConnected()).thenReturn(true);
        testConnect();
    }
    
    @Test
    public void testDisconnect() throws MqttException {
        IMqttToken token = mock(IMqttToken.class);
        when(mqtt.connect((MqttConnectOptions) anyObject())).thenReturn(token);
        when(mqtt.disconnect()).thenReturn(token);
        doNothing().when(token).waitForCompletion();   
        
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        xbmq.connectMqtt();
        xbmq.disconnect();
    }
    
    @Test
    public void testDisconnectWhenNotConnected() throws MqttException {
        IMqttToken token = mock(IMqttToken.class);                
        when(mqtt.disconnect()).thenReturn(token);
        doNothing().when(token).waitForCompletion();   
        
        Xbmq xbmq = new Xbmq(xbee, mqtt);        
        when(xbee.isOpen()).thenReturn(false);
        when(mqtt.isConnected()).thenReturn(false);
        xbmq.disconnect();
    }
    
    @Test
    public void testGetLwtTopicWithNoRoot() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        String expectedTopic = testGatewayAddress + "/online";
        String actualTopic = xbmq.getLwtTopic();
        assertEquals("invalid LWT topic", expectedTopic, actualTopic);
    }        
    
    @Test
    public void testDisconnectWhenXBeeNotConnected() throws MqttException {
        IMqttToken token = mock(IMqttToken.class);
        when(mqtt.disconnect()).thenReturn(token);
        when(mqtt.connect((MqttConnectOptions) anyObject())).thenReturn(token);
        doNothing().when(token).waitForCompletion();   
        
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        xbmq.disconnect();                
    }
            
    @Test
    public void testGetLwtTopicWithRoot() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        xbmq.setRootTopic("testRootTopic");
        String expectedTopic = "testRootTopic/" + testGatewayAddress + "/online";
        String actualTopic = xbmq.getLwtTopic();
        assertEquals("invalid LWT topic", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetGatewayTopic() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        String expectedTopic = testGatewayAddress.toString();
        String actualTopic = xbmq.getGatewayTopic();
        assertEquals("invalid gateway topic", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetGatewayTopicWithRoot() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);
        xbmq.setRootTopic("rootTopic");
        String expectedTopic = "rootTopic/" + testGatewayAddress.toString();
        String actualTopic = xbmq.getGatewayTopic();
        assertEquals("invalid gateway topic", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetDeviceTopic() {
        Xbmq xbmq = new Xbmq(xbee, mqtt);        
        String expectedTopic = testGatewayAddress + "/" + testDeviceAddress;
        String actualTopic = xbmq.getDeviceTopic(testDeviceAddress);
        assertEquals("invalid device topic", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetDeviceTopicWithRoot() {
        Xbmq xbmq = new Xbmq(xbee, mqtt); 
        xbmq.setRootTopic("rootTopic");
        String expectedTopic = "rootTopic/" + testGatewayAddress + "/" + testDeviceAddress;
        String actualTopic = xbmq.getDeviceTopic(testDeviceAddress);
        assertEquals("invalid device topic", expectedTopic, actualTopic);
    }            
}
