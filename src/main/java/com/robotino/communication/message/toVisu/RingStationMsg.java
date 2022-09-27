package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;

import java.util.LinkedList;
import java.util.List;

/**
 * Wird noch nicht verwendet
 */
public class RingStationMsg {

    final List<String []> ringStation = new LinkedList<>();
    private static final int MSG_TYPE = 107;

    public RingStationMsg(String name, String color1, String prize1, String color2, String prize2, String payed){
        ringStation.add(new String[]{name, color1, prize1, color2, prize2, payed});

        createAndSendMsg();
    }

    private void createAndSendMsg() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        List<ObjectNode> listOfNode = new LinkedList<>();

        for (String[] s : ringStation) {
            ObjectNode jsonTemp = objectMapper.createObjectNode();
            jsonTemp.put("Name", s[0]);
            jsonTemp.put("Color1", s[1]);
            jsonTemp.put("Prize1", s[2]);
            jsonTemp.put("Color2", s[3]);
            jsonTemp.put("Prize2", s[4]);
            jsonTemp.put("Payed", s[5]);
            listOfNode.add(jsonTemp);
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.addAll(listOfNode);
        json.put("msgType", MSG_TYPE);
        json.put("value", arrayNode);

        try {
            MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
            mqttPublish.createAndSendMsg(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
