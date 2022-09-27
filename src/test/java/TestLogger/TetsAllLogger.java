package TestLogger;

import com.robotino.helperClass.Log;

public class TetsAllLogger {

    public TetsAllLogger(){
        Log.logger.info("Test MainGui Looger");
        //Log.incomingMqttMsg.info("Test incomingMqttMsg Logger");
        //Log.publishedMqttMsg.info("Test publishedMqttMsg Logger");
        //Log.incomingRefboxMsg.info("Test incomingRefboxMsg Logger");
        //Log.publishedRefboxMsg.info("Test publishedRefboxMsg Logger");
        //Log.eventBus.info("Test eventbus Logger");
        //Log.game.info("Test game Logger");
        //Log.startedSystem.info("Test statedSystem Logger");
        //Log.driveSystem.info("Test driveSystem Logger");
        //Log.mqttMsgHandler.info("Test mqttMsgHandler");
        //Log.logVisu.info("Test visu Logger");
    }

    public static void main(String[] args) {
        new TetsAllLogger();
    }
}
