/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq.listener;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.message.MqttIOMessage;
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
 * Listen for unsolicited IO Samples from the XBeeNetwork and process them. 
 */
public class XbmqSampleReceiveListener implements IIOSampleReceiveListener {
    
    private final Xbmq xbmq;
    
    public XbmqSampleReceiveListener(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Called when a sample is received.
     * @param rxbd The remote device which sent the sample.
     * @param ios Sample data.
     */
    @Override
    public void ioSampleReceived(RemoteXBeeDevice rxbd, IOSample ios) {        
        if (ios.hasDigitalValues()) {
            HashMap<IOLine, IOValue> digitalValues = ios.getDigitalValues();
            MqttIOMessage message = new MqttIOMessage(xbmq);
            for (Map.Entry<IOLine, IOValue> entry : digitalValues.entrySet()) {
                Integer value = entry.getValue().equals(IOValue.HIGH) ? 1 : 0;                
                try {
                    message.send(rxbd.get64BitAddress(), entry.getKey(), value);
                } catch (MqttException ex) {
                    Logger.getLogger(XbmqSampleReceiveListener.class.getName()).log(Level.ERROR, ex);
                }
            }
        }
        if (ios.hasAnalogValues()) {
            HashMap<IOLine, Integer> analogValues = ios.getAnalogValues();
            MqttIOMessage message = new MqttIOMessage(xbmq);
            for (Map.Entry<IOLine, Integer> entry : analogValues.entrySet()) {                                        
                try {
                    message.send(rxbd.get64BitAddress(), entry.getKey(), entry.getValue());
                } catch (MqttException ex) {
                    Logger.getLogger(XbmqSampleReceiveListener.class.getName()).log(Level.ERROR, ex);
                }
            }
        }

    }
}
