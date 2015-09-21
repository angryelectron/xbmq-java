/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq;

import static com.angryelectron.xbmq.XbmqTopic.Topic.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates MQTT topics specific to this gateway, including topics to which the
 * gateway publishes as well as the topics to which the gateway subscribes. The
 * general topic format as well as request and response topics are defined here.
 * <br/>
 * This class has no dependencies on any Xbmq classes, allowing it to be used by
 * client applications that need to publish and subscribe to Xbmq gateways.
 *
 */
public class XbmqTopic {

    private static final Pattern topicPattern = Pattern.compile("(.*\\/)?([0-9a-fA-F]{16})\\/([0-9a-fA-F]{16})(\\/.)*");
    private final String gw;
    private final String root;

    /**
     * Re-define values from org.eclipse.paho.client.mqttv3.MqttTopic so this
     * class to be used by 3rd party applications without depending on the Paho
     * MQTT library.
     */
    public static final String TOPIC_LEVEL_SEPARATOR = "/";
    public static final String SINGLE_LEVEL_WILDCARD = "+";

    /**
     * Re-define IOLines from com.digi.xbee.api.io.IOLine. This allows to class
     * to validate IOLine names and 3rd party applications to use this class
     * without depending on the Digi XBee Java library.
     */
    private final Set<String> ioLines = new HashSet<>(Arrays.asList(
            "DIO0_AD0",
            "DIO1_AD1",
            "DIO10_PWM0",
            "DIO11_PWM1",
            "DIO12",
            "DIO13",
            "DIO14",
            "DIO15",
            "DIO16",
            "DIO17",
            "DIO18",
            "DIO19",
            "DIO2_AD2",
            "DIO3_AD3",
            "DIO4_AD4",
            "DIO5_AD5",
            "DIO6",
            "DIO7",
            "DIO8",
            "DIO9"
    ));

    /**
     * Publish and subscribe topic stubs. Defined from the perspective of the
     * gateway.
     */
    public static enum Topic {

        /**
         * Gateway publishes results of AT commands.
         */
        ATPUBTOPIC("atOut"),
        /**
         * Client publishes AT command requests.
         */
        ATSUBTOPIC("atIn"),
        /**
         * Gateway publishes data packets.
         */
        DATAPUBTOPIC("dataOut"),
        /**
         * Client publishes data to be sent to the XBee network.
         */
        DATASUBTOPIC("dataIn"),
        /**
         * Gateway publishes IO samples.
         */
        IOPUBTOPIC("io"),
        /**
         * Client requests IO updates.
         */
        IOSUBTOPIC("ioUpdate"),
        /**
         * Gateway publishes node-discovery results.
         */
        DISCOPUBTOPIC("discoveryResponse"),
        /**
         * Client requests node-discovery.
         */
        DISCOSUBTOPIC("discoveryRequest"),
        /**
         * Gateway online status.
         */
        ONLINETOPIC("online");

        private final String value;

        Topic(String value) {
            this.value = value;
        }
    }

    /**
     * Constructor. To access to an XbmqTopic object pre-configured for the
     * gateway, use {@link com.angryelectron.xbmq.Xbmq#getTopics()}.
     *
     * @param rootTopic Root topic to prepend to all topics.
     * @param gw 64-bit gateway address (Local XBee address).
     */
    public XbmqTopic(String rootTopic, String gw) {
        if (rootTopic == null) {
            this.root = "";
        } else if (rootTopic.matches("[^#\\+\\/]*")) {
            this.root = rootTopic;
        } else {
            throw new IllegalArgumentException("Invalid character (#+/) in topic.");
        }

        if (gw.matches("[0-9a-fA-F]{16}")) {
            this.gw = gw.toUpperCase();
        } else {
            throw new IllegalArgumentException("Gateway must be 64-bit XBee address");
        }
    }

