package TestGripsProbobufLib;

import com.grips.refbox.*;
import com.robotino.helperClass.Data;
import org.robocup_logistics.llsf_msgs.GameStateProtos;
import org.robocup_logistics.llsf_msgs.MachineInfoProtos;
import org.robocup_logistics.llsf_msgs.OrderInfoProtos;

import java.io.IOException;
import java.util.function.Consumer;

public class TestProtobuf {

    public static final int SEND_INTERVAL_IN_MS = 1000;
    public final RefboxClient client;


    public TestProtobuf() throws InterruptedException {
        try {
            Data.loadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

            //Konfiguration Public Peer:
            PeerConfig publicPeer = new PeerConfig(4444, 4445);
            //IP Refbox setzen:
            RefboxConnectionConfig connectionConfig = new RefboxConnectionConfig();
            connectionConfig.setIp("172.26.107.213"); //172.26.108.62 Refbox Taric
            connectionConfig.setPublicPeer(publicPeer);

            //Private Peers Definieren:
            PeerConfig magentaPeer = new PeerConfig(4442,4447);
            connectionConfig.setMagentaPeer(magentaPeer);
            PeerConfig cayenPeer = new PeerConfig(4441, 4446);
            connectionConfig.setCyanPeer(cayenPeer);

            //Team Konfiguration
        TeamConfig teamConfig = new TeamConfig("randomkey", "MAGENTA", "Solidus"); //CYAN, //randomkey2

            // Handler Instanzieren
            RefboxHandler privateHandler = new RefboxHandler();
            RefboxHandler publicHandler = new RefboxHandler();

            // Client Instanzieren
            client = new RefboxClient(connectionConfig,
                    teamConfig,
                    privateHandler,
                    publicHandler,
                    SEND_INTERVAL_IN_MS);
            //Server Starten
            client.startServer();

        //client.sendPrepareRS(MachineClientUtils.Machine.RS2, MachineClientUtils.RingColor.RING_GREEN);
        //Optional<MachineClientUtils.MachineState> macheState = client.getStateForMachine(MachineClientUtils.Machine.RS2);
        //client.updateMachineStates(MachineInfoProtos.MachineInfo.newBuilder().build());

        //TeamConfig teamConfig = new TeamConfig("randomkey", "MAGENTA", "Carologistics");

        //rbcm = new RefBoxConnectionManager(connectionConfig,
        //teamConfig,
        //privateHandler,
        //publicHandler);

        //Consumer<BeaconSignalProtos.BeaconSignal> BeaconInfo = beaconInfoOut -> System.out.println("Beacon:" + beaconInfoOut);
        //publicHandler.setBeaconSignalCallback(BeaconInfo);

       //Consumer<ExplorationInfoProtos.ExplorationInfo> ExploInfo = ExploInfoOut -> System.out.println("Explo Public");
       //publicHandler.setExplorationInfoCallback(ExploInfo);

        //Consumer<RobotInfoProtos.RobotInfo> RoboInfo = RoboInfoOut -> System.out.println("Robo" + RoboInfoOut);
        //publicHandler.setRobotInfoCallback(RoboInfo);

        Consumer<GameStateProtos.GameState> GameState = GameStateOut -> System.out.println("GameState:" + GameStateOut);
        publicHandler.setGameStateCallback(GameState);

        Consumer<MachineInfoProtos.MachineInfo> MachInfo = machInfoOut -> {
            System.out.println(machInfoOut);
        //    client.updateMachineStates(machInfoOut);
        };
        privateHandler.setMachineInfoCallback(MachInfo);

        Consumer<OrderInfoProtos.OrderInfo> OrderInfo = ordIfoOut -> System.out.println("Order: " + ordIfoOut);
        publicHandler.setOrderInfoCallback(OrderInfo);
/*
       Consumer<RingInfoProtos.RingInfo> RingInfo = RingInfoOut -> System.out.println("Ring:"+RingInfoOut);
        privateHandler.setRingInfoCallback(RingInfo);

        Consumer<NavigationChallengeProtos.NavigationRoutes> RoutInfo = RoutInfoOut -> System.out.println("Route: " + RoutInfoOut);
        privateHandler.setNavigationRoutesCallback(RoutInfo);



        //Consumer<MachineReportProtos.MachineReportInfo> MachReport = MachReportOut -> System.out.println(MachReportOut);
        //privateHandler.setMachineReportInfoCallback(MachReport);


        //Consumer<VersionProtos.VersionInfo> VersionInfo = VersionInfoOut -> System.out.println(VersionInfoOut);
        //publicHandler.setVersionInfoCallback(VersionInfo);

*/
        while (true) {
            client.sendBeaconSignal(3, "testConnection", 1.5f, 1.5f, 90.0f);

        //    /*client.sendPrepareBS(MachineClientUtils.MachineSide.Input, MachineClientUtils.BaseColor.BASE_SILVER);
             //client.sendPrepareCS(MachineClientUtils.Machine.CS2, MachineClientUtils.CSOp.RETRIEVE_CAP);
        //    client.sendPrepareDS(2,2); //Gate: 1-3
        //    client.sendPrepareRS(MachineClientUtils.Machine.RS2, MachineClientUtils.RingColor.RING_ORANGE);
        //    client.sendPrepareSS(MachineClientUtils.Machine.SS, 2,4); //shelf: 0-5, slot: 0-7 */
//
          Thread.sleep(3000);
        }



    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new TestProtobuf();
    }
}
