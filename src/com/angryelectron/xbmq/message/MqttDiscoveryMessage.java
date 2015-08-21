/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.Xbmq;
import com.digi.xbee.api.RemoteXBeeDevice;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Format and publish node-discovery results as an MQTT message. 
 */
public class MqttDiscoveryMessage {
        
    private final Xbmq xbmq;
    
    public MqttDiscoveryMessage(Xbmq xbmq) {
        this.xbmq = xbmq;
    }
    
    /**
     * Discovery results are returned in an MQTT message using one of
     * these formats.
     */
    public static enum Format {JSON, CSV, XML};
        
    /**
     * Publish the discovery results as an MQTT message.
     * @param devices A list of devices to publish.
     * @param format The format used to publish the message.
     * @throws MqttException if the message cannot be published.
     */
    public void send(List<RemoteXBeeDevice> devices, Format format) throws MqttException {                
        MqttMessage message = new MqttMessage();
        switch (format) {
            case CSV:
                message.setPayload(toCSV(devices).getBytes());
                break;
            case XML:
                message.setPayload(toXML(devices).getBytes());
                break;
            case JSON:
            default:
                message.setPayload(toJSON(devices).getBytes());
                break;
        }
        String topic = xbmq.getTopics().pubDiscovery();
        xbmq.publishMqtt(topic, message);        
    }
                                   
    /**
     * Build a JSON-formatted list.
     * @param devices List of devices to format.
     * @return JSON.
     */
    private String toJSON(List<RemoteXBeeDevice> devices) {
        StringBuilder builder = new StringBuilder("{\"devices\": [");        
        for (int i=0; i < devices.size(); i++) {
            builder.append("\"");
            builder.append(devices.get(i).get64BitAddress().toString());
            builder.append("\"");
            if (i != devices.size() - 1) {
                builder.append(",");
            }
        }        
        builder.append("]}");
        return builder.toString();
    }
    
    /**
     * Build a CSV-formatted list.
     * @param devices List of devices to format.
     * @return CSV
     */
    private String toCSV(List<RemoteXBeeDevice> devices) {
        StringBuilder builder = new StringBuilder();        
        for (int i=0; i < devices.size(); i++) {            
            builder.append(devices.get(i).get64BitAddress().toString());            
            if (i != devices.size() - 1) {
                builder.append(",");
            }
        }                
        return builder.toString();
    }

    /**
     * Build an XML-formatted list.     
     * @param devices List of devices to format.
     * @return XML
     */
    private String toXML(List<RemoteXBeeDevice> devices) {
        StringBuilder builder = new StringBuilder("<devices>");        
        for (RemoteXBeeDevice device : devices) {
            builder.append("<address>");
            builder.append(device.get64BitAddress().toString());
            builder.append("</address>");
        }        
        builder.append("</devices>");
        return builder.toString();

    }                        
}