/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author abythell
 */
public class XbmqTopic {

    private static final Pattern topicPattern = Pattern.compile("(.*\\/)?([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})(\\/.)*");
    private final String gw;
    private final String root;
    private final String ATPUBTOPIC = "atOut";
    private final String ATSUBTOPIC = "atIn";
    private final String DATAPUBTOPIC = "dataOut";
    private final String DATASUBTOPIC = "dataIn";
    private final String IOPUBTOPIC = "io";
    private final String IOSUBTOPIC = "ioUpdate";
    private final String DISCOPUBTOPIC = "discoveryResponse";
    private final String DISCOSUBTOPIC = "discoveryRequest";
    private final String ONLINEPUBTOPIC = "online";
    private final String ONLINESUBTOPIC = ONLINEPUBTOPIC;
    
    public XbmqTopic(String rootTopic, String gw) {        
        if (rootTopic == null) {
            this.root = "";
        } else if (rootTopic.matches("[^#\\+\\/]*")) {
            this.root = rootTopic;
        } else {
            throw new IllegalArgumentException("Invalid character (#+/) in topic.");
        }

        if (gw.matches("[0-9a-fA-F]{16}")) {
            this.gw = gw;
        } else {
            throw new IllegalArgumentException("Gateway must be 64-bit XBee address");
        }
    }

    private String gwTopic() {
        StringBuilder builder = new StringBuilder();
        if (!root.isEmpty()) {
            builder.append(root);
            builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        }
        builder.append(gw);
        return builder.toString();
    }

    private String gwTopic(String subtopic) {
        StringBuilder builder = new StringBuilder(gwTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(subtopic);
        return builder.toString();
    }

    private String deviceTopic(String device, String subTopic) {
        StringBuilder builder = new StringBuilder(gwTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        if (device == null) {
            builder.append("+");
        } else {
            builder.append(device);
        }
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(subTopic);
        return builder.toString();
    }

    private String ioTopic(String device, String line) {                        
        line = line.toUpperCase();
        if (!line.matches("DIO[0-9]{1,2}(/[ADPWM]{2,3}[0-9])?")) {
            throw new IllegalArgumentException("Invalid line name.");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(deviceTopic(device, IOPUBTOPIC));
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(line.replaceAll(MqttTopic.TOPIC_LEVEL_SEPARATOR, "_"));
        return builder.toString();
    }

    public String pubAt(String device) {
        return deviceTopic(device, ATPUBTOPIC);
    }

    public String pubData(String device) {
        return deviceTopic(device, DATAPUBTOPIC);
    }

    public String pubIO(String device, String line) {
        return ioTopic(device, line);
    }

    public String pubDiscovery() {
        return gwTopic(DISCOPUBTOPIC);
    }

    public String pubOnline() {
        return gwTopic(ONLINEPUBTOPIC);
    }

    public String subAt() {
        return deviceTopic(null, ATSUBTOPIC);
    }

    public String subData() {
        return deviceTopic(null, DATASUBTOPIC);
    }

    public String subIO(String device, String line) {
        return ioTopic(device, line);
    }

    public String subDiscovery() {
        return gwTopic(DISCOSUBTOPIC);
    }

    public String subOnline(boolean wildcard) {
        StringBuilder builder = new StringBuilder();
        if (wildcard) {
            if (!root.isEmpty()) {
                builder.append(root);
                builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);                
            }
            builder.append(MqttTopic.SINGLE_LEVEL_WILDCARD);
            builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
            builder.append(ONLINESUBTOPIC);
            return builder.toString();
        } else {
            return gwTopic(ONLINESUBTOPIC);
        }
    }
    
    public static String parseAddress(String topic) {
        Matcher matcher = topicPattern.matcher(topic);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid topic: " + topic);
        }
        return new String(matcher.group(3));
    }

}
