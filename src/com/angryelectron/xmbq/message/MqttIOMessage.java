/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;

/**
 *
 * @author abythell
 */
public class MqttIOMessage extends MqttBaseMessage {
        
    private static final String TOPIC = "io";    
    private final IOLine line;    
    
    public MqttIOMessage(XBee64BitAddress address, IOLine line, Integer value) {        
        this.address = address;
        this.line = line;
        this.message.setPayload(value.toString().getBytes());        
    }
                
    /**     
     * Note: while the enum is DIO0_AD, the name is DIO0/AD.  Slashes must be
     * replaced (with underscores) since the slash is the MQTT topic separator.
     
     * @return Reply topic.
     */
    @Override
    String getPublishTopic() {
        StringBuilder builder = new StringBuilder(super.getPublishTopic());
        builder.append(SEPARATOR);
        builder.append(TOPIC);
        builder.append(SEPARATOR);
        builder.append(line.getName().replaceAll(SEPARATOR, "_"));
        builder.append(SEPARATOR);
        return builder.toString();
    }
        
}
