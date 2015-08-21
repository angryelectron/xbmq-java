/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

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
        
}
