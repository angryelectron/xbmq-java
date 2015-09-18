/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.XbmqTopic.Topic;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Build and send an XBee AT command from an incoming Mqtt message. If the AT
 * command returns a value, publish it via {@link MqttAtMessage}.
 */
public class XBeeAtMessage implements XBeeMessage {

    private final Xbmq xbmq;
    private String parameter;   //AT command.
    private String response;    //value returned by AT command.
    private XBee64BitAddress device;

    /**
     * Constructor.
     *
     * @param xbmq
     */
    public XBeeAtMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }

    /**
     * Valid AT commands, according to
     * <a href="http://examples.digi.com/wp-content/uploads/2012/07/XBee_ZB_ZigBee_AT_Commands.pdf"
     * >XBee Command Reference Tables</a>
     */
    final Set<String> atCommands = new HashSet<>(Arrays.asList(
            "DH",
            "DL",
            "MY",
            "MP",
            "NC",
            "SH",
            "SL",
            "SE",
            "DE",
            "CI",
            "NP",
            "DD",
            "CH",
            "ID",
            "OP",
            "NH",
            "BH",
            "OI",
            "NT",
            "NO",
            "SC",
            "SD",
            "ZS",
            "NJ",
            "JV",
            "NW",
            "JN",
            "AR",
            "EE",
            "EO",
            "NK",
            "KY",
            "PL",
            "PM",
            "DB",
            "PP",
            "AO",
            "BD",
            "NB",
            "SB",
            "RO",
            "D8",
            "D7",
            "D6",
            "D5",
            "D4",
            "D3",
            "D2",
            "D1",
            "D0",
            "IR",
            "IC",
            "P0",
            "P1",
            "P2",
            "P3",
            "LT",
            "PR",
            "RP",
            "%V",
            "V+",
            "TP",
            "VR",
            "HV",
            "AI",
            "CT",
            "GT",
            "CC",
            "SM",
            "SN",
            "SP",
            "ST",
            "SO",
            "WH",
            "PO",
            "AT",
            "NR",
            "DN",
            "AP",
            "NI"
    ));

    /**
     * Valid AT commands that don't take parameters and don't return a response.
     */
    final Set<String> executionCommands = new HashSet<>(Arrays.asList(
            "AC", //apply changes
            "WR", //write
            "RE", //restore defaults
            "FR", //software reset            
            "SI", //sleep immediately            
            "1S", //force sensors sample
            "CN", //exit command mode            
            "CB1", //one commission button press
            "CB2", //two button press
            "CB3", //etc.
            "CB4" //add more as required                       
    ));

    /**
     * Valid commands that require special handling by topics other than the
     * AT-request topic.
     */
    final Set<String> reservedCommands = new HashSet<>(Arrays.asList(
            "ND",
            "IS"
    ));

    /**
     * Pattern used to quickly validate AT commands.
     */
    private final Pattern pattern = Pattern.compile("([a-zA-Z1%][a-zA-Z0-9\\+][0-9]?)(=(.+))?");

    /**
     * Send AT command to XBee. Use to get or set AT parameters.
     *
     * @param rxb A RemoteXBeeDevice containing the destination address.
     * @param message An XBee AT command (eg. AT) or an AT command and value
     * (eg. AT=value).
     * @throws com.digi.xbee.api.exceptions.XBeeException if the command cannot
     * be executed.
     * @see XBeeDiscoveryMessage for alternate method of sending "ND" commands.
     * @see XBeeISMessage for alternate method of sending "IS" commands.
     */
    @Override
    public void transmit(RemoteXBeeDevice rxb, MqttMessage message) throws XBeeException {
        String cmd = new String(message.getPayload(), StandardCharsets.UTF_8);
        Matcher matcher = pattern.matcher(cmd);
        if (!matcher.find()) {
            throw new XBeeException("Invalid AT command.");
        }
        device = rxb.get64BitAddress();
        parameter = matcher.group(1).toUpperCase();

        if (reservedCommands.contains(parameter)) {
            throw new XBeeException(parameter + " is accessed with another topic.");
        } else if (matcher.group(3) == null) {
            response = getParameter(rxb, parameter);
        } else {
            setParameter(rxb, parameter, matcher.group(3));
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
        return XbmqTopic.matches(Topic.ATSUBTOPIC, topic);
    }

    /**
     * Set an AT parameter
     *
     * @param rxd RemoteXBeeDevice
     * @param parameter AT parameter
     * @param value parameter value
     * @throws XBeeException if the AT parameter cannot be set.
     */
    private void setParameter(RemoteXBeeDevice rxd, String parameter, String value) throws XBeeException {        if (atCommands.contains(parameter)) {
            if (parameter.equals("NI")) {
                rxd.setNodeID(value);
            } else {
                if (value.matches("0[xX].+")) {
                    value = value.substring(2);
                }
                if (value.equals("0")) {
                    value = "00";
                }
                byte[] v = DatatypeConverter.parseHexBinary(value);
                rxd.setParameter(parameter, v);
            }
        } else if (executionCommands.contains(parameter)) {
            throw new XBeeException(parameter + " cannot be set.");
        } else {
            throw new XBeeException("Invalid AT command.");
        }

    }

    /**
     * Get an AT parameter
     *
     * @param rxd RemoteXBeeDevice
     * @param parameter AT parameter
     * @throws XBeeException If the parameter cannot be fetched from the XBee.
     */
    private String getParameter(RemoteXBeeDevice rxd, String parameter) throws XBeeException {
        if (atCommands.contains(parameter)) {
            if (parameter.equals("NI")) {
                rxd.readDeviceInfo();
                return rxd.getNodeID();
            } else {
                return DatatypeConverter.printHexBinary(rxd.getParameter(parameter));
            }
        } else if (executionCommands.contains(parameter)) {
            switch (parameter) {
                case "ND":
                    throw new XBeeException("ND must use 'discoveryRequest' topic.");
                case "IS":
                    throw new XBeeException("IS must use 'ioUpdate' topic.");
                default:
                    rxd.executeParameter(parameter);
                    return null;
            }
        } else {
            throw new XBeeException("Invalid AT command.");
        }
    }

    /**
     * Publish MQTT message.
     * @throws MqttException if message cannot be published. 
     */
    @Override
    public void publish() throws MqttException {
        if ((response != null) && (!response.isEmpty())) {
            MqttAtMessage msg = new MqttAtMessage(xbmq);
            msg.send(device, parameter, response);
        }
    }
        
}