    /**
     * Get the gateway topic. Eg. "rootTopic"/"gateway-address", or simply
     * "gateway-address" if rootTopic is not configured. This topic is not used
     * directly, rather used as a base for building other topics.
     *
     * @return Gateway topic.
     */
    public String gwTopic() {
        StringBuilder builder = new StringBuilder();
        if (!root.isEmpty()) {
            builder.append(root);
            builder.append(TOPIC_LEVEL_SEPARATOR);
        }
        builder.append(gw);
        return builder.toString();
    }

    /**
     * Get the gateway subtopic. Eg. "rootTopic"/"gateway-address"/"subtopic",
     * or simply "gateway-address"/"subtopic" if rootTopic is not configured.
     * This topic is not used directly, rather used as a base for building other
     * topics.
     *
     * @param subtopic Sub topic appended after root topic and gateway address.
     * @return Gateway subtopic.
     */
    private String gwTopic(String subtopic) {
        StringBuilder builder = new StringBuilder(gwTopic());
        builder.append(TOPIC_LEVEL_SEPARATOR);
        builder.append(subtopic);
        return builder.toString();
    }

    /**
     * Get the device topic.
     *
     * @param device 64-Bit XBee device address. If device is null, the topic
     * will use a single-level wildcard (+) instead of a device address.
     * @param subTopic Sub topic to append after root topic, gateway address,
     * and device address.
     * @return Device subtopic.
     */
    private String deviceTopic(String device, String subTopic) {
        StringBuilder builder = new StringBuilder(gwTopic());
        builder.append(TOPIC_LEVEL_SEPARATOR);
        if (device == null) {
            builder.append(SINGLE_LEVEL_WILDCARD);
        } else {
            builder.append(device);
        }
        builder.append(TOPIC_LEVEL_SEPARATOR);
        builder.append(subTopic);
        return builder.toString();
    }

