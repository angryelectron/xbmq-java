/*
 * Xbmq - XBee / MQTT Gateway 
 * Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.angryelectron.xbmq.listener.XbmqDataReceiveListener;
import com.angryelectron.xbmq.listener.XbmqMqttCallback;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import java.io.File;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Main entry point for the application. Parses command line and reads
 * properties file to determine XBee and MQTT settings. Connects to XBee device
 * and MQTT broker. Sets up listeners for XBee IOSamples and data packages, as
 * well as MQTT subscriptions. Sets up a runtime hook to shutdown when Ctrl-C is
 * pressed.
 */
public class Main {

    private static final String progVersion = "1.0";
    private static final String progName = "xbmq";

    /**
     * @param args the command line arguments
     * @throws com.digi.xbee.api.exceptions.XBeeException
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public static void main(String[] args) throws XBeeException, MqttException, InterruptedException {

        XbmqConfig config = new XbmqConfig();
        CommandLine cmd = null;
        try {
            CommandLineParser parser = new PosixParser();
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        if (cmd.hasOption("h") || cmd.hasOption("v")) {
            getHelp();
            System.exit(0);
        }

        String port = (cmd.hasOption("p")) ? cmd.getOptionValue("p")
                : config.getXBeePort();
        String baud = (cmd.hasOption("b")) ? cmd.getOptionValue("b")
                : config.getXBeeBaud().toString();
        String rootTopic = (cmd.hasOption("t")) ? cmd.getOptionValue("t")
                : config.getRootTopic();
        String broker = (cmd.hasOption("u")) ? cmd.getOptionValue("u")
                : config.getBroker();

        /**
         * Ensure RXTX knows about non-standard serial ports. For details see
         * http://angryelectron.com/rxtx-on-raspbian/.
         */
        Properties properties = System.getProperties();
        String currentPorts = properties.getProperty("gnu.io.rxtx.SerialPorts", port);
        if (currentPorts.equals(port)) {
            properties.setProperty("gnu.io.rxtx.SerialPorts", port);
        } else {
            properties.setProperty("gnu.io.rxtx.SerialPorts", currentPorts
                    + File.pathSeparator + port);
        }

        XBeeDevice xbee = new XBeeDevice(port, Integer.parseInt(baud));
        xbee.open();
        MqttAsyncClient mqtt = new MqttAsyncClient(broker, xbee.get64BitAddress().toString());        
        final Xbmq xbmq = new Xbmq(xbee, mqtt, rootTopic);
        
        /**
         * From MqttClient Javadocs:  It is recommended to call 
         * IMqttClient.setCallback(MqttCallback) prior to connecting in order 
         * that messages destined for the client can be accepted as soon as the 
         * client is connected.
         */
        mqtt.setCallback(new XbmqMqttCallback(xbmq));

        boolean connected = false;
        while (!connected) {
            try {
                xbmq.connectMqtt();
                connected = true;
            } catch (MqttException ex) {
                Logger.getLogger(Main.class).log(Level.ERROR, ex);
                Thread.sleep(60000);
            }
        }
        
        /**
         * Log future error messages to MQTT
         */        
        Logger.getRootLogger().addAppender(new XbmqAppender(xbmq));

        /**
         * Setup listeners for unsolicited packets from the XBee network.
         */
        xbee.addDataListener(new XbmqDataReceiveListener(xbmq));
        xbee.addIOSampleListener(new XbmqSampleReceiveListener(xbmq));

        /**
         * Subscribe to topics.
         */
        XbmqTopic t = xbmq.getTopics();
        String[] topics = {
            t.atRequest(),
            t.dataRequest(),
            t.discoveryRequest(),
            t.ioUpdateRequest(null)
        };
        int[] qos = {0, 0, 0, 0};
        
        mqtt.subscribe(topics, qos);

        Logger.getLogger(Main.class).log(Level.INFO, "Starting XBMQ gateway "
                + xbmq.getTopics().gwTopic());

        /**
         * Add shutdown hooks to stop logger on Ctrl-C.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.getLogger(Main.class).log(Level.INFO, "Stopping XBMQ gateway "
                        + xbmq.getTopics().gwTopic());
                try {
                    xbmq.disconnect();
                } catch (MqttException ex) {
                    Logger.getLogger(this.getClass()).log(Level.ERROR, null, ex);
                }
            }
        });
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", false, "show help");
        options.addOption("v", false, "show version");
        Option port = OptionBuilder.withArgName("port").hasArg()
                .withDescription("XBee serial port").create("p");
        options.addOption(port);
        Option baud = OptionBuilder.withArgName("baud").hasArg()
                .withDescription("XBee baud rate").create("b");
        options.addOption(baud);
        Option rootTopic = OptionBuilder.withArgName("rootTopic").hasArg()
                .withDescription("Mqtt root topic").create("t");
        options.addOption(rootTopic);
        Option broker = OptionBuilder.withArgName("broker").hasArg()
                .withDescription("Mqtt broker").create("u");
        options.addOption(broker);
        return options;
    }

    private static void getHelp() {
        System.out.println(progName + " verion " + progVersion);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(progName, getOptions());
    }

}
