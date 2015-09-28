/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.io.IOLine;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author abythell
 */
public class XbmqTopicTest {

    private final String gw = "1234567812345678";
    private final String device = "ABCDABCDABCDABCD";
    private final String rootTopic = "rootTopic";
    private XbmqTopic topic;

    public XbmqTopicTest() {

    }

    @Before
    public void setUp() {
        topic = new XbmqTopic(rootTopic, gw);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidGateway() {        
        XbmqTopic badTopic = new XbmqTopic(rootTopic, "invalid-gateway");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidTopic() {
        XbmqTopic badTopic = new XbmqTopic("illegal/#/+/topic", gw);
    }

    @Test
    public void testGetPubAT() {
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/atOut";
        String actualTopic = topic.atResponse(device);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetPubATNullDevice() {
        String expectedTopic = "rootTopic/1234567812345678/+/atOut";
        String actualTopic = topic.atResponse(null);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetPubData() {
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/dataOut";
        String actualTopic = topic.dataResponse(device);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetPubDataNullDevice() {
        String expectedTopic = "rootTopic/1234567812345678/+/dataOut";
        String actualTopic = topic.dataResponse(null);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetPubIO() {
        IOLine line = IOLine.DIO0_AD0;
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/io/DIO0_AD0";
        String actualTopic = topic.ioResponse(device, line.getName());
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetPubIONullDevice() {
        IOLine line = IOLine.DIO0_AD0;
        String expectedTopic = "rootTopic/1234567812345678/+/io/DIO0_AD0";
        String actualTopic = topic.ioResponse(null, line.getName());
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetPubDiscovery() {
        String expectedTopic = "rootTopic/1234567812345678/discoveryResponse";
        String actualTopic = topic.discoveryResponse();
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetPubOnline() {
        String expectedTopic = "rootTopic/1234567812345678/online";
        String actualTopic = topic.online(false);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubAT() {
        String expectedTopic = "rootTopic/1234567812345678/+/atIn";
        String actualTopic = topic.atRequest();
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubData() {
        String expectedTopic = "rootTopic/1234567812345678/+/dataIn";
        String actualTopic = topic.dataRequest();
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubIO() {
        IOLine line = IOLine.DIO0_AD0;
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/io/DIO0_AD0";
        String actualTopic = topic.ioResponse(device, line.getName());
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetSubIONullLine() {        
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/io/+";
        String actualTopic = topic.ioResponse(device, null);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    
    @Test
    public void testGetSubIOUpdate() {        
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/ioUpdate";
        String actualTopic = topic.ioUpdateRequest(device);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test
    public void testGetSubIOUpdateBroadcast() {
        IOLine line = IOLine.DIO0_AD0;
        String expectedTopic = "rootTopic/1234567812345678/+/ioUpdate";
        String actualTopic = topic.ioUpdateRequest(null);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetSubIOWithBadLine() {
        String line = "DIO1234/PWM3";
        String expectedTopic = "rootTopic/1234567812345678/ABCDABCDABCDABCD/io/DIO0_AD0";
        String actualTopic = topic.ioResponse(device, line);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubIONullDevice() {
        IOLine line = IOLine.DIO0_AD0;
        String expectedTopic = "rootTopic/1234567812345678/+/io/DIO0_AD0";
        String actualTopic = topic.ioResponse(null, line.getName());
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubDiscovery() {
        String expectedTopic = "rootTopic/1234567812345678/discoveryRequest";
        String actualTopic = topic.discoveryRequest();
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubOnline() {
        String expectedTopic = "rootTopic/1234567812345678/online";
        String actualTopic = topic.online(false);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test
    public void testGetSubOnlineBroadcast() {
        String expectedTopic = "rootTopic/+/online";
        String actualTopic = topic.online(true);
        assertEquals("topic mismatch", expectedTopic, actualTopic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseAddressFromGwTopic() {
        String subAtTopic = "rootTopic/1234567812345678/subTopic";
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseAddressFromGwTopicNoRoot() {
        String subAtTopic = "ABCDABCDABCDABCD/subTopic";
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
    }

    @Test
    public void testParseAddressFromDevTopic() {
        String subAtTopic = "rootTopic/1234567812345678/" + device;
        String expectedAddress = device;
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
        assertEquals("invalid address", expectedAddress, actualAddress);
    }

    @Test
    public void testParseAddressFromDevTopicNoRoot() {
        String subAtTopic = "1234567812345678/" + device;
        String expectedAddress = device;
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
        assertEquals("invalid address", expectedAddress, actualAddress);
    }

    @Test
    public void testParseAddressFromIOTopic() {
        String subAtTopic = "rootTopic/1234567812345678/" + device + "/io/DIO0_AD0";
        String expectedAddress = device;
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
        assertEquals("invalid address", expectedAddress, actualAddress);
    }
    
    @Test
    public void testParseAddressFromIOTopicNoRoot() {
        String subAtTopic = "1234567812345678/" + device + "/io/DIO0_AD0";
        String expectedAddress = device;
        String actualAddress = XbmqTopic.parseAddress(subAtTopic);
        assertEquals("invalid address", expectedAddress, actualAddress);
    }
    
    @Test
    public void testLogTopic() {
        String expected = "rootTopic/1234567812345678/log";
        String actual = topic.log();
        assertEquals("topic mismatch", expected, actual);
    }
    
        
}
