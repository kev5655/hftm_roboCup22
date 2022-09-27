package com.robotino.logistics.Stations;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.logistics.Station;
import com.robotino.communication.refbox.send.StationSignal;
import com.robotino.helperClass.Log;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;


/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 */
public class BaseStation extends Station implements Subscriber {

    public static final BaseStation BASE_STATION_INSTANCE = new BaseStation();
    public static BaseStation getInstance(){ return BASE_STATION_INSTANCE; }

    private BaseStation(){
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }


    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if(e.getStations().get(i) instanceof BaseStation baseStation){
                setName(baseStation.getName());
                setType(baseStation.getName());
                setState(baseStation.getState());
                setTeamColor(baseStation.getTeamColor());
                setStationField(baseStation.getStationField().getZone());
                setA(baseStation.getA());
                Log.machine.info("Store Machine: " + this.toString());
            }
        }
    }

    public void prep(MachineClientUtils.MachineSide side, MachineClientUtils.BaseColor baseColor){
        StationSignal.getInstance().prepBS(side, baseColor);
    }

    @Override
    public String toString() {
        return "BaseStation{}" + ", " + super.toString();
    }
}