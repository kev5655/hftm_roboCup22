package com.robotino.communication.refbox.send;

import com.grips.refbox.RefboxClient;
import com.robotino.communication.mqtt.MqttMsgHandler;
import com.robotino.communication.refbox.RefboxConnection;
import com.robotino.helperClass.Log;
import com.robotino.robo.Robo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sendet das Beacon Signal an die Refbox.
 * Das Beacon Signal ist die Position des Roboters sie musse die Koordinaten nach Rulebook haben.
 */
public class BeaconSignal{

    private final Set<Robo> robos = new HashSet<>();

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    final RefboxClient client;
    Thread thread;

    private static final BeaconSignal BEACON_SIGNAL_INSTANCE = new BeaconSignal();
    public static BeaconSignal getInstance(){ return  BEACON_SIGNAL_INSTANCE; }
    private BeaconSignal(){
        client = RefboxConnection.getInstance().getClient();
    }

    /**
     * Versendet das Beacon-Signal für einen Roboter, es ist nicht möglich zweimal den gleichen Roboter zu übergeben
     * da im Hintergrund ein HashSet verwendet wird. Die Koordinaten vom Roboter sind schon im richtigen Format so das
     * die Refbox korrekte und lesbare Koordinaten bekommt.
     * @param robo Für den die Koordinate versendet werden sollen
     */
    public void startSendingBeaconSignal(Robo robo){
        thread = new Thread(() -> {
            robos.add(robo);
            if(robos.size() == 1){
                scheduler.scheduleAtFixedRate(interruptRun, 0, 1, TimeUnit.SECONDS);
            }

        });
        thread.start();
    }

    /**
     * Stop das Senden des Beacon-Signals für einen Roboter ist noch nicht getestet
     */
    public void stopSendingBeaconSignal(Robo robo){
        if(robos.isEmpty()){
            scheduler.shutdown();
        }
        robos.remove(robo);
    }

    final Runnable interruptRun = new Runnable() {
        public void run() {
            for(Robo r : robos){
                if(r.getOriginalCoordinate() != null){
                    Log.publishedRefboxMsg.info("Sending Beacon Signal");
                    client.sendBeaconSignal(r.getNumber(),
                            "r" + r.getNumber(),
                            (float)r.getOriginalX(),
                            (float)r.getOriginalY(),
                            (float)r.getOriginalA());
                }else{
                    Log.publishedRefboxMsg.warn("Roboter mit der Number: " + r.getNumber() + "hat noch kein Position von Robotino über Mqtt erhalten! Ist MqttMsgHandler stated: " + MqttMsgHandler.getInstance().isStated());
                    Log.logger.warn("Roboter mit der Number: " + r.getNumber() + "hat noch kein Position von Robotino über Mqtt erhalten! Ist MqttMsgHandler stated: " + MqttMsgHandler.getInstance().isStated());
                }
            }
        }
    };
}