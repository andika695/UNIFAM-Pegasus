package controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import model.Challenges;
import model.ChallengesManager;
import model.UserData;

public class FXMLFamilyProgresController implements Initializable {

    @FXML
    private LineChart<String, Number> progres;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progres.setTitle("Progress Harian");

        UserData loggedIn = UserList.getLoginAccount();
        if (loggedIn == null || !loggedIn.getRole().equalsIgnoreCase("Family")) return;

        String namaLogin = loggedIn.getNama();

        ChallengesManager.getInstance().loadFromXML();
        List<Challenges> all = ChallengesManager.getInstance().getChallengeList();

        Map<String, Integer> completedPerDate = new TreeMap<>();
        for (Challenges c : all) {
            if (c.getName().equalsIgnoreCase(namaLogin) && "selesai".equalsIgnoreCase(c.getStatus())) {
                completedPerDate.put(c.getDate(), completedPerDate.getOrDefault(c.getDate(), 0) + 1);
            }
        }

        // Masukkan ke LineChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Challenge Selesai");

        for (Map.Entry<String, Integer> entry : completedPerDate.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        progres.getData().add(series);
    }
}
