/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
class XbmqSampleReceiveListener implements IIOSampleReceiveListener {

    private static final Xbmq xbmq = Xbmq.getInstance();

    @Override
    public void ioSampleReceived(RemoteXBeeDevice rxbd, IOSample ios) {
        HashMap<IOLine, IOValue> ioValues = new HashMap<>();

        if (ios.hasDigitalValues()) {
            HashMap<IOLine, IOValue> digitalValues = ios.getDigitalValues();
            for (Map.Entry<IOLine, IOValue> entry : digitalValues.entrySet()) {
                Integer value = entry.getValue().equals(IOValue.HIGH) ? 1 : 0;
                XbmqMessage message = XbmqMessage.createIOResponse(
                        rxbd.get64BitAddress(), entry.getKey(), value);
                try {
                    message.publish();
                } catch (MqttException ex) {
                    Logger.getLogger(XbmqSampleReceiveListener.class.getName()).log(Level.ERROR, ex);
                }
            }
        }

        if (ios.hasAnalogValues()) {
            HashMap<IOLine, Integer> analogValues = ios.getAnalogValues();
            for (Map.Entry<IOLine, Integer> entry : analogValues.entrySet()) {
                XbmqMessage message = XbmqMessage.createIOResponse(
                        rxbd.get64BitAddress(), entry.getKey(), entry.getValue());
                try {
                    message.publish();
                } catch (MqttException ex) {
                    Logger.getLogger(XbmqSampleReceiveListener.class.getName()).log(Level.ERROR, ex);
                }
            }

        }

    }
}
