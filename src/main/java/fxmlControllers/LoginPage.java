package fxmlControllers;

import Connections.FTPConnection;
import Utility.ControllerFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginPage {

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel; // The Label for displaying the error message

    @FXML
    private void handleLogin(ActionEvent event) throws SQLException, IOException {
        // Get the values from the username and password fields
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        // try logging in and check if it failed
        if (!FTPConnection.connect(username, password)){
            errorLabel.setVisible(true);
            passwordInput.setText(""); // clear input field
        }
        else{
            ControllerFunctions.swapScene("fmxlmain", loginButton);
        }
    }
}
