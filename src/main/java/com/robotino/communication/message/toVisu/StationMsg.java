package com.robotino.communication.message.toVisu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublishForVisu;
import com.robotino.logistics.Station;

import java.util.LinkedList;
import java.util.List;

public class StationMsg{

    public static final int MSG_TYPE = 105;
    final List<String []> stationsGui = new LinkedList<>();

    public StationMsg (List<Station> stations){
        for(Station station : stations) {
            stationsGui.add(new String[]{station.getName(), station.getType(),
                    station.getState().toString(), station.getTeamColor(),
                    String.valueOf(station.getA()),});
            }
        createAndSendMsg();
    }

    public void createAndSendMsg(){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        List<ObjectNode> listOfNode = new LinkedList<>();

        for (String [] s : stationsGui) {
            ObjectNode jsonTemp = objectMapper.createObjectNode();
            jsonTemp.put("Name", s[0]);
            jsonTemp.put("Type", s[1]);
            jsonTemp.put("State", s[2]);
            jsonTemp.put("TeamColor", s[3]);
            jsonTemp.put("Rotation", s[4]);
            listOfNode.add(jsonTemp);
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.addAll(listOfNode);
        json.put("msgType", MSG_TYPE);
        json.put("value", arrayNode);

        try {
            MqttPublishForVisu mqttPublish = MqttPublishForVisu.getInstance();
            mqttPublish.createAndSendMsg(json);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}