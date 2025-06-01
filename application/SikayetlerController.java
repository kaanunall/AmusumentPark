package application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SikayetlerController {

    @FXML
    private ComboBox<String> turComboBox;
    @FXML
    private TextArea mesajTextArea;
    @FXML
    private Button gonderButton;

    private String kullaniciTC;
    private String kullaniciAd;
    private String kullaniciSoyad;

    @FXML
    public void initialize() {
        // ComboBox'ı doldur
        turComboBox.setItems(FXCollections.observableArrayList("Yorum", "Şikayet"));
    }

    public void setKullaniciTC(String tc) {
        this.kullaniciTC = tc;
        // TC'ye göre Ad ve Soyad bilgilerini çek
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "SELECT Ad, SoyAd FROM uyeler WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                this.kullaniciAd = rs.getString("Ad");
                this.kullaniciSoyad = rs.getString("SoyAd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı bilgileri alınırken bir hata oluştu.");
        }
    }

    @FXML
    private void gonderButton_click() {
        String tur = turComboBox.getValue();
        String mesaj = mesajTextArea.getText();

        if (kullaniciTC == null || kullaniciAd == null || kullaniciSoyad == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı bilgileri eksik.");
            return;
        }

        if (tur == null || tur.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen bir tür seçin (Yorum/Şikayet).");
            return;
        }

        if (mesaj == null || mesaj.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen mesajınızı girin.");
            return;
        }

        // Veritabanına kaydet
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "INSERT INTO sikayetler (TC, Ad, SoyAd, Mesaj, Tür, Tarih) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, kullaniciTC);
            pstmt.setString(2, kullaniciAd);
            pstmt.setString(3, kullaniciSoyad);
            pstmt.setString(4, mesaj);
            pstmt.setString(5, tur);
            pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Mesajınız başarıyla gönderildi.");
                // Alanları temizle
                turComboBox.setValue(null);
                mesajTextArea.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Mesaj gönderilirken bir hata oluştu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Veritabanı hatası: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 