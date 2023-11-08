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

    public void clearFields() {userField.setText(""); passField.setText("");}

    //deletes user from the database
    public void deleteUser()
    {
        try{
            FTPServerFunctions.deleteUser(userField.getText());
            clearFields();
        }catch(SQLException e){
            showError("User does not exist!");
        }
    }

    //adds user to the database
    public void createUser()
    {
        try
        {
            if(isAdmin.isSelected())
            {
                FTPServerFunctions.addUser(userField.getText(), passField.getText(), true);
                clearFields();
            }
            else
            {
                FTPServerFunctions.addUser(userField.getText(), passField.getText(), false);
                clearFields();
            }

        } catch (UserAlreadyExists e) {
            showError("Account Already Exists!");
            clearFields();
        } catch (SQLException e){
            showError("Username or password contains invalid characters!");
            clearFields();
        }
    }

    public void reactivateUser()
    {
        try{
            FTPServerFunctions.reactivateUser(userField.getText());
            clearFields();
        }catch(SQLException e){
            showError("User does not exist!");
            clearFields();
        }
    }

    //Display error message
    private void showError(String message)
    {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

}
