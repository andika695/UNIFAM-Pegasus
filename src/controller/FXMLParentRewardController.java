package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ArrayList; // Import ArrayList kustom Anda
import model.Reward;
import model.RewardManager;

import java.util.Optional;

public class FXMLParentRewardController {

    @FXML
    private TableView<Reward> rewardTable;
    @FXML
    private TableColumn<Reward, String> colName;
    @FXML
    private TableColumn<Reward, Integer> colPoint;

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPoint;

    private ArrayList<Reward> rewardList;

    @FXML
    public void initialize() {

        this.rewardList = RewardManager.getInstance().getRewardList();

        colName.setCellValueFactory(new PropertyValueFactory<>("namaReward"));
        colPoint.setCellValueFactory(new PropertyValueFactory<>("pointReward"));

        refreshTable();

        rewardTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getNamaReward());
                txtPoint.setText(String.valueOf(newSelection.getPointReward()));
            }
        });
    }

    private void refreshTable() {
        rewardTable.getItems().clear();
        for (Reward reward : rewardList) {
            rewardTable.getItems().add(reward);
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPoint.clear();
        rewardTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateInput() {
        String name = txtName.getText();
        String pointStr = txtPoint.getText();
        String errorMessage = "";

        if ((name == null || name.trim().isEmpty()) && (pointStr == null || pointStr.trim().isEmpty())) {
            errorMessage = "Nama reward dan jumlah poin tidak boleh kosong.";
        } else if (name == null || name.trim().isEmpty()) {
            errorMessage = "Nama reward tidak boleh kosong.";
        } else if (pointStr == null || pointStr.trim().isEmpty()) {
            errorMessage = "Jumlah poin tidak boleh kosong.";
        }

        if (!pointStr.trim().isEmpty()) {
            try {
                int point = Integer.parseInt(pointStr);
                if (point <= 0) {
                    if (!errorMessage.isEmpty()) {
                        errorMessage += "\n";
                    }
                    errorMessage += "Poin harus lebih dari 0.";
                }
            } catch (NumberFormatException e) {
                if (!errorMessage.isEmpty()) {
                    errorMessage += "\n";
                }
                errorMessage += "Poin hanya boleh diisi dengan angka.";
            }
        }

        if (!errorMessage.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", "Harap perbaiki kesalahan berikut:", errorMessage);
            return false;
        }

        return true;
    }

    @FXML
    private void handleAdd() {
        if (validateInput()) {
            String name = txtName.getText();
            int point = Integer.parseInt(txtPoint.getText());

            // Menambahkan data ke ArrayList kustom
            rewardList.add(new Reward(name, point));
            RewardManager.getInstance().saveToXML();

            refreshTable();
            clearFields();

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Berhasil Ditambahkan",
                    "Reward baru telah berhasil disimpan.");
        }
    }

    @FXML
    private void handleEdit() {
        Reward selected = rewardTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (validateInput()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Konfirmasi Edit");
                confirmAlert.setHeaderText("Anda akan mengubah reward: " + selected.getNamaReward());
                confirmAlert.setContentText("Apakah Anda yakin ingin melanjutkan perubahan?");

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    selected.setNamaReward(txtName.getText());
                    selected.setPointReward(Integer.parseInt(txtPoint.getText()));

                    rewardTable.refresh();
                    RewardManager.getInstance().saveToXML();
                    clearFields();

                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Berhasil Diubah",
                            "Perubahan pada reward telah berhasil disimpan.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Seleksi", "Item Belum Dipilih",
                    "Silakan pilih reward yang ingin diubah dari tabel.");
        }
    }

    @FXML
    private void handleDelete() {
        Reward selected = rewardTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            clearFields();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText("Anda akan menghapus reward: " + selected.getNamaReward());
            alert.setContentText("Apakah Anda yakin ingin melanjutkan?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deletedRewardName = selected.getNamaReward();

                rewardList.remove(selected);
                RewardManager.getInstance().saveToXML();

                refreshTable();

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Berhasil Dihapus",
                        "Reward '" + deletedRewardName + "' telah dihapus.");
            }

        } else {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Seleksi", "Item Belum Dipilih",
                    "Silakan pilih reward yang ingin dihapus dari tabel.");
        }
    }
}