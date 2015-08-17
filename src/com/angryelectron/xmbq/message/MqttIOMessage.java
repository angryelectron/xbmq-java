/**
 * Xbmq - XBee / MQTT Gateway Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Publish XBee IO data as MQTT messages.
 */
public class MqttIOMessage implements MqttBaseMessage {

    static final String PUBTOPIC = "io";
    static final String SUBTOPIC = "ioUpdate";
    private IOLine line;

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
        this.line = line;
        String topic = getPublishTopic(address);
        Xbmq.getInstance().publishMqtt(topic, message);
    }

    /**
     * Get MQTT topic used to publish IO samples.
     *
     * @param address Device associated with the sample.
     * @return rootTopic/gateway-address/device-address/io/ioLine.
     */
    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(Xbmq.getInstance().getDeviceTopic(address));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        /**
         * Note: while the enum is DIO0_AD, the name is DIO0/AD. Slashes must be
         * replaced (with underscores) since the slash is the MQTT topic
         * separator.
         */
        builder.append(line.getName().replaceAll(MqttTopic.TOPIC_LEVEL_SEPARATOR, "_"));
        return builder.toString();
    }

    /**
     * Get the MQTT topic used to listen for IO Update requests.
     * @return rootTopic/gateway-address/+/ioUpdate.
     */
    @Override
    public String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder(Xbmq.getInstance().getGatewayTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(MqttTopic.SINGLE_LEVEL_WILDCARD);
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(SUBTOPIC);
        return builder.toString();
    }

}
