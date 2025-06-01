package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class PersonelEkleController {
    @FXML private TextField emailField;
    @FXML private PasswordField sifreField;
    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField tcField;
    @FXML private TextField telefonField;
    @FXML private ImageView resimView;
    
    private File secilenResim;
    private PersonelDuzenleController personelDuzenleController;
    private static final String UPLOAD_DIR = "C:\\xampp\\htdocs\\uploads\\resimler\\";

    @FXML
    private void initialize() {
        // Uploads klasörünü oluştur
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Resim klasörü oluşturulurken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void resimSec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        secilenResim = fileChooser.showOpenDialog(resimView.getScene().getWindow());
        if (secilenResim != null) {
            resimView.setImage(new Image(secilenResim.toURI().toString()));
        }
    }

    @FXML
    private void kaydet() {
        // Alanları kontrol et - null kontrolü ekleyerek
        if (emailField.getText() == null || emailField.getText().isEmpty() || 
            sifreField.getText() == null || sifreField.getText().isEmpty() || 
            adField.getText() == null || adField.getText().isEmpty() || 
            soyadField.getText() == null || soyadField.getText().isEmpty() || 
            tcField.getText() == null || tcField.getText().isEmpty() || 
            telefonField.getText() == null || telefonField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen tüm alanları doldurun.");
            return;
        }

        String resimYolu = null;
        if (secilenResim != null) {
            try {
                // Resmi kopyala
                String dosyaAdi = System.currentTimeMillis() + "_" + secilenResim.getName();
                Path hedefYol = Paths.get(UPLOAD_DIR + dosyaAdi);
                Files.copy(secilenResim.toPath(), hedefYol);
                resimYolu = hedefYol.toString();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Resim yüklenirken bir hata oluştu: " + e.getMessage());
                return;
            }
        }

        String sql = "INSERT INTO kullanicigiris (email, sifre, Ad, SoyAd, TC, Telefon, resim_yolu) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection("**", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, emailField.getText());
            pstmt.setString(2, sifreField.getText());
            pstmt.setString(3, adField.getText());
            pstmt.setString(4, soyadField.getText());
            pstmt.setString(5, tcField.getText());
            pstmt.setString(6, telefonField.getText());
            pstmt.setString(7, resimYolu);
            
            pstmt.executeUpdate();
            
            if (personelDuzenleController != null) {
                personelDuzenleController.verileriYenile();
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Personel başarıyla eklendi.");
            kapat();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Personel eklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void iptal() {
        kapat();
    }

    private void kapat() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    public void setPersonelDuzenleController(PersonelDuzenleController controller) {
        this.personelDuzenleController = controller;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 