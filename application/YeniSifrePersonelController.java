package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class YeniSifrePersonelController {
    @FXML
    private PasswordField yeniSifreField;
    @FXML
    private PasswordField sifreTekrarField;
    @FXML
    private Button sifreGuncelleButton;
    @FXML
    private Label statusLabel;
    
    private String tc;
    private String email;
    
    public void setKullaniciBilgileri(String tc, String email) {
        this.tc = tc;
        this.email = email;
    }
    
    @FXML
    private void sifreGuncelleButton_click() {
        String yeniSifre = yeniSifreField.getText().trim();
        String sifreTekrar = sifreTekrarField.getText().trim();
        
        if (yeniSifre.isEmpty() || sifreTekrar.isEmpty()) {
            showStatus("Lütfen tüm alanları doldurun.");
            return;
        }
        
        if (!yeniSifre.equals(sifreTekrar)) {
            showStatus("Şifreler eşleşmiyor.");
            return;
        }
        
        if (yeniSifre.length() < 6) {
            showStatus("Şifre en az 6 karakter olmalıdır.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "UPDATE kutuphane.kullanicigiris SET Sifre = ? WHERE TC = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, yeniSifre);
                ps.setString(2, tc);
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showStatus("Şifreniz başarıyla güncellendi.");
                    // Giriş sayfasına yönlendir
                    openGirisPage();
                } else {
                    showStatus("Şifre güncellenirken bir hata oluştu: Kullanıcı bulunamadı.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showStatus("Veritabanı hatası: " + e.getMessage());
        }
    }
    
    private void openGirisPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Form1.fxml"));
            Stage stage = (Stage) sifreGuncelleButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Giriş sayfası açılırken hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void geriDonButton_click() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dogrulamaPersonel.fxml"));
            Stage stage = (Stage) sifreGuncelleButton.getScene().getWindow();
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