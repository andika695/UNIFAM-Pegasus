package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.UserData;
import model.RewardManager; 

import java.net.URL;
import java.util.ResourceBundle;

import app.OpenScene;

public class FXMLLoginController implements Initializable {

    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerHyperlink;

    private UserList userList = new UserList();

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        loginButton.setDisable(true);

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Username and password cannot be empty.");
            loginButton.setDisable(false); 
            return;
        }

        UserData loggedInUser = isValidLogin(username, password);

        if (loggedInUser != null) {
            showAlert(AlertType.INFORMATION, "Login Success", "Welcome " + loggedInUser.getNama() + "!");

            if (loggedInUser.getRole().equals("Parent")) {
                RewardManager.getInstance().setCurrentUser(loggedInUser.getUsername());
            } else if (loggedInUser.getRole().equals("Family")) {
                UserData parentUser = findParentOfFamily(loggedInUser);
                if (parentUser != null) {
                    RewardManager.getInstance().setCurrentUser(parentUser.getUsername());
                } else {
                    RewardManager.getInstance().setCurrentUser(loggedInUser.getUsername());
                }
            }

            if (loggedInUser.getRole().equals("Parent")) {
                directToParentDashboard();
            } else if (loggedInUser.getRole().equals("Family")) {
                OpenScene object = new OpenScene();
                Pane halaman = object.getPane("/view/FXMLFamilyDashboard");
                mainPane.setCenter(halaman);
            } else {
                directToParentDashboard();
            }
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Username or password not registered.");
            loginButton.setDisable(false); 
        }
    }

    private UserData findParentOfFamily(UserData familyUser) {
        if (userList != null && !userList.getUserList().isEmpty()) {
            for (UserData user : userList.getUserList()) {
                if (user.getRole().equals("Parent") && user.getFamily() != null) {
                    for (UserData family : user.getFamily()) {
                        if (family.getUsername().equals(familyUser.getUsername())) {
                            return user;
                        }
                    }
                }
            }
        }
        return null;
    }

    @FXML
    private void handleRegisterHyperlink(ActionEvent event) {
        directToRegistrationPage();
    }

    private void directToRegistrationPage() {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLRegister");
        mainPane.setCenter(halaman);
    }

    private void directToParentDashboard() {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentDashboard");
        mainPane.setCenter(halaman);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList.loadUser();
    }

    private UserData isValidLogin(String username, String password) {
        if (userList != null && !userList.getUserList().isEmpty()) {
            for (UserData user : userList.getUserList()) { 
                if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                    UserList.setLoginAccount(user);
                    return user; 
                }

                if (user.getFamily() != null) {
                    for (UserData family : user.getFamily()) {
                        if (username.equals(family.getUsername()) && password.equals(family.getPassword())) {
                            UserList.setLoginAccount(family);
                            return family;
                        }
                    }
                }
            }
        }
        return null; 
    }
}
