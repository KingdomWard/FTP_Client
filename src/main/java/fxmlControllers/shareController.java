package com.share.share;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class shareController {
    @FXML
    private Stage primaryStage;

    private File selectedFile;

    @FXML
    private void browseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File to Share");
        selectedFile = fileChooser.showOpenDialog(primaryStage);
    }

    @FXML
    private void shareFile(ActionEvent event) {
        if (selectedFile != null) {

            System.out.println("Sharing file: " + selectedFile.getName());
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
