XBee MQTT Gateway
=================
Xbmq is a gateway between an XBee network and MQTT.  Discover, monitor IO, read
and write data, get and set AT parameters of any XBee device on a network 
by publishing and subscribing to MQTT messages.

### Examples

To configure D0 as an input, publish "D0=3" to 
'rootTopic/64bit-gateway-address/64bit-device-address/atRequest

To be notified whenever the D0 input changes state, subscribe to
'rootTopic/64bit-gateway-address/64bit-device-address/io/DIO0_AD0'.

Get a list of all nodes on the network in a JSON-list by subscribing to
'rootTopic/64bit-gateway-address/discoveryResponse', then sending "JSON" to
'rootTopic/64bit-gateway-address/discoveryRequest'

Requirements
--------
1. Java-enabled computer or embedded device with a serial port and networking to
act as the gateway.
2. Java 7 runtime.
3. An XBee device in API mode (AP=2) connected to the gateway by direct UART 
connection or by serial port adapter.
4. One or more additional XBee devices with the same network/pan ID as the
gateway XBee.

Installation
------------

*To build from source please see Developers below*

1. Download and unzip the latest release from 
[GitHub](https://github.com/angryelectron/xbmq/releases).
2. Change to the xbmq-[platform] directory.
3. Edit xbmq.properties (see Configuration below).
4. Run xbmq.sh (Linux/Mac, may need to chmod +x first) or xbmq.bat (Windows).

An initscript is also provided to allow Debian (and similar) distros to start
xbmq automatically on boot. To use:

1. Edit xbmqd and change DAEMON to point to wherever you've installed xbmq.sh.
2. chmod +x xbmqd
3. sudo cp xbmqd /etc/init.d/.
4. sudo update-rc.d xbmq defaults

Configuration
-------------
Configure the gateway by modifying xbmq.properties.  If xmbq.properties is not 
found, the default values () will be used.  The properties file and/or defaults
can be overridden by specifying new values on the command line.  Use the `-h`
option on the command line for more details.

* `port` - serial port for local XBee connection (/dev/tty/USB0).
* `baud` - XBee baud rate (9600).
* `rootTopic` - a top-level MQTT topic under which all other topics will be created.
* `broker` - an MQTT broker (tcp://iot.eclipse.org:1883)

When configuring the XBee devices, ensure that all XBees accessible from the 
gateway use the same network/PAN ID and that the local XBee is in API mode (AP=2).

Usage
------
Once the gateway is running, MQTT clients can access the XBee network using the
topics below.  Java clients can include com.angryelectron.xbmq.XbmqTopics for 
easy access to Xmbq topics.

Note the following conventions used when describing topics:

* `rootTopic` - top-level MQTT topic under which all other topics will be created.
This is an optional, but recommended setting.
* `gw` - the 64-bit address of the local XBee attached to the gateway.  Eg. 0013A200408DE1FE
* `xbee` - the 64-bit address of the remote XBee.  Eg. 0013A200408B1F78

Mqtt wildcards (#, +) can also be used to subscribe to topics where appropriate.

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
gateway.  This is an "expensive" request as it will block the gateway for 
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

Message: an AT command, just as it would be entered into a serial terminal.  For
a complete list of AT commands, please see the [XBee Command Reference Tables]
(http://examples.digi.com/wp-content/uploads/2012/07/XBee_ZB_ZigBee_AT_Commands.pdf).
Note that ND and IS commands, while supported using other topics, cannot be send
using this topic.

Publish to this topic to get or set AT parameters.  To get a parameter, send the
command as the message (eg. D0).  To set a parameter, send the
command and an assigned value separated by an equal sign. (eg. D0=3).

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
1. Check `xmbq.log` on the gateway device.  Xbmq does not currently provide very
good feedback about errors to MQTT clients.
2. Review the [Wiki](https://github.com/angryelectron/xbmq/wiki) and [list of 
issues](https://github.com/angryelectron/xbmq/issues) on [GitHub]
(https://github.com/angryelectron).
3. For further help, submit a [new issue]
(https://github.com/angryelectron/xbmq/issues/new).

Developers
----------
xbmq is built using the Netbeans IDE but can be built simply with Java 7 SDK and
Apache Ant.  Simply open xbmq as a Netbeans project to build, or run 'ant' from
the command line.

To build a distribution archive, run 'ant dist'.  Distribution archives include
a sample properties file, this README, and RXTX native libraries.

Contributions to the xbmq project are welcome and can be submitted by using the 
[ol' fork-n-pull](https://help.github.com/articles/using-pull-requests/).

About
-----
Copyright 2015 Andrew Bythell, abythell@ieee.org.

xbmq is free software: you can redistribute it and/or 
modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or (at your option) 
any later version.

xbmq is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with xbmq.
If not, see http://www.gnu.org/licenses/.