package gui.node;

import gui.communication.GuiMqttMsgHandler;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ShowTextOutput implements InvalidationListener {
    public static Group group;
    HBox hBox;
    VBox vBox;
    public static final TextArea textArea = new TextArea("Logs: ");

    private final int field_height;

    public ShowTextOutput(Group group, int field_height){
        ShowTextOutput.group = group;
        this.field_height = field_height;
        GuiMqttMsgHandler.getInstance().addListener(this);
        draw("");
    }

    public static void deleteLogs(ActionEvent actionEvent) {
        textArea.clear();
        textArea.setText("Logs: ");
    }

    private void draw(String log) {
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(field_height);
        textArea.setEditable(false);
        textArea.appendText("\n" + log);
        vBox = new VBox(10);
        hBox = new HBox(10);

        vBox.getChildren().add(textArea);
        hBox.getChildren().add(vBox);
        group.getChildren().add(hBox);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            String log = ((GuiMqttMsgHandler)observable).getLog();
            draw(log);
        });
    }
}
