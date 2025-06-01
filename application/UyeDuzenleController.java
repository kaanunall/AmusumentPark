package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class UyeDuzenleController {
    @FXML private TextField uyeTcField;
    @FXML private TextField uyeAdField;
    @FXML private TextField uyeSoyadField;
    @FXML private TextField uyeEmailField;
    @FXML private TextField uyeTelefonField;
    @FXML private Button uyeKaydetButton;
    @FXML private Button uyeIptalButton;
    
    private YetkiliController.Uye secilenUye;
    
    public void setUye(YetkiliController.Uye uye) {
        this.secilenUye = uye;
        uyeTcField.setText(uye.getTc());
        uyeAdField.setText(uye.getAd());
        uyeSoyadField.setText(uye.getSoyad());
        uyeEmailField.setText(uye.getEmail());
        uyeTelefonField.setText(uye.getTelefon());
    }
    
    @FXML
    private void uyeKaydetButton_click() {
        String telefon = uyeTelefonField.getText().trim();
        
        // Telefon numarası kontrolü
        if (telefon.length() > 15) {
            showAlert("Uyarı", "Telefon numarası 15 karakterden uzun olamaz.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "UPDATE uyeler SET Ad = ?, Soyad = ?, Email = ?, Telefon = ? WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, uyeAdField.getText());
            pstmt.setString(2, uyeSoyadField.getText());
            pstmt.setString(3, uyeEmailField.getText());
            pstmt.setString(4, telefon);
            pstmt.setString(5, secilenUye.getTc());
            
            pstmt.executeUpdate();
            
            showAlert("Başarılı", "Üye bilgileri başarıyla güncellendi.");
            
            // Pencereyi kapat
            uyeTelefonField.getScene().getWindow().hide();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Üye güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void uyeIptalButton_click() {
        uyeTelefonField.getScene().getWindow().hide();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 