package com.robotino.communication.refbox.receive;

import com.grips.model.teamserver.MachineClientUtils;
import com.grips.refbox.RefboxHandler;
import com.robotino.eventBus.intern.RingInfoBus;
import com.robotino.eventBus.intern.RingInfoEvent;
import com.robotino.logistics.Stations.RingStation1;
import com.robotino.logistics.Stations.RingStation2;
import org.robocup_logistics.llsf_msgs.ProductColorProtos;
import org.robocup_logistics.llsf_msgs.RingInfoProtos;

import java.util.function.Consumer;
/**
 * Wertet den empfangenen Ring Kosten der Refbox aus
 */
public class RingInfo {

    final RefboxHandler handler;

    public RingInfo(RefboxHandler handler){
        this.handler = handler;
    }

    public void startReceiveMsg() {

        Consumer<RingInfoProtos.RingInfo> ringInfo = ringInfoMsg -> {
            //System.out.println("Ring:"+ringInfoMsg);
            for (int i = 0; i < ringInfoMsg.getRingsCount(); i++) {
                ProductColorProtos.RingColor ringColor = ringInfoMsg.getRings(i).getRingColor();
                int cost = ringInfoMsg.getRings(i).getRawMaterial();
                MachineClientUtils.RingColor color;
                switch (ringColor.toString()){
                    case "RING_ORANGE" -> color = MachineClientUtils.RingColor.RING_ORANGE;
                    case "RING_BLUE" -> color = MachineClientUtils.RingColor.RING_BLUE;
                    case "RING_YELLOW" -> color = MachineClientUtils.RingColor.RING_YELLOW;
                    case "RING_GREEN" -> color = MachineClientUtils.RingColor.RING_GREEN;
                    default -> throw new IllegalArgumentException("Es gibt keine RingColor mit dem String: " + ringColor);
                }
                RingStation1 rs1 = RingStation1.getInstance();
                RingStation2 rs2 = RingStation2.getInstance();
                // ToDo kann mit eine neue Objekt besser gelöst werden -> Objekt Ring mit Farbe und Kosten
                if(rs1.hasRingColor(color)){
                    if(rs1.hasRingColor1(color)){
                        rs1.setRing1Cost(cost);
                    }
                    else if(rs1.hasRingColor2(color)) {
                        rs1.setRing2Cost(cost);
                    }
                    else{
                        throw new IllegalArgumentException("Preis konnte von der Farbe: " + color + " nicht zugewiesen werden");
                    }
                }else if(rs2.hasRingColor(color)) {

                    if (rs2.hasRingColor1(color)) {
                        rs2.setRing1Cost(cost);
                    } else if (rs2.hasRingColor2(color)) {
                        rs2.setRing2Cost(cost);
                    } else {
                        throw new IllegalArgumentException("Preis konnte von der Farbe: " + color + " nicht zugewiesen werden");
                    }
                }
            }
            RingInfoBus.getInstance().publish(new RingInfoEvent());
        };
        handler.setRingInfoCallback(ringInfo);
    }
}
