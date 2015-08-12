/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.RemoteXBeeDevice;
import java.util.List;

/**
 *
 * @author abythell
 */
public class MqttDiscoveryMessage extends MqttBaseMessage {
    
    static final String PUBTOPIC = "discoveryResponse";        
    static final String SUBTOPIC = "discoveryRequest";
    public static enum Format {JSON};
    
    public MqttDiscoveryMessage(List<RemoteXBeeDevice> devices, Format format) {                
        switch (format) {
            case JSON:
            default:
                this.message.setPayload(toJSON(devices).getBytes());
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
        builder.append(SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }
    
    public static String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder();
        builder.append(xbmq.getRootTopic());
        builder.append(SEPARATOR);
        builder.append(xbmq.getGatewayId());
        builder.append(SEPARATOR);
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
                        
}