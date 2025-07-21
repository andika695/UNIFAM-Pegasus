package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import model.Challenges;
import model.UserData;
import model.ChallengesManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLFamilyChallengesController implements Initializable {

    @FXML
    private Label lbTotalChallenges;

    @FXML
    private FlowPane fpChallenge;

    private ObservableList<Challenges> filteredChallenges = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChallengesManager.getInstance().loadFromXML();

        UserData loggedInUser = UserList.getLoginAccount();
        if (loggedInUser == null) return;

        String namaLogin = loggedInUser.getNama();

        ObservableList<Challenges> allChallenges = ChallengesManager.getInstance().getChallengeList();
        filteredChallenges.clear();
        for (Challenges c : allChallenges) {
            if (c.getName().equalsIgnoreCase(namaLogin) && c.getStatus().equalsIgnoreCase("belum selesai")) {
                filteredChallenges.add(c);
            }
        }


        lbTotalChallenges.textProperty().bind(
                Bindings.size(filteredChallenges).asString("Total Challenges: %d")
        );

        filteredChallenges.addListener((ListChangeListener<Challenges>) change -> {
            refreshFlowPane(filteredChallenges);
        });

        refreshFlowPane(filteredChallenges);
    }

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

    private void refreshFlowPane(ObservableList<Challenges> challengeList) {
        fpChallenge.getChildren().clear();

        for (Challenges challenge : challengeList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLChallengesCard.fxml"));
                AnchorPane card = loader.load();

                Label challengeLabel = (Label) card.lookup("#cardchallengeLabel");
                Label pesanLabel = (Label) card.lookup("#cardPesanlabel");
                Label dateLabel = (Label) card.lookup("#cardDateLabel");
                Button tombolSelesai = (Button) card.lookup("#cardtombolSelesai");

                if (challengeLabel != null) challengeLabel.setText(challenge.getChallenge());
                if (pesanLabel != null) pesanLabel.setText(challenge.getPesan());
                if (dateLabel != null) dateLabel.setText(challenge.getDate());
                if (tombolSelesai != null) {
                    tombolSelesai.setOnAction(e -> handleSelesai(challenge));
                }

                fpChallenge.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("❌ Gagal memuat kartu untuk: " + challenge.getChallenge());
            }
        }
    }

    private void handleSelesai(Challenges target) {
        ChallengesManager manager = ChallengesManager.getInstance();
        ObservableList<Challenges> all = manager.getChallengeList();
        LocalDate date = LocalDate.now();
        String strDate = date.toString();

        for (Challenges c : all) {
            if (c.getName().equalsIgnoreCase(target.getName())
                    && c.getChallenge().equalsIgnoreCase(target.getChallenge())
                    && c.getPesan().equalsIgnoreCase(target.getPesan())) {
                c.setStatus("selesai");
                c.setDate(strDate);
                break;
            }
        }

        filteredChallenges.remove(target);
        manager.saveToXML();

        peringatan(Alert.AlertType.INFORMATION, "Selamat", "Selamat anda menyelesaikan "+target.getChallenge(), false);
    }
}
