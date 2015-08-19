/**
 * Xbmq - XBee / MQTT Gateway Copyright 2015 Andrew Bythell, <abythell@ieee.org>
 */
package com.angryelectron.xbmq;

import com.angryelectron.xbmq.listener.XbmqSampleReceiveListener;
import com.angryelectron.xbmq.listener.XbmqDataReceiveListener;
import com.angryelectron.xbmq.listener.XbmqMqttCallback;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
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
 * Main entry point for the application
 */
public class Main {

    private static final String progVersion = "1.0";
    private static final String progName = "xbmq";

    /**
     * @param args the command line arguments
     * @throws com.digi.xbee.api.exceptions.XBeeException
     * @throws org.eclipse.paho.client.mqttv3.MqttException
     */
    public static void main(String[] args) throws XBeeException, MqttException {

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

        XBeeDevice xbee = new XBeeDevice(port, Integer.parseInt(baud));        
        MqttAsyncClient mqtt = new MqttAsyncClient(broker, xbee.get64BitAddress().toString()); 
        final Xbmq xbmq = new Xbmq(xbee, mqtt, rootTopic);        

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
            t.subAt(),
            t.subData(),
            t.subDiscovery(),
            t.subIOUpdate(null)
        };
        int[] qos = {0, 0, 0, 0};
        
        mqtt.setCallback(new XbmqMqttCallback(xbmq));
        mqtt.subscribe(topics, qos);

        /**
         * Add shutdown hooks to stop logger on Ctrl-C.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
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
