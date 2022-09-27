package com.robotino.helperClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Ist verlinkt mit dem log4j2SecondOld.xml File in /src/main/resources/log4j2Second.xml.
 * In dieser Datei werden alle bestimmungen über die verschiedenen Logger bestimmt
 */
public class Log {

    static{
        System.setProperty("log4j.configurationFile", "log4j2Second.xml");
    }

    //public static final Logger rootLogger = LogManager.getRootLogger();
    public static final Logger logger = LogManager.
            getLogger(Log.class);
    public static final Logger incomingMqttMsg = LogManager.
            getLogger(Log.class.getName() + ".logIncomingMqttMsgHandler");

    public static final Logger publishedMqttMsg = LogManager.
            getLogger(Log.class.getName() + ".logPublishedMqttMsg");

    public static final Logger incomingRefboxMsg = LogManager.
            getLogger(Log.class.getName() + ".logIncomingRefboxMsg");

    public static final Logger publishedRefboxMsg = LogManager.
            getLogger(Log.class.getName() + ".logPublishedRefboxMsg");
    //Braucht es evtl. nicht
    public static final Logger eventBus = LogManager.
            getLogger(Log.class.getName() + ".logEventBus");

    public static final Logger jobCreator = LogManager.
            getLogger(Log.class.getName() + ".logJobCreator");

    public static final Logger game = LogManager.
            getLogger(Log.class.getName() + ".logGame");
    //Braucht es evtl. nicht
    public static final Logger startedSystem = LogManager.
            getLogger(Log.class.getName() + ".logStartedSystem");

    public static final Logger driveSystem = LogManager.
            getLogger(Log.class.getName() + ".logDriveSystem");
    //Braucht es evtl. nicht
    public static final Logger mqttMsgHandler = LogManager.
            getLogger(Log.class.getName() + ".logMqttMsgHandler");

    public static final Logger logVisu = LogManager.
            getLogger(Log.class.getName() + ".logVisu");

    public static final Logger machine = LogManager.
            getLogger(Log.class.getName() + ".logmachine");

    public static final Logger navigationChallenge = LogManager.getLogger(Log.class.getName() + "logNavChallenge");

}
