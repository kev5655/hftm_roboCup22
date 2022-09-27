package com.robotino.communication.message.toRobo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublish;
import com.robotino.helperClass.Log;
import com.robotino.robo.Robo;

/**
 * Sendet den Stopp-Befehl für den Roboter
 */
public class StopProcessMsg {

    private static final int MSG_TYPE = 1;
    private final Robo robo;
    private final boolean stopOrStart;

    public StopProcessMsg(Robo robo, boolean stopOrStart){
        this.robo = robo;
        this.stopOrStart = stopOrStart;
        createAndSendMsg();
    }

    private void createAndSendMsg(){

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put("msgType", MSG_TYPE);
        json.put("roboNr", robo.getNumber());
        json.put("stopprocess", stopOrStart);

        Log.publishedMqttMsg.info("Stop Nachricht wurde kreiert: " + json);
        MqttPublish mqttPublish = MqttPublish.getInstance();
        mqttPublish.createAndSendMsg(json);
    }
}
