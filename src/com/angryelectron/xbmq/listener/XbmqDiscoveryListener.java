/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.listener;

import com.angryelectron.xmbq.message.MqttDiscoveryMessage;
import com.angryelectron.xmbq.message.MqttDiscoveryMessage.Format;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author abythell
 */
public class XbmqDiscoveryListener implements IDiscoveryListener {

    private final List<RemoteXBeeDevice> devices;
    private final Format format;

    public XbmqDiscoveryListener(Format format) {
        this.devices = new ArrayList<>();
        this.format = format;
    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice rxbd) {
        devices.add(rxbd);
    }

    @Override
    public void discoveryError(String string) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, string);
    }

    @Override
    public void discoveryFinished(String error) {
        MqttDiscoveryMessage message = new MqttDiscoveryMessage();
        try {
            if (error != null) {
                message.send(error, format);
            } else {
                message.send(devices, format);
            }
        } catch (MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
    }

}
