package Utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Should be used in 2 places:
 * 1. Within controllers to swap scenes or create pop-ups
 * 2. when starting the app to initialize it with the init method
 */
public class FXMLSceneController {
    private static Stage primaryStage;

    public static void init(Stage primaryStage, String windowTitle, String filename) throws IOException {
        FXMLSceneController.primaryStage = primaryStage;

        // Set the scene on the primary stage
        swapScene(filename);

        // Set the stage title
        primaryStage.setTitle(windowTitle);

        // Show the stage
        primaryStage.show();
    }

    /**
     * Swaps the current scene with the scene specified by the filename
     * @param filename the name of the fxml file to load
     * @throws IOException if the file is not found
     */
    public static void swapScene(String filename) throws IOException {
        Scene scene = loadFXMLScene(filename);
        primaryStage.setScene(scene);
    }

    /**
     * Loads the fxml file specified by the filename
     * @param filename the name of the fxml file to load
     * @return the scene loaded from the fxml file
     * @throws IOException if the file is not found
     */
    private static Scene loadFXMLScene(String filename) throws IOException {
        if (!filename.endsWith(".fxml"))
            filename += ".fxml";

        URL resourceUrl = FXMLSceneController.class.getResource("/fxml/" + filename);
        if (resourceUrl == null) {
            throw new IOException("FXML file not found: " + filename);
        }

        return new Scene(FXMLLoader.load(Objects.requireNonNull(resourceUrl)));
    }

    /**
     * Creates a pop-up window with the specified fxml file
     * @param filename the name of the fxml file to load
     * @param windowName the name of the window
     * @throws IOException if the file is not found
     */
    public static void createPopUp(String filename, String windowName) throws IOException {
        // Create a new stage for the secondary window
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle(windowName);

        Scene scene = loadFXMLScene(filename);

        // Set the scene for the secondary stage
        secondaryStage.setScene(scene);

        // Set modality (optional) - Modality.WINDOW_MODAL, Modality.APPLICATION_MODAL, or Modality.NONE
        secondaryStage.initModality(Modality.APPLICATION_MODAL);

        // Show the secondary window
        secondaryStage.show();
    }
}
