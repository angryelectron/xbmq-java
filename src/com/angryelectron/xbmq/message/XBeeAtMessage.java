/*
 * XbmqDefaultProvider - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Relay AT messages transmit via MQTT to an XBee network.
 */
public class XBeeAtMessage implements XBeeMessage {

    private final Xbmq xbmq;
    private String parameter;
    private String response;
    private XBee64BitAddress device;

    public XBeeAtMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }

    /**
     * All valid AT commands, according to
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
     * Valid commands that are handled by other topics.
     */
    final Set<String> reservedCommands = new HashSet<>(Arrays.asList(
            "ND",
            "IS"
    ));

    private final Pattern pattern = Pattern.compile("([a-zA-Z1%][a-zA-Z0-9\\+][0-9]?)(=(.+))?");
    
    /**
     * Send AT command to XBee. Use to get or set AT parameters.
     *
     * @param rxb
     * @param message Valid XBee AT command. Eg. "D0" to get or "D0=4" to set.
     * The following AT commands are not supported: CB, AT, AP. The following AT
     * commands are accessed using other topics: ND, IS. Note: Not all AT
     * commands have been throughly tested.
     * @throws com.digi.xbee.api.exceptions.XBeeException
     * @see XBeeDiscoveryMessage
     * @see XBeeISMessage
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
        return topic.contains(XbmqTopic.ATSUBTOPIC);
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
        if (atCommands.contains(parameter)) {            
            if (parameter.equals("NI")) {
                rxd.setNodeID(value);
            } else {
                rxd.setParameter(parameter, value.getBytes());        
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
                return XbmqUtils.bytesToString(rxd.getParameter(parameter));
            }
        }else if (executionCommands.contains(parameter)) {
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

    @Override
    public void publish() throws MqttException {
        if ((response != null) && (!response.isEmpty())) {
            MqttAtMessage msg = new MqttAtMessage(xbmq);
            msg.send(device, parameter, response);
        }
    }

}
