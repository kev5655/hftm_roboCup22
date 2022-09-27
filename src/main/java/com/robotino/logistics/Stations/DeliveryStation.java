package com.robotino.logistics.Stations;

import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.communication.refbox.send.StationSignal;

/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 */
public class DeliveryStation extends Station implements Subscriber {

    public static final DeliveryStation DELIVER_STATION_INSTANCE = new DeliveryStation();
    public static DeliveryStation getInstance(){ return DELIVER_STATION_INSTANCE; }

    private DeliveryStation(){
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }

    public void prep(int gate, int id){
        StationSignal.getInstance().prepDS(gate, id);
    }

    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if (e.getStations().get(i) instanceof DeliveryStation deliveryStation) {

                setName(deliveryStation.getName());
                setType(deliveryStation.getName());
                setState(deliveryStation.getState());
                setTeamColor(deliveryStation.getTeamColor());
                setStationField(deliveryStation.getStationField().getZone());
                setA(deliveryStation.getA());
                Log.machine.info("Store Machine: " + this.toString());
                //MachineInfoBus.getInstance().unsubscribe(this);
            }
        }
    }

    @Override
    public String toString() {
        return "DeliveryStation{}" + ", " + super.toString();
    }
}
