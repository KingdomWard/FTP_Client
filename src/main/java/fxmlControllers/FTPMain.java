package fxmlControllers;

import Entities.FileItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FTPMain implements Initializable {

    @FXML
    private TreeTableView<FileItem> ftable;

    @FXML
    private TreeTableColumn<FileItem, String> fname;
    @FXML
    private TreeTableColumn<FileItem, String> fsize;
    @FXML
    private TreeTableColumn<FileItem, String> ftype;
    @FXML
    private TreeTableColumn<FileItem, String> dadded;

    @FXML
    private Button uploadButton;
    @FXML
    private Button shareButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources){

        // Set cell value factories
        fname.setCellValueFactory(new TreeItemPropertyValueFactory<>("fname"));
        fsize.setCellValueFactory(new TreeItemPropertyValueFactory<>("fsize"));
        ftype.setCellValueFactory(new TreeItemPropertyValueFactory<>("ftype"));
        dadded.setCellValueFactory(new TreeItemPropertyValueFactory<>("dadded"));

        // Create some objects and add them to the table
        FileItem item0 = new FileItem("a", "b", "c", "d");
        FileItem item1 = new FileItem("b", "c", "d", "a");
        FileItem item2 = new FileItem("Folder 1", new ArrayList<FileItem>(){{
            add(item1);
            add(item0);
        }});

        FileItem item3 = new FileItem("c", "d", "a", "b");
        FileItem item4 = new FileItem("d", "a", "b", "c");
        FileItem item5 = new FileItem("Folder 2", new ArrayList<FileItem>(){{
            add(item3);
            add(item4);
        }});

        System.out.println("made it");

        ftable.setRoot(generateTreeItems(new ArrayList<FileItem>(){{add(item2); add(item5);}}, new FileItem("Username")));
    }

    // Helper method to generate TreeItems from the data list
    private TreeItem<FileItem> generateTreeItems(List<FileItem> data, FileItem RootFolder) {
        TreeItem<FileItem> root = new TreeItem<>(RootFolder);
        data.forEach(item -> {
            if (item.isFolder())
                root.getChildren().add(generateTreeItems(item.getChildren(), item));
            else
                root.getChildren().add(new TreeItem<>(item));
        });
        return root;
    }
}