    /**
     * Get the IO topic used by the gateway to publish IO samples and by clients
     * to subscribe to IO sample updates.
     *
     * @param device 64-bit XBee address of device.
     * @param line IOLine name. Supported line names are defined in
     * <a href="http://ftp1.digi.com/support/documentation/xbjlib/javadoc/javadoc_1.1.0/com/digi/xbee/api/io/IOLine.html">com.digi.xbee.api.io.IOLine</a>.
     * @return IO topic.
     */
    private String ioTopic(String device, String line) {
        if (line == null) {
            line = SINGLE_LEVEL_WILDCARD;
        } else {
            line = line.toUpperCase().replaceAll(TOPIC_LEVEL_SEPARATOR, "_");
            if (!ioLines.contains(line)) {
                throw new IllegalArgumentException("Invalid line name.");
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(deviceTopic(device, IOPUBTOPIC.value));
        builder.append(TOPIC_LEVEL_SEPARATOR);
        builder.append(line);
        return builder.toString();
    }

    /**
     * AT-command response topic. The gateway publishes the results of AT
     * commands to this topic. Clients can subscribe to this topic to receive
     * responses to AT-command requests.
     *
     * @param device 64-Bit XBee Address, or null to subscribe to all AT command
     * responses.
     * @return AT command response topic. The device address is replaced with a
     * single-topic wildcard if device is null.
     */
    public String atResponse(String device) {
        return deviceTopic(device, ATPUBTOPIC.value);
    }

    /**
     * Data response topic. The gateway publishes the payload of any data
     * packets broadcasted on the XBee network. Clients can subscribe to this
     * topic to receive responses to data requests.
     *
     * @param device 64-Bit XBee Address or null to subscribe to all data
     * responses.
     * @return Data-packet topic. The device address is replaced with a single-
     * topic wildcard is the device is null.
     */
    public String dataResponse(String device) {
        return deviceTopic(device, DATAPUBTOPIC.value);
    }

    /**
     * IO response topic. The gateway publishes IO sample data received from
     * devices on the XBee network. Clients can subscribe to this topic to
     * receive IO pin changes.
     *
     * @param device 64-Bit XBee Address or null to subscribe to pin changes
     * from all devices on the network.
     * @param line IOLine name or null. Supported line names are defined in
     * <a href="http://ftp1.digi.com/support/documentation/xbjlib/javadoc/javadoc_1.1.0/com/digi/xbee/api/io/IOLine.html">com.digi.xbee.api.io.IOLine</a>.
     * @return IO topic used by the gateway to publish IO pin changes or by a
     * client to subscribe to IO pin changes. If device and/or line are null, a
     * single-level-wildcard topic is created.
     */
    public String ioResponse(String device, String line) {
        return ioTopic(device, line);
    }

    /**
     * Discovery response topic. The gateway publishes node-discovery results to
     * this topic. Clients can subscribe to this topic to receive the response.
     *
     * @return Node-discovery response topic.
     */
    public String discoveryResponse() {
        return gwTopic(DISCOPUBTOPIC.value);
    }

    /**
     * AT request topic. The gateway subscribes to this topic to process AT
     * commands destined for XBee devices behind the gateway.  Clients publish
     * to this topic to send AT commands to remote XBee devices.
     *
     * @return Wildcard topic used by the gateway to receive and relay AT
     * commands for ALL devices behind the gateway.
     */
    public String atRequest() {
        return deviceTopic(null, ATSUBTOPIC.value);
    }

    /**
     * Data request topic. The gateway subscribes to this topic to process
     * data commands destined for XBee devices behind the gateway.  Clients
     * publish to this topic to send data messages to remote XBee devices.
     *
     * @return Wildcard topic used by the gateway to receive and relay data
     * messages for ALL devices behind the gateway.
     *
     */
    public String dataRequest() {
        return deviceTopic(null, DATASUBTOPIC.value);
    }

    /**
     * IO update request topic.  The gateway subscribes to this topic to process
     * IO sample update requests.  Clients publish to this topic to force IO samples.
     *
     * @param device 64-Bit XBee address to request update from or null for a
     * single level wildcard topic. Clients must specify the device address as
     * publishing to a wildcard topic is not allowed.
     * @return Topic used by the gateway to listen for IO update requests, or by
     * a client to request IO updates from a specific device.
     */
    public String ioUpdateRequest(String device) {
        return deviceTopic(device, IOSUBTOPIC.value);
    }

    /**
     * Discovery request topic.  The gateway subscribes to this topic to process
     * Node-discovery requests.  Clients publish to this topic to initiate discovery.
     *
     * @return Gateway / network discovery topic.
     */
    public String discoveryRequest() {
        return gwTopic(DISCOSUBTOPIC.value);
    }

    /**
     * Online topic.  The gateway publishes '1' to this topic when connected to
     * an MQTT broker.  When the gateway goes offline, the last-will-and-testament
     * feature publishes '0'.  Clients can subscribe to this topic to be notified
     * of the gateway's status.
     * @param wildcard Subscribe to the online status of ALL gateways when true.     
     * Use with caution when using this in conjunction with a public broker and/or
     * an empty root-topic.
     * @return Gateway online status topic.
     */
    public String online(boolean wildcard) {
        StringBuilder builder = new StringBuilder();
        if (wildcard) {
            if (!root.isEmpty()) {
                builder.append(root);
                builder.append(TOPIC_LEVEL_SEPARATOR);
            }
            builder.append(SINGLE_LEVEL_WILDCARD);
            builder.append(TOPIC_LEVEL_SEPARATOR);
            builder.append(ONLINETOPIC.value);
            return builder.toString();
        } else {
            return gwTopic(ONLINETOPIC.value);
        }
    }

    /**
     * Extract a 64-bit XBee device address from a topic.
     * @param topic The topic to parse.
     * @return A 64-bit address string.
     */
    public static String parseAddress(String topic) {
        Matcher matcher = topicPattern.matcher(topic);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid topic: " + topic);
        }
        return matcher.group(3);
    }

    /**
     * Check if a topic matches a specific type.
     * @param a Topic type.
     * @param b Topic string.
     * @return True if 'b' is a topic of type 'a'.
     */
    public static boolean matches(Topic a, String b) {
        return b.contains(a.value);
    }

}
