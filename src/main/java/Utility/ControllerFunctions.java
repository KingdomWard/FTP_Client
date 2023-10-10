package Utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ControllerFunctions {
    public static void swapScene(String filename, Node anyExistingNode) throws IOException {
        Scene scene = CreateFXMLScene("ftpmain");
        Stage primaryStage = (Stage) anyExistingNode.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    public static Scene CreateFXMLScene(String filename) throws IOException {
        if (!filename.endsWith(".fxml")) filename += ".fxml";
        URL resourceUrl = ControllerFunctions.class.getResource("/fxml/" + filename);
        if (resourceUrl == null) {
            throw new IOException("FXML file not found: " + filename);
        }
        return new Scene(FXMLLoader.load(Objects.requireNonNull(resourceUrl)));
    }
}
