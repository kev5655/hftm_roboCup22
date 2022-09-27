package Mqtt.Mqtt;

import gui.communication.GuiMqttMsgHandler;
import gui.communication.GuiMqttSubscribe;


public class TestMqtt {

    public TestMqtt() {
        new GuiMqttSubscribe().start();
        Thread mqttThread = new Thread(GuiMqttMsgHandler.getInstance(),"guiMqttHandler");
        mqttThread.start();
    }

    public static void main(String[] args) {
        new TestMqtt();
    }

}
