package fxmlControllers;

import Entities.FileItem;
import Utility.FTPServerFunctions;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import lombok.SneakyThrows;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FTPShare implements Initializable
{
    @FXML
    private TreeTableColumn<String, String> userList;
    @FXML
    private TreeTableView<String> userTable;

    private final TreeItem<String> userRoot = new TreeItem<>("Users");

    private FileItem file;


    /**
     * Initializes the scene
     * @param url the url of the scene
     * @param resourceBundle the resource bundle of the scene
     */
    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        ArrayList<String> usersArray = FTPServerFunctions.getAllUsers();
        for (String s : usersArray)
        {
            userRoot.getChildren().add(new TreeItem<>(s));
        }
        userList.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        userTable.setRoot(userRoot);
    }

    /**
     * Shares the file with the selected user
     * @throws SQLException if the user does not exist
     */
    @FXML
    private void handleShare() throws SQLException {
        TreeItem<String> selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null)
        {
            FTPServerFunctions.fileShare(selectedUser.getValue());
        }
    }

    /**
     * Sets the file to be shared
     * @param file the file to be shared
     */
    public void setFile(FileItem file)
    {
        this.file = file;
    }
}
