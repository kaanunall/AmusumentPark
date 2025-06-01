package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class Form1Controller {
    private static final String DB_URL = "**";
    private static final String USER = "root";
    private static final String PASS = "";
    
    private VeriTabani vt = new VeriTabani();
    private VeriTabaniUye vtuye = new VeriTabaniUye();
    private VeriTabaniYonetim vtyonetim = new VeriTabaniYonetim();
   
    @FXML
    private Hyperlink sifremiUnuttumLink_click;
    @FXML
    private Hyperlink personel_sifre;
    @FXML
    private Hyperlink personel_sifre_click;
    @FXML 
    private Hyperlink sifremiUnuttumLink;
    @FXML
    private Button button_giris;
    @FXML
    private Hyperlink openKayitOlPage;
    @FXML
    private TextField kayit_ad;
    @FXML 
    private TextField kayit_telefon;
    @FXML
    private TextField kayit_soyad;
    @FXML
    private TextField kayit_mail;
    @FXML
    private PasswordField kayit_sifre;
    @FXML
    private Button kayit_button;
    @FXML
    private TextField kayit_tc;
    @FXML
    private Button uye_button_giris;
    @FXML
    private Button yonetici_button_giris;
    @FXML
    private TextField tf_email_giris;
    @FXML
    private TextField uye_mail;
    @FXML
    private TextField yonetici_mail;
    @FXML
    private PasswordField tf_sifre_giris;
    @FXML
    private PasswordField uye_sifre;
    @FXML
    private PasswordField yonetici_sifre;
    @FXML
    private Hyperlink hp_giris;

    @FXML
    void button_girirs_click(ActionEvent event) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vt.baglan();
            String email = tf_email_giris.getText().trim();
            String sifre = tf_sifre_giris.getText().trim();
            String sorgu = "SELECT * FROM kullanicigiris WHERE email=? and sifre=?";

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Veritabanı bağlantısı başarısız!");
                return;
            }

            ps = conn.prepareStatement(sorgu);
            ps.setString(1, email);
            ps.setString(2, sifre);
            rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Giriş Başarılı.");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("yetkili.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Kullanıcı Paneli");
                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) button_giris.getScene().getWindow()).close();
            } else {
                JOptionPane.showMessageDialog(null, "Email ya da şifreniz yanlış.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    void uye_button_giris_click(ActionEvent event) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vtuye.baglan();
            String email = uye_mail.getText().trim();
            String sifre = uye_sifre.getText().trim();
            String sorgu = "SELECT * from uyeler WHERE email=? and sifre=?";

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Veritabanı bağlantısı başarısız!");
                return;
            }

            ps = conn.prepareStatement(sorgu);
            ps.setString(1, email);
            ps.setString(2, sifre);
            rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Giriş Başarılı.");
                String kullaniciAdi = rs.getString("Ad");
                String kullaniciTC = rs.getString("TC");
                String kullaniciSoyad = rs.getString("SoyAd");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("uye.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Kullanıcı Paneli");

                uyecontroller uyeController = loader.getController();
                uyeController.setKullaniciBilgileri(kullaniciAdi, kullaniciTC, kullaniciSoyad);

                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) uye_button_giris.getScene().getWindow()).close();
            } else {
                JOptionPane.showMessageDialog(null, "Email ya da şifreniz yanlış.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void kayit_button_click(ActionEvent event) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = vtuye.baglan();
            String tc = kayit_tc.getText().trim();
            String email = kayit_mail.getText().trim();
            String sifre = kayit_sifre.getText().trim();
            String Ad = kayit_ad.getText().trim();
            String SoyAd = kayit_soyad.getText().trim();
            String Telefon = kayit_telefon.getText().trim();
            
            if (email.isEmpty() || sifre.isEmpty() || Ad.isEmpty() || SoyAd.isEmpty() || tc.isEmpty() || Telefon.isEmpty() ) {
                JOptionPane.showMessageDialog(null, "Doldurulmayan alanlar var!");
                return;
            }
            
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Veritabanı bağlantısı başarısız!");
                return;
            }
            
            String sorgu = "INSERT INTO uyeler (TC, email, sifre, Ad, SoyAd, Telefon) VALUES (?, ?, ?, ?, ?, ?)";
            
            ps = conn.prepareStatement(sorgu);
            ps.setString(1, tc);
            ps.setString(2, email);
            ps.setString(3, sifre);
            ps.setString(4, Ad);
            ps.setString(5, SoyAd);
            ps.setString(6, Telefon);
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Kayıt başarılı.");
                Stage stage = (Stage) kayit_button.getScene().getWindow();
                stage.close();
            } else {
                JOptionPane.showMessageDialog(null, "Kayıt başarısız.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Hata: Bu kimlik numarası mevcut olarak kullanılmaktadır. " );
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void yonetici_button_giris_click() {
        String email = yonetici_mail.getText();
        String sifre = yonetici_sifre.getText();
        
        if (email.isEmpty() || sifre.isEmpty()) {
            showAlert(AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT TC, Ad, SoyAd FROM yonetim.uyeler WHERE email = ? AND sifre = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, sifre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String tc = rs.getString("TC");
                String ad = rs.getString("Ad");
                String soyad = rs.getString("SoyAd");
                
                // Yönetici sayfasını aç
                FXMLLoader loader = new FXMLLoader(getClass().getResource("yonetici.fxml"));
                Parent root = loader.load();
                YoneticiController controller = loader.getController();
                
                // Yönetici bilgilerini aktar
                controller.setYoneticiBilgileri(email, tc, ad);
                
                Stage stage = new Stage();
                stage.setTitle("Yönetici Paneli");
                stage.setScene(new Scene(root));
                stage.show();
                
                // Giriş penceresini kapat
                Stage loginStage = (Stage) yonetici_button_giris.getScene().getWindow();
                loginStage.close();
            } else {
                showAlert(AlertType.ERROR, "Hata", "Geçersiz e-posta veya şifre.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hata", "Giriş yapılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void geri_click() {
        Stage stage = (Stage) kayit_button.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void hp_geri() {
        Stage stage = (Stage) hp_giris.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openKayitOlPage() {
        try {
            // Mutlak yol kullanarak FXML dosyasını yükleme
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Form1Controller.class.getResource("kayitol.fxml"));
            
            // FXML yüklemeden önce location kontrolü
            if (loader.getLocation() == null) {
                throw new IOException("kayit.fxml dosyası bulunamadı!");
            }
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Kayıt Ol");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            // Hata mesajını göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("kayit.fxml dosyası yüklenirken hata oluştu!\nHata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void sifremiUnuttumLink_click() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sifremiUnuttum.fxml"));
            Stage stage = (Stage) tf_sifre_giris.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hata", "Şifre sıfırlama sayfası açılırken bir hata oluştu!");
        }
    }
    
    //Personel Şifre Unuttum Kısmı
    @FXML
    private void personel_sifre_click() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sifremiUnuttumPersonel.fxml"));
            Stage stage = (Stage) tf_sifre_giris.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hata", "Şifre sıfırlama sayfası açılırken bir hata oluştu!");
        }
    }
    //Personel Şidre Unuttum Kısmı Sonu
    
    

    @FXML
    void initialize() {
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
