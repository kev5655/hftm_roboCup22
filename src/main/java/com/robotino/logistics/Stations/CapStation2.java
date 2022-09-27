package com.robotino.logistics.Stations;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.communication.message.toRobo.GrippingMsg;
import com.robotino.communication.message.toVisu.LogMsg;
import com.robotino.logistics.Station;
import com.robotino.communication.refbox.send.StationSignal;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.game.Order;
import com.robotino.helperClass.Log;

/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 */
public class CapStation2 extends Station implements Subscriber {

    private final Order.CapColor capColor = Order.CapColor.Black;
    private static GrippingMsg.MSG shelfPosition = GrippingMsg.MSG.FROM_SHELF_TO_PLACE_TWO;
    private int capsInStock = 0;

    public static final CapStation2 CAP_STATION_2_INSTANCE = new CapStation2();
    public static CapStation2 getInstance(){ return CAP_STATION_2_INSTANCE; }

    private CapStation2(){
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }


    public Order.CapColor getCapColor(){
        return capColor;
    }

    public GrippingMsg.MSG getShelfPosition(){
        if(GrippingMsg.MSG.FROM_SHELF_TO_PLACE_ONE == shelfPosition){
            shelfPosition = GrippingMsg.MSG.FROM_SHELF_TO_PLACE_TWO;
        }else if(GrippingMsg.MSG.FROM_SHELF_TO_PLACE_TWO == shelfPosition){
            shelfPosition = GrippingMsg.MSG.FROM_SHELF_TO_PLACE_THREE;
        }else if(GrippingMsg.MSG.FROM_SHELF_TO_PLACE_THREE == shelfPosition){
            shelfPosition = GrippingMsg.MSG.FROM_SHELF_TO_PLACE_ONE;
        }
        return shelfPosition;
    }


    public boolean hasCapColor(Order.CapColor capColor){
        if(this.capColor == null) return false;
        return this.capColor == capColor;
    }

    public int getCapsInStock() {
        return capsInStock;
    }

    public void addCapInStock() {
        this.capsInStock++;
    }

    public void removeCapInStock() {
        this.capsInStock--;
    }

    public void prep(MachineClientUtils.CSOp csoP){
        StationSignal.getInstance().prepCS(MachineClientUtils.Machine.CS2, csoP);
    }

    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if (e.getStations().get(i) instanceof CapStation2 capStation2){

                setName(capStation2.getName());
                setType(capStation2.getName());
                setState(capStation2.getState());
                setTeamColor(capStation2.getTeamColor());
                setStationField(capStation2.getStationField().getZone());
                setA(capStation2.getA());
                Log.machine.info("Store Machine: " + this.toString());
                new LogMsg(this.getName() + "\n"
                        + "Cap Colors: " + capColor + "\n"
                        + "Cap in Stock: " + capsInStock);
                //MachineInfoBus.getInstance().unsubscribe(this);
            }
        }
    }

    @Override
    public String toString() {
        return "CapStation2{" +
                "capColor=" + capColor +
                ", capsInStock=" + capsInStock +
                ", " + super.toString() +
                '}';
    }
}
