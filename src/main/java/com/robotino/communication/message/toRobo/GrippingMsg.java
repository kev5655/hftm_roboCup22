package com.robotino.communication.message.toRobo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublish;
import com.robotino.helperClass.Log;
import com.robotino.robo.Robo;

/**
 * Ist für das Versenden von Gripping Commands
 */
public class GrippingMsg {

    private int msgType;
    private final boolean grippingCommand;
    private final Robo robo;

    public GrippingMsg(Robo robo, MSG type, boolean grippingCommand) {
        this.robo = robo;
        this.grippingCommand = grippingCommand;
        switch (type) {
            case INPUT_GRIPPING -> this.msgType = 10;
            case OUTPUT_GRIPPING -> this.msgType = 11;
            case SLIDE_GRIPPING -> this.msgType = 12;
            case FROM_SHELF_TO_PLACE_ONE -> this.msgType = 13;
            case FROM_SHELF_TO_PLACE_TWO -> this.msgType = 14;
            case FROM_SHELF_TO_PLACE_THREE -> this.msgType = 15;
        }
        createAndSendMsg();
    }

    private void createAndSendMsg() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put("msgType", msgType);
        json.put("roboNr", robo.getNumber());
        json.put("startGripping", grippingCommand);

        Log.publishedMqttMsg.info("Gripping Nachricht wurde kreiert: " + json);
        MqttPublish mqttPublish = MqttPublish.getInstance();
        mqttPublish.createAndSendMsg(json);
    }

    public int getMsgType() {
        return msgType;
    }

    public enum MSG {
        INPUT_GRIPPING,
        OUTPUT_GRIPPING,
        SLIDE_GRIPPING,
        FROM_SHELF_TO_PLACE_ONE,
        FROM_SHELF_TO_PLACE_TWO,
        FROM_SHELF_TO_PLACE_THREE
    }
}
