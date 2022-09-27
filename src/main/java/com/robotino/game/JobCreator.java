package com.robotino.game;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.logistics.Stations.BaseStation;
import com.robotino.logistics.Stations.RingStation1;
import com.robotino.logistics.Stations.RingStation2;
import com.robotino.game.jobs.*;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

import java.util.LinkedList;
import java.util.List;

/**
 * Erstellt anhand eines Produktes eine JobList die dan im ProductHandler abgearbeitet wird
 */
public class JobCreator {
    
    private final Order order;
    private final Robo robo;
    private final List<Executable> jobList = new LinkedList<>();

    public JobCreator(Order order, Robo robo){
        this.order = order;
        this.robo = robo;
        createJob();
    }

    /**
     * Erstellt die Jobliste wird aber noch nicht aufgeführt!
     */
    private void createJob() {
        Log.jobCreator.info("Create JobList for Order: " + order.getType() + " and Robo: " + robo.toString());
        String ringColors = String.valueOf(order.getRingColors());
        Log.jobCreator.info("Order: Type=" + order.getType() +
                ", id=" + order.getId() +
                ", BaseColor=" + order.getBaseColor() +
                ", CapColor=" + order.getCapColor() +
                ", RingColors=" + ringColors +
                ", deliveryGate=" + order.getDeliveryGate() +
                ", deliveryPeriodeBegin=" + order.getDeliveryPeriodBegin() +
                ", deliveryPeriodeEnd=" + order.getDeliveryPeriodEnd());
        switch (order.getType()){
            case C0 -> {
                // Bereite das Cap für den Burger for
                // 1. Fährt zu der Richtigen CapStation anhand der Cap Farbe
                // 2. Setzt den Dummy Burger auf die CapStation
                // 3. Sendet der CapStation den Befehl, den Cap zu entfernen
                // Achtung Dummy Base ist jetzt am Output der CapStation, jetzt muss der Dummy Burger Recycled werden
                jobList.add(new PrepareCap(robo,
                        order.getCapColor()));
                Log.jobCreator.info("Add PrepareCap to JobList params: CapColor=" + order.getCapColor());
                // Bezahlt mit einem Dummy Base für einen Ring
                // 1. Fährt zu der Station an der, der Dummy Base ist
                // 2. Greift den Dummy Base
                // 3. Fährt zu der RingStation an der Bezahlt werden soll
                // 4. Gibt den Dummy Base ab um zu bezahlen und bezahlt auch an der richtigen RS (interne Bezahlung)
                jobList.add(new RecycleBase(robo,
                        PrepareCap.getBurgerPickUpPosition(order.getCapColor()),
                        RingStation1.getInstance())); // Kann auch RS2 sein
                Log.jobCreator.info("Add RecycleBase to JobList params: pickUpStation=" + PrepareCap.getBurgerPickUpPosition(order.getCapColor()) + ", deliveryStation=" + RingStation1.getInstance());
                // Einfacher Fahrbefehl
                // 1. Roboter fährt zur BaseStation
                //jobList.add(new SimpleDriveJob(robo, new BaseStation(), Station.Side.OUTPUT));
                // Forder eine Base an mit der richtigen Farbe
                // 1. Roboter fährt zur BaseStation
                // 2. Sendet der BaseStation den Befehl eine Base auszugeben
                jobList.add(new PrepareBase(robo,  order.getBaseColor()));
                Log.jobCreator.info("Add PrepareBase to JobList params: BaseColor=" + order.getBaseColor());
                // Holt den Burger an der BaseStation ab und montiert den Cap
                // 1. Fährt zu der BaseStation
                // 2. Greift die Base
                // 3. Fährt zu der Richtigen CapStation
                // 4. Gibt die Base an der CapStation ab
                // 5. Sendet der RingStation den befehl den Cap zu montieren
                jobList.add(new PutCapOnBurger(robo, BaseStation.getInstance(), order.getCapColor()));
                Log.jobCreator.info("Add PutCapOnBurger to JobList params: pickUpStation=" + BaseStation.getInstance() + ", capColor=" + order.getCapColor());
                // Gibt den fertigen Burger an der DeliveryStation ab
                // 1. Fährt zu der CapStation an der, der fertige Burger wartet
                // 2. Greift den Burger
                // 3. Fährt zu der Delivery Station
                // 4. Gibt den Burger ab
                // 5. Sendet der DeliveryStation den Befahl das der Burger abgegeben wurde
                if(Data.isGAME()){
                    jobList.add(new Delivery(robo,
                            PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()),
                            order.getId(), order.getDeliveryGate()));
                    Log.jobCreator.info("Add Delivery to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()) + ", orderId=" + order.getId() + ", deliveryGate=" + order.getDeliveryGate());
                }else {
                    jobList.add(new ShowOffBurger(robo, PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor())));
                    //Log.jobCreator.info("Add ShowOffBurger to JobList params: pickUpStation=" + );
                }


            }
            case C1 -> {
                jobList.add(new PrepareCap(robo,
                        order.getCapColor()));
                Log.jobCreator.info("Add PrepareCap to JobList params: CapColor=" + order.getCapColor());
                // Fragt, ab wo man als Nächste Zahlen sollte
                int firstRingNumber = 0;
                Station ringStationThatHasFirstRing = evaluateWhichRingStationForPayment(order, firstRingNumber);

                jobList.add(new RecycleBase(robo,
                        PrepareCap.getBurgerPickUpPosition(order.getCapColor()),
                        ringStationThatHasFirstRing)); // Kann auch RS2 sein
                Log.jobCreator.info("Add RecycleBase to JobList params: pickUpStation=" + PrepareCap.getBurgerPickUpPosition(order.getCapColor()) + ", deliveryStation=" + ringStationThatHasFirstRing);
                int ringCost = evaluateIterationForPayment(order, ringStationThatHasFirstRing, firstRingNumber);

                payAtRingStation(ringCost, ringStationThatHasFirstRing);

                //jobList.add(new SimpleDriveJob(robo, new BaseStation(), Station.Side.OUTPUT));
                jobList.add(new PrepareBase(robo, order.getBaseColor()));
                Log.jobCreator.info("Add PrepareBase to JobList params: BaseColor=" + order.getBaseColor());
                // Holt der Base ab und setzt einen Ring auf die Base
                // 1. Fährt zu der BaseStation
                // 2. Greift die Base
                // 3. Fährt zu der RingStation
                // 4. Setzt die Base in die RingStation
                // 5. Sendet an die RingStation den Befehl das der Ring montiert werden soll
                jobList.add(new PutRingOnBurger(robo, BaseStation.getInstance(), order.getRingColors().get(firstRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + BaseStation.getInstance() + ", ringColor=" + order.getRingColors().get(firstRingNumber));
                jobList.add(new PutCapOnBurger(robo, ringStationThatHasFirstRing, order.getCapColor()));
                Log.jobCreator.info("Add PutCapOnBurger to JobList params: pickUpStation=" + ringStationThatHasFirstRing + ", capColor=" + order.getCapColor());
                if(Data.isGAME()){
                    jobList.add(new Delivery(robo,
                            PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()),
                            order.getId(), order.getDeliveryGate()));
                    Log.jobCreator.info("Add Delivery to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()) + ", orderId=" + order.getId() + ", deliveryGate=" + order.getDeliveryGate());
                }else {
                    jobList.add(new ShowOffBurger(robo, PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor())));
                    Log.jobCreator.info("Add ShowOffBurger to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()));
                }
            }
            case C2 -> {
                jobList.add(new PrepareCap(robo,
                        order.getCapColor()));
                Log.jobCreator.info("Add PrepareCap to JobList params: CapColor=" + order.getCapColor());
                //Auswähle der RingStation für die Zahlung und am schluss Prepare

                /*----------- Bezahlt Dummy beim bei der ersten Ringstation die Kostet -----------*/
                // Berechnet die Ringkosten beachtet auch schon bezahlte Dummy Basen

                int ringIndex = getFirstRingIndexThatCost(order);
                Station ringStationThatHasFirstRingThatCost = evaluateWhichRingStationForPaymentForFirstRing(order, ringIndex);

                jobList.add(new RecycleBase(robo,
                        PrepareCap.getBurgerPickUpPosition(order.getCapColor()),
                        ringStationThatHasFirstRingThatCost)); // Kann auch RS2 sein
                Log.jobCreator.info("Add RecycleBase to JobList params: pickUpStation=" + PrepareCap.getBurgerPickUpPosition(order.getCapColor()) + ", deliveryStation=" + ringStationThatHasFirstRingThatCost);

                /*-----------Erster Ring wird verarbeitet-----------*/
                int firstRingNumber = 0;
                Station ringStationThatHasFirstRing = evaluateWhichRingStationForPayment(order, firstRingNumber);
                int ringCost = evaluateIterationForPayment(order, ringStationThatHasFirstRing, firstRingNumber);
                payAtRingStation(ringCost, ringStationThatHasFirstRing);

                /*-----------zweiter Ring wird verarbeitet-----------*/
                //Auswähle der RingStation für die Zahlung und am schluss Prepare
                int secondRingNumber = 1;
                Station ringStationThatHasSecondRing = evaluateWhichRingStationForPayment(order, secondRingNumber);
                // Berechnet die Ringkosten beachtet auch schon bezahlte Dummy Basen
                ringCost = evaluateIterationForPayment(order, ringStationThatHasSecondRing, secondRingNumber);

                payAtRingStation(ringCost, ringStationThatHasSecondRing);

                //jobList.add(new SimpleDriveJob(robo, new BaseStation(), Station.Side.OUTPUT));
                jobList.add(new PrepareBase(robo, order.getBaseColor()));
                Log.jobCreator.info("Add PrepareBase to JobList params: BaseColor=" + order.getBaseColor());
                jobList.add(new PutRingOnBurger(robo, BaseStation.getInstance(), order.getRingColors().get(firstRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + BaseStation.getInstance() + ", ringColor=" + order.getRingColors().get(firstRingNumber));
                jobList.add(new PutRingOnBurger(robo, ringStationThatHasFirstRing, order.getRingColors().get(secondRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + ringStationThatHasFirstRing + ", ringColor=" + order.getRingColors().get(secondRingNumber));
                jobList.add(new PutCapOnBurger(robo, ringStationThatHasSecondRing, order.getCapColor()));
                Log.jobCreator.info("Add PutCapOnBurger to JobList params: pickUpStation=" + ringStationThatHasSecondRing + ", capColor=" + order.getCapColor());
                if(Data.isGAME()) {
                    jobList.add(new Delivery(robo,
                            PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()),
                            order.getId(), order.getDeliveryGate()));
                    Log.jobCreator.info("Add Delivery to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()) + ", orderId=" + order.getId() + ", deliveryGate=" + order.getDeliveryGate());
                }else {
                    jobList.add(new ShowOffBurger(robo, PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor())));
                    Log.jobCreator.info("Add ShowOffBurger to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()));
                }
            }
            case C3 -> {
                jobList.add(new PrepareCap(robo,
                        order.getCapColor()));
                Log.jobCreator.info("Add PrepareCap to JobList params: CapColor=" + order.getCapColor());
                //Auswähle der RingStation für die Zahlung und am schluss Prepare
                int ringIndex = getFirstRingIndexThatCost(order);
                Station ringStationThatHasFirstRingThatCost = evaluateWhichRingStationForPaymentForFirstRing(order, ringIndex);

                jobList.add(new RecycleBase(robo,
                        PrepareCap.getBurgerPickUpPosition(order.getCapColor()),
                        ringStationThatHasFirstRingThatCost)); // Kann auch RS2 sein
                Log.jobCreator.info("Add RecycleBase to JobList params: pickUpStation=" + PrepareCap.getBurgerPickUpPosition(order.getCapColor()) + ", deliveryStation=" + ringStationThatHasFirstRingThatCost);

                /*-----------Erster Ring wird verarbeitet-----------*/
                // Berechnet die Ringkosten beachtet auch schon bezahlte Dummy Basen
                int firstRingNumber = 0;
                Station ringStationThatHasFirstRing = evaluateWhichRingStationForPayment(order, firstRingNumber);
                int ringCost = evaluateIterationForPayment(order, ringStationThatHasFirstRing, firstRingNumber);

                payAtRingStation(ringCost, ringStationThatHasFirstRing);

                /*-----------zweiter Ring wird verarbeitet-----------*/
                //Auswähle der RingStation für die Zahlung und am schluss Prepare
                int secondRingNumber = 1;
                Station ringStationThatHasSecondRing = evaluateWhichRingStationForPayment(order, secondRingNumber);
                // Berechnet die Ringkosten beachtet auch schon bezahlte Dummy Basen
                ringCost = evaluateIterationForPayment(order, ringStationThatHasSecondRing, secondRingNumber);

                payAtRingStation(ringCost, ringStationThatHasSecondRing);

                /*-----------dritter Ring wird verarbeitet-----------*/
                //Auswähle der RingStation für die Zahlung und am schluss Prepare
                int thirdRingNumber = 2;
                Station ringStationThatHasThirdRing = evaluateWhichRingStationForPayment(order, thirdRingNumber);
                // Berechnet die Ringkosten beachtet auch schon bezahlte Dummy Basen
                ringCost = evaluateIterationForPayment(order, ringStationThatHasThirdRing, thirdRingNumber);

                payAtRingStation(ringCost, ringStationThatHasThirdRing);

                //jobList.add(new SimpleDriveJob(robo, new BaseStation(), Station.Side.OUTPUT));
                jobList.add(new PrepareBase(robo, order.getBaseColor()));
                Log.jobCreator.info("Add PrepareBase to JobList params: BaseColor=" + order.getBaseColor());
                jobList.add(new PutRingOnBurger(robo, BaseStation.getInstance(), order.getRingColors().get(firstRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + BaseStation.getInstance() + ", ringColor=" + order.getRingColors().get(firstRingNumber));
                jobList.add(new PutRingOnBurger(robo, ringStationThatHasFirstRing, order.getRingColors().get(secondRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + ringStationThatHasFirstRing + ", ringColor=" + order.getRingColors().get(secondRingNumber));
                jobList.add(new PutRingOnBurger(robo, ringStationThatHasSecondRing, order.getRingColors().get(thirdRingNumber)));
                Log.jobCreator.info("Add PutRingOnBurger to JobList params: pickUpStation=" + ringStationThatHasSecondRing + ", ringColor=" + order.getRingColors().get(thirdRingNumber));
                jobList.add(new PutCapOnBurger(robo,  ringStationThatHasThirdRing, order.getCapColor()));
                Log.jobCreator.info("Add PutCapOnBurger to JobList params: pickUpStation=" + ringStationThatHasThirdRing + ", capColor=" + order.getCapColor());
                if(Data.isGAME()) {
                    jobList.add(new Delivery(robo,
                            PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()),
                            order.getId(), order.getDeliveryGate()));
                    Log.jobCreator.info("Add Delivery to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()) + ", orderId=" + order.getId() + ", deliveryGate=" + order.getDeliveryGate());
                }else {
                    jobList.add(new ShowOffBurger(robo, PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor())));
                    Log.jobCreator.info("Add ShowOffBurger to JobList params: pickUpStation=" + PutCapOnBurger.getBurgerPickUpPosition(order.getCapColor()));
                }
            }
        }
    }

    /**
     * Wird verwendetet, um die Ringe an der Ringstation zu bezahlen, mit Basen aus der BaseStation
     * @param payCycle gibt an wie viele Ringe bezahlt werden müssen
     * @param ringStation gibt an, an welcher RingStation bezahlt werden muss
     */
    private void payAtRingStation(int payCycle, Station ringStation){
        if(payCycle <= 0){
            // Der Ring koste nichts
            if(ringStation instanceof RingStation1 ringStation1){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss kein Ring an der: " + ringStation1.getName() + " bezahlt werden");
            }else if(ringStation instanceof RingStation2 ringStation2){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss kein Ring an der: " + ringStation2.getName() + " bezahlt werden");
            }
        }else if(payCycle == 1){
            if(ringStation instanceof RingStation1 ringStation1){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss ein Ring an der: " + ringStation1.getName() + " bezahlt werden");
            }else if(ringStation instanceof RingStation2 ringStation2){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss ein Ring an der: " + ringStation2.getName() + " bezahlt werden");
            }
            jobList.add(new PrepareBase(robo, order.getBaseColor())); // Burger erstellen
            jobList.add(new RecycleBase(robo, BaseStation.getInstance(), ringStation)); // Burger erstellen
        }else if(payCycle == 2){
            if(ringStation instanceof RingStation1 ringStation1){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss zwei Ring an der: " + ringStation1.getName() + " bezahlt werden");
            }else if(ringStation instanceof RingStation2 ringStation2){
                Log.jobCreator.info("Job: [" + order.getType() + "] - Es muss zwei Ring an der: " + ringStation2.getName() + " bezahlt werden");
            }
            jobList.add(new PrepareBase(robo, order.getBaseColor())); // Für die Bezahlung des Rings
            jobList.add(new RecycleBase(robo, BaseStation.getInstance(), ringStation)); // Für die Bezahlung des Rings
            jobList.add(new PrepareBase(robo, order.getBaseColor())); // Burger erstellen
            jobList.add(new RecycleBase(robo, BaseStation.getInstance(), ringStation)); // Burger erstellen
        }
    }

    /**
     * Evaluiert welche Ringstation die Ringfarbe hat und gibt die Station zurück
     * @param order Order aus der die Ringfarbe gelesen wurde
     * @param ringNr Ringnummer auf welchen ring abgefragt wird
     * @return Gibt die Station zurück an der sich die Ringfarbe befindet
     */
    private Station evaluateWhichRingStationForPayment(Order order, int ringNr) {
        MachineClientUtils.RingColor ringColor = order.getRingColors().get(ringNr);
        RingStation1 ringStation1 = RingStation1.getInstance();
        RingStation2 ringStation2 = RingStation2.getInstance();
        if(ringStation1.hasRingColor(ringColor)){
            Log.jobCreator.info("evaluate RingStation -> RingStation1 has ringColor: " + ringColor);
            return ringStation1;
        }else if(ringStation2.hasRingColor(ringColor)){
            Log.jobCreator.info("evaluate RingStation -> RingStation2 has ringColor: " + ringColor);
            return ringStation2;
        }
        throw new IllegalArgumentException("Ring Farbe wurde nicht gefunden: " + ringColor);
    }

    /**
     * Gibt der index zurück der Ringliste der als erste etwas kostet
     * @param order Order auf die abgefragt wird
     * @return git den index zurück, wen keine Kosten gefunden wurde, wird der erste index zurückgegeben
     */
    private int getFirstRingIndexThatCost(Order order){
        RingStation1 rs1 = RingStation1.getInstance();
        RingStation2 rs2 = RingStation2.getInstance();
        for(int i = 0; i < order.getRingColors().size(); i++){
            if(rs1.hasRingColor(order.getRingColors().get(i))){
                if(rs1.getRingCost(order.getRingColors().get(i)) > 0){
                    return i;
                }

            }else if(rs2.hasRingColor(order.getRingColors().get(i))){
                if(rs2.getRingCost(order.getRingColors().get(i)) > 0){
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Evaluiert an welcher Station etwas bezahlen muss, um den RecycleBase-Job prozess zu optimieren,
     * so wird wirklich an einer Station bezahlt, die auch bezahlt werden muss.
     * @param order Order auf die abgefragt wird
     * @param indexOfRingColor Ringnummer auf welchen ring abgefragt wird
     * @return Gibt die Station zurück, an der sich die Ringfarbe befindet und mindestens eine DummyBase bezahlt werden muss
     */
    private Station evaluateWhichRingStationForPaymentForFirstRing(Order order, int indexOfRingColor) {
        RingStation1 rs1 = RingStation1.getInstance();
        RingStation2 rs2 = RingStation2.getInstance();
        if(rs1.hasRingColor(order.getRingColors().get(indexOfRingColor))){
             if(rs1.getRingCost(order.getRingColors().get(indexOfRingColor)) > 0){
                 return rs1;
             }

        }else if(rs2.hasRingColor(order.getRingColors().get(indexOfRingColor))){
            if(rs2.getRingCost(order.getRingColors().get(indexOfRingColor)) > 0){
                return rs2;
            }
        }

        return rs1;
    }

    /**
     * Evaluiert wie oft man an einer Ringstation zahlen muss
     * @param order Order auf die abgefragt wird
     * @param ringStation Ringstation die überprüft wird
     * @param ringNr Ringnummer um den richtigen Ring auszuwählen
     * @return Gibt an wie viele male an der Station bezahlt werden soll
     */
    private int evaluateIterationForPayment(Order order, Station ringStation, int ringNr) {
        MachineClientUtils.RingColor ringColor = order.getRingColors().get(ringNr);
        Log.jobCreator.info("find out how many repetitions it takes to pay the costs for RingColor: " + ringColor.toString());
        if(ringStation instanceof RingStation1 ringStation1){
            int iterations = ringStation1.getCostsStillRequired(ringColor);
            Log.jobCreator.info("it takes: " + iterations + " repetitions");
            return iterations;
        }else if(ringStation instanceof  RingStation2 ringStation2){
            int iterations = ringStation2.getCostsStillRequired(ringColor);
            Log.jobCreator.info("it takes: " + iterations + " repetitions");
            return iterations;
        }
        throw new IllegalArgumentException("Kosten fur den Ring konnte nicht herausgefunden werden: " + order);
    }
    public List<Executable> getJobList() {
        return jobList;
    }
}
