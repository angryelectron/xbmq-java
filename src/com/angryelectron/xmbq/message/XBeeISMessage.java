/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqUtils;
import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
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
public class XBeeISMessage implements XBeeMessage {
        
    @Override
    public void send(String topic, MqttMessage mm) throws Exception {
        XBeeDevice xbee = Xbmq.getInstance().getXBee();
        XBee64BitAddress address = XbmqUtils.getAddressFromTopic(topic);
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        IOSample sample = rxd.readIOSample();
        
        /**
         * Use the unsolicited IO listener to process the result.        
         */
        XbmqSampleReceiveListener listener = new XbmqSampleReceiveListener();
        listener.ioSampleReceived(rxd, sample);
    }        

    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttIOMessage.SUBTOPIC);
    }    
        
}
