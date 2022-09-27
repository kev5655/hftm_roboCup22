package com.robotino.logistics.Stations;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.communication.refbox.send.StationSignal;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 *              Zusätzliche Informationen für die Ring-Farben welche hergestellt werden können.
 */
public class RingStation1 extends Station implements Subscriber {

    private MachineClientUtils.RingColor ringColor1;
    private MachineClientUtils.RingColor ringColor2;
    private int ring1Cost;
    private int ring2Cost;
    private Map<MachineClientUtils.RingColor, Integer> ringColors;
    private int payedBurgers = 0;

    public static final RingStation1 RING_STATION_1_INSTANCE = new RingStation1();
    public static RingStation1 getInstance(){
        Log.machine.info("Using RingStation1");
        return RING_STATION_1_INSTANCE;
    }

    private RingStation1(){
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }

    public MachineClientUtils.RingColor getRingColor1(){
        return ringColor1;
    }

    public MachineClientUtils.RingColor getRingColor2(){
        return ringColor2;
    }

    public Map<MachineClientUtils.RingColor, Integer> getRingColors() {
        return ringColors;
    }

    public int getCostsStillRequired(MachineClientUtils.RingColor ringColor){
        if(hasRingColor(ringColor)){
            if(ringColor1 == ringColor){
                Log.game.info(RingStation1.class.getName() + "Kosten die noch zu bezahlen sind: " + (ring1Cost - payedBurgers) + " Ring Kosten für die Farbe: " + ringColor + " schon bezahlte Kosten: " + payedBurgers);
                return ring1Cost - payedBurgers;
            }else if(ringColor2 == ringColor){
                Log.game.info(RingStation1.class.getName() + "Kosten die noch zu bezahlen sind: " + (ring2Cost - payedBurgers) + " Ring Kosten für die Farbe: " + ringColor + " schon bezahlte Kosten: " + payedBurgers);
                return ring2Cost - payedBurgers;
            }
        }
        throw new IllegalArgumentException("RingColor wurde nicht gefunden: " + ringColor);
    }

    public int getRingCost(MachineClientUtils.RingColor ringColor){
        if(hasRingColor(ringColor)){
            if(ringColor1 == ringColor){
                return ring1Cost;
            }else if(ringColor2 == ringColor){
                return ring2Cost;
            }
        }
        throw new IllegalArgumentException("RingColor wurde nicht gefunden: " + ringColor);
    }

    public int getPayedValue(){
        return payedBurgers;
    }
    public void payment(){
        payedBurgers++;
    }
    public void payout(int cost){
        payedBurgers = payedBurgers - cost;
    }


    public void setRingColor1(MachineClientUtils.RingColor ringColor1){
        this.ringColor1 = ringColor1;
    }

    public void setRingColor2(MachineClientUtils.RingColor ringColor2){
        this.ringColor2 = ringColor2;
    }

    public boolean hasRingColor(MachineClientUtils.RingColor ringColor){
        if(getRingColor1() == null){
            return false;
        }
        if(getRingColor2() == null){
            return false;
        }

        if(getRingColor1() == ringColor){
            return true;
        }else return getRingColor2() == ringColor;
    }
    public boolean hasRingColor1(MachineClientUtils.RingColor ringColor){
        return getRingColor1() == ringColor;
    }
    public boolean hasRingColor2(MachineClientUtils.RingColor ringColor){
        return getRingColor2() == ringColor;
    }




    public void setRing1Cost(int ring1Cost){
        this.ring1Cost = ring1Cost;
    }

    public void setRing2Cost(int ring2Cost){
        this.ring2Cost = ring2Cost;
    }

    public void prep(MachineClientUtils.RingColor ringColor){
        StationSignal.getInstance().prepRS(MachineClientUtils.Machine.RS1, ringColor);
    }

    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if (e.getStations().get(i) instanceof RingStation1 ringStation1) {

                setName(ringStation1.getName());
                setType(ringStation1.getName());
                setState(ringStation1.getState());
                setTeamColor(ringStation1.getTeamColor());
                setStationField(ringStation1.getStationField().getZone());
                setA(ringStation1.getA());

                ringColors = new HashMap<>(){{
                    put(ringStation1.getRingColor1(), ring1Cost);
                    put(ringStation1.getRingColor2(), ring2Cost);
                }};
                Log.machine.info("Store Machine: " + this.toString());

            }
        }
    }

    @Override
    public String toString() {
        return "RingStation1{" +
                "ringColor1=" + ringColor1 +
                ", ringColor2=" + ringColor2 +
                ", ringPrize1=" + ring1Cost +
                ", ringPrize2=" + ring2Cost +
                ", payedBurgers=" + payedBurgers +
                ", " + super.toString() +
                '}';
    }
}
