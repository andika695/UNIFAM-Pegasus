package controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import app.OpenScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class FXMLFamilyDashboard implements Initializable {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button logOut;


    private boolean peringatan(Alert.AlertType type, String title, String message, boolean waitForResponse) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (waitForResponse) {
            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
        } else {
            alert.show();
            return true;
        }
    }

    @FXML
    private void logoutButton(ActionEvent event){
        boolean cek = peringatan(Alert.AlertType.CONFIRMATION, "logout", "anda yakin mau keluar?",true);
        if (cek) {
            OpenScene object = new OpenScene();
            Pane page = object.getPane("/view/FXMLLogin");
            mainPane.setCenter(page); 
            System.out.println("User logged out.");
        } else {
            System.out.println("Logout dibatalkan.");
        }
    }

    @FXML
    private void handleFamilyDashboard(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLFamilyDashboard");
        mainPane.setCenter(halaman);
    }
    
    @FXML
    private void handleFamilyReward(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLFamilyReward");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void handleExercise(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLFamilyExercise");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void handleFamilyChat(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLFamilyChat");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void handleFamilyChallenges(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLChallengesFamily");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void handleFamilyProgres(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLFamilyProgres");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void handleFamilyProfile(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLProfile");
        mainPane.setCenter(halaman);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
