
package com.robotino;

import com.robotino.communication.mqtt.MqttSubscribe;


import com.robotino.helperClass.Data;
import com.robotino.rcll.Controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Zahn
 */
// ToDo Code Duplicate optimieren

public class Main {
    public static void main(String[] args) {

        System.out.println("Start Programm");
        try {
            Data.loadFile(); // Load Gameplay Data
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Main().start();
    }

    public void start(){

        /*
        * Starten das Empfangen der Mqtt-Nachrichten jedoch werden diese nur in eine Queue gespeichert,
        * die erst ausgelesen werd, wen der MqttMsgHandler gestartet wird
         */
        MqttSubscribe.getInstance();

        /*
        * Starte das Spiel je nachdem was in der gamePlayRefboxTaric.xml eingestellt wurde
        */
        new Controller();


    }
    
}
