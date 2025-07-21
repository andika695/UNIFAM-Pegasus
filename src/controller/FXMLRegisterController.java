package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.UserData;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import app.OpenScene;

public class FXMLRegisterController implements Initializable {
    OpenScene openScene = new OpenScene();

    @FXML
    private Label labelParent;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private TextField txtParentName;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField tfUsia;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Button registerButton;

    @FXML
    Hyperlink loginHyperlink;

    UserList userList = new UserList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userList.loadUser();

        cbRole.getItems().addAll("Parent", "Family");
        cbRole.setValue("Parent");

        txtParentName.setVisible(false);
        labelParent.setVisible(false);

    }

    @FXML
    private void handleRoleChange(ActionEvent event) {
        String role = cbRole.getValue();
        boolean isFamily = "Family".equalsIgnoreCase(role);
        txtParentName.setVisible(isFamily);
        labelParent.setVisible(isFamily);
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        registerButton.setDisable(true);

        String name = txtName.getText();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String usia = tfUsia.getText();
        String role = cbRole.getValue();

        // Validasi input
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || usia.isEmpty()) {
            showAlert("Registration Failed", "All fields must be filled out.");
            registerButton.setDisable(false); // Aktifkan kembali tombol
            return;
        }

        if ("Parent".equalsIgnoreCase(role)) {
            if (email.isEmpty()) {
                showAlert("Gagal", "Email wajib diisi untuk akun Parent.");
                registerButton.setDisable(false);
                return;
            }
            if (!isValidEmail(email)) {
                showAlert("Gagal", "Format email tidak valid.");
                registerButton.setDisable(false);
                return;
            }

            if (isEmailTaken(email)) {
                showAlert("Registration Failed", "Email already registered.");
                registerButton.setDisable(false); // Aktifkan kembali tombol
                return;
            }
        }

        if (password.length() < 8) {
            showAlert("Registration Failed", "Password must be at least 8 characters.");
            registerButton.setDisable(false); // Aktifkan kembali tombol
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.");
            registerButton.setDisable(false); // Aktifkan kembali tombol
            return;
        }

        // Cek apakah username atau email sudah terdaftar
        if (isUsernameTaken(username)) {
            showAlert("Registration Failed", "Username already taken.");
            registerButton.setDisable(false); // Aktifkan kembali tombol
            return;
        }

        if ("Parent".equalsIgnoreCase(role)) {
            registerParentUser(name, username, email, password, usia);
        } else if ("Family".equalsIgnoreCase(role)) {
            String parentName = txtParentName.getText().trim();
            if (parentName.isEmpty()) {
                showAlert("Gagal", "Masukkan nama Parent.");
                registerButton.setDisable(false);
                return;
            }

            registerFamilyUser(name, username, password, parentName, usia);
        }

        registerButton.setDisable(false); // Aktifkan kembali tombol jika ada error sebelum redirect
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*@(?:[a-zA-Z0-9]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isUsernameTaken(String username) {
        for (UserData user : userList.getUserList()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmailTaken(String email) {
        for (UserData user : userList.getUserList()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void directToLogin() {
        Pane page = openScene.getPane("/view/FXMLLogin");
        mainPane.setCenter(page);
        System.out.println("Direct to login!");
    }

    @FXML
    private void handleLoginHyperlink(ActionEvent event) {
        directToLogin();
    }

    private void registerParentUser(String name, String username, String email, String password, String usia) {
        UserData user = new UserData(name, username, email, password, "Parent", usia, "");

        ArrayList<UserData> users = userList.loadUser();
        if (users == null)
            users = new ArrayList<>();

        users.add(user);
        saveToXML(users);
        showAlert("Berhasil", "Akun Parent berhasil dibuat.");
        directToLogin();
    }

    private void registerFamilyUser(String name, String username, String password, String parentName, String usia) {
        ArrayList<UserData> users = userList.loadUser();
        boolean parentFound = false;

        for (UserData parent : users) {
            if (parent.getRole().equals("Parent") && parent.getNama().equalsIgnoreCase(parentName)) {
                UserData family = new UserData(name, username, "", password, "Family", usia, "");
                parent.getFamily().add(family);
                parentFound = true;
                break;
            }
        }

        if (parentFound) {
            saveToXML(users);
            showAlert("Berhasil", "Akun Family berhasil ditambahkan.");
            directToLogin();
        } else {
            showAlert("Gagal", "Parent tidak ditemukan.");
        }
    }

    private void saveToXML(ArrayList<UserData> users) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("user", UserData.class);
        String xml = xstream.toXML(users);
        try (FileOutputStream f = new FileOutputStream("users.xml")) {
            f.write(xml.getBytes("UTF-8"));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}