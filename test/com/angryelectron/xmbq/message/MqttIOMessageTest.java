/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xmbq.message;

import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class MqttIOMessageTest {

    private Xbmq xbmq;
    private final XbmqTopic topics = new XbmqTopic("rootTopic", "ABCDABCDABCDABCD");
    private final XBee64BitAddress device = new XBee64BitAddress("1234567812345678");

    public MqttIOMessageTest() {
    }

    @Before
    public void setUp() {
        xbmq = mock(Xbmq.class);
        when(xbmq.getTopics()).thenReturn(topics);
    }

    @Test
    public void testSend() throws Exception {                                
        MqttIOMessage message = new MqttIOMessage(xbmq);
        message.send(device, IOLine.DIO0_AD0, 1);

        ArgumentCaptor<MqttMessage> argument = ArgumentCaptor.forClass(MqttMessage.class);
        verify(xbmq).publishMqtt(
                eq(topics.pubIO(device.toString(), IOLine.DIO0_AD0.getName())),
                argument.capture());
        assertTrue("invalid message payload", 
                Arrays.equals("1".getBytes(), argument.getValue().getPayload()));
    }

}
