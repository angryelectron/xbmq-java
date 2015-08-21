/*
 
 */
package com.angryelectron.xbmq;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Subscribe to MQTT topic and synchronously await for incoming messages.
 * Can be used during testing to confirm published messages.
 * @author abythell
 */
public class MqttSyncSubscribe {
    
    private final MqttAsyncClient mqtt;    
    private byte[] payload = null;
    private boolean haveResult = false;    
    
    private final MqttCallback callback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable thrwbl) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void messageArrived(String topic, MqttMessage mm) throws Exception {
            payload = mm.getPayload();
            haveResult = true;
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken imdt) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    
    /**
     * Constructor.
     * @param mqtt Established mqtt client.
     */
    public MqttSyncSubscribe(MqttAsyncClient mqtt) {
        this.mqtt = mqtt;                
    }
    
    /**
     * Subscribe and wait for incoming message.
     * @param topic Subscribe to this topic.
     * @param seconds Wait this long before timing out.
     * @return
     * @throws MqttException if topic subscription fails.
     * @throws TimeoutException if no message arrives in time.
     */
    public byte[] getPayload(String topic, int seconds) throws MqttException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable() {

            @Override
            public Void call() throws Exception {
                while (!haveResult){
                    Thread.sleep(500);
                }
                return null;
            }            
            
        });        
        mqtt.setCallback(callback);
        mqtt.subscribe(topic, 0).waitForCompletion();        
        try {
            future.get(seconds,TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException ex) {
            return null;
        }
        return payload;
        
    }
    
}
