package com.robotino.communication.refbox.receive;

import com.grips.refbox.RefboxHandler;
import com.robotino.eventBus.refbox.NavigationRouteBus;
import com.robotino.eventBus.refbox.NavigationRouteEvent;
import org.robocup_logistics.llsf_msgs.NavigationChallengeProtos;
import org.robocup_logistics.llsf_msgs.ZoneProtos;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Wertet den empfangenen Navigationsrouten von der Refbox aus
 */
public class NavigationRoutes {

    final RefboxHandler handler;

    public NavigationRoutes(RefboxHandler handler){
        this.handler = handler;
        receiveMsg();
    }

    private void receiveMsg() {
        Consumer<NavigationChallengeProtos.NavigationRoutes> routInfo =
                routInfoOut -> convertRouteToZoneAndSend(routInfoOut.getRoutesList().listIterator());
        handler.setNavigationRoutesCallback(routInfo);

    }

    private void convertRouteToZoneAndSend(ListIterator<NavigationChallengeProtos.Route> listIterator) {
        List<String> zoneList = new LinkedList<>();
        while (listIterator.hasNext()) {
            NavigationChallengeProtos.Route route = listIterator.next();
            List<ZoneProtos.Zone> zoneList1 = route.getRouteList();
            for(ZoneProtos.Zone zone : zoneList1) {
                zoneList.add(zone.toString());
            }
        }
        NavigationRouteEvent navigationRoute = new NavigationRouteEvent(zoneList);
        NavigationRouteBus.getInstance().publish(navigationRoute);
    }
}
