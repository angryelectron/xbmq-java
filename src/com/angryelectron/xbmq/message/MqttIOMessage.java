/**
 * Xbmq - XBee / MQTT Gateway Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Publish XBee IO data as MQTT messages.
 */
public class MqttIOMessage {
        
    private final Xbmq xbmq;

    public MqttIOMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Send an MQTT message for the specified IO line.
     *
     * @param address Device associated with this IO line.
     * @param line Name of the IO line.
     * @param value Value of the IO line - 0, 1, or 10-bit value.
     * @throws MqttException if message cannot be published.
     */
    public void send(XBee64BitAddress address, IOLine line, Integer value) throws MqttException {
        MqttMessage message = new MqttMessage(value.toString().getBytes());
        message.setRetained(true);        
        String topic = xbmq.getTopics().pubIO(address.toString(), line.getName());
        xbmq.publishMqtt(topic, message);
    }
}
