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
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author abythell
 */
public class XBeeDataMessage implements XBeeMessage {    

    @Override
    public boolean subscribesTo(String topic) {
        return topic.contains(MqttDataMessage.SUBTOPIC);
    }

    @Override
    public void send(String topic, MqttMessage message) throws XBeeException {        
        XBeeDevice xbee = Xbmq.getInstance().getXBee();
        XBee64BitAddress address = XbmqUtils.getAddressFromTopic(topic);
        RemoteXBeeDevice rxd = new RemoteXBeeDevice(xbee, address);
        xbee.sendData(rxd, message.getPayload());        
    }
}
