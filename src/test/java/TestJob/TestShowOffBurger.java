package TestJob;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.communication.mqtt.MqttSubscribe;
import com.robotino.game.jobs.ShowOffBurger;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

import java.io.IOException;

public class TestShowOffBurger {

    public TestShowOffBurger(){
        try {
            Data.loadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MqttSubscribe.getInstance();



        Robo robo = new Robo(2, Data.getROBOTS_START_FIELDS().get(1));
        Station station = new Station("CapStation", "CS1",
                MachineClientUtils.MachineState.IDLE, "CYAN",
                "M_Z32", 0);
        //station.setStationField("M_Z32");
        //station.setA(0);
//
        //MachineInfoEvent event = new MachineInfoEvent(List.of(station));
        //MachineInfoBus.getInstance().publish(event);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new ShowOffBurger(robo, station).execute();
    }

    public static void main(String[] args) {
        new TestShowOffBurger();
    }
}
