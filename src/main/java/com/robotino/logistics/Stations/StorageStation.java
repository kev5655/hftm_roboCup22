package com.robotino.logistics.Stations;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.logistics.Station;
import com.robotino.communication.refbox.send.StationSignal;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Log;
import com.robotino.logistics.StatusStorageStations;

import java.util.List;

/**
 * Wird noch nicht verwendet
 *
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 *              Zusätzliche Informationen des Statuses, für jeden Slot der Storage-Station.
 *              Zusätzliche Funktionen für das Hinzufügen & Updaten eines Slots.
 *              Bzw. Melden der Refbox für die entsprechende Funktion.
 */
public class StorageStation extends Station implements Subscriber {

    private List<StatusStorageStations> shelfStatus;

    public StorageStation() {
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }
    public StorageStation(int i) {
        super();
    }

    /**
     * Hinzufügen eines neuen Statuses zur Liste.
     * Kann nur hinzugefügt werden, wenn dieser noch nicht in der Liste ist.
     * @param status Neuer Status.
     */
    public void addStatus(StatusStorageStations status) {
        if(!(shelfStatus.contains(status))){
            shelfStatus.add(status);
        }
    }

    /**
     * Updaten eines "alten" Statuses zu einem neuen.
     * Z.B., wenn ein Element aus dem Lager genommen wurde.
     * @param status updated Status.
     */
    public void updateShelfStatus(StatusStorageStations status){
        for (int i = 0; i < shelfStatus.size(); i++) {
            if((shelfStatus.get(i).getShelf() == status.getShelf())
            && (shelfStatus.get(i).getSlot() == status.getSlot())){
                shelfStatus.remove(i);
                shelfStatus.add(status);
            }
        }
    }

    /**
     * Überprüft ob ein Slot in der Storage-Station besetzt ist oder nicht.
     * @param shelf zum Prüfen.
     * @param slot zum Prüfen.
     * @return true wenn, besetzt, false wenn, frei.
     */
    public boolean isSlotUsed(int shelf, int slot){
        for (int i = 0; i < shelfStatus.size(); i++) {
            if((shelfStatus.get(i).getShelf() == shelf) && (shelfStatus.get(i).getSlot() == slot)){
                return true;
            }
        }
        return false;
    }

    public void prep(int shelf, int gate){
        StationSignal.getInstance().prepSS(MachineClientUtils.Machine.SS, shelf, gate);
    }

    /**********Getter**********/
    public List<StatusStorageStations>getShelfStatus(){
        return shelfStatus;
    }

    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if (e.getStations().get(i) instanceof StorageStation storageStation){
                setName(storageStation.getName());
                setType(storageStation.getName());
                setState(storageStation.getState());
                setTeamColor(storageStation.getTeamColor());
                setStationField(storageStation.getStationField().getZone());
                setA(storageStation.getA());
                Log.machine.info(this.toString());
                MachineInfoBus.getInstance().unsubscribe(this);
            }
        }
    }
}