package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class biletlercontroller implements Initializable {

    @FXML
    private ComboBox<String> bankaSecim;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField biletAdet;
    @FXML
    private Label bakiyeLabel;

    private String kullaniciTC;
    private double seciliHesapBakiyesi;
    private static final double BILET_FIYATI = 100.0; 

    private Runnable onBiletAlimBasarili;
    
    public void setOnBiletAlimBasarili(Runnable callback) {
        this.onBiletAlimBasarili = callback;
    }

    @FXML
    private void biletSatinAl() {
        try {
            if (bankaSecim.getValue() == null) {
                showAlert("Hata", "Lütfen bir banka hesabı seçin!");
                return;
            }

            if (biletAdet.getText().isEmpty()) {
                showAlert("Hata", "Lütfen bilet adetini girin!");
                return;
            }

            int adet = Integer.parseInt(biletAdet.getText());
            if (adet <= 0) {
                showAlert("Hata", "Bilet adeti 0'dan büyük olmalıdır!");
                return;
            }

            double toplamTutar = BILET_FIYATI * adet;
            if (toplamTutar > seciliHesapBakiyesi) {
                showAlert("Hata", "Yetersiz bakiye! Gerekli bakiye: " + toplamTutar + " TL");
                return;
            }

            // Veritabanı işlemleri
            Connection conn = getConnection();
            if (conn == null) {
                showAlert("Hata", "Veritabanı bağlantısı başarısız!");
                return;
            }

            try {
                conn.setAutoCommit(false); // Transaction başlat

                // Bakiye güncelleme
                String bakiyeSorgu = "UPDATE banka SET bakiye = bakiye - ? WHERE HesapNo = ?";
                PreparedStatement bakiyeStmt = conn.prepareStatement(bakiyeSorgu);
                bakiyeStmt.setDouble(1, toplamTutar);
                bakiyeStmt.setString(2, bankaSecim.getValue().split(" - ")[0]);
                bakiyeStmt.executeUpdate();

                // Bilet adedi güncelleme
                String checkSorgu = "SELECT COUNT(*) as count FROM biletler WHERE TC = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSorgu);
                checkStmt.setString(1, kullaniciTC);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int kayitVar = rs.getInt("count");

                String biletSorgu;
                PreparedStatement biletStmt;
                
                if (kayitVar > 0) {
                    // TC ile kayıt varsa sadece adet güncelle
                    biletSorgu = "UPDATE biletler SET Adet = Adet + ? WHERE TC = ?";
                    biletStmt = conn.prepareStatement(biletSorgu);
                    biletStmt.setInt(1, adet);
                    biletStmt.setString(2, kullaniciTC);
                } else {
                    // TC ile kayıt yoksa yeni kayıt oluştur
                    biletSorgu = "INSERT INTO biletler (TC, Ad, SoyAd, Adet) VALUES (?, ?, ?, ?)";
                    biletStmt = conn.prepareStatement(biletSorgu);
                    biletStmt.setString(1, kullaniciTC);
                    biletStmt.setString(2, uyecontroller.girisYapanKullaniciAdi);
                    biletStmt.setString(3, uyecontroller.girisYapanKullaniciSoyad);
                    biletStmt.setInt(4, adet);
                }
                
                biletStmt.executeUpdate();

                conn.commit(); 

                showAlert("Başarılı", "Bilet satın alma işlemi başarıyla tamamlandı!");
                
                
                seciliHesapBakiyesi -= toplamTutar;
                bakiyeLabel.setText("Seçili Hesap Bakiyesi: " + seciliHesapBakiyesi + " TL");

             
                biletAdet.clear();
                
               
                if (onBiletAlimBasarili != null) {
                    onBiletAlimBasarili.run();
                }
                
                
                Stage stage = (Stage) biletAdet.getScene().getWindow();
                stage.close();

            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            } finally {
                conn.setAutoCommit(true); 
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (NumberFormatException e) {
            showAlert("Hata", "Lütfen geçerli bir sayı girin!");
        } catch (SQLException e) {
            showAlert("Hata", "Veritabanı hatası: " + e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kullaniciTC = uyecontroller.girisYapanKullaniciTC;
        String kullaniciAdi = uyecontroller.girisYapanKullaniciAdi;
        welcomeLabel.setText("Merhaba " + kullaniciAdi);
       
        bankaHesaplariniYukle();
        
        // ComboBox seçim değişikliğini dinle
        bankaSecim.setOnAction(event -> {
            if (bankaSecim.getValue() != null) {
                guncelleBakiyeGosterimi();
            }
        });
    }

    private void bankaHesaplariniYukle() {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                showAlert("Hata", "Veritabanı bağlantısı başarısız!");
                return;
            }

            String sql = "SELECT HesapNo, Ad, SoyAd, bakiye FROM banka WHERE TC = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, kullaniciTC);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String hesapNo = rs.getString("HesapNo");
                        String ad = rs.getString("Ad");
                        String soyad = rs.getString("SoyAd");
                        double bakiye = rs.getDouble("bakiye");
                        bankaSecim.getItems().add(hesapNo + " - " + ad + " " + soyad + " (" + bakiye + " TL)");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Hata", "Veritabanı hatası: " + e.getMessage());
        }
    }

    private void guncelleBakiyeGosterimi() {
        try (Connection conn = getConnection()) {
            if (conn == null) return;

            String selectedValue = bankaSecim.getValue();
            if (selectedValue == null) return;

            String hesapNo = selectedValue.split(" - ")[0];
            String sql = "SELECT bakiye FROM banka WHERE HesapNo = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, hesapNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        seciliHesapBakiyesi = rs.getDouble("bakiye");
                        bakiyeLabel.setText("Seçili Hesap Bakiyesi: " + seciliHesapBakiyesi + " TL");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Hata", "Veritabanı hatası: " + e.getMessage());
        }
    }

    private Connection getConnection() {
        String url = "**";
        String kullaniciAdi = "root";
        String sifre = "";

        try {
            return DriverManager.getConnection(url, kullaniciAdi, sifre);
        } catch (SQLException e) {
            showAlert("Hata", "Veritabanı bağlantısı başarısız: " + e.getMessage());
            return null;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
