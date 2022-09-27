package TestDriveController;

import com.robotino.communication.mqtt.MqttMsgHandler;
import com.robotino.communication.mqtt.MqttSubscribe;
import com.robotino.drive.DriveController;
import com.robotino.eventBus.*;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TestDriveController implements Subscriber {

    final Robo robo;
    final Queue<Coordinate> route = new LinkedList<>();

    public TestDriveController() {
        System.out.println("Start TEST-DRIVE-CONTROLLER");
        MqttSubscribe.getInstance();
        Thread mqttThread = new Thread(MqttMsgHandler.getInstance(),"mqttHandler");
        mqttThread.start();
        FinishDriveBus.getInstance().subscribe(this);


        List<Station> stations = getStations();
        MachineInfoEvent machineInfoEvent = new MachineInfoEvent(stations);
        MachineInfoBus.getInstance().publish(machineInfoEvent);

        robo = new Robo(1, Data.getROBOTS_START_FIELDS().get(0));
        route.add(robo.getStartCoordinate());
        route.add(new Coordinate(-2.5, 2.5, 0)); //new Field("M_Z11").getCoordinate()
        //route.add(new Coordinate(-2.5, 2.5, 0)); //new Field("M_Z52").getCoordinate()
        //route.add(new Coordinate(-2.5, 3.5, 225)); //new Field("M_Z34").getCoordinate()
        route.add(robo.getStartCoordinate());
        //new DriveController(robo, robo.getStartCoordinate(), new Coordinate(-0.5, 0.5, 90));
        new DriveController(robo, route.poll(), route.peek());

    }

    public List<Station> getStations(){
        //Station BS1 = new Station("CS1", "RS","idle", "CYAN", "M_Z22", 0);
        //Station BS2 = new Station("CS1", "RS","idle", "CYAN", "M_Z32", 0);
        //Station BS3 = new Station("CS1", "RS","idle", "CYAN", "M_Z42", 0);

        //Station BS4 = new Station("BS1", "M_Z42", "idel", "type", 45);
        //Station BS2 = new Station("BS1", "M_Z77", "idel", "type", 270);
        //Station BS3 = new Station("BS1", "M_Z54", "idel", "type", 135);
        //Station BS4 = new Station("BS1", "M_Z36", "idel", "type", 225);
        //Station BS5 = new Station("BS1", "M_Z21", "idel", "type", 0);
        //Station BS6 = new Station("BS1", "M_Z28", "idel", "type", 0);
        //Station BS7 = new Station("BS1", "M_Z15", "idel", "type", 90);
        //Station BS8 = new Station("BS1", "C_Z72", "idel", "type", 135);
        //Station BS9 = new Station("BS1", "C_Z77", "idel", "type", 90);
        //Station BS10 = new Station("BS1", "C_Z54", "idel", "type", 45);
        //Station BS11 = new Station("BS1", "C_Z36", "idel", "type", 315);
        //Station BS12 = new Station("BS1", "C_Z21", "idel", "type", 0);
        //Station BS13 = new Station("BS1", "C_Z28", "idel", "type", 180);
        //Station BS14 = new Station("BS1", "C_Z15", "idel", "type", 90);

        List<Station> stations = new LinkedList<>();

        //stations.add(BS1);
        //stations.add(BS2);
        //stations.add(BS3);
        //stations.add(BS4);
        //stations.add(BS5);
        //stations.add(BS6);
        //stations.add(BS7);
        //stations.add(BS8);
        //stations.add(BS9);
        //stations.add(BS10);
        //stations.add(BS11);
        //stations.add(BS12);
        //stations.add(BS13);
        //stations.add(BS14);
        return stations;
    }


    public static void main(String[] args) { new TestDriveController();

    }

    @Override
    public void onReceive(Event event) {
        //new DriveController(robo, robo.getCurrentCoordinate(), robo.getStartCoordinate());
        try {
            Thread.sleep(5000);
            if(!route.isEmpty()){
                new DriveController(robo, route.poll(), route.peek());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
