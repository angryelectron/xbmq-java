/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq.listener;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.angryelectron.xmbq.message.XBeeAtMessage;
import com.angryelectron.xmbq.message.XBeeDiscoveryMessage;
import com.angryelectron.xmbq.message.XBeeDataMessage;
import com.angryelectron.xmbq.message.XBeeISMessage;
import com.angryelectron.xmbq.message.XBeeMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Listen and process messages from subscribed MQTT topics. 
 */
public class XbmqMqttCallback implements MqttCallback {

    final private Xbmq xbmq;
    private final ArrayList<XBeeMessage> messageTypes;
    
    public XbmqMqttCallback(Xbmq xbmq) {
        this.xbmq = xbmq;
        this.messageTypes = new ArrayList<>(
                Arrays.asList(
                        new XBeeAtMessage(xbmq),
                        new XBeeDataMessage(xbmq),
                        new XBeeDiscoveryMessage(xbmq),
                        new XBeeISMessage(xbmq)
                ));        
    }
    
    /**
     * Called when the connection to the MQTT broker is lost.
     * @param thrwbl ?
     */
    @Override
    public void connectionLost(Throwable thrwbl) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
    }

    /**
     * Handle messages from subscribed topics.
     *
     * Note from docs: "If an implementation of this method throws an exception,
     * then the client will be shutdown".
     *
     * @param topic MQTT topic
     * @param mm MQTT message
     * @throws Exception if things go very wrong.
     */
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {        
        XBee64BitAddress address = new XBee64BitAddress(XbmqTopic.parseAddress(topic));
        RemoteXBeeDevice device = new RemoteXBeeDevice(xbmq.getXBee(), address);
        try {            
            for (XBeeMessage m : messageTypes) {
                if (m.subscribesTo(topic)) {
                    m.transmit(device, mm);
                    m.publish();
                }
            }
        } catch (XBeeException | MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
    }

    /**
     * Called when an MQTT message has completed delivery.
     * @param imdt Token.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
