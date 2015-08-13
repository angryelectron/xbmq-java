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
    
    public static String bytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (Byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    private static Pattern pattern = Pattern.compile(".*\\/([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})\\/.+");
    
    public static XBee64BitAddress getAddressFromTopic(String topic) {        
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(2));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
    
    public static XBee64BitAddress getGatewayFromTopic(String topic) {
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(1));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
}
