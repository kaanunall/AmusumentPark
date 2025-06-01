package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KampanyaEkleController {
    @FXML private TextField kampanyaIdField;
    @FXML private TextArea aciklamaField;
    @FXML private Button kaydetButton;
    @FXML private Button iptalButton;
    
    @FXML
    private void kaydetButton_click() {
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
            
            // Veritabanına ekle
            try (Connection conn = DriverManager.getConnection("**", "root", "")) {
                String sql = "INSERT INTO kampanyabilgi (KampanyaID, Aciklama) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, kampanyaId);
                stmt.setString(2, aciklama);
                stmt.executeUpdate();
                
                showAlert("Başarılı", "Kampanya başarıyla eklendi");
                
                // Formu temizle
                kampanyaIdField.clear();
                aciklamaField.clear();
                
                // Pencereyi kapat
                Stage stage = (Stage) kaydetButton.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                showAlert("Hata", "Kampanya eklenirken bir hata oluştu: " + e.getMessage());
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
} 