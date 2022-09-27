package com.robotino.logistics.Stations;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.logistics.Station;
import com.robotino.communication.refbox.send.StationSignal;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Tochterklasse von Stations, kennt alle Funktionen der Vaterklasse.
 *              Zusätzliche Informationen für die Ring-Farben welche hergestellt werden können.
 */
public class RingStation2 extends Station implements Subscriber {

    private MachineClientUtils.RingColor ringColor1;
    private MachineClientUtils.RingColor ringColor2;
    private int ringPrize1;
    private int ringPrize2;
    private Map<MachineClientUtils.RingColor, Integer> ringColors;
    private int payedBurgers = 0;

    public static final RingStation2 RING_STATION_2_INSTANCE = new RingStation2();
    public static RingStation2 getInstance(){
        Log.machine.info("Using RingStation1");
        return RING_STATION_2_INSTANCE;
    }

    private RingStation2(){
        super();
        MachineInfoBus.getInstance().subscribe(this);
    }


    /**********Getters**********/
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
                return ringPrize1 - payedBurgers;
            }else if(ringColor2 == ringColor){
                return ringPrize2 - payedBurgers;
            }
        }
        throw new IllegalArgumentException("RingColor wurde nicht gefunden: " + ringColor);
    }

    public int getRingCost(MachineClientUtils.RingColor ringColor){
        if(hasRingColor(ringColor)){
            if(ringColor1 == ringColor){
                return ringPrize1;
            }else if(ringColor2 == ringColor){
                return ringPrize2;
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



    /**********Setters**********/
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

    public void setRing1Cost(int ringPrize1){
        this.ringPrize1 = ringPrize1;
    }

    public void setRing2Cost(int ringPrize2){
        this.ringPrize2 = ringPrize2;
    }

    public void prep(MachineClientUtils.RingColor ringColor){
        StationSignal.getInstance().prepRS(MachineClientUtils.Machine.RS2, ringColor);
    }

    @Override
    public void onReceive(Event event) {
        MachineInfoEvent e = (MachineInfoEvent) event;

        for (int i = 0; i < e.getStations().size(); i++) {
            if (e.getStations().get(i) instanceof RingStation2 ringStation2) {

                setName(ringStation2.getName());
                setType(ringStation2.getName());
                setState(ringStation2.getState());
                setTeamColor(ringStation2.getTeamColor());
                setStationField(ringStation2.getStationField().getZone());
                setA(ringStation2.getA());

                ringColors = new HashMap<>() {{
                    put(ringStation2.getRingColor1(), ringPrize1);
                    put(ringStation2.getRingColor2(), ringPrize2);
                }};
                Log.machine.info("Store Machine: " + this.toString());

            }
        }
    }

    @Override
    public String toString() {
        return "RingStation2{" +
                "ringColor1=" + ringColor1 +
                ", ringColor2=" + ringColor2 +
                ", ringPrize1=" + ringPrize1 +
                ", ringPrize2=" + ringPrize2 +
                ", payedBurgers=" + payedBurgers +
                ", " + super.toString() +
                '}';
    }
}

