package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Optional;

public class PersonelDuzenleController {
    @FXML private TableView<Personel> personelTable;
    @FXML private TableColumn<Personel, String> emailColumn;
    @FXML private TableColumn<Personel, String> sifreColumn;
    @FXML private TableColumn<Personel, String> adColumn;
    @FXML private TableColumn<Personel, String> soyadColumn;
    @FXML private TableColumn<Personel, String> tcColumn;
    @FXML private TableColumn<Personel, String> telefonColumn;
    @FXML private TableColumn<Personel, String> resimColumn;
    @FXML private TextField searchField;

    private ObservableList<Personel> personelList = FXCollections.observableArrayList();
    private static final String UPLOAD_DIR = "C:\\xampp\\htdocs\\uploads";

    @FXML
    public void initialize() {
        // Sütunları ayarla
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        sifreColumn.setCellValueFactory(new PropertyValueFactory<>("sifre"));
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        soyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        tcColumn.setCellValueFactory(new PropertyValueFactory<>("tc"));
        telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        resimColumn.setCellValueFactory(new PropertyValueFactory<>("resimYolu"));

        // Arama fonksiyonunu ayarla
        FilteredList<Personel> filteredData = new FilteredList<>(personelList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(personel -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return personel.getAd().toLowerCase().contains(lowerCaseFilter) ||
                       personel.getSoyad().toLowerCase().contains(lowerCaseFilter) ||
                       personel.getTc().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Personel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(personelTable.comparatorProperty());
        personelTable.setItems(sortedData);

        // Verileri yükle
        verileriYukle();
    }

    private void verileriYukle() {
        personelList.clear();
        String sql = "SELECT * FROM kullanicigiris";
        
        try (Connection conn = DriverManager.getConnection("**", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Personel personel = new Personel(
                    rs.getString("email"),
                    rs.getString("sifre"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("TC"),
                    rs.getString("Telefon"),
                    rs.getString("resim_yolu")
                );
                personelList.add(personel);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Veriler yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void yeniPersonelEkle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personelEkle.fxml"));
            Parent root = loader.load();
            PersonelEkleController controller = loader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Yeni Personel Ekle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setPersonelDuzenleController(this);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Personel ekleme sayfası açılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void personelDuzenle() {
        Personel seciliPersonel = personelTable.getSelectionModel().getSelectedItem();
        if (seciliPersonel == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen düzenlenecek personeli seçin.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personelDuzenleDetay.fxml"));
            Parent root = loader.load();
            PersonelDuzenleDetayController controller = loader.getController();
            
            controller.setPersonel(seciliPersonel);
            controller.setPersonelDuzenleController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Personel Düzenle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Personel düzenleme sayfası açılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void personelSil() {
        Personel seciliPersonel = personelTable.getSelectionModel().getSelectedItem();
        if (seciliPersonel == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silinecek personeli seçin.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Onay");
        alert.setHeaderText("Personel Silme");
        alert.setContentText(seciliPersonel.getAd() + " " + seciliPersonel.getSoyad() + " isimli personeli silmek istediğinize emin misiniz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM kullanicigiris WHERE TC = ?";
            
            try (Connection conn = DriverManager.getConnection("**", "root", "");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, seciliPersonel.getTc());
                pstmt.executeUpdate();
                
                // Resmi sil
                if (seciliPersonel.getResimYolu() != null && !seciliPersonel.getResimYolu().isEmpty()) {
                    Path resimYolu = Paths.get(seciliPersonel.getResimYolu());
                    Files.deleteIfExists(resimYolu);
                }
                
                personelList.remove(seciliPersonel);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Personel başarıyla silindi.");
                
            } catch (SQLException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Personel silinirken bir hata oluştu: " + e.getMessage());
            }
        }
    }

    @FXML
    private void kapat() {
        Stage stage = (Stage) personelTable.getScene().getWindow();
        stage.close();
    }

    public void verileriYenile() {
        verileriYukle();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}