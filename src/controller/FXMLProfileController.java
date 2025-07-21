package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import model.UserData;

public class FXMLProfileController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label pwLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button clearPasswordButton;

    private UserList userList = new UserList();
    private UserData currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserProfile();
    }

    private void loadUserProfile() {
        currentUser = UserList.getLoginAccount();
        
        if (currentUser != null) {
            welcomeLabel.setText("Welcome " + currentUser.getNama() + "!");
            
            usernameLabel.setText(currentUser.getUsername());
            nameLabel.setText(currentUser.getNama());
            emailLabel.setText(currentUser.getEmail() != null && !currentUser.getEmail().isEmpty() 
                             ? currentUser.getEmail() 
                             : "Family account does not need email");
            roleLabel.setText(currentUser.getRole());
            
           
            pwLabel.setText(currentUser.getPassword());
        } else {
           
            welcomeLabel.setText("Welcome Guest!");
            usernameLabel.setText("Not logged in");
            nameLabel.setText("Not logged in");
            emailLabel.setText("Not logged in");
            roleLabel.setText("Guest");
            pwLabel.setText("No password");
        }
    }


    @FXML
    private void changePassword(ActionEvent event) {
        if (currentUser == null) {
            showAlert(AlertType.ERROR, "Error", "No user logged in!");
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

    
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "All password fields must be filled!");
            return;
        }


        if (!currentPassword.equals(currentUser.getPassword())) {
            showAlert(AlertType.ERROR, "Authentication Error", "Current password is incorrect!");
            return;
        }

     
        if (newPassword.length() < 8) {
            showAlert(AlertType.WARNING, "Validation Error", "New password must be at least 8 characters long!");
            return;
        }

     
        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.WARNING, "Validation Error", "New password and confirm password do not match!");
            return;
        }

        if (newPassword.equals(currentPassword)) {
            showAlert(AlertType.WARNING, "Validation Error", "New password must be different from current password!");
            return;
        }

      
        if (updatePasswordInDatabase(newPassword)) {
            showAlert(AlertType.INFORMATION, "Success", "Password has been changed successfully!");
            clearPasswordFields();
            
            
            pwLabel.setText(newPassword);
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to update password. Please try again.");
        }
    }

  
    private boolean updatePasswordInDatabase(String newPassword) {
        try {
            
            ArrayList<UserData> allUsers = userList.loadUser();
            
            boolean updated = false;
            
           
            for (UserData user : allUsers) {
                
                if (user.getUsername().equals(currentUser.getUsername()) && 
                    user.getRole().equals(currentUser.getRole())) {
                    user.setPassword(newPassword);
                    currentUser.setPassword(newPassword); 
                    updated = true;
                    break;
                }
                
                
                if (user.getRole().equals("Parent") && user.getFamily() != null) {
                    for (UserData family : user.getFamily()) {
                        if (family.getUsername().equals(currentUser.getUsername()) && 
                            currentUser.getRole().equals("Family")) {
                            family.setPassword(newPassword);
                            currentUser.setPassword(newPassword); 
                            updated = true;
                            break;
                        }
                    }
                    if (updated) break;
                }
            }
            
            if (updated) {
                
                saveToXML(allUsers);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }


    private void saveToXML(ArrayList<UserData> users) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("user", UserData.class);
        String xml = xstream.toXML(users);
        
        try (FileOutputStream f = new FileOutputStream("users.xml")) {
            f.write(xml.getBytes("UTF-8"));
        }
    }


    @FXML
    private void clearPasswordFields(ActionEvent event) {
        clearPasswordFields();
    }
    
    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

   
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void refreshProfile() {
        loadUserProfile();
    }
}
