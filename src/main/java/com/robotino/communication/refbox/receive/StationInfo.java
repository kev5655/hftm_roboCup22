package com.robotino.communication.refbox.receive;

import com.grips.model.teamserver.MachineClientUtils;
import com.grips.refbox.RefboxClient;
import com.grips.refbox.RefboxHandler;
import com.robotino.communication.message.toVisu.StationMsg;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;

import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.*;
import org.robocup_logistics.llsf_msgs.MachineInfoProtos;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

// ToDo Station nicht über ein ein Bus übergeben sondern direkt in das Singleton der jeweiligen Stationen schreiben

/**
 * Wertet den empfangenen Stationen von der Refbox aus
 */
public class StationInfo {

    Station station;
    final RefboxHandler handler;
    final RefboxClient client;
    final OrderInfo orderInfo;
    private boolean onceTime = true;

    public StationInfo(RefboxHandler handler, RefboxClient client, OrderInfo orderInfo) {
        this.handler = handler;
        this.client = client;
        this.orderInfo = orderInfo;
        receiveMsg();
    }

    private void receiveMsg() {
        Consumer<MachineInfoProtos.MachineInfo> MachineInfo =
                machInfoOut ->{
                    Log.machine.info(machInfoOut);
                    stationHandler(machInfoOut.getMachinesCount(), machInfoOut.getMachinesList());
                    if(onceTime){
                        onceTime = false;
                        orderInfo.startReceiveMsg(); // Starte dem Empfange der MachineInfo meldungen
                    }
                    client.updateMachineStates(machInfoOut);
        };
        handler.setMachineInfoCallback(MachineInfo);
    }

    private void stationHandler(int machineCount, List<MachineInfoProtos.Machine> machinesList){
        Thread.currentThread().setName("Incoming-Refbox-Station");
        List<Station> stations = new LinkedList<>();
        for (int i = 0; i < machineCount; i++) {
            station = null;

            String name = machinesList.get(i).getName();
            String type = machinesList.get(i).getType();
            String stateStr = machinesList.get(i).getState();
            MachineClientUtils.MachineState state = MachineClientUtils.parseMachineState(stateStr);
            String teamColor = machinesList.get(i).getTeamColor().toString();
            String zone = machinesList.get(i).getZone().toString();
            int a = machinesList.get(i).getRotation();

            // ToDo kann vereinfacht werden
            switch (name){
                case "C-RS1", "M-RS1" -> {
                    MachineClientUtils.RingColor ringColor1 =
                            whichRingColor(machinesList.get(i).getRingColors(0).toString());
                    MachineClientUtils.RingColor ringColor2 =
                            whichRingColor(machinesList.get(i).getRingColors(1).toString());

                    station = RingStation1.getInstance();
                    ((RingStation1)station).setRingColor1(ringColor1);
                    ((RingStation1)station).setRingColor2(ringColor2);
                }

                case "C-RS2", "M-RS2" -> {
                    MachineClientUtils.RingColor ringColor1 =
                            whichRingColor(machinesList.get(i).getRingColors(0).toString());
                    MachineClientUtils.RingColor ringColor2 =
                            whichRingColor(machinesList.get(i).getRingColors(1).toString());

                    station = RingStation2.getInstance();
                    ((RingStation2)station).setRingColor1(ringColor1);
                    ((RingStation2)station).setRingColor2(ringColor2);
                }

                case "C-CS1", "M-CS1" -> station = CapStation1.getInstance();
                case "C-CS2", "M-CS2" -> station = CapStation2.getInstance();
                case "C-BS", "M-BS" -> station = BaseStation.getInstance();
                case "C-DS", "M-DS" -> station = DeliveryStation.getInstance();
                case "C-SS", "M-SS" -> station = new StorageStation(1);

                default -> throw new IllegalStateException("Unexpected value: " + type);
            }

            station.
                    setName(name).
                    setType(type).
                    setState(state).
                    setTeamColor(teamColor).
                    setStationField(zone).
                    setA(a);

            stations.add(station);
        }

        new StationMsg(stations);
        MachineInfoEvent machineInfoEvent = new MachineInfoEvent(stations);
        MachineInfoBus.getInstance().publish(machineInfoEvent);
    }

    public MachineClientUtils.RingColor whichRingColor(String ringColor){
        switch (ringColor){
            case "RING_ORANGE" -> { return MachineClientUtils.RingColor.RING_ORANGE; }
            case "RING_BLUE" -> { return MachineClientUtils.RingColor.RING_BLUE; }
            case "RING_YELLOW" -> { return MachineClientUtils.RingColor.RING_YELLOW; }
            case "RING_GREEN" -> { return MachineClientUtils.RingColor.RING_GREEN; }
            default -> throw new IllegalArgumentException("Es gibt keine RingColor mit dem String: " + ringColor);
        }
    }
}
