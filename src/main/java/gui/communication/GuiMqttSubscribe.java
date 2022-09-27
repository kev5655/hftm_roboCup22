package gui.communication;

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
 * @description Abonnieren des MQTT-Brockers, welcher die Kommunikation zwischen dem
 *              Java Programm und dem Node-Red sicherstellt.
 *
 *              Diese Klasse darf jeweils nur einmal aufgerufen werden, weshalb
 *              diese mit dem Singleton-Pattern realisiert wurde. Zudem muss die Klasse
 *              beim starten des Programms aufgerufen werden, bevor die Challenge startet.
 *              (Aufruf via wie folgt "MqttSubscribe.getInstance")
 *
 *              Die run Methode wird direkt von RCL MainGui aufgerufen.
 *
 *              Hier werden mehrere verschiedene Topics subschriebed.
 */
public class GuiMqttSubscribe implements MqttCallback {

    //Singleton Pattern
    private static final GuiMqttSubscribe MQTT_SUBSCRIBER_INSTANCE = new GuiMqttSubscribe();
    public static GuiMqttSubscribe getInstance() {
        return MQTT_SUBSCRIBER_INSTANCE;
    }

    MqttClient client = null;

    //Broker adresse = "tcp://172.26.107.212:1883"
    final String BROKER = "tcp://" + Data.getMQTT_BROKER_IP() + ":1883";

    //Quality of Service  Level (Level 1 "at least once")
    final int QOS = 1;

    // Alle Topic auf die Subscribe werden
    private static final String TOPIC_ROUTE_ROBO_1 = "visu/robo1/route";
    private static final String TOPIC_ROUTE_ROBO_2 = "visu/robo2/route";
    private static final String TOPIC_ROUTE_ROBO_3 = "visu/robo3/route";
    private static final String TOPIC_OBSTACLE = "visu/obstacles";
    private static final String TOPIC_STATION = "visu/stations";
    private static final String TOPIC_LOGS = "visu/logs";
    private static final String TOPIC_ORDERS = "visu/orders";


    private static final List<String> TOPIC_LIST = new LinkedList<>();
    static{
        TOPIC_LIST.add(TOPIC_ROUTE_ROBO_1);
        TOPIC_LIST.add(TOPIC_ROUTE_ROBO_2);
        TOPIC_LIST.add(TOPIC_ROUTE_ROBO_3);
        TOPIC_LIST.add(TOPIC_OBSTACLE);
        TOPIC_LIST.add(TOPIC_STATION);
        TOPIC_LIST.add(TOPIC_LOGS);
        TOPIC_LIST.add(TOPIC_ORDERS);
    }

    public void start() {
        // client Instanziieren
        try {
            client = new MqttClient(BROKER, String.valueOf(System.nanoTime()), new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // MqttMsgHandler wird gestartet
        //Thread mqttThread = new Thread(GuiMqttMsgHandler.getInstance(),"mqttHandler");
        //mqttThread.start();
        //Log.incomingRefboxMsg.info("MqttMsgHandler started");


        // Setzt das Callback für die ankommenden Nachrichten
        client.setCallback(this);
        // Setzt die Connection Optionen
        MqttConnectOptions mqOptions = new MqttConnectOptions();
        mqOptions.setCleanSession(true);

        try {
            client.connect(mqOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        subscribeOnTopicList();

    }

    /**
     * Subscribed auf alle Topics
     */
    private void subscribeOnTopicList() {
        try {
            for(String TOPIC : GuiMqttSubscribe.TOPIC_LIST) {
                client.subscribe(TOPIC);
                //Log.incomingMqttMsg.info("Topic Subscribed: " + TOPIC);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode, falls die Verbindung unterbrochen wurde.
     * @param throwable Error / Exeption meldung, falls Fehler auftreten
     */
    @Override
    public void connectionLost(Throwable throwable) {
        //Log.incomingMqttMsg.error("Verbindung zu MqttBroker verloren: " + throwable.toString());
        Log.logger.error("Verbindung zu MqttBroker verloren: " + throwable.toString());
    }

    /**
     * Aufruf der Methode in Evaluate Msg, falls eine neue Msg eintrifft.
     * @param topic Das betreffende Topic z.B. Gripping.
     * @param msg Die betreffende Msg z.B GrippingIsDone.
     * @throws Exception Falls Programm nicht durchgeführt werden konnte.
     */
    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        JsonNode json;
        ObjectMapper mapper = new ObjectMapper();
        GuiMqttMsgHandler handler = GuiMqttMsgHandler.getInstance();

        json = mapper.readTree(msg.toString());
        handler.addQueueEvent(json);
        //Log.logVisu.info("Received Msg: " + msg.toString() + " on Topic: " + topic);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}


