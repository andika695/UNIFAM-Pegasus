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
            
            // Tampilkan password user yang asli
            pwLabel.setText(currentUser.getPassword());
        } else {
            // Jika tidak ada user yang login
            welcomeLabel.setText("Welcome Guest!");
            usernameLabel.setText("Not logged in");
            nameLabel.setText("Not logged in");
            emailLabel.setText("Not logged in");
            roleLabel.setText("Guest");
            pwLabel.setText("No password");
        }
    }

    /**
     * Handle change password action
     */
    @FXML
    private void changePassword(ActionEvent event) {
        if (currentUser == null) {
            showAlert(AlertType.ERROR, "Error", "No user logged in!");
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validasi input kosong
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "All password fields must be filled!");
            return;
        }

        // Validasi current password
        if (!currentPassword.equals(currentUser.getPassword())) {
            showAlert(AlertType.ERROR, "Authentication Error", "Current password is incorrect!");
            return;
        }

        // Validasi panjang password baru
        if (newPassword.length() < 8) {
            showAlert(AlertType.WARNING, "Validation Error", "New password must be at least 8 characters long!");
            return;
        }

        // Validasi konfirmasi password
        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.WARNING, "Validation Error", "New password and confirm password do not match!");
            return;
        }

        // Validasi password baru tidak sama dengan yang lama
        if (newPassword.equals(currentPassword)) {
            showAlert(AlertType.WARNING, "Validation Error", "New password must be different from current password!");
            return;
        }

        // Update password
        if (updatePasswordInDatabase(newPassword)) {
            showAlert(AlertType.INFORMATION, "Success", "Password has been changed successfully!");
            clearPasswordFields();
            
            // Update password label dengan password baru
            pwLabel.setText(newPassword);
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to update password. Please try again.");
        }
    }

    /**
     * Update password di database (XML file)
     */
    private boolean updatePasswordInDatabase(String newPassword) {
        try {
            // Load all users
            ArrayList<UserData> allUsers = userList.loadUser();
            
            boolean updated = false;
            
            // Update password untuk user yang login
            for (UserData user : allUsers) {
                // Cek jika ini adalah user yang login (untuk Parent atau user biasa)
                if (user.getUsername().equals(currentUser.getUsername()) && 
                    user.getRole().equals(currentUser.getRole())) {
                    user.setPassword(newPassword);
                    currentUser.setPassword(newPassword); // Update juga current user object
                    updated = true;
                    break;
                }
                
                // Jika user yang login adalah Family, cari di dalam family list
                if (user.getRole().equals("Parent") && user.getFamily() != null) {
                    for (UserData family : user.getFamily()) {
                        if (family.getUsername().equals(currentUser.getUsername()) && 
                            currentUser.getRole().equals("Family")) {
                            family.setPassword(newPassword);
                            currentUser.setPassword(newPassword); // Update juga current user object
                            updated = true;
                            break;
                        }
                    }
                    if (updated) break;
                }
            }
            
            if (updated) {
                // Save ke XML
                saveToXML(allUsers);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Save data users ke XML file
     */
    private void saveToXML(ArrayList<UserData> users) throws IOException {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("user", UserData.class);
        String xml = xstream.toXML(users);
        
        try (FileOutputStream f = new FileOutputStream("users.xml")) {
            f.write(xml.getBytes("UTF-8"));
        }
    }

    /**
     * Clear semua password fields
     */
    @FXML
    private void clearPasswordFields(ActionEvent event) {
        clearPasswordFields();
    }
    
    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refresh profile data - berguna jika ada update dari tempat lain
     */
    public void refreshProfile() {
        loadUserProfile();
    }
}