/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.RemoteXBeeDevice;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class MqttDiscoveryMessage extends MqttBaseMessage {
    
    static final String PUBTOPIC = "discoveryResponse";        
    static final String SUBTOPIC = "discoveryRequest";
    
    public static enum Format {JSON, CSV, XML};
    
    public MqttDiscoveryMessage(List<RemoteXBeeDevice> devices, Format format) {                
        switch (format) {
            case CSV:
                this.message.setPayload(toCSV(devices).getBytes());
                break;
            case XML:
                this.message.setPayload(toXML(devices).getBytes());
                break;
            case JSON:
            default:
                this.message.setPayload(toJSON(devices).getBytes());
                break;
        }
        
    }

    public MqttDiscoveryMessage(String error, Format format) {
        //TODO: make error message obey response format
        this.message.setPayload(error.getBytes());        
    }
                        
    @Override
    String getPublishTopic() {
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }
    
    public static String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(SUBTOPIC);
        return builder.toString();
    }
    
    public final String toJSON(List<RemoteXBeeDevice> devices) {
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