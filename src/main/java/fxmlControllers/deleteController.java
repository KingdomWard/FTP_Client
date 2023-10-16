package com.delete.delete;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.List;

public class deleteController {

    @FXML
    private ListView<File> fileListView;

    @FXML
    private void addFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Files to Add");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("All Files", "*.*")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            // Only add files if the user didn't cancel the dialog
            fileListView.getItems().addAll(selectedFiles);
        }
    }

    @FXML
    private void deleteSelectedFiles() {
        File selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            boolean deleted = selectedFile.delete();
            if (deleted) {
                fileListView.getItems().remove(selectedFile);
                showAlert("File Deleted", "File has been deleted.");
            } else {
                showAlert("Error", "Failed to delete the file.");
            }
        } else {
            showAlert("Error", "No file selected.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
