/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xmbq.message;

import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * Common interface for all MQTT messages. 
 */
public interface MqttBaseMessage {            
    public String getPublishTopic(XBee64BitAddress address);    
    public String getSubscriptionTopic();
}
