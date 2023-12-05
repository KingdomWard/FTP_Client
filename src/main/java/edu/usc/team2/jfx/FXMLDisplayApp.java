package edu.usc.team2.jfx;

import Utility.FXMLSceneController;
import javafx.application.Application;
import javafx.stage.Stage;

public class FXMLDisplayApp extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLSceneController.init(primaryStage, "FTP Client", "LoginPage.fxml");
    }

    public static void main(String[] args) {launch(args);}
}
