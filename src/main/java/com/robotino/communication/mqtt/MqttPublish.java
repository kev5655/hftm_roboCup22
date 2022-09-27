package com.robotino.communication.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Zahn
 * @description Die Klasse MqttPublish dient dem Versenden von Msg an das Node-Red.
 *              Die Msg wird dabei als String im Json Format publiziert.
 *
 *              Diese Klasse darf jeweils nur einmal aufgerufen werden, weshalb
 *              diese mit dem Singleton-Pattern realisiert wurde.
 *              (Aufruf via wie folgt "MqttPublish.getInstance")
 */

public class MqttPublish {

    //Broker adresse = "tcp://172.26.107.212:1883" Refbox IP
    private static final String BROKER_IP = "tcp://" + Data.getMQTT_BROKER_IP() + ":1883";
    MqttConnectOptions connOpts;

    //Quality of Service  Level (Level 1 "at least once")
    private static final int QOS = 1;

    /**
     * Generiert alle Topics für alle drei Roboter
     */
    private static final Map<Integer, Map<Integer, String>> TOPIC_LIST_ROBO = new HashMap<>();
    static{
        for(int i = 1; i <= 3; i++){
            int I = i;
            TOPIC_LIST_ROBO.put(I, new HashMap<>(){{
                put(0, "robo" + I + "/fromJava/driveCommand");
                put(1, "robo" + I + "/fromJava/stopProcess");
                put(10, "robo" + I + "/fromJava/startGripping/Input");
                put(11, "robo" + I + "/fromJava/startGripping/Output");
                put(12, "robo" + I + "/fromJava/startGripping/Slide");
                put(13, "robo" + I + "/fromJava/startGripping/1toInput");
                put(14, "robo" + I + "/fromJava/startGripping/2toInput");
                put(15, "robo" + I + "/fromJava/startGripping/3toInput");
            }});
        }
    }

    private MqttClient client = null;

    // Singleton Pattern
    private static final MqttPublish MQTT_PUBLISH_INSTANCE = new MqttPublish();
    public static MqttPublish getInstance() {
        return MQTT_PUBLISH_INSTANCE;
    }

    private MqttPublish(){
        // client Instanziieren
        try {
            client = new MqttClient(BROKER_IP,
                    String.valueOf(System.nanoTime()),
                    new MemoryPersistence()); // So wird keine Datei erstellt
            Log.publishedMqttMsg.info("Mqtt Client instanziiert");
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            Log.logger.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            e.printStackTrace();
        }

        // connection Optionen werde festgelegt
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(1000);

        // connection zum Broker wird aufgebaut
        try {
            client.connect(connOpts);
            Log.publishedMqttMsg.info("Verbunden mit dem Broker über die IP: " + Data.getMQTT_BROKER_IP());
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            Log.logger.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            e.printStackTrace();
        }
    }


    /**
     * Erstellung eines Json-Objekts aus einem String.
     * Und anschliessendes zusammensetzen der Msg so wie dessen versenden.
     * @param jsonMsg JsonNode der Msg zum Versenden
     *                (eine Node für eine Liste mit Objekt Daten in jedem Node).
     */
    public void createAndSendMsg(ObjectNode jsonMsg){

        //Instanziierung eines Jackson Objekts und rücksetzen des Payloads,
        ObjectMapper mapper = new ObjectMapper();
        String payload = null;
        try {
            payload = mapper.writeValueAsString(jsonMsg);
        } catch (JsonProcessingException e) {
            Log.publishedMqttMsg.error("Die Nachricht konnte nicht in ein JSON-String umgewandelt werden: " + e);
            Log.logger.error("Die Nachricht konnte nicht in ein JSON-String umgewandelt werden: " + e);
            e.printStackTrace();
        }

        int msgType = jsonMsg.get("msgType").asInt();
        int roboNr = jsonMsg.get("roboNr").asInt();

        assert payload != null; // Beendet das Programm wen die Bedingung falsch ist
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(QOS);
        message.setRetained(true);

        MqttTopic topic = client.getTopic(TOPIC_LIST_ROBO.get(roboNr).get(msgType));

        try {
            topic.publish(message);
            Log.publishedMqttMsg.info("Nachricht wurde versendet: " + payload + " auf das Topic: " + TOPIC_LIST_ROBO.get(roboNr).get(msgType));
        } catch (MqttException e) {
            Log.publishedMqttMsg.error("Nachricht konnte nicht versendet werden: " + payload + " auf das Topic: " + TOPIC_LIST_ROBO.get(roboNr).get(msgType));
            Log.logger.error("Nachricht konnte nicht versendet werden: " + payload + " auf das Topic: " + TOPIC_LIST_ROBO.get(roboNr).get(msgType));
            e.printStackTrace();
        }
    }
}