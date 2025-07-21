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

public class FXMLParentDashboardController implements Initializable {

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
        
       boolean cek = peringatan(Alert.AlertType.CONFIRMATION, "logout", "anda yakin mau keluar!!!",true);


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
    private void keHalaman0(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentDashboard");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman1(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLChallengesParent");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman2(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLAddMember");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman3(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentExercise");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman4(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentReward");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman5(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentChat");
        mainPane.setCenter(halaman);

    }

    @FXML
    private void keHalaman6(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLParentProgres");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void keHalaman7(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/view/FXMLProfile");
        mainPane.setCenter(halaman);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
