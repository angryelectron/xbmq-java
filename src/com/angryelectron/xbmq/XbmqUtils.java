/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    /**
     * Publish an MQTT message.
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
            
    public static String getGatewayTopic() {        
        Xbmq xbmq = Xbmq.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(xbmq.getGatewayId());
        return builder.toString();
    }
    
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
