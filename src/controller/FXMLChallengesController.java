package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.Challenges;
import model.UserData;

public class FXMLChallengesController implements Initializable {

    @FXML
    private BorderPane mainPane;

    @FXML
    private ComboBox<String> cbNama;

    @FXML
    private TextField tfPesan;
    
    @FXML
    private TextField tfchallenge;

    @FXML
    private TextField tfIndex;

    @FXML
    private Button Add;

    @FXML
    private Button Edit;

    @FXML
    private Button delete;

    @FXML
    private TableView<Challenges> userTable;

    @FXML
    private TableColumn<Challenges, String> challengesColumn;

    @FXML
    private TableColumn<Challenges, String> pesanColumn;

    @FXML 
    private TableColumn<Challenges, String> dateColumm;

    @FXML
    private TableColumn<Challenges, String> statusColumn;

    private ArrayList<UserData> userList = new ArrayList<>();

    private ArrayList<Challenges> challengeList = new ArrayList<>();

    private void saveToXML() {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[] { Challenges.class });
        xstream.alias("challenge", Challenges.class);
        String xml = xstream.toXML(challengeList);

        try (FileOutputStream fos = new FileOutputStream("challenges.xml")) {
            fos.write(xml.getBytes("UTF-8"));
        } catch (IOException e) {
            System.out.println("Error saat menyimpan challenge XML: " + e.getMessage());
        }
    }

    private void bacaFileXML() {
        File file = new File("challenges.xml");
        if (!file.exists()) return;

        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[] { Challenges.class });
        xstream.alias("challenge", Challenges.class);

        try {
            challengeList = (ArrayList<Challenges>) xstream.fromXML(file);
        } catch (Exception e) {
            System.out.println("Gagal load challenge XML: " + e.getMessage());
        }
        userTable.sort();
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

    @FXML
    private void handleComboChanged(ActionEvent event) {
        String selectedNama = cbNama.getSelectionModel().getSelectedItem();
    if (selectedNama == null ) return;

    ObservableList<Challenges> filtered = FXCollections.observableArrayList();
    for (Challenges c : challengeList) {
        if (selectedNama.equalsIgnoreCase(c.getName())) {
            filtered.add(c);
        }
    }

    userTable.setItems(filtered);
    userTable.getSortOrder().setAll(statusColumn);
    statusColumn.setSortType(TableColumn.SortType.ASCENDING);
    userTable.sort();
    }

   @FXML
    private void addButton(ActionEvent event) {
        String nama = cbNama.getSelectionModel().getSelectedItem();
        String challengeText = tfchallenge.getText().trim();
        String pesanText = tfPesan.getText().trim();

        LocalDate date = LocalDate.now();
        String dateStr = date.toString();

        if (nama == null || nama.isEmpty()) {
            peringatan(Alert.AlertType.WARNING, "Pilih Nama", "Silakan pilih member terlebih dahulu.",false);
            return;
        }

        if (challengeText.isEmpty() || pesanText.isEmpty()) {
            peringatan(Alert.AlertType.WARNING, "Data Kosong", "Challenge dan Pesan tidak boleh kosong.", false);
            return;
        }

        Challenges dataBaru = new Challenges(dateStr,nama, challengeText, pesanText,"belum Selesai");
        challengeList.add(dataBaru);
        saveToXML(); // simpan ke challenges.xml

        tfchallenge.clear();
        tfPesan.clear();
        peringatan(Alert.AlertType.INFORMATION, "Sukses", "Challenge berhasil ditambahkan.", false);
        
        handleComboChanged(null);
        userTable.sort();
    }



    @FXML
    private void editButton(ActionEvent event) {
        Challenges selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            peringatan(Alert.AlertType.WARNING, "Tidak Ada Data", "Silakan pilih data challenge dari tabel untuk diedit.", false);
            return;
        }

        String newChallenge = tfchallenge.getText().trim();
        String newPesan = tfPesan.getText().trim();

        if (newChallenge.isEmpty() || newPesan.isEmpty()) {
            peringatan(Alert.AlertType.WARNING, "Kosong", "Challenge dan pesan tidak boleh kosong.", false);
            return;
        }

        selected.setChallenge(newChallenge);
        selected.setPesan(newPesan);

        saveToXML();          
        userTable.refresh();  
        tfchallenge.clear();
        tfPesan.clear();
        userTable.sort();

        peringatan(Alert.AlertType.INFORMATION, "Berhasil", "Challenge berhasil diperbarui.", false);
    }


    @FXML
    private void deleteButton(ActionEvent event){
        Challenges selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            peringatan(Alert.AlertType.WARNING, "Tidak Ada Data", "Silakan pilih data challenge dari tabel untuk dihapus.", false);
            return;
        }
        tfchallenge.clear();
        tfPesan.clear();
        boolean confirm = peringatan(Alert.AlertType.CONFIRMATION,
            "Konfirmasi Hapus",
            "Yakin ingin menghapus challenge ini?", true);

        if (!confirm) return;

        challengeList.remove(selected);

        saveToXML();          
        handleComboChanged(null);
        userTable.sort();

        peringatan(Alert.AlertType.INFORMATION, "Berhasil", "Challenge berhasil diperbarui.", false);
    }


    private void loadFamilyNamesOnly() {
    ObservableList<String> namaList = FXCollections.observableArrayList();

    UserData loggedIn = UserList.getLoginAccount();
    if (loggedIn != null && "Parent".equalsIgnoreCase(loggedIn.getRole())) {
        if (loggedIn.getFamily() != null) {
            for (UserData family : loggedIn.getFamily()) {
                namaList.add(family.getNama());
            }
        }
    }

    cbNama.setItems(namaList);
}


   @Override
    public void initialize(URL location, ResourceBundle resources) {

        userList = new UserList().loadUser();
        loadFamilyNamesOnly();
        
        cbNama.setOnAction(this::handleComboChanged);

        
        challengesColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getChallenge()));
        pesanColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPesan()));
        dateColumm.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        statusColumn.setCellFactory(column -> new javafx.scene.control.cell.TextFieldTableCell<Challenges, String>() {
            @Override
            public void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    if (status.equalsIgnoreCase("selesai")) {
                        setStyle("-fx-background-color: #d4fcd7; -fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #ffe0e0; -fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        statusColumn.setComparator((s1, s2) -> {
             String val1 = s1.toLowerCase();
            String val2 = s2.toLowerCase();
            if (val1.equals("belum selesai") && val2.equals("selesai")) return -1;
            if (val1.equals("selesai") && val2.equals("belum selesai")) return 1;
            return val1.compareTo(val2);
        });

        statusColumn.setSortType(TableColumn.SortType.ASCENDING);
        userTable.getSortOrder().add(statusColumn);

        
        bacaFileXML(); 
        userTable.sort();

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null) {
        tfchallenge.setText(newVal.getChallenge());
        tfPesan.setText(newVal.getPesan());
    }
});


 }
}
