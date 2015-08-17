/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Publish AT command responses as MQTT messages. 
 */
public class MqttAtMessage implements MqttBaseMessage {
    
    public static String PUBTOPIC = "atOut";
    public static String SUBTOPIC = "atIn";  
    private final Xbmq xbmq;
        
    public MqttAtMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
            
    /**
     * Format and publish the AT response as an MQTT message.
     * @param address The address of the device that sent the response.
     * @param command The command that initiated the response.
     * @param value The value of the response.
     * @throws MqttException if the message cannot be published.
     */
    public void send(XBee64BitAddress address, String command, String value) throws MqttException {
        MqttMessage message = new MqttMessage((command + "=" + value).getBytes());        
        xbmq.publishMqtt(getPublishTopic(address), message);
    }

    /**
     * Get the topic used to publish AT responses for the specified device.
     * @param address The address of the device that sent the response.
     * @return MQTT topic: rootTopic/gateway-address/device-address/atOut.
     */
    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(xbmq.getDeviceTopic(address));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }

    /**
     * Get the topic used to subscribe to incoming AT requests.
     * @return MQTT topic:  rootTopic/gateway-address/+/atIn.
     */
    @Override
    public String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder(xbmq.getGatewayTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(MqttTopic.SINGLE_LEVEL_WILDCARD);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(SUBTOPIC);        
        return builder.toString();
    }
    
}
