/*
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq.listener;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.message.MqttDiscoveryMessage;
import com.angryelectron.xbmq.message.MqttDiscoveryMessage.Format;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Collect responses from remote devices during the network discovery process
 * and publish the result as an Mqtt message.
 */
public class XbmqDiscoveryListener implements IDiscoveryListener {

    private final Xbmq xbmq;
    private final List<RemoteXBeeDevice> devices;
    private final Format format;

    /**
     * Constructor.
     *
     * @param xbmq
     * @param format The format used for the discovery response.
     */
    public XbmqDiscoveryListener(Xbmq xbmq, Format format) {
        this.xbmq = xbmq;
        this.devices = new ArrayList<>();
        this.format = format;
    }

    /**
     * Called when a new device is discovered.
     *
     * @param rxbd The newly discovered device.
     */
    @Override
    public void deviceDiscovered(RemoteXBeeDevice rxbd) {
        devices.add(rxbd);
    }

    /**
     * Called when an error occurs during discovery.
     *
     * @param error The error message.
     */
    @Override
    public void discoveryError(String error) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, error);
    }

    /**
     * Called when the discovery process is complete.
     *
     * @param error null unless an error has occurred.
     */
    @Override
    public void discoveryFinished(String error) {
        MqttDiscoveryMessage message = new MqttDiscoveryMessage(xbmq);
        try {
            message.send(devices, format);
        } catch (MqttException ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, ex);
        }
    }

}
