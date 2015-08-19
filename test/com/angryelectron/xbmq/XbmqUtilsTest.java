/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.models.XBee64BitAddress;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author abythell
 */
public class XbmqUtilsTest {

    public XbmqUtilsTest() {
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
        String topic = "rootTopic/0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getAddressFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("1111111111111111");
        assertEquals("address mismatch", expectedAddress, actualAddress);
    }

    @Test
    public void testGetAddresssFromTopicNoRoot() {
        String topic = "0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getAddressFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("1111111111111111");
        assertEquals("address mismatch", expectedAddress, actualAddress);
    }

    @Test
    public void testGetAddressFromTopicInvalidAddress() {
        String topic = "rootTopic/0000000000000000/badaddress/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getAddressFromTopic(topic);
        XBee64BitAddress expectedAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
        assertEquals("invalid address not detetcted", expectedAddress, actualAddress);

    }

    @Test
    public void testGetGatewayAddressFromTopic() {
        String topic = "rootTopic/0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getGatewayFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("0000000000000000");
        assertEquals("address mismatch", expectedAddress, actualAddress);
    }
    
    @Test
    public void testGetGatewayAddressFromTopicNoRoot() {
        String topic = "0000000000000000/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getGatewayFromTopic(topic);
        XBee64BitAddress expectedAddress = new XBee64BitAddress("0000000000000000");
        assertEquals("address mismatch", expectedAddress, actualAddress);
    }

    @Test
    public void testGetGatwayAddressBadAddress() {
        String topic = "rootTopic/badaddress/1111111111111111/subtopic";
        XBee64BitAddress actualAddress = XbmqUtils.getAddressFromTopic(topic);
        XBee64BitAddress expectedAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
        assertEquals("invalid address not detetcted", expectedAddress, actualAddress);
    }

}
