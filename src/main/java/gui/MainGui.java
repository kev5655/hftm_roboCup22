package gui;

import com.robotino.helperClass.Data;
import gui.communication.GuiMqttMsgHandler;
import gui.communication.GuiMqttSubscribe;
import gui.data.GuiData;
import gui.node.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane gridPane = new GridPane();
        Group groupGamefield = new Group();

        Group groupHead = new Group();
        Group groupMpsStation = new Group();
        Group groupTextOutput = new Group();
        Group groupOrsers = new Group();
        Group groupRingStations = new Group();
        //ShowSpot showSpot = new ShowSpot(groupGamefield);
        Data.loadFile();
        GuiMqttSubscribe.getInstance().start();
        Thread mqttThread = new Thread(GuiMqttMsgHandler.getInstance(),"guiMqttHandler");
        mqttThread.start();

        new ShowGamefield(groupGamefield,
                GuiData.getFIELD_HEIGHT(),
                GuiData.getFIELD_WIDTH(),
                GuiData.getFIELD_SIZE(),
                GuiData.getGRID_SIZE(),
                GuiData.getHALF_MARGIN());

        new ShowHead(groupHead);
        new ShowPathAndObstacle(groupGamefield);
        new ShowMpsState(groupMpsStation);
        new ShowTextOutput(groupTextOutput, GuiData.getFIELD_HEIGHT());
        new ShowOrder(groupOrsers);
        new ShowRingStation(groupRingStations);

        gridPane.add(groupHead, 0, 0);
        gridPane.add(groupGamefield,0,1);
        gridPane.add(groupMpsStation, 0, 2);
        gridPane.add(groupTextOutput, 1, 1);
        gridPane.add(groupOrsers, 0, 3);
        gridPane.add(groupRingStations, 1, 2);

        Scene scene = new Scene(gridPane,
                GuiData.getFIELD_WIDTH() + GuiData.getMARGIN(),
                GuiData.getFIELD_HEIGHT() + GuiData.getMARGIN());

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setTitle("RoboCup Team Solidus");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}