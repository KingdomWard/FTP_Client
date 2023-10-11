package Utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FXMLSceneController {
    private static Stage primaryStage;
    private static final List<Scene> cachedScenes = new ArrayList<>();
    private static final List<String> cachedFiles = new ArrayList<>();

    public static void init(Stage primaryStage, String windowTitle, String filename) throws Exception {
        FXMLSceneController.primaryStage = primaryStage;

        // Set the scene on the primary stage
        swapScene(filename);

        // Set the stage title
        primaryStage.setTitle(windowTitle);

        // Show the stage
        primaryStage.show();
    }

    public static void swapScene(String filename) throws IOException {
        Scene scene = getFXMLScene(filename);
        primaryStage.setScene(scene);
    }

    private static Scene getFXMLScene(String filename) throws IOException {
        if (!filename.endsWith(".fxml"))
            filename += ".fxml";

        // check if cached
        int index;
        if ((index = cachedFiles.indexOf(filename)) != -1){
            return cachedScenes.get(index);
        }

        // if not cached, load it
        URL resourceUrl = FXMLSceneController.class.getResource("/fxml/" + filename);
        if (resourceUrl == null) {
            throw new IOException("FXML file not found: " + filename);
        }
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(resourceUrl)));

        //cache it
        cachedScenes.add(scene);
        cachedFiles.add(filename);

        return scene;
    }

    private static void createPopUp(String filename) throws IOException {
        // Create a new stage for the secondary window
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Secondary Window");

        Scene scene = getFXMLScene(filename);

        // Set the scene for the secondary stage
        secondaryStage.setScene(scene);

        // Set modality (optional) - Modality.WINDOW_MODAL, Modality.APPLICATION_MODAL, or Modality.NONE
        secondaryStage.initModality(Modality.APPLICATION_MODAL);

        // Show the secondary window
        secondaryStage.show();
    }
}
