/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqUtils;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class MqttDiscoveryMessage implements MqttBaseMessage {
    
    static final String PUBTOPIC = "discoveryResponse";        
    static final String SUBTOPIC = "discoveryRequest";
    
    public static enum Format {JSON, CSV, XML};
        
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
        String topic = getPublishTopic(XBee64BitAddress.UNKNOWN_ADDRESS);
        XbmqUtils.publishMqtt(topic, message);
        
    }

    public void send(String error, Format format) throws MqttException {
        //TODO: make error message obey response format
        MqttMessage message = new MqttMessage(error.getBytes());
        String topic = getPublishTopic(XBee64BitAddress.UNKNOWN_ADDRESS);
        XbmqUtils.publishMqtt(topic, message);        
    }
                        
    @Override
    public String getPublishTopic(XBee64BitAddress address) {
        StringBuilder builder = new StringBuilder(XbmqUtils.getGatewayTopic());        
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(PUBTOPIC);
        return builder.toString();
    }
    
    @Override
    public  String getSubscriptionTopic() {
        StringBuilder builder = new StringBuilder(XbmqUtils.getGatewayTopic());        
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(SUBTOPIC);
        return builder.toString();
    }
    
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