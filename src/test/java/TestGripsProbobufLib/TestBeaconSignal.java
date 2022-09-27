package TestGripsProbobufLib;

import com.robotino.helperClass.Data;
import com.robotino.robo.Robo;

import java.io.IOException;

public class TestBeaconSignal {

    public TestBeaconSignal() throws InterruptedException, IOException {
        //MqttSubscribe.getInstance();
        Data.loadFile();
        Robo r1 = new Robo(1, Data.getROBOTS_START_FIELDS().get(0));
        //Robo r2 = new Robo(1, Data.getROBOS_START_FILDS().get(1));
        //Robo r3 = new Robo(1, Data.getROBOS_START_FILDS().get(1));


        //BeaconSignal.getInstance().startSendingBeaconSignal(r1);


    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new TestBeaconSignal();
    }
}
