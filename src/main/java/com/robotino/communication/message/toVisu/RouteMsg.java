package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;
import com.robotino.drive.Path;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RouteMsg {

    private final int msgType;
    final List<double []> javaPath = new LinkedList<>();

    public RouteMsg(Robo robo, Path path){
        for(Coordinate cord : new ArrayList<>(path.getAStarPath())){
            javaPath.add(new double[]{cord.getX(), cord.getY()});
        }
        switch (robo.getNumber()) {
            case 1 -> msgType = 101;
            case 2 -> msgType = 102;
            case 3 -> msgType = 103;
            default -> throw new IllegalArgumentException("Invalid robot number: " + robo.getNumber());
        }
        createAndSendMsg();
    }


    private void createAndSendMsg(){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        List<ObjectNode> listOfNode = new LinkedList<>();
        for(double [] cord : javaPath){
            ObjectNode jsonTemp = objectMapper.createObjectNode();
            jsonTemp.put("x", cord[0]);
            jsonTemp.put("y", cord[1]);
            listOfNode.add(jsonTemp);
        }

        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.addAll(listOfNode);
        json.put("msgType", msgType);
        json.put("value", arrayNode);


        MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
        mqttPublish.createAndSendMsg(json);

    }
}
