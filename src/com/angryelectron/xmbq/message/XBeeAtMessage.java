/*
 * XbmqDefaultProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Relay AT messages send via MQTT to an XBee network.
 */
public class XBeeAtMessage implements XBeeMessage {
    
    private final Xbmq xbmq;
       
    public XBeeAtMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    private final Set<String> unsupportedCommands = new HashSet<>(Arrays.asList(
            "ND", //node discovery - handled by other mechanisms
            "CB", //commission button - CB[N]
            "AT", //not supported by the XBee API
            "AP", //won't work if API mode is disabled            
            "IS" //use XBeeISMmessage() instead
    ));
    private final Set<String> executionCommands = new HashSet<>(Arrays.asList(
            "AC", //apply changes
            "WR", //write
            "RE", //restore defaults
            "FR", //software reset            
            "SI", //sleep immediately            
            "1S", //force sensors sample
            "CN" //exit command mode            
    ));

    /**
     * Send AT command to XBee. Use to get or set AT parameters.
     *
     * @param topic MQTT topic of the AT request
     * @param message Valid XBee AT command. Eg. "D0" to get or "D0=4" to set.
     * The following AT commands are not supported: CB, AT, AP. The following AT
     * commands are accessed using other topics: ND, IS. Note: Not all AT
     * commands have been throughly tested.
     * @throws Exception if message cannot be sent to XBee or response cannot be
     * published to MQTT.
     * @see XBeeDiscoveryMessage
     * @see XBeeISMessage
     */
    @Override
    public void send(String topic, MqttMessage message) throws Exception {
        XBeeDevice xbee = xbmq.getXBee();
        XBee64BitAddress address = XbmqUtils.getAddressFromTopic(topic);
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        String msg = new String(message.getPayload(), StandardCharsets.UTF_8);
        if (msg.isEmpty()) {
            throw new XBeeException("Message cannot be empty - ignoring.");
        }
        String[] param = msg.split("=");
        if (unsupportedCommands.contains(param[0])) {
            Logger.getLogger(this.getClass()).log(Level.WARN, "Unsupported command: " + msg);
        } else if (msg.contains("=")) {
            setParameter(rxd, param[0], param[1]);
        } else {
            getParameter(rxd, param[0]);
        }
    }

    /**
     * Check if this topic can be handled by this type of message.
     *
     * @param topic Topic to test.
     * @return true if topic can be handled.
     */
    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttAtMessage.SUBTOPIC);
    }

    /**
     * Set an AT parameter
     *
     * @param rxd RemoteXBeeDevice
     * @param parameter AT parameter
     * @param value parameter value
     * @throws XBeeException if the AT parameter cannot be set.
     */
    private void setParameter(RemoteXBeeDevice rxd, String parameter, String value) throws XBeeException {
        if (parameter.toUpperCase().equals("NI")) {
            rxd.setNodeID(value);
        } else {
            rxd.setParameter(parameter, value.getBytes());
        }
    }

    /**
     * Get an AT parameter
     *
     * @param rxd RemoteXBeeDevice
     * @param parameter AT parameter
     * @throws XBeeException If the parameter cannot be fetched from the XBee.
     * @throws MqttException If the result cannot be published to the broker.
     */
    private void getParameter(RemoteXBeeDevice rxd, String parameter) throws XBeeException, MqttException {
        if (executionCommands.contains(parameter)) {
            rxd.executeParameter(parameter);
        } else if (parameter.toUpperCase().equals("NI")) {
            rxd.readDeviceInfo();
            MqttAtMessage msg = new MqttAtMessage(xbmq);
            msg.send(rxd.get64BitAddress(),
                    parameter, rxd.getNodeID());
        } else {
            String result = XbmqUtils.bytesToString(rxd.getParameter(parameter));
            MqttAtMessage msg = new MqttAtMessage(xbmq);
            msg.send(rxd.get64BitAddress(), parameter, result);
        }
    }

}
