package com.vuongpv.submaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class SubMakerMain extends Application
{
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(
                SubMakerMain.class.getResource("/com/vuongpv/submaker/SubMaker.fxml"));

        Pane mainPane = fxmlLoader.load();

        final SubMakerController controller = fxmlLoader.getController();
        controller.setStage(primaryStage);


        Scene scene = new Scene(mainPane);
        primaryStage.setTitle("SubMaker 1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
