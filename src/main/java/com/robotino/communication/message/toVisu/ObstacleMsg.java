package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;
import com.robotino.drive.Obstacle;

import java.util.LinkedList;
import java.util.List;

public class ObstacleMsg {

    final List<int []> coords = new LinkedList<>();
    final List<Integer> nameIdentifier = new LinkedList<>();

    public ObstacleMsg(List<Obstacle> obstacles) {
        for(Obstacle o : obstacles){
            coords.addAll(o.getNotFree());
            for (int i = 0; i < o.getNotFree().size(); i++) {
                nameIdentifier.add(o.getNameIdentifier());
            }
        }
        createAndSendMsg();
    }

    public void createAndSendMsg(){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        List<ObjectNode> listOfNode = new LinkedList<>();
        int counter = 0;

        for(int [] cord : coords){
            ObjectNode jsonTemp = objectMapper.createObjectNode();
            jsonTemp.put("x", cord[0]);
            jsonTemp.put("y", cord[1]);
            jsonTemp.put("nameIdentifier", nameIdentifier.get(counter));
            listOfNode.add(jsonTemp);
            counter++;
        }

        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.addAll(listOfNode);
        json.put("msgType", 104);
        json.put("value", arrayNode);

        MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
        mqttPublish.createAndSendMsg(json);
    }
}
