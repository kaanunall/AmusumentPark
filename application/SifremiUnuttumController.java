package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SifremiUnuttumController {
    @FXML
    private TextField emailField;
    @FXML
    private Button kodGonderButton;
    @FXML
    private Button geriDonButton;
    @FXML
    private Label statusLabel;
    
    private EmailService emailService = new EmailService();
    
    @FXML
    private void kodGonderButton_click() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            showStatus("Lütfen e-posta adresinizi girin.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            // Kullanıcıyı e-posta adresine göre bul
            String sql = "SELECT TC FROM uyegiris.uyeler WHERE Email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String tc = rs.getString("TC");
                        String verificationCode = emailService.generateVerificationCode();
                        
                        // Kodu veritabanına kaydet
                        String insertSql = "INSERT INTO sifre_sifirlama (TC, kod, son_gecerlilik, kullanildi) VALUES (?, ?, ?, false)";
                        try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                            insertPstmt.setString(1, tc);
                            insertPstmt.setString(2, verificationCode);
                            insertPstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plus(10, ChronoUnit.MINUTES)));
                            insertPstmt.executeUpdate();
                        }
                        
                        // E-posta gönder
                        emailService.sendPasswordResetEmail(email, verificationCode);
                        showStatus("Doğrulama kodu e-posta adresinize gönderildi.");
                        openVerificationPage(tc, email);
                    } else {
                        showStatus("Bu e-posta adresi ile kayıtlı bir kullanıcı bulunamadı.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showStatus("Veritabanı hatası: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void openVerificationPage(String tc, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dogrulama.fxml"));
            Parent root = loader.load();
            
            DogrulamaController controller = loader.getController();
            controller.setKullaniciBilgileri(tc, email);
            
            Stage stage = (Stage) kodGonderButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Doğrulama sayfası açılırken hata oluştu.");
        }
    }
    
    @FXML
    private void geriDonButton_click() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Form1.fxml"));
            Stage stage = (Stage) geriDonButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Giriş sayfası açılırken hata oluştu.");
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
    }
} 