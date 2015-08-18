/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Access to XBee device and MQTT broker.
 */
public class Xbmq {
    
    private final XBeeDevice xbee;
    private MqttAsyncClient mqtt;
    private String rootTopic;
    
    public Xbmq(XBeeDevice xbee) {
        this.xbee = xbee;        
    }
    
    /**
     * Connect to devices and brokers.     
     * @param broker MQTT broker (eg. tcp://broker.name:1883)
     * @param rootTopic Root MQTT topic
     * @throws XBeeException if connection to XBee fails
     * @throws MqttException if connection to MQTT broker fails
     */   
    public void connect(String broker, String rootTopic) throws XBeeException, MqttException {   
                
        if (!xbee.isOpen()) {
            xbee.open();
        }
        
        this.rootTopic = rootTopic;        
        
        /**
         * Setup last will and testament.
         */
        

        /**
         * Connect.
         */
        MqttConnectOptions options = new MqttConnectOptions();
        options.setWill(getLwtTopic(), "0".getBytes(), 0, true);         
        options.setCleanSession(true);        
        mqtt = new MqttAsyncClient(broker, getGatewayAddress().toString()); 
        mqtt.connect(options).waitForCompletion();
        
        /**
         * Set online status.
         */        
        this.publishMqtt(getLwtTopic(), new MqttMessage("1".getBytes()));
    }
    
    public String getLwtTopic() {
        StringBuilder topicBuilder = new StringBuilder(rootTopic);
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append(getGatewayAddress());
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append("online");
        return topicBuilder.toString();        
    }
    
    /**
     * Connect to devices and brokers.  Reads settings from xmbq.properties
     * or uses default values if not found.  Defaults are:  port=/dev/ttyUSB0, 
     * baud=9600, rootTopic="", broker=tcp://test.mosquitto.org:1883.
     * @throws XBeeException if connection to XBee fails
     * @throws MqttException if connection to MQTT broker fails
     */
    
    public void connect() throws XBeeException, MqttException {
        XbmqConfig config = new XbmqConfig();
        connect(config.getBroker(), config.getRootTopic());
    }
    
    /**
     * Get the ID of this Gateway.  The ID is actually the 64-bit address of the
     * local XBee attached to the gateway, and is the second-level topic for all
     * requests and responses to/from this gateway.
     * @return The 64-bit address of the XBee radio attached to the gateway, or 
     * FFFFFFFFFFFFFFFF if the address is unknown.
     * 
     */
    
    public XBee64BitAddress getGatewayAddress() {                
            return xbee.get64BitAddress();        
    }
    
    /**
     * Access the XBeeDevice attached to the gateway.
     * @return XBeeDevice or null if not connected.
     * @see #connect() 
     * @see #connect(int, java.lang.String, java.lang.String, java.lang.String) 
     */
    
    public XBeeDevice getXBee() {        
        return xbee;
    }
    
    /**
     * Access the MQTT client attached to the gateway.
     * @return MqttAsyncClient or null if not connected.
     * @see #connect() 
     * @see #connect(int, java.lang.String, java.lang.String, java.lang.String) 
     */
    
    public MqttAsyncClient getMqttClient() {              
        return mqtt;
    }
    
    /**
     * Get the MQTT top-level topic. 
     * @return Root topic, or an empty string if not set.
     */
    
    public String getRootTopic() {
        if (rootTopic == null) {
            return "";
        } else {
            return rootTopic;
        }
    }
    
    /**
     * Close XBee device and MQTT client.  Blocks until MQTT connection is
     * closed to ensure last will and testament is published.
     * @throws MqttException 
     */
    
    public void disconnect() throws MqttException {
        xbee.close();
        mqtt.disconnect().waitForCompletion();        
    }
    
        
    private static final Pattern pattern = Pattern.compile(".*\\/([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})(\\/.)*");
    
    /**
     * Parse a topic and extract the 64-bit address of an XBee device.
     * @param topic Topic to parse.
     * @return 64-bit address.
     */
    
    public XBee64BitAddress getAddressFromTopic(String topic) {        
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(2));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
    
    /**
     * Parse a topic and extract the 64-bit address of the gateway.
     * @param topic Topic to parse.
     * @return 64-bit gateway address.
     */
    
    public XBee64BitAddress getGatewayFromTopic(String topic) {
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            return new XBee64BitAddress(matcher.group(1));
        } else {
            return XBee64BitAddress.UNKNOWN_ADDRESS;
        }                        
    }
    
    /**
     * Publish an MQTT message.
     * @param topic Topic to publish.
     * @param message Message to publish.
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    
    public void publishMqtt(String topic, MqttMessage message) throws MqttException {                
        mqtt.publish(topic, message, null, new IMqttActionListener(){
            
            @Override
            public void onSuccess(IMqttToken imt) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onFailure(IMqttToken imt, Throwable thrwbl) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, thrwbl);
            }
        
        });
    }
            
    /**
     * Build an Xbmq topic using the gateway address.
     * @return Topic using the format: rootTopic/64-bit-gateway-address.
     */
    
    public String getGatewayTopic() {                
        StringBuilder builder = new StringBuilder();
        builder.append(this.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(this.getGatewayAddress());
        return builder.toString();
    }
    
    /**
     * Build and Xbmq topic using the gateway and device address.
     * @param address 64-bit address of the device to include.
     * @return Topic using the format:  
     * rootTopic/64bit-gateway-address/64bit-device-address
     */
    
    public String getDeviceTopic(XBee64BitAddress address) {                
        StringBuilder builder = new StringBuilder();
        builder.append(this.getRootTopic());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(this.getGatewayAddress());
        builder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        builder.append(address.toString());
        return builder.toString();
    }
    

}
