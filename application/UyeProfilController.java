package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UyeProfilController implements Initializable {
    
    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField emailField;
    @FXML private PasswordField sifreField;
    @FXML private Button kaydetButton;
    
    private String userTC;
    private VeriTabaniUye vt = new VeriTabaniUye();
    
    public void setUserTC(String tc) {
        this.userTC = tc;
        loadUserData();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Kullanıcı bilgilerini yükle
        loadUserData();
    }
    
    private void loadUserData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vt.baglan();
            String sql = "SELECT * FROM uyeler WHERE TC = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userTC);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                adField.setText(rs.getString("Ad"));
                soyadField.setText(rs.getString("SoyAd"));
                emailField.setText(rs.getString("email"));
            }
            
        } catch (SQLException e) {
            showAlert("Hata", "Veri yükleme hatası: " + e.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void kaydetButtonClick(ActionEvent event) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = vt.baglan();
            String sql;
            if(sifreField.getText().isEmpty()) {
                sql = "UPDATE uyeler SET Ad=?, SoyAd=?, email=? WHERE TC=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, adField.getText());
                ps.setString(2, soyadField.getText());
                ps.setString(3, emailField.getText());
                ps.setString(4, userTC);
            } else {
                sql = "UPDATE uyeler SET Ad=?, SoyAd=?, email=?, sifre=? WHERE TC=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, adField.getText());
                ps.setString(2, soyadField.getText());
                ps.setString(3, emailField.getText());
                ps.setString(4, sifreField.getText());
                ps.setString(5, userTC);
            }
            
            int affected = ps.executeUpdate();
            
            if(affected > 0) {
                showAlert("Başarılı", "Profil bilgileri güncellendi!");
                // Pencereyi kapat
                Stage stage = (Stage) kaydetButton.getScene().getWindow();
                stage.close();
            }
            
        } catch (SQLException e) {
            showAlert("Hata", "Güncelleme hatası: " + e.getMessage());
        } finally {
            try {
                if(ps != null) ps.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 