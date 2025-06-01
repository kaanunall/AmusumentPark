package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class YoneticiController {
    private static final String YONETIM_DB_URL = "**";
    private static final String UYEGIRIS_DB_URL = "**";
    private static final String USER = "root";
    private static final String PASS = "";
    
    @FXML
    private TableView<Sikayet> sikayetTable;
    @FXML
    private Timeline welcomeLabelTimeline;
    
    @FXML
    private TableColumn<Sikayet, String> sikayetAd;
    
    @FXML
    private TableColumn<Sikayet, String> sikayetSoyad;
    
    @FXML
    private TableColumn<Sikayet, String> sikayetMesaj;
    
    @FXML
    private TableColumn<Sikayet, String> sikayetTür;
    
    @FXML
    private TableColumn<Sikayet, String> sikayetTarih;
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button musteriyonetim;
    
    @FXML
    private Button personelyonetim;
    
    @FXML
    private Button profilyonetim;
    
    private String yoneticiEmail;
    private String yoneticiTC;
    private String yoneticiAd;
    
    @FXML
    public void initialize() {
        // Sütunları veri modeliyle eşleştir
        sikayetAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        sikayetSoyad.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        sikayetMesaj.setCellValueFactory(new PropertyValueFactory<>("mesaj"));
        sikayetTür.setCellValueFactory(new PropertyValueFactory<>("tur"));
        sikayetTarih.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        
        // Butonlara tıklama olaylarını ekle
        musteriyonetim.setOnAction(e -> musteriDuzenleSayfasiniAc());
        personelyonetim.setOnAction(e -> personelDuzenleSayfasiniAc());
        profilyonetim.setOnAction(e -> profilDuzenleSayfasiniAc());
        
        // Şikayetleri yükle
        sikayetleriYukle();
        
        // Welcome label için otomatik yenileme zamanlayıcısı
        startWelcomeLabelRefresh();
        
        // Yönetici adını 2 saniyede bir güncelle
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(2), event -> yoneticiAdGuncelle())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void startWelcomeLabelRefresh() {
        // Her 1 saniyede bir çalışacak zamanlayıcı
        welcomeLabelTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> updateWelcomeLabel())
        );
        welcomeLabelTimeline.setCycleCount(Timeline.INDEFINITE); // Sonsuz döngü
        welcomeLabelTimeline.play();
    }
    
    private void updateWelcomeLabel() {
        // Şu anki saati al
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String currentTime = now.format(formatter);
        
        // Etiketi güncelle
        welcomeLabel.setText("Merhaba " + yoneticiAd + " - Saat: " + currentTime);
    }
    
    public void setYoneticiBilgileri(String email, String tc, String ad) {
        this.yoneticiEmail = email;
        this.yoneticiTC = tc;
        this.yoneticiAd = ad;
        welcomeLabel.setText("Merhaba " + yoneticiAd);
    }
    
    @FXML
    private void musterilerSayfasiniAc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("musteriler.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Müşteriler");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Hata", "Müşteriler sayfası açılırken bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void showAlert(AlertType error, String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	private void yoneticiBilgileriniYukle() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(YONETIM_DB_URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement("SELECT Ad, TC FROM uyeler WHERE email = ?")) {
                
                ps.setString(1, yoneticiEmail);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    yoneticiAd = rs.getString("Ad");
                    yoneticiTC = rs.getString("TC");
                    welcomeLabel.setText("Merhaba " + yoneticiAd);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void musteriDuzenleSayfasiniAc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("musteriler.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Müşteri Yönetimi");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void personelDuzenleSayfasiniAc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personelduzenle.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Personel Yönetimi");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void profilDuzenleSayfasiniAc() {
        try {
            // Yönetici bilgilerini veritabanından al
            String ad = yoneticiAd;
            String soyad = "", email = yoneticiEmail, sifre = "", telefon = "";
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(YONETIM_DB_URL, USER, PASS);
                     PreparedStatement ps = conn.prepareStatement("SELECT SoyAd, sifre, Telefon FROM uyeler WHERE email = ?")) {
                    
                    ps.setString(1, yoneticiEmail);
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        soyad = rs.getString("SoyAd");
                        sifre = rs.getString("sifre");
                        telefon = rs.getString("Telefon");
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            // Profil düzenleme sayfasını aç
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profilduzenle.fxml"));
            Parent root = loader.load();
            profilduzenlecontroller controller = loader.getController();
            
            // Yönetici bilgilerini controller'a aktar
            controller.setProfilData(ad, soyad, email, sifre, telefon);
            
            Stage stage = new Stage();
            stage.setTitle("Profil Düzenle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sikayetleriYukle() {
        ObservableList<Sikayet> sikayetler = FXCollections.observableArrayList();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(UYEGIRIS_DB_URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM sikayetler ORDER BY Tarih DESC");
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    Sikayet sikayet = new Sikayet(
                        rs.getString("Ad"),
                        rs.getString("SoyAd"),
                        rs.getString("Mesaj"),
                        rs.getString("Tür"),
                        rs.getString("Tarih")
                    );
                    sikayetler.add(sikayet);
                }
                
                sikayetTable.setItems(sikayetler);
                
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void yoneticiAdGuncelle() {
        try (Connection conn = DriverManager.getConnection(UYEGIRIS_DB_URL, USER, PASS)) {
            String sql = "SELECT Ad FROM yonetim.uyeler WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, yoneticiTC);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                yoneticiAd = rs.getString("Ad");
                welcomeLabel.setText("Merhaba " + yoneticiAd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Şikayet veri modeli
    public static class Sikayet {
        private String ad;
        private String soyad;
        private String mesaj;
        private String tur;
        private String tarih;
        
        public Sikayet(String ad, String soyad, String mesaj, String tur, String tarih) {
            this.ad = ad;
            this.soyad = soyad;
            this.mesaj = mesaj;
            this.tur = tur;
            this.tarih = tarih;
        }
        
        // Getter metodları
        public String getAd() { return ad; }
        public String getSoyad() { return soyad; }
        public String getMesaj() { return mesaj; }
        public String getTur() { return tur; }
        public String getTarih() { return tarih; }
    }
} 