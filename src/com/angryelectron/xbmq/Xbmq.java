/**
 * Xbmq - XBee / MQTT Gateway
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */

package com.angryelectron.xbmq;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * Global access to XBee device and MQTT broker.  Singleton.
 */
public class Xbmq {
    
    private XBeeDevice xbee;
    private MqttAsyncClient mqtt;
    private String rootTopic;
    private String gatewayId;
    
    private Xbmq(){}         
    
    private static class SingletonHelper{
        private static final Xbmq INSTANCE = new Xbmq();
    }

    /**
     * Get single instance of this class.
     * @return Global Xbmq object.
     */
    public static Xbmq getInstance(){
        return SingletonHelper.INSTANCE;
    }
            
    /**
     * Connect to devices and brokers.
     * @param baud XBee serial baud rate (eg. 9600)
     * @param port XBee serial port name (eg. /dev/ttyUSB0)
     * @param broker MQTT broker (eg. tcp://broker.name:1883)
     * @param rootTopic Root MQTT topic
     * @throws XBeeException if connection to XBee fails
     * @throws MqttException if connection to MQTT broker fails
     */
    public void connect(int baud, String port, String broker, String rootTopic) throws XBeeException, MqttException {   
                
        this.rootTopic = rootTopic;
        
        /**
         * Open XBee first so we can pass the 64-bit address as the client ID
         * to MQTT.  
         */
        xbee = new XBeeDevice(port, baud);        
        xbee.open();    
        String gateway = getGatewayId();
        
        /**
         * Setup last will and testament.
         */
        StringBuilder topicBuilder = new StringBuilder(rootTopic);
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append(gateway);
        topicBuilder.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
        topicBuilder.append("online");
        String lwtTopic = topicBuilder.toString();

        /**
         * Connect.
         */
        MqttConnectOptions options = new MqttConnectOptions();
        options.setWill(lwtTopic, "0".getBytes(), 0, true);        
        mqtt = new MqttAsyncClient(broker, getGatewayId()); 
        mqtt.connect(options).waitForCompletion();
        
        /**
         * Set online status.
         */
        mqtt.publish(lwtTopic, "1".getBytes(), 0, true);
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
        connect(config.getXBeeBaud(), config.getXBeePort(), config.getBroker(), config.getRootTopic());
    }
    
    /**
     * Get the ID of this Gateway.  The ID is actually the 64-bit address of the
     * local XBee attached to the gateway, and is the second-level topic for all
     * requests and responses to/from this gateway.
     * @return The 64-bit address of the XBee radio attached to the gateway, or 
     * FFFFFFFFFFFFFFFF if the address is unknown.
     * 
     */
    public String getGatewayId() {
        if (xbee == null) {
            return XBee64BitAddress.UNKNOWN_ADDRESS.toString();
        }
        if ((gatewayId == null) || gatewayId.isEmpty()) {
            gatewayId = xbee.get64BitAddress().toString();
        }
        return gatewayId;
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
    String getRootTopic() {
        if (rootTopic == null) {
            return "";
        } else {
            return rootTopic;
        }
    }

}
