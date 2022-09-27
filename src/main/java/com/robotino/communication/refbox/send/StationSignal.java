package com.robotino.communication.refbox.send;

import com.grips.model.teamserver.*;
import com.grips.refbox.*;
import com.robotino.communication.refbox.RefboxConnection;
import com.robotino.helperClass.Log;

/**
 * @author Fabian Leuenberger
 * @date 23.06.2022
 * @description Klasse wird als Singleton umgesetzt, so das diese nur einmal instanziiert wird,
 *              und deren Instanz wiedergegeben wird. Baut eine Verbindung mit der Refbox auf,
 *              sobald diese Klasse instanziiert wird. Stellt verschiedene Funktionen zur Verfügung,
 *              um Stationen vorzubereiten oder die Position des Robotinos der Refbox zu melden.
 */
public class StationSignal {

    private final RefboxClient client;

    private static final StationSignal STATION_SIGNAL_INSTANCE = new StationSignal();
    public static StationSignal getInstance(){ return STATION_SIGNAL_INSTANCE; }
    private StationSignal(){
        this.client = RefboxConnection.getInstance().getClient();
    }

    /**
     * Sendet eine Nachricht das die Base-Station eine Base mit der gewünschten Farbe an den gewünschten
     * Output bringen soll.
     * @param side gewünschte Seite Input oder Output.
     * @param baseColor gewünschte Farbe der Base, Red, Black oder Silber.
     */
    public void prepBS(MachineClientUtils.MachineSide side, MachineClientUtils.BaseColor baseColor){
        //if(Data.isGAME()) {
            client.sendPrepareBS(side, baseColor);
            Log.publishedRefboxMsg.info("Send to BS");
        //}
    }

    /**
     * Sendet eine Nachricht das die Cap-Station ein Cap montieren oder demontieren soll.
     * @param machine Type der Maschine z.B. CS1
     * @param csoP Entfernen oder montieren der Cap z.B. MOUNT_CAP oder RETRIEVE_CAP
     */
    public void prepCS(MachineClientUtils.Machine machine, MachineClientUtils.CSOp csoP){
        //if(Data.isGAME()) {
            client.sendPrepareCS(machine, csoP);
            Log.publishedRefboxMsg.info("Send to CS");
        //}
    }

    /**
     * Sendet eine Nachricht das eine Element verschickt wird.
     * @param gate Das einzusortierende Gate 1-3
     * @param id Die ID des gefertigten Elements anhand der Order.
     */
    public void prepDS(int gate, int id){
        if(gate > 3 || gate < 1){
            throw new IllegalArgumentException("DeliveryStation kann nicht an gate: " + gate + "prepare werden");
        }
        //if(Data.isGAME()) {
            client.sendPrepareDS(gate, id);
            Log.publishedRefboxMsg.info("Send to DS");
        //}
    }

    /**
     * Sendet eine Nachricht das die Ring-Station ein Ring vorbereiten soll.
     * @param machine Type der Maschine z.B. RS1
     * @param ringColor Welche bereitgestellt werden soll, z.B. RING_BLUE
     */
    public void prepRS(MachineClientUtils.Machine machine, MachineClientUtils.RingColor ringColor){
        //if(Data.isGAME()) {
            client.sendPrepareRS(machine, ringColor);
            Log.publishedRefboxMsg.info("Send to RS");
        //}
    }

    /**
     * Sendet eine Nachricht, das in die Storage-Station einsortiert werden möchte.
     * @param machine Type der Maschine z.B. SS1
     * @param shelf Das Regal in welche eingelagert werden möchte.
     * @param slot Der Slot in welcher eingelagert werden möchte.
     */
    public void prepSS(MachineClientUtils.Machine machine, int shelf, int slot){
        //if(Data.isGAME()) {
            client.sendPrepareSS(machine, shelf, slot);
            Log.publishedRefboxMsg.info("Send to SS");
        //}
    }

    /**
     * Sendet eine Nachricht zum Zurücksetzen einer Station.
     * @param machine Type der Machine z.B. SS1
     */
    public void sendResetMachine (MachineClientUtils.Machine machine){
        //if(Data.isGAME()) {
            client.sendResetMachine(machine);
            Log.publishedRefboxMsg.info("Send Reset");
        //}
    }
}