/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq;

import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Utilities for working with Xbmq. 
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
        
    private static Pattern pattern = Pattern.compile(".*\\/([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})(\\/.)*");
    
    /**
     * Parse a topic and extract the 64-bit address of an XBee device.
     * @param topic Topic to parse.
     * @return 64-bit address.
     */
    public static XBee64BitAddress getAddressFromTopic(String topic) {        
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(2));
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
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(1));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
    
    /**
     * Publish an MQTT message.
     * @param topic Topic to publish.
     * @param message Message to publish.
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public static void publishMqtt(String topic, MqttMessage message) throws MqttException {        
        MqttAsyncClient mqtt = Xbmq.getInstance().getMqttClient();
        mqtt.publish(topic, message, null, new IMqttActionListener(){

            @Override
            public void onSuccess(IMqttToken imt) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onFailure(IMqttToken imt, Throwable thrwbl) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
            }
        
        });
    }
            
    /**
     * Build an Xbmq topic using the gateway address.
     * @return Topic using the format: rootTopic/64-bit-gateway-address.
     */
    public static String getGatewayTopic() {        
        Xbmq xbmq = Xbmq.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(xbmq.getGatewayId());
        return builder.toString();
    }
    
    /**
     * Build and Xbmq topic using the gateway and device address.
     * @param address 64-bit address of the device to include.
     * @return Topic using the format:  
     * rootTopic/64bit-gateway-address/64bit-device-address
     */
    public static String getDeviceTopic(XBee64BitAddress address) {        
        Xbmq xbmq = Xbmq.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(address.toString());
        return builder.toString();
    }
}
