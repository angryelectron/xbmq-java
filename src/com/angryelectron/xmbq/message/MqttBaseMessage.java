/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.digi.xbee.api.models.XBee64BitAddress;

/**
 *
 * @author abythell
 */
public interface MqttBaseMessage {            
    public String getPublishTopic(XBee64BitAddress address);    
    public String getSubscriptionTopic();
}
