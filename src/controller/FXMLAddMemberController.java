package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.UserData;

public class FXMLAddMemberController implements Initializable {

    private ObservableList<UserData> data = FXCollections.observableArrayList();

    private boolean Delete = false;

    @FXML
    private TextField TxNama;

   
    @FXML
    private TextField TxUsername;

    @FXML
    private Button BtnAdd;

    @FXML
    private Button BtnDelete;

    @FXML
    private DatePicker DPTanggalLahir;

    @FXML
    private TableView<UserData> ViewData;

    @FXML
    private TableColumn<UserData, String> ColumnNama;

    @FXML
    private TableColumn<UserData, String> ColumnTanggalLahir;

    @FXML
    private TableColumn<UserData, String> ColumnUsia;

    @FXML
    private TableColumn<UserData, String> ColumnUsername;

    private UserList userList = new UserList();
    private UserData userLogin;
    private UserData selectedUser;

    private void saveToXML(ArrayList<UserData> users) {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[] { UserData.class });
        xstream.alias("user", UserData.class);
        String xml = xstream.toXML(users);
        try (FileOutputStream f = new FileOutputStream("users.xml")) {
            f.write(xml.getBytes("UTF-8"));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean isUsernameTaken(String username) {
        for (UserData user : userList.getUserList()) {
            if (user.getUsername().equalsIgnoreCase(username))
                return true;
            for (UserData family : user.getFamily()) {
                if (family.getUsername().equalsIgnoreCase(username))
                    return true;
            }
        }
        return false;
    }

    

    @FXML
    private void ButtonTambah(ActionEvent event) {
        String nama = TxNama.getText();
        String username = TxUsername.getText();

        LocalDate tanggalLahirDate = DPTanggalLahir.getValue();
        if (nama.isEmpty() || username.isEmpty() || tanggalLahirDate == null) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Harap isi semua field dan pilih tanggal lahir.",
                    false);
            return;
        }

        String tanggallahir = tanggalLahirDate.toString();

        if (isUsernameTaken(username)) {
            showAlert(Alert.AlertType.WARNING, "Gagal", "Username sudah digunakan.", false);
            return;
        }

        if (userLogin != null && "Parent".equalsIgnoreCase(userLogin.getRole())) {
            UserData newFamily = new UserData(nama, username, "", "12345678", "Family", "", tanggallahir);
            userLogin.getFamily().add(newFamily);
            data.add(newFamily);

            for (UserData user : userList.getUserList()) {
                if (user.getUsername().equals(userLogin.getUsername())) {
                    user.setFamily(userLogin.getFamily());
                    break;
                }
            }

            saveToXML(userList.getUserList());
            ViewData.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data berhasil ditambahkan.", false);
            clearFields();

        }
    }

    @FXML
    private void HapusData(ActionEvent event) {
        if (selectedUser != null && userLogin != null) {
            clearFields();
            boolean confirm = showAlert(Alert.AlertType.CONFIRMATION, "Konfirmasi Hapus",
                    "Yakin ingin menghapus data ini?", true);
            if (!confirm)
                return;

            userLogin.getFamily().remove(selectedUser);
            data.remove(selectedUser);

            for (UserData user : userList.getUserList()) {
                if (user.getUsername().equals(userLogin.getUsername())) {
                    user.setFamily(userLogin.getFamily());
                    break;
                }
            }

            saveToXML(userList.getUserList());
            ViewData.refresh();
        } else {
            showAlert(Alert.AlertType.WARNING, "Error", "Pilih data yang ingin dihapus.", false);
        }
    }

    @FXML
    private void UpdateData(ActionEvent event) {
        UserData selectedItem = ViewData.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String nama = TxNama.getText();
            String username = TxUsername.getText();
            LocalDate tanggalLahir = DPTanggalLahir.getValue();

            if (nama.isEmpty() || username.isEmpty() || tanggalLahir == null) {
                showAlert(Alert.AlertType.WARNING, "Error", "Isi semua field untuk update!", false);
                return;
            }

            if (isUsernameTaken(username) && !username.equals(selectedItem.getUsername())) {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Username sudah digunakan.", false);
                return;
            }

            int umur = hitungUmur(tanggalLahir, LocalDate.now());

            selectedItem.setNama(nama);
            selectedItem.setUsername(username);
            selectedItem.settanggallahir(tanggalLahir.toString());
            selectedItem.setUsia(String.valueOf(umur));
            clearFields();

            // Simpan dan refresh tampilan
            saveToXML(userList.getUserList());
            ViewData.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data berhasil diupdate.", false);
        } else {
            showAlert(Alert.AlertType.WARNING, "Error", "Pilih data yang ingin diupdate.", false);
        }
    }

    private boolean showAlert(Alert.AlertType alertType, String title, String message, boolean waitForResponse) {
        Alert alert = new Alert(alertType);
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

    private void clearFields() {
        TxNama.clear();
        TxUsername.clear();
        DPTanggalLahir.setValue(null);
    }

    private int hitungUmur(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate != null && currentDate != null && !birthDate.isAfter(currentDate)) {
            return Period.between(birthDate, currentDate).getYears();
        }
        return 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLogin = UserList.getLoginAccount();
        if (userLogin != null && "Parent".equalsIgnoreCase(userLogin.getRole())) {
            data.addAll(userLogin.getFamily());
        }

        ColumnNama.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
        ColumnUsia.setCellValueFactory(cellData -> {
            String tanggalLahirStr = cellData.getValue().gettanggallahir();
            try {
                LocalDate tanggalLahir = LocalDate.parse(tanggalLahirStr);
                int umur = hitungUmur(tanggalLahir, LocalDate.now());
                return new SimpleStringProperty(String.valueOf(umur));
            } catch (Exception e) {
                return new SimpleStringProperty("-");
            }
        });
        ColumnUsername.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        ColumnTanggalLahir.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().gettanggallahir()));

        ViewData.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (!Delete && newSelection != null) {
                selectedUser = newSelection;
                TxNama.setText(newSelection.getNama());
                TxUsername.setText(newSelection.getUsername());

                try {
                    LocalDate tanggalLahir = LocalDate.parse(newSelection.gettanggallahir());
                    DPTanggalLahir.setValue(tanggalLahir);

                } catch (Exception e) {
                    DPTanggalLahir.setValue(null);

                }
            }
        });

        ViewData.setItems(data);

        // Hitung usia otomatis saat tanggal lahir dipilih
        DPTanggalLahir.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

            }
        });
    }
}
