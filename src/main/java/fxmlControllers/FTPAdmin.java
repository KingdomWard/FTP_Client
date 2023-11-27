package fxmlControllers;

import Exceptions.UserAlreadyExists;
import Utility.FTPServerFunctions;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class FTPAdmin
{
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    @FXML
    private Label errorLabel;
    @FXML
    private CheckBox isAdmin;

    /**
     * Clears the username and password fields
     */
    public void clearFields() {userField.setText(""); passField.setText("");}

    /**
     * Deletes user from the database
     */
    public void deleteUser()
    {
        try{
            FTPServerFunctions.deleteUser(userField.getText());
            clearFields();
        }catch(SQLException e){
            showError("User does not exist!");
        }
    }

    /**
     * Creates a new user in the database
     * @throws UserAlreadyExists if the user already exists
     * @throws SQLException if the username or password contains invalid characters
     */
    public void createUser()
    {
        try {
            if(isAdmin.isSelected()) {
                FTPServerFunctions.addUser(userField.getText(), passField.getText(), true);
                clearFields();
            }
            else {
                FTPServerFunctions.addUser(userField.getText(), passField.getText(), false);
                clearFields();
            }
        }
        catch (UserAlreadyExists e) {
            showError("Account Already Exists!");
            clearFields();
        }
        catch (SQLException e){
            showError("Username or password contains invalid characters!");
            clearFields();
        }
    }

    /**
     * Reactivates a user in the database
    */
    public void reactivateUser()
    {
        try{
            FTPServerFunctions.reactivateUser(userField.getText());
            clearFields();
        }
        catch(SQLException e){
            showError("User does not exist!");
            clearFields();
        }
    }

    /**
     * Shows an error message
     */
    private void showError(String message)
    {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

}
