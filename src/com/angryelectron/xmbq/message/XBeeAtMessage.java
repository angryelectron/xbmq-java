/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XBeeAtMessage implements XBeeMessage {
    
    private final Set<String> unsupportedCommands = new HashSet<>(Arrays.asList(
            "ND", //node discovery - handled by other mechanisms
            "CB", //commission button - CB[N]
            "AT", //not supported by the XBee API
            "AP" //won't work if API mode is disabled            
    ));
    private final Set<String> executionCommands = new HashSet<>(Arrays.asList(
            "AC", //apply changes
            "WR", //write
            "RE", //restore defaults
            "FR", //software reset            
            "SI", //sleep immediately
            "IS", //force IO sample
            "1S", //force sensors sample
            "CN" //exit command mode            
    ));
    
    @Override
    public void send(String topic, MqttMessage message) throws Exception {        
        XBeeDevice xbee = Xbmq.getInstance().getXBee();
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

    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttAtMessage.SUBTOPIC);
    }

    private void setParameter(RemoteXBeeDevice rxd, String parameter, String value) throws XBeeException {
        if (parameter.toUpperCase().equals("NI")) {
            rxd.setNodeID(value);
        } else {
            rxd.setParameter(parameter, value.getBytes());
        }
    }

    private void getParameter(RemoteXBeeDevice rxd, String parameter) throws XBeeException, MqttException {        
        if (executionCommands.contains(parameter)) {
            rxd.executeParameter(parameter);
        } else if (parameter.toUpperCase().equals("NI")) {  
            rxd.readDeviceInfo();
            MqttAtMessage msg = new MqttAtMessage(rxd.get64BitAddress(), 
                    parameter, rxd.getNodeID());
            msg.send();
        } else {
            String result = XbmqUtils.bytesToString(rxd.getParameter(parameter));
            MqttAtMessage msg = new MqttAtMessage(rxd.get64BitAddress(), parameter, result);
            msg.send();
        }
    }
    
}
