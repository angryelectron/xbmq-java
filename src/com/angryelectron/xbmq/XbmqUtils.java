/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author abythell
 */
public class XbmqUtils {
    /**
     * Convert an array of bytes to a string of zero-padded hex bytes. Hex values
     * A-F are returned as lower-case characters.
     * @param bytes Byte array to convert.
     * @return Byte array as a plain-text lower-case string (no ASCII conversion).
     */    
    public static String bytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (Byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    private static final Pattern topicPattern = Pattern.compile("(.*\\/)?([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})(\\/.)*");
    
    public static XBee64BitAddress getAddressFromTopic(String topic) {        
        Matcher matcher = topicPattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(3));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
    
    /**
     * Parse a topic and extract the 64-bit address of the gateway.
     * @param topic Topic to parse.
     * @return 64-bit gateway address.
     */
    
    public static XBee64BitAddress getGatewayFromTopic(String topic) {
        Matcher matcher = topicPattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(2));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
}
