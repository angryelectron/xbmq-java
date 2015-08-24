/**
 * Xbmq - XBee / MQTT Gateway Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.XBeeDevice;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Manage XBee and MQTT connections, topics, and listeners. A single instance of
 * this class is used to create XBee and MQTT messages and allow them to access
 * the same XBee and Mqtt objects throughout the application.
 */
public class Xbmq {

    private final XBeeDevice xbee;
    private final MqttAsyncClient mqtt;
    private String rootTopic = "";
    private final XbmqTopic topics;

    /**
     * Constructor.
     *
     * @param xbee The XBeeDevice connected to the local XBee.
     * @param mqtt The MqttAsyncClient connected to the MQTT broker.
     * @param rootTopic A top-level topic prefixed to all other topics. Can be
     * null or empty.
     */
    public Xbmq(XBeeDevice xbee, MqttAsyncClient mqtt, String rootTopic) {
        if ((mqtt == null) || (xbee == null)) {
            throw new IllegalArgumentException("XBee and/or Mqtt cannot be null.");
        }
        if (!xbee.isOpen()) {
            throw new IllegalArgumentException("XBee is not open.");
        }
        if ((mqtt.getClientId() == null) || (mqtt.getClientId().isEmpty())) {
            throw new IllegalArgumentException("Mqtt requires clientID.");
        }
        this.xbee = xbee;
        this.mqtt = mqtt;
        this.rootTopic = rootTopic;
        this.topics = new XbmqTopic(rootTopic, mqtt.getClientId());
    }

    /**
     * Access Mqtt subscription and publication topics. Topics contain the
     * address of the local XBee and are thereforce specific to each instance of
     * the gateway.
     *
     * @return XbmqTopic object configured for this gateway.
     */
    public XbmqTopic getTopics() {
        return topics;
    }

    /**
     * Connect to Mqtt broker. Sets up last-will-and-testament and publishes an
     * 'online' message as part of the connection process.
     *
     * @throws MqttException if connection to MQTT broker fails
     */
    public void connectMqtt() throws MqttException {
        if (mqtt.isConnected()) {
            throw new MqttException(MqttException.REASON_CODE_CLIENT_CONNECTED);
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setWill(topics.online(false), "0".getBytes(), 0, true);
        options.setCleanSession(true);
        mqtt.connect(options).waitForCompletion();

        /**
         * Set online status.
         */
        this.publishMqtt(topics.online(false), new MqttMessage("1".getBytes()));
    }

    /**
     * Access the local XBee radio via Digi's XBEE API client.
     *
     * @return XBeeDevice.
     */
    public XBeeDevice getXBee() {
        return xbee;
    }

    /**
     * Close XBee device and MQTT client. Blocks until MQTT connection is
     * closed. Closes the XBee serial port connection.
     *
     * @throws MqttException if Mqtt disconnection fails.
     */
    public void disconnect() throws MqttException {
        xbee.close();
        this.publishMqtt(topics.online(false), new MqttMessage("0".getBytes()));
        mqtt.disconnect().waitForCompletion();
    }

    /**
     * Publish a message to the MQTT broker. Asynchronous. If the MQTT message
     * cannot be published, an error is sent to the logger.
     *
     * @param topic Message topic.
     * @param message Message body.
     * @throws MqttException if the message cannot be published.
     */
    public void publishMqtt(String topic, MqttMessage message) throws MqttException {
        mqtt.publish(topic, message, null, new IMqttActionListener() {

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
     * Get a new instance of an XBEE IOSample listener. This is mainly to
     * facilitate unit testing. The XbmqSampleReceiveListener can always be
     * invoked directly.
     *
     * @return XbmqSampleReceiveListener.
     */
    public XbmqSampleReceiveListener sampleListenerFactory() {
        return new XbmqSampleReceiveListener(this);
    }

}
