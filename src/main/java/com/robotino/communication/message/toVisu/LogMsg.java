package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;

public class LogMsg {
    private final String log;
    private static final int MSG_TYPE = 106;

    public LogMsg(String log){
        this.log = log;
        createAndSendMsg();
    }

    private void createAndSendMsg() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("msgType", MSG_TYPE);
        json.put("value", log);

        MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
        mqttPublish.createAndSendMsg(json);
    }
}
