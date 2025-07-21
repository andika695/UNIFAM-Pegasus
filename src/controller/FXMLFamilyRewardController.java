package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import model.ArrayList; 
import model.Reward;
import model.RewardManager;

import java.io.IOException;

public class FXMLFamilyRewardController {

    @FXML
    private FlowPane rewardFlowPane;

    @FXML
    private Label labelTotalReward;

    private void handleClaimReward(Reward reward) {
        if (reward != null) {
            String claimedRewardName = reward.getNamaReward();
            
            RewardManager.getInstance().getRewardList().remove(reward);
            RewardManager.getInstance().saveToXML();

            
            refreshFlowPane();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil Klaim Reward");
            alert.setHeaderText(null);
            alert.setContentText("Kamu berhasil mengklaim: " + claimedRewardName + " 🎉");
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        refreshFlowPane();
    }

    private void refreshFlowPane() {
        ArrayList<Reward> rewards = RewardManager.getInstance().getRewardList();

        labelTotalReward.setText("Total Reward: " + rewards.size());

        rewardFlowPane.getChildren().clear(); 

        for (Reward reward : rewards) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLRewardCard.fxml"));
                AnchorPane rewardCardPane = loader.load();

                Label nameLabel = (Label) rewardCardPane.lookup("#cardNameLabel");
                Label pointLabel = (Label) rewardCardPane.lookup("#cardPointLabel");
                Button claimButton = (Button) rewardCardPane.lookup("#cardClaimButton");

                if (nameLabel != null) {
                    nameLabel.setText(reward.getNamaReward());
                }
                if (pointLabel != null) {
                    pointLabel.setText("Poin: " + reward.getPointReward());
                }
                if (claimButton != null) {
                    claimButton.setOnAction(e -> handleClaimReward(reward));
                }

                rewardFlowPane.getChildren().add(rewardCardPane);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Gagal memuat RewardCard.fxml untuk reward: " + reward.getNamaReward() + ". Error: "
                        + e.getMessage());
            }
        }
    }
}
