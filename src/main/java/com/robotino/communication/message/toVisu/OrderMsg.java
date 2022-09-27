package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;
import com.robotino.game.Order;

import java.util.LinkedList;
import java.util.List;

/**
 * Wird noch nicht verwendet
 */
public class OrderMsg{

    public static final int MSG_TYPE = 107;
    final List<String []> orderGui = new LinkedList<>();

    public OrderMsg (Order order){
        String type = String.valueOf(order.getType());
        switch (type) {
            case "C0" -> orderGui.add(new String[]{
                    String.valueOf(order.getId()),
                    String.valueOf(order.getType()),
                    String.valueOf(order.getBaseColor()),
                    String.valueOf(order.getCapColor())});
            case "C1" -> orderGui.add(new String[]{
                    String.valueOf(order.getId()),
                    String.valueOf(order.getType()),
                    String.valueOf(order.getBaseColor()),
                    String.valueOf(order.getCapColor()),
                    String.valueOf(order.getRingColors().get(0))});
            case "C2" -> orderGui.add(new String[]{
                    String.valueOf(order.getId()),
                    String.valueOf(order.getType()),
                    String.valueOf(order.getBaseColor()),
                    String.valueOf(order.getCapColor()),
                    String.valueOf(order.getRingColors().get(0)),
                    String.valueOf(order.getRingColors().get(1))});
            case "C3" -> orderGui.add(new String[]{
                    String.valueOf(order.getId()),
                    String.valueOf(order.getType()),
                    String.valueOf(order.getBaseColor()),
                    String.valueOf(order.getCapColor()),
                    String.valueOf(order.getRingColors().get(0)),
                    String.valueOf(order.getRingColors().get(1)),
                    String.valueOf(order.getRingColors().get(2)),});
        }
        createAndSendMsg();
    }

    public void createAndSendMsg(){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        List<ObjectNode> listOfNode = new LinkedList<>();

        for (String [] s : orderGui) {
            String type = s[0].replace("\"", "");
            ObjectNode jsonTemp = objectMapper.createObjectNode();

            jsonTemp.put("Id", s[0]);
            jsonTemp.put("Type", s[1]);
            jsonTemp.put("BaseColor", s[2]);
            jsonTemp.put("CapColor", s[3]);

            switch (type) {
                case "C1" -> {
                    jsonTemp.put("RingColor1", s[4]);
                    jsonTemp.put("RingColor2", "-");
                    jsonTemp.put("RingColor3", "-");
                }
                case "C2" -> {
                    jsonTemp.put("RingColor1", s[4]);
                    jsonTemp.put("RingColor2", s[5]);
                    jsonTemp.put("RingColor3", "-");
                }
                case "C3" -> {
                    jsonTemp.put("RingColor1", s[4]);
                    jsonTemp.put("RingColor2", s[5]);
                    jsonTemp.put("RingColor3", s[6]);
                }
            }
            listOfNode.add(jsonTemp);
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.addAll(listOfNode);
        json.put("msgType", MSG_TYPE);
        json.put("value", arrayNode);


        MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
        mqttPublish.createAndSendMsg(json);
    }
}