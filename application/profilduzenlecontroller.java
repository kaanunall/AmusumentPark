package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class profilduzenlecontroller {
    private static final String DB_URL = "**";
    private static final String USER = "root";
    private static final String PASS = "";
    
    @FXML
    private TextField adField;
    
    @FXML
    private TextField soyadField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField sifreField;
    
    @FXML
    private TextField telefonField;
    
    @FXML
    private Button kaydetButton;
    
    @FXML
    private Button iptalButton;
    
    private String originalEmail;
    
    public void setProfilData(String ad, String soyad, String email, String sifre, String telefon) {
        adField.setText(ad);
        soyadField.setText(soyad);
        emailField.setText(email);
        sifreField.setText(sifre);
        telefonField.setText(telefon);
        originalEmail = email;
    }
    
    @FXML
    private void kaydetButtonClick() {
        String yeniAd = adField.getText();
        String yeniSoyad = soyadField.getText();
        String yeniEmail = emailField.getText();
        String yeniSifre = sifreField.getText();
        String yeniTelefon = telefonField.getText();
        
        if (yeniAd.isEmpty() || yeniSoyad.isEmpty() || yeniEmail.isEmpty() || yeniSifre.isEmpty() || yeniTelefon.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "UPDATE uyeler SET Ad = ?, SoyAd = ?, email = ?, sifre = ?, Telefon = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, yeniAd);
            pstmt.setString(2, yeniSoyad);
            pstmt.setString(3, yeniEmail);
            pstmt.setString(4, yeniSifre);
            pstmt.setString(5, yeniTelefon);
            pstmt.setString(6, originalEmail);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Profil bilgileriniz güncellendi.");
                closeWindow();
            } else {
                showAlert(AlertType.ERROR, "Hata", "Profil güncellenirken bir hata oluştu.");
            }
            
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Hata", "Veritabanı hatası: " + e.getMessage());
        }
    }
    
    @FXML
    private void iptalButtonClick() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) iptalButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 