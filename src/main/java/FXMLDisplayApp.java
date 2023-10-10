import Connections.DBConnection;
import Connections.FTPConnection;
import Utility.ControllerFunctions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FXMLDisplayApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //  Load the FXML file and create a scene with the loaded FXML content
        //Scene scene = ControllerFunctions.CreateFXMLScene("LoginPage");
        Scene scene = ControllerFunctions.CreateFXMLScene("FTPMain");

        // Set the scene on the primary stage
        primaryStage.setScene(scene);

        // Set the stage title
        primaryStage.setTitle("FXML Display Example");

        // Show the stage
        primaryStage.show();

        System.out.println("Loaded");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
