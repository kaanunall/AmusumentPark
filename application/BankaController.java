package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class BankaController {
    @FXML private ListView<String> hesapListView;
    @FXML private Label welcomeLabel;
    @FXML private Label hesapNoLabel;
    @FXML private Label adSoyadLabel;
    @FXML private Label bakiyeLabel;
    @FXML private TextField hesapNoField;
    @FXML private TextField islemTutariField;
    
    private String kullaniciTC;
    private String kullaniciAdi;
    private String kullaniciSoyadi;
    
    public void setKullaniciBilgileri(String ad, String tc, String soyad) {
        this.kullaniciAdi = ad;
        this.kullaniciTC = tc;
        this.kullaniciSoyadi = soyad;
        welcomeLabel.setText("Merhaba " + ad);
        hesaplariYukle();
    }
    
    private void hesaplariYukle() {
        ObservableList<String> hesaplar = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "SELECT HesapNo FROM banka WHERE TC = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kullaniciTC);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                hesaplar.add(rs.getString("HesapNo"));
            }
        } catch (SQLException e) {
            showAlert("Hata", "Hesaplar yüklenirken bir hata oluştu: " + e.getMessage());
        }
        hesapListView.setItems(hesaplar);
        
        // ListView seçim olayını dinle
        hesapListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    hesapBilgileriniGoster(newValue);
                }
            }
        );
    }
    
    @FXML
    private void hesapEkle() {
        if (hesapNoField.getText().isEmpty()) {
            showAlert("Uyarı", "Lütfen hesap numarası giriniz.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            
            String sqlUye = "SELECT Ad, SoyAd FROM uyeler WHERE TC = ?";
            PreparedStatement psUye = conn.prepareStatement(sqlUye);
            psUye.setString(1, kullaniciTC);
            ResultSet rsUye = psUye.executeQuery();
            
            if (rsUye.next()) {
                String ad = rsUye.getString("Ad");
                String soyad = rsUye.getString("SoyAd");
                
                // Banka tablosuna yeni hesap ekle
                String sqlBanka = "INSERT INTO banka (HesapNo, TC, Ad, SoyAd, bakiye) VALUES (?, ?, ?, ?, 0)";
                PreparedStatement psBanka = conn.prepareStatement(sqlBanka);
                psBanka.setString(1, hesapNoField.getText());
                psBanka.setString(2, kullaniciTC);
                psBanka.setString(3, ad);
                psBanka.setString(4, soyad);
                psBanka.executeUpdate();
                
                hesaplariYukle();
                hesapNoField.clear();
                showAlert("Başarılı", "Hesap başarıyla eklendi.");
            }
        } catch (SQLException e) {
            showAlert("Hata", "Hesap eklenirken bir hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void hesapSil() {
        String seciliHesap = hesapListView.getSelectionModel().getSelectedItem();
        if (seciliHesap == null) {
            showAlert("Uyarı", "Lütfen silinecek hesabı seçiniz.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "DELETE FROM banka WHERE TC = ? AND HesapNo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kullaniciTC);
            ps.setString(2, seciliHesap);
            ps.executeUpdate();
            
            hesaplariYukle();
            hesapNoLabel.setText("Hesap No: ");
            adSoyadLabel.setText("Ad Soyad: ");
            bakiyeLabel.setText("Bakiye: ");
            showAlert("Başarılı", "Hesap başarıyla silindi.");
        } catch (SQLException e) {
            showAlert("Hata", "Hesap silinirken bir hata oluştu: " + e.getMessage());
        }
    }
    
    @FXML
    private void paraYatir() {
        islemYap(true);
    }
    
    @FXML
    private void paraCek() {
        islemYap(false);
    }
    
    private void islemYap(boolean paraYatirma) {
        String seciliHesap = hesapListView.getSelectionModel().getSelectedItem();
        if (seciliHesap == null) {
            showAlert("Uyarı", "Lütfen bir hesap seçiniz.");
            return;
        }
        
        try {
            double tutar = Double.parseDouble(islemTutariField.getText());
            if (tutar <= 0) {
                showAlert("Uyarı", "Geçerli bir tutar giriniz.");
                return;
            }
            
            Connection conn = DriverManager.getConnection("**", "root", "");
            
            // Önce mevcut bakiyeyi kontrol et
            String bakiyeSorgu = "SELECT bakiye FROM banka WHERE TC = ? AND HesapNo = ?";
            PreparedStatement ps = conn.prepareStatement(bakiyeSorgu);
            ps.setString(1, kullaniciTC);
            ps.setString(2, seciliHesap);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double mevcutBakiye = rs.getDouble("bakiye");
                if (!paraYatirma && tutar > mevcutBakiye) {
                    showAlert("Uyarı", "Yetersiz bakiye.");
                    return;
                }
                
                // Bakiye güncelleme
                String sql = "UPDATE banka SET bakiye = bakiye " + (paraYatirma ? "+" : "-") + " ? WHERE TC = ? AND HesapNo = ?";
                ps = conn.prepareStatement(sql);
                ps.setDouble(1, tutar);
                ps.setString(2, kullaniciTC);
                ps.setString(3, seciliHesap);
                ps.executeUpdate();
                
                hesapBilgileriniGoster(seciliHesap);
                islemTutariField.clear();
                showAlert("Başarılı", "İşlem başarıyla gerçekleştirildi.");
            }
            conn.close();
        } catch (NumberFormatException e) {
            showAlert("Hata", "Geçerli bir tutar giriniz.");
        } catch (SQLException e) {
            showAlert("Hata", "İşlem sırasında bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void hesapBilgileriniGoster(String hesapNo) {
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "SELECT Ad, SoyAd, bakiye FROM banka WHERE TC = ? AND HesapNo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kullaniciTC);
            ps.setString(2, hesapNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                hesapNoLabel.setText("Hesap No: " + hesapNo);
                adSoyadLabel.setText("Ad Soyad: " + rs.getString("Ad") + " " + rs.getString("SoyAd"));
                bakiyeLabel.setText("Bakiye: " + rs.getDouble("bakiye") + " TL");
            }
        } catch (SQLException e) {
            showAlert("Hata", "Hesap bilgileri gösterilirken bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void sayfayiKapat() {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.close();
    }
} 