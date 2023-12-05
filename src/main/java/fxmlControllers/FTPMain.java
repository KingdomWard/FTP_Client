package fxmlControllers;

import Entities.FileItem;
import Entities.Folder;
import Entities.Item;
import Utility.FTPServerFunctions;
import Utility.FXMLSceneController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FTPMain implements Initializable {

    @FXML
    private TreeTableView<Item> ftable;

    @FXML
    private TreeTableColumn<Item, String> fname;
    @FXML
    private TreeTableColumn<Item, String> fsize;
    @FXML
    private TreeTableColumn<Item, String> dadded;
    @FXML
    private TreeTableColumn<Item, String> fowner;

    @FXML
    private Button uploadButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Menu menuAdmin;
    @FXML
    private TextField searchField;
    @FXML
    private Label errorDisplay;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        initializeScene();
    }


    /**
     * Initializes the scene
     */
    public void initializeScene()
    {
        // Set cell value factories
        fname.setCellValueFactory(new TreeItemPropertyValueFactory<>("fname"));
        fsize.setCellValueFactory(new TreeItemPropertyValueFactory<>("fsize"));
        fowner.setCellValueFactory(new TreeItemPropertyValueFactory<>("fowner"));
        dadded.setCellValueFactory(new TreeItemPropertyValueFactory<>("dadded"));

        try {
            ArrayList<FileItem> userFiles = FTPServerFunctions.getUserFiles();
            ArrayList<FileItem> sharedFiles = FTPServerFunctions.getSharedFiles();

            System.out.println(userFiles);

            Folder userFolder = new Folder(FTPServerFunctions.getUsername(), new ArrayList<Item>(){{
                this.addAll(userFiles);
            }});

            Folder sharedFolder = new Folder("Shared", new ArrayList<Item>(){{
                this.addAll(sharedFiles);
            }});

            ftable.setRoot(generateTreeItems(new ArrayList<Item>(){{add(userFolder); add(sharedFolder);}}
                    , new Folder("root")));
            ftable.setShowRoot(false);

            //check if admin, if not hide the Menu Admin
            if(!FTPServerFunctions.isUserAdmin())
            {
                menuAdmin.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();}
    }

    /**
     * Generates TreeItems from the data list
     * @param data the list of items to be added to the tree
     * @param RootFolder the root folder of the tree
     * @return the root of the tree
     */
    private TreeItem<Item> generateTreeItems(List<Item> data, Folder RootFolder) {
        TreeItem<Item> root = new TreeItem<>(RootFolder);
        data.forEach(item -> {
            if (item instanceof Folder)
                root.getChildren().add(generateTreeItems(((Folder) item).getChildren(), (Folder) item));
            else
                root.getChildren().add(new TreeItem<>(item));
        });
        return root;
    }

    /**
     * Handles the upload button
     * @throws Exception if the file is not found
     */
    @FXML
    private void handleUpload() throws Exception {
        // This gets the OS File Explorer
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        // If canceled, return.. Otherwise selected file is file
        if (file == null)
            return;

        FileItem fileItem = new FileItem(file.getName(), Long.toString(file.length()), FTPServerFunctions.getUsername());

        // Use FTPServerFunctions to upload file
        FTPServerFunctions.uploadFileInfo(fileItem, file);

        // Refresh the table (it should be fine to refresh here)
        initializeScene();
    }

    /**
     * Handles the delete button
     * @throws SQLException if the file is not found in DB
     * @throws IOException if the file is not found in tree
     */
    @FXML
    private void handleDelete() throws SQLException, IOException {
        // Whatever the user has selected
        TreeItem<Item> selectedFile = ftable.getSelectionModel().getSelectedItem();

        if (selectedFile == null) return; //check null
        if (!(selectedFile.getValue() instanceof FileItem)) return; //check that file is selected and not folder

        // Use FTPServerFunctions to delete file
        FTPServerFunctions.deleteFile((FileItem) selectedFile.getValue());

        // remove the item (don't refresh as it closes all drop-downs)
        selectedFile.getParent().getChildren().remove(selectedFile);
    }

    /**
     * Handles the download button
     * @throws Exception if the file is not found or null
     */
    @FXML
    private void handleDownload() throws Exception {
        // Whatever the user has selected
        TreeItem<Item> selectedFile = ftable.getSelectionModel().getSelectedItem();

        if (selectedFile == null) return; //check null
        if (!(selectedFile.getValue() instanceof FileItem)) return; //check that file is selected and not folder

        // File Explorer for selecting where you want to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        fileChooser.setInitialFileName(((FileItem) selectedFile.getValue()).getFname());

        // Path to save the file to
        File localFilePath = fileChooser.showSaveDialog(downloadButton.getScene().getWindow());

        // If canceled; return
        if (localFilePath == null) return;

        // OutputStream
        OutputStream fos = Files.newOutputStream(localFilePath.toPath());

        // Use FTPServerFunctions to download file
        FTPServerFunctions.downloadFile((FileItem) selectedFile.getValue(), fos);

        // Refresh the table
        initializeScene();
    }

    /**
     * Handles the admin button
     * @throws IOException if the file is not found
     * @throws SQLException if the file is not found
     */
    @FXML
    public void handleFileAdmin() throws IOException, SQLException {
        if(FTPServerFunctions.isUserAdmin())
            FXMLSceneController.createPopUp("FTPAdmin.fxml", "Admin");
    }

    /**
     * Handles the logout button
     * @throws IOException if the file is not found
     */
    @FXML
    public void handleFileLogout() throws IOException
    {
        FXMLSceneController.swapScene("LoginPage");
        FTPServerFunctions.clearUsername();
    }

    /**
     * Handles the share button
     * @throws IOException if the file is not found
     */
    @FXML
    public void handleShareButton() throws IOException
    {
        // Whatever the user has selected
        TreeItem<Item> selectedFile = ftable.getSelectionModel().getSelectedItem();

        if (selectedFile == null) return; //check null
        if (!(selectedFile.getValue() instanceof FileItem)) return; //check that file is selected and not folder

        // Get selected file from tree
        FTPServerFunctions.setFile((FileItem) selectedFile.getValue());
        FXMLSceneController.createPopUp("FTPShare.fxml", "Share");
    }


    /**
     * Handles the search button
     * @param event the key event
     */
    @FXML
    private void handleKeyPress(KeyEvent event)
    {
        if(event.getCode().equals(KeyCode.ENTER))
        {
            handleSearch();
        }
    }

    /**
     * Handles the search button
     */
    @FXML
    private void handleSearch() {
        String searchString = searchField.getText().toLowerCase();
        searchAndSelectItem(ftable.getRoot(), searchString);
    }

    /**
     * Searches for the item with the specified name and selects it
     * @param root the root of the tree
     * @param searchString the name of the item to search for
     */
    private void searchAndSelectItem(TreeItem<Item> root, String searchString) {
        root.setExpanded(true); // Expand all nodes for better visibility

        for (TreeItem<Item> item : root.getChildren()) {
            // Check if the item's name contains the search string
            if (item.getValue().getFname().toLowerCase().contains(searchString)) {
                // Select the matching item
                ftable.getSelectionModel().select(item);
                ftable.scrollTo(item.getParent().getChildren().indexOf(item));
                return; // Stop searching after the first match
            }

            // Recursively search in the child items
            if (!item.getChildren().isEmpty()) {
                searchAndSelectItem(item, searchString);
            }
        }
    }

    private void displayError(String errorMessage)
    {
        errorDisplay.setText(errorMessage);
    }
}


