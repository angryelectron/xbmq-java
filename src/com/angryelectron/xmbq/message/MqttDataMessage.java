/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Publish XBee data packets as MQTT messages. 
 */
public class MqttDataMessage implements MqttBaseMessage {

    static final String PUBTOPIC = "dataOut";
    static final String SUBTOPIC = "dataIn";

    /**
     * Publish XBee data to MQTT topic.
     * @param message The data to be published.
     * @throws MqttException if the message cannot be published.
     */
    public void send(XBeeMessage message) throws MqttException {
        MqttMessage m = new MqttMessage(message.getData());
        XBee64BitAddress address = message.getDevice().get64BitAddress();
        Xbmq.getInstance().publishMqtt(getPublishTopic(address), m);
    }

    /**
     * The MQTT topic used to publish XBee data responses.
     * @param address XBee address that send the data.
     * @return rootTopic/gateway-address/device-address/dataOut.
     */
    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(Xbmq.getInstance().getDeviceTopic(address));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }

    /**
     * The MQTT topic used to receive incoming data requests.
     * @return rootTopic/gateway-address/+/dataIn.
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