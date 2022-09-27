package com.robotino.communication.message.toRobo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.communication.mqtt.MqttPublish;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

/**
 * @author Kevin Zahn
 * @description Erzeugt eine Msg welche an das RobotinoView geschickt wird, per MQTT.
 *              Bei der Msg handelt es sich um die Koordinate welche der Robotino anfahren soll.
 *              Die Koordinate kommt von der Klasse Coordinate über die entsprechenden getter.
 *              Die Msg wird über die Klasse MattPublish.send in das Node-Red verschickt.
 */

public class DriveMsg {

    private static final int MSG_TYPE = 0;
    private final int roboNr;
    private final int speed;
    final Coordinate driveToCord;

    /**
     * Constructor, welcher direkt die Methode driveCommand ausführt.
     * @param robo mit der Roboter nummer
     * @param roboCord Soll-Koordinate welche angefahren werden soll (x, y, a) (Double).
     */
    public DriveMsg(Robo robo, Coordinate roboCord, int speed){
        this.roboNr = robo.getNumber();
        this.speed = speed;
        this.driveToCord = roboCord;
        createAndSendMsg();
    }

    /**
     * Verpackt die Soll-Koordinate in eine Json Datei welche per MQTT verschickt wird.
     * Msg Type für dieses Topic ist immer 0.
     */
    private void createAndSendMsg() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put("xPos", (int) driveToCord.getX());
        json.put("yPos", (int) driveToCord.getY());
        json.put("angle", (int) driveToCord.getA());
        json.put("msgType", MSG_TYPE);
        json.put("roboNr", roboNr);
        json.put("speed", speed);

        Log.publishedMqttMsg.info("Drive Nachricht wurde kreiert: " + json);
        MqttPublish mqttPublish = MqttPublish.getInstance();
        mqttPublish.createAndSendMsg(json);
    }
}