package com.robotino.communication.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Zahn
 * @description Abonnieren des MQTT-Brokers, welcher die Kommunikation zwischen dem
 *              Java Programm und dem Node-Red sicherstellt.
 *
 *              Diese Klasse darf jeweils nur einmal aufgerufen werden, weshalb
 *              diese mit dem Singleton-Pattern realisiert wurde. Zudem muss die Klasse
 *              beim Starten des Programms aufgerufen werden, bevor die Challenge startet.
 *              (Aufruf via wie folgt "MqttSubscribe.getInstance")
 *
 *              Die run Methode wird direkt von RCL MainGui aufgerufen.
 *
 *              Hier werden mehrere verschiedene Topics subscribed.
 */
public class MqttSubscribe implements MqttCallback {

    //Singleton Pattern
    private static final MqttSubscribe MQTT_SUBSCRIBER_INSTANCE = new MqttSubscribe();
    public static MqttSubscribe getInstance() {
        return MQTT_SUBSCRIBER_INSTANCE;
    }

    MqttClient client = null;


    // Alle Topic auf die Subscribe werden
    private final String TOPIC_AR_TAG_ROBO_1 = "robo1/fromRobotino/arTag";
    private final String TOPIC_WHERE_IS_ROBO_1 = "robo1/fromRobotino/positionRobo";
    private final String TOPIC_GRIPPING_STATUS_ROBO_1 = "robo1/fromRobotino/grippingStatus";
    private final String TOPIC_AR_TAG_ROBO_2 = "robo2/fromRobotino/arTag";
    private final String TOPIC_WHERE_IS_ROBO_2 = "robo2/fromRobotino/positionRobo";
    private final String TOPIC_GRIPPING_STATUS_ROBO_2 = "robo2/fromRobotino/grippingStatus";
    private final String TOPIC_AR_TAG_ROBO_3 = "robo3/fromRobotino/arTag";
    private final String TOPIC_WHERE_IS_ROBO_3 = "robo3/fromRobotino/positionRobo";
    private final String TOPIC_GRIPPING_STATUS_ROBO_3 = "robo3/fromRobotino/grippingStatus";

    private final List<String> TOPIC_LIST = new LinkedList<>();


    private MqttSubscribe() {
        TOPIC_LIST.add(TOPIC_AR_TAG_ROBO_1);
        TOPIC_LIST.add(TOPIC_WHERE_IS_ROBO_1);
        TOPIC_LIST.add(TOPIC_GRIPPING_STATUS_ROBO_1);
        TOPIC_LIST.add(TOPIC_AR_TAG_ROBO_2);
        TOPIC_LIST.add(TOPIC_WHERE_IS_ROBO_2);
        TOPIC_LIST.add(TOPIC_GRIPPING_STATUS_ROBO_2);
        TOPIC_LIST.add(TOPIC_AR_TAG_ROBO_3);
        TOPIC_LIST.add(TOPIC_WHERE_IS_ROBO_3);
        TOPIC_LIST.add(TOPIC_GRIPPING_STATUS_ROBO_3);

        String s = Data.getMQTT_BROKER_IP();
        String BROKER =  "tcp://" + s + ":1883";

        try {
            client = new MqttClient(BROKER, String.valueOf(System.nanoTime()), new MemoryPersistence());
            Log.incomingMqttMsg.info("Mqtt Client Instanziiert");
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            Log.logger.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            e.printStackTrace();
        }

        // MqttMsgHandler wird gestartet
        Thread mqttThread = new Thread(MqttMsgHandler.getInstance(),"mqttHandler");
        mqttThread.start();
        Log.incomingMqttMsg.info("MqttMsgHandler started");


        // Setzt der Callback für die ankommenden Nachrichten
        client.setCallback(this);
        // Setzt die Connection Optionen
        MqttConnectOptions mqOptions = new MqttConnectOptions();
        mqOptions.setCleanSession(true);

        try {
            client.connect(mqOptions);
            Log.incomingMqttMsg.info("Verbunden mit dem Broker über die IP: " + Data.getMQTT_BROKER_IP());
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            Log.logger.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            e.printStackTrace();
        }

        subscribeOnTopicList(TOPIC_LIST);

    }

    /**
     * Subscribed auf alle Topics
     * @param TOPIC_LIST Liste der Topics
     */
    private void subscribeOnTopicList(List<String> TOPIC_LIST) {
        try {
            for(String TOPIC : TOPIC_LIST) {
                client.subscribe(TOPIC);
                Log.incomingMqttMsg.info("Topic Subscribed: " + TOPIC);
            }
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Es konnte kein Verbindung auf ein Topic erstellt werden: " + e);
            Log.logger.error("Es konnte kein Verbindung auf ein Topic erstellt werden: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Methode, falls die Verbindung unterbrochen wurde.
     * @param throwable Error / Exception meldung, falls Fehler auftreten
     */
    @Override
    public void connectionLost(Throwable throwable) {
        Log.incomingMqttMsg.error("Verbindung zu MqttBroker verloren: " + throwable.toString());
        Log.logger.error("Verbindung zu MqttBroker verloren: " + throwable);
    }

    /**
     * Aufruf der Methode in Evaluate Msg, falls eine neue Msg eintrifft.
     * @param topic Das betreffende Topic z.B. Gripping.
     * @param msg Die betreffende Msg z. B. GrippingIsDone.
     * @throws Exception Falls Programm nicht durchgeführt werden konnte.
     */
    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        JsonNode json;
        ObjectMapper mapper = new ObjectMapper();
        MqttMsgHandler handler = MqttMsgHandler.getInstance();

        json = mapper.readTree(msg.toString());
        handler.addQueueEvent(json);
        Log.incomingMqttMsg.info("Received Msg: " + msg + " on Topic: " + topic);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}


