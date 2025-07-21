package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import model.UserData;
import model.Challenges;
import model.ChallengesManager;

public class FXMLParentProgresController implements Initializable {

    @FXML
    private BarChart<String, Number> nama;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserData parent = UserList.getLoginAccount();
        if (parent == null || !"Parent".equalsIgnoreCase(parent.getRole())) return;

        ChallengesManager.getInstance().loadFromXML();
        ObservableList<Challenges> challengeList = ChallengesManager.getInstance().getChallengeList();

        HashMap<String, Integer> completedCounts = new HashMap<>();

        for (UserData family : parent.getFamily()) {
            String namaAnak = family.getNama();
            int count = 0;

            for (Challenges c : challengeList) {
                if (c.getName().equalsIgnoreCase(namaAnak) &&
                    c.getStatus().equalsIgnoreCase("selesai")) {
                    count++;
                }
            }

            completedCounts.put(namaAnak, count);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Progress");

        for (Map.Entry<String, Integer> entry : completedCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        nama.getData().add(series);
    }
}
