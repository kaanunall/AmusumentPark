package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DogrulamaController {
    @FXML
    private TextField kodField;
    @FXML
    private Button dogrulaButton;
    @FXML
    private Label statusLabel;
    
    private String tc;
    private String email;
    
    public void setKullaniciBilgileri(String tc, String email) {
        this.tc = tc;
        this.email = email;
    }
    
    @FXML
    private void dogrulaButton_click() {
        String kod = kodField.getText().trim();
        
        if (kod.isEmpty()) {
            showStatus("Lütfen doğrulama kodunu girin.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            // Kodu kontrol et
            String sql = "SELECT * FROM sifre_sifirlama WHERE TC = ? AND kod = ? AND kullanildi = FALSE AND son_gecerlilik > ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tc);
                ps.setString(2, kod);
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Kodu kullanıldı olarak işaretle
                        sql = "UPDATE sifre_sifirlama SET kullanildi = TRUE WHERE TC = ? AND kod = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(sql)) {
                            updatePs.setString(1, tc);
                            updatePs.setString(2, kod);
                            updatePs.executeUpdate();
                        }
                        
                        // Yeni şifre sayfasına yönlendir
                        openYeniSifrePage();
                    } else {
                        showStatus("Geçersiz veya süresi dolmuş kod. Lütfen tekrar deneyin.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showStatus("Doğrulama sırasında bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void openYeniSifrePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("yeniSifre.fxml"));
            Parent root = loader.load();
            
            YeniSifreController controller = loader.getController();
            controller.setKullaniciBilgileri(tc, email);
            
            Stage stage = (Stage) dogrulaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Yeni şifre sayfası açılırken hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void geriDonButton_click() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sifremiUnuttum.fxml"));
            Stage stage = (Stage) dogrulaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Geri dönülürken hata oluştu: " + e.getMessage());
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
    }
} 