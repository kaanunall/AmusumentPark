package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KampanyaDuzenleController {
    @FXML private TableView<Kampanya> kampanyaTableView;
    @FXML private TableColumn<Kampanya, Integer> kampanyaIdColumn;
    @FXML private TableColumn<Kampanya, String> aciklamaColumn;
    @FXML private TextField kampanyaIdField;
    @FXML private TextArea aciklamaField;
    @FXML private Button guncelleButton;
    @FXML private Button iptalButton;
    
    private Kampanya secilenKampanya;
    
    @FXML
    private void initialize() {
        // Tablo sütunlarını ayarla
        kampanyaIdColumn.setCellValueFactory(new PropertyValueFactory<>("kampanyaId"));
        aciklamaColumn.setCellValueFactory(new PropertyValueFactory<>("aciklama"));
        
        // Kampanyaları yükle
        kampanyalariYukle();
        
        // Tablo satır seçim olayını dinle
        kampanyaTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    secilenKampanya = newValue;
                    kampanyaIdField.setText(String.valueOf(newValue.getKampanyaId()));
                    aciklamaField.setText(newValue.getAciklama());
                }
            }
        );
    }
    
    private void kampanyalariYukle() {
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "SELECT * FROM kampanyabilgi ORDER BY KampanyaID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            ObservableList<Kampanya> kampanyaListesi = FXCollections.observableArrayList();
            while (rs.next()) {
                kampanyaListesi.add(new Kampanya(
                    rs.getInt("KampanyaID"),
                    rs.getString("Aciklama")
                ));
            }
            
            kampanyaTableView.setItems(kampanyaListesi);
        } catch (SQLException e) {
            showAlert("Hata", "Kampanyalar yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void guncelleButton_click() {
        if (secilenKampanya == null) {
            showAlert("Uyarı", "Lütfen bir kampanya seçin");
            return;
        }
        
        String kampanyaIdStr = kampanyaIdField.getText().trim();
        String aciklama = aciklamaField.getText().trim();
        
        if (kampanyaIdStr.isEmpty()) {
            showAlert("Uyarı", "Lütfen kampanya ID'si girin");
            return;
        }
        
        if (aciklama.isEmpty()) {
            showAlert("Uyarı", "Lütfen kampanya açıklaması girin");
            return;
        }
        
        try {
            int kampanyaId = Integer.parseInt(kampanyaIdStr);
            
            // Veritabanını güncelle
            try (Connection conn = DriverManager.getConnection("**", "root", "")) {
                String sql = "UPDATE kampanyabilgi SET KampanyaID = ?, Aciklama = ? WHERE KampanyaID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, kampanyaId);
                stmt.setString(2, aciklama);
                stmt.setInt(3, secilenKampanya.getKampanyaId());
                stmt.executeUpdate();
                
                showAlert("Başarılı", "Kampanya başarıyla güncellendi");
                
                // Tabloyu yenile
                kampanyalariYukle();
                
                // Formu temizle
                kampanyaIdField.clear();
                aciklamaField.clear();
                secilenKampanya = null;
            } catch (SQLException e) {
                showAlert("Hata", "Kampanya güncellenirken bir hata oluştu: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Uyarı", "Kampanya ID'si sayı olmalıdır");
        }
    }
    
    @FXML
    private void iptalButton_click() {
        // Pencereyi kapat
        Stage stage = (Stage) iptalButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Yardımcı sınıf
    public static class Kampanya {
        private int kampanyaId;
        private String aciklama;
        
        public Kampanya(int kampanyaId, String aciklama) {
            this.kampanyaId = kampanyaId;
            this.aciklama = aciklama;
        }
        
        public int getKampanyaId() { return kampanyaId; }
        public String getAciklama() { return aciklama; }
    }
} 