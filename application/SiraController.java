package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class SiraController implements Initializable {
    @FXML 
    private Label welcomeLabel;
    @FXML 
    private Label siraDurumLabel;
    @FXML 
    private ComboBox<String> oyuncakComboBox;
    @FXML 
    private TableView<SiraKayit> siraTableView;
    @FXML 
    private TableColumn<SiraKayit, Integer> siraNoColumn;
    @FXML 
    private TableColumn<SiraKayit, String> adColumn;
    @FXML
    private TableColumn<SiraKayit, String> soyadColumn;
    @FXML 
    private Button sirayaKatilButton;
    @FXML 
    private Button siraIptalButton;
    @FXML 
    private Button kapatButton;
@FXML
    private String kullaniciTC;
@FXML
    private String kullaniciAdi;
@FXML
    private String kullaniciSoyad;
@FXML
    private String seciliOyuncak;
    private Timeline refreshTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Tablo sütunlarını ayarla
        siraNoColumn.setCellValueFactory(cellData -> cellData.getValue().siraNoProperty().asObject());
        adColumn.setCellValueFactory(cellData -> cellData.getValue().adProperty());
        soyadColumn.setCellValueFactory(cellData -> cellData.getValue().soyadProperty());
        
        // ComboBox'a oyuncakları ekle
        oyuncakComboBox.getItems().addAll(
            "dönme dolap",
            "fatihin gemisi",
            "hava kafesi",
            "korku tüneli",
            "hızlı tren"
        );
        
        // ComboBox değiştiğinde sırayı güncelle
        oyuncakComboBox.setOnAction(e -> {
            guncelleSiraDurumu();
            startAutoRefresh();
        });
        
        // Otomatik yenileme için Timeline oluştur
        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> guncelleSiraDurumu())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void startAutoRefresh() {
        refreshTimeline.stop();
        if (oyuncakComboBox.getValue() != null) {
            refreshTimeline.play();
        }
    }

    public void setKullaniciBilgileri(String tc, String ad, String soyad) {
        this.kullaniciTC = tc;
        this.kullaniciAdi = ad;
        this.kullaniciSoyad = soyad;
        welcomeLabel.setText("Hoş geldiniz, " + ad + " " + soyad);
        guncelleSiraDurumu();
    }

    private void siraNolariniDuzenle(String tabloAdi, Connection conn) throws SQLException {
        // Mevcut sıraları al ve yeniden düzenle
        String selectQuery = "SELECT TC, sıra FROM `" + tabloAdi + "` WHERE sıra > 0 ORDER BY sıra";
        PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
        ResultSet rs = selectStmt.executeQuery();
        
        int yeniSira = 1;
        while (rs.next()) {
            String updateQuery = "UPDATE `" + tabloAdi + "` SET sıra = ? WHERE TC = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, yeniSira);
            updateStmt.setString(2, rs.getString("TC"));
            updateStmt.executeUpdate();
            yeniSira++;
        }
    }

    private void guncelleSiraDurumu() {
        String seciliOyuncak = oyuncakComboBox.getValue();
        if (seciliOyuncak == null) return;
        
        String tabloAdi = seciliOyuncak.replaceAll("\\s+", "").toLowerCase();
        ObservableList<SiraKayit> siraList = FXCollections.observableArrayList();
        
        try (Connection conn = DriverManager.getConnection(
                "**", "root", "")) {
            
            // Sıra numaralarını düzenle
            siraNolariniDuzenle(tabloAdi, conn);
            
            // Kullanıcının sıra durumunu kontrol et
            String siraDurumQuery = "SELECT sıra, Ad, SoyAd FROM `" + tabloAdi + "` WHERE TC = ?";
            PreparedStatement siraDurumStmt = conn.prepareStatement(siraDurumQuery);
            siraDurumStmt.setString(1, kullaniciTC);
            ResultSet siraDurumRs = siraDurumStmt.executeQuery();
            
            if (siraDurumRs.next()) {
                int siraNo = siraDurumRs.getInt("sıra");
                siraDurumLabel.setText("Sıra Numaranız: " + siraNo);
            } else {
                siraDurumLabel.setText("Sırada değilsiniz");
            }
            
            // Tüm sırayı getir
            String siraQuery = "SELECT sıra, Ad, SoyAd FROM `" + tabloAdi + "` WHERE sıra > 0 ORDER BY sıra";
            PreparedStatement siraStmt = conn.prepareStatement(siraQuery);
            ResultSet siraRs = siraStmt.executeQuery();
            
            while (siraRs.next()) {
                siraList.add(new SiraKayit(
                    siraRs.getInt("sıra"),
                    siraRs.getString("Ad"),
                    siraRs.getString("SoyAd")
                ));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Sıra durumu güncellenirken bir hata oluştu: " + e.getMessage());
        }
        
        siraTableView.setItems(siraList);
    }

    @FXML
    private void sirayaKatil() {
        String seciliOyuncak = oyuncakComboBox.getValue();
        if (seciliOyuncak == null) {
            showAlert("Uyarı", "Lütfen bir oyuncak seçin!");
            return;
        }
        
        String tabloAdi = seciliOyuncak.replaceAll("\\s+", "").toLowerCase();
        
        try (Connection conn = DriverManager.getConnection(
                "**", "root", "")) {
            
            // Kullanıcının zaten sırada olup olmadığını kontrol et
            String kontrolQuery = "SELECT COUNT(*) FROM `" + tabloAdi + "` WHERE TC = ?";
            PreparedStatement kontrolStmt = conn.prepareStatement(kontrolQuery);
            kontrolStmt.setString(1, kullaniciTC);
            ResultSet kontrolRs = kontrolStmt.executeQuery();
            
            if (kontrolRs.next() && kontrolRs.getInt(1) > 0) {
                showAlert("Uyarı", "Zaten bu oyuncak için sırada bekliyorsunuz!");
                return;
            }
            
            // Son sıra numarasını bul
            String sonSiraQuery = "SELECT MAX(sıra) FROM `" + tabloAdi + "`";
            PreparedStatement sonSiraStmt = conn.prepareStatement(sonSiraQuery);
            ResultSet sonSiraRs = sonSiraStmt.executeQuery();
            
            int yeniSiraNo = 1;
            if (sonSiraRs.next() && sonSiraRs.getInt(1) > 0) {
                yeniSiraNo = sonSiraRs.getInt(1) + 1;
            }
            
            // Yeni sıra kaydı ekle
            String ekleQuery = "INSERT INTO `" + tabloAdi + "` (TC, Ad, SoyAd, sıra) VALUES (?, ?, ?, ?)";
            PreparedStatement ekleStmt = conn.prepareStatement(ekleQuery);
            ekleStmt.setString(1, kullaniciTC);
            ekleStmt.setString(2, kullaniciAdi);
            ekleStmt.setString(3, kullaniciSoyad);
            ekleStmt.setInt(4, yeniSiraNo);
            ekleStmt.executeUpdate();
            
            showAlert("Başarılı", "Sıraya başarıyla katıldınız! Sıra numaranız: " + yeniSiraNo);
            guncelleSiraDurumu();
            startAutoRefresh();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Sıraya katılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void siraIptal() {
        String seciliOyuncak = oyuncakComboBox.getValue();
        if (seciliOyuncak == null) {
            showAlert("Uyarı", "Lütfen bir oyuncak seçin!");
            return;
        }
        
        String tabloAdi = seciliOyuncak.replaceAll("\\s+", "").toLowerCase();
        
        try (Connection conn = DriverManager.getConnection(
                "**", "root", "")) {
            
            // Kullanıcının sırada olup olmadığını kontrol et
            String kontrolQuery = "SELECT COUNT(*) FROM `" + tabloAdi + "` WHERE TC = ?";
            PreparedStatement kontrolStmt = conn.prepareStatement(kontrolQuery);
            kontrolStmt.setString(1, kullaniciTC);
            ResultSet kontrolRs = kontrolStmt.executeQuery();
            
            if (kontrolRs.next() && kontrolRs.getInt(1) == 0) {
                showAlert("Uyarı", "Bu oyuncak için sırada bekleyen bir kaydınız bulunmuyor!");
                return;
            }
            
            // Sıra kaydını sil
            String silQuery = "DELETE FROM `" + tabloAdi + "` WHERE TC = ?";
            PreparedStatement silStmt = conn.prepareStatement(silQuery);
            silStmt.setString(1, kullaniciTC);
            silStmt.executeUpdate();
            
            // Sıra numaralarını yeniden düzenle
            siraNolariniDuzenle(tabloAdi, conn);
            
            showAlert("Başarılı", "Sıra kaydınız başarıyla iptal edildi!");
            guncelleSiraDurumu();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Sıra iptal edilirken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void kapat() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
        ((Stage) welcomeLabel.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class SiraKayit {
        private final javafx.beans.property.IntegerProperty siraNo;
        private final javafx.beans.property.StringProperty ad;
        private final javafx.beans.property.StringProperty soyad;
        
        public SiraKayit(int siraNo, String ad, String soyad) {
            this.siraNo = new javafx.beans.property.SimpleIntegerProperty(siraNo);
            this.ad = new javafx.beans.property.SimpleStringProperty(ad);
            this.soyad = new javafx.beans.property.SimpleStringProperty(soyad);
        }
        
        public javafx.beans.property.IntegerProperty siraNoProperty() { return siraNo; }
        public javafx.beans.property.StringProperty adProperty() { return ad; }
        public javafx.beans.property.StringProperty soyadProperty() { return soyad; }
    }
} 