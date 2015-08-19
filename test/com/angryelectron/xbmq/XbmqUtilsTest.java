/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

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
}
