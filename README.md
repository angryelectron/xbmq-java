XBee MQTT Gateway
=================
Xbmq is a gateway between an XBee network and MQTT.  Discover, monitor IO, read
and write data, get and set AT parameters from any MQTT client.

Hardware
--------
Xbmq will run on any device that has a serial port, network connection, and Java7.  
It is intended to be used on low-power devices such as BeagleBone and 
RaspberryPi, but should run on any Java-enabled device with networking.

Configuration
-------------
Configure the gateway by modifying xbmq.properties.  If xmbq.properties is not 
found, the default values () will be used.

* `port` - serial port for local XBee connection (/dev/tty/USB0).
* `baud` - XBee baud rate (9600).
* `rootTopic` - a top-level MQTT topic under which all other topics will be created.
* `broker` - an MQTT broker (tcp://iot.eclipse.org:1883)

Ensure that all XBees accessible from the gateway use the same network/PAN ID and that
the local XBee is in API mode (AP=2).

MQTT Topics
------
Once the gateway is running, MQTT clients can access the XBee network using the
following topics and conventions.

* `rootTopic` - top-level MQTT topic under which all other topics will be created.
* `gw` - the 64-bit address of the local XBee attached to the gateway.  Eg. 0013A200408DE1FE
* `xbee` - the 64-bit address of the remote XBee.  Eg. 0013A200408B1F78

### rootTopic/gw/online

Example: example/0013A200408DE1FE/online

Message: 1 if the gateway is online, 0 if the gateway is offline.

Subscribe to receive online status messages.  Messages for this topic are retained.
This is the last will and testament, so online will be 0 if the gateway is not
running for whatever reason.


### rootTopic/gw/discoveryRequest

Example: example/0013A200408DE1FE/discoveryRequest

Message: response format.  Supported formats are:  JSON, XML, CSV.  JSON is the
default and will be used if an invalid format is given.

Publish a message to this topic to initiate the discovery process (ND) on the 
gateway.  This is an "expensive" request as it can block the gateway for up to
15 seconds, delaying any other incoming requests.

### rootTopic/gw/discoveryResponse

Example: example/0013A200408DE1FE/discoveryResponse

Message: A formatted list containing the 64-bit device address of all
discovered devices.  Format will be JSON, XML, or CSV depending on the message
used to make the request.

Subscribe to this topic prior to publishing a discoveryRequest to receive the
results of the discovery.

### rootTopic/gw/xbee/atIn

Example: example/0013A200408DE1FE/0013A200408B1F78/atIn

Message: an AT command, just as it would be entered into a serial terminal.

Publish to this topic to get or set AT parameters.  To get a parameter, send the
2-character command as the message (eg. D0).  To set a parameter, send the
command and an assigned value (eg. D0=3).

### rootTopic/gw/xbee/atOut

Example: example/0013A200408DE1FE/0013A200408B1F78/atIn

Message: an AT command response (eg. D0=3).

Subscribe to this topic to receive the responses from AT commands issued through
the atIn topic.

### rootTopic/gw/xbee/dataIn

Example: example/0013A200408DE1FE/0013A200408B1F78/dataIn

Message: an array of bytes containing the data to be sent to the target XBee.

Publishing to this topic will send a data message to the target XBee.

### rootTopic/gw/xbee/dataOut

Example: example/0013A200408DE1FE/0013A200408B1F78/dataIn

Message: an array of bytes containing the data received from the target XBee.

Subscribe to this topic to receive data packets sent by the target XBee.

### rootTopic/gw/xbee/io/ioline

Example: example/0013A200408DE1FE/0013A200408B1F78/io/DIO0_AD0

Message: 1 or 0 for digital pins, 10-bit value for analog pins.

This is a read-only topic that is published whenever an IO sample is received from
an xbee.  `ioline` is one of the enumeration types from com.digi.xbee.api.io.IOLine:

* DIO0_AD0
* DIO1_AD1
* DIO2_AD2
* DIO3_AD3
* DIO4_AD4
* DIO5_AD5
* DIO6
* DIO7
* DIO8
* DIO9
* DIO10_PWM0
* DIO11_PWM1
* DIO12
* DIO13
* DIO14
* DIO15
* DIO16
* DIO17
* DIO18
* DIO19

Note: To change the value or mode of an IO pin, see the [atIn](#atIn) topic.

### rootTopic/gw/xbee/ioUpdate

Example: example/0013A200408DE1FE/0013A200408B1F78/ioUpdate

Message: Empty.

Publishing to this topic will force the XBee to update the status of all enabled
IO pins.  Results are published to the respective IO topics.

Troubleshooting
---------------
See xmbq.log.  Xbmq does not currently provide very good feedback about errors 
to MQTT clients.
