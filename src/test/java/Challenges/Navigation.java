package Challenges;

import com.robotino.communication.mqtt.MqttMsgHandler;
import com.robotino.communication.mqtt.MqttSubscribe;
import com.robotino.communication.refbox.RefboxConnection;
import com.robotino.rcll.Controller;


public class Navigation {
    public Navigation(){
        MqttSubscribe.getInstance();
        Thread mqttThread = new Thread(MqttMsgHandler.getInstance(),"mqttHandler");
        mqttThread.start();
        RefboxConnection refboxConnection = RefboxConnection.getInstance();

        Controller controller = new Controller();
    }

    public static void main(String[] args) {
        new Navigation();
    }
}
