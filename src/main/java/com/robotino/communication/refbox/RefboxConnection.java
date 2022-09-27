package com.robotino.communication.refbox;

import com.grips.refbox.*;
import com.robotino.communication.refbox.receive.*;
import com.robotino.helperClass.Data;

public class RefboxConnection {

    private static final int SEND_INTERVAL_IN_MS = 1000;

    private final RefboxClient client;

    private static final RefboxConnection REFBOX_INSTANCE = new RefboxConnection();
    public static RefboxConnection getInstance() { return REFBOX_INSTANCE; }
    private RefboxConnection(){

        // Konfiguration Public Peer:
        PeerConfig publicPeer = new PeerConfig(
                Data.getREFBOX_PUBLIC_PEER_RECEIVE_PORT(), // receive: 4444
                Data.getREFBOX_PUBLIC_PEER_SEND_PORT()); // send: 4445

        // IP Refbox setzen:
        RefboxConnectionConfig connectionConfig = new RefboxConnectionConfig();
        connectionConfig.setIp(Data.getREFBOX_IP()); // 172.26.107.212 Refbox HFTM // 172.26.108.62 Refbox Taric
        connectionConfig.setPublicPeer(publicPeer);

        // Private Peers Definieren:
        PeerConfig magentaPeer = new PeerConfig(
                Data.getREFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT(), // receive: 4442
                Data.getREFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT()); // send: 4447
        connectionConfig.setMagentaPeer(magentaPeer);

        PeerConfig cyanPeer = new PeerConfig(
                Data.getREFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT(), // receive: 4441
                Data.getREFBOX_PRIVATE_CYAN_PEER_SEND_PORT()); // send: 4446
        connectionConfig.setCyanPeer(cyanPeer);

        // Team Konfiguration
        TeamConfig teamConfig = new TeamConfig(
                Data.getENCRYPTION_KEY(),   // hftm: randomkey, Taric: randomkey2
                Data.getTEAM_COLOR(),       // Team Farbe: MAGENTA oder CYAN möglich
                Data.getTEAM_NAME());       // Solidus

        // Handler Instanziieren
        RefboxHandler privateHandler = new RefboxHandler();
        RefboxHandler publicHandler = new RefboxHandler();

        // Client Instanziieren
        client = new RefboxClient(connectionConfig,
                teamConfig,
                privateHandler,
                publicHandler,
                SEND_INTERVAL_IN_MS);
        // Server Starten
        client.startServer();


        // Empfange der Nachrichten
        // ToDo Momentan wird das Empfange in einer gewissen Reihenfolge gemacht des muss aber nicht so sein sollte umgebaut werden
        new GameState(publicHandler);
        new NavigationRoutes(privateHandler);

        RingInfo ringInfo = new RingInfo(privateHandler);
        OrderInfo orderInfo = new OrderInfo(publicHandler, ringInfo);

        new StationInfo(privateHandler, client, orderInfo);

    }
    public RefboxClient getClient() {
        return client;
    }

}
