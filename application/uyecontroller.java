package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Hyperlink;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.animation.Animation;

public class uyecontroller {

    @FXML
    private TableView<Puan> puanTable;
    @FXML
    private TableColumn<Puan, Integer> idColumn;
    @FXML
    private TableColumn<Puan, String> adColumn;
    @FXML
    private TableColumn<Puan, String> soyadColumn;
    @FXML
    private TableColumn<Puan, Integer> puanColumn;

    @FXML
    private TableView<Kampanya> kampanyaTable;
    @FXML
    private TableColumn<Kampanya, Integer> kampanyaIdColumn;
    @FXML
    private TableColumn<Kampanya, String> kampanyaAciklamaColumn;
    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Button bilet_al;
    @FXML
    private Button queueButton;
    
    @FXML
    private String kullaniciTC;

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label biletAdetLabel;
    
    @FXML
    private AnchorPane biletPane;
    
    @FXML
    private ImageView ticketIcon;
    
    @FXML
    private Rectangle highlightRect;

    @FXML
    private AnchorPane notificationPane;
    
    @FXML
    private Label notificationLabel;
    
    @FXML
    private ImageView notificationIcon;
    
    private Timeline hideNotificationTimeline;

    private Timeline refreshTimeline;

    @FXML
    private Hyperlink yorumsikayet;

    @FXML
    private void bilet_al_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bilet.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Bilet Satın Alma");
            
            // Biletler controller'ı al
            biletlercontroller controller = loader.getController();
            
            // Bilet alım başarılı olduğunda çağrılacak metodu ayarla
            controller.setOnBiletAlimBasarili(() -> {
                guncelleBiletSayisi();
                showNotification("Bilet adedi güncellendi");
            });
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void biletPaneHoverEffect() {
        // Hover animasyonu
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), highlightRect);
        fadeTransition.setToValue(0.1);
        fadeTransition.play();
        
        // İkon animasyonu
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), ticketIcon);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.play();
        
        // Gölge efekti
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#0598ff"));
        glow.setWidth(20);
        glow.setHeight(20);
        biletPane.setEffect(glow);
    }

    @FXML
    private void biletPaneExitEffect() {
        // Hover animasyonu
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), highlightRect);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        
        // İkon animasyonu
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), ticketIcon);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
        
        // Normal gölge efekti
        DropShadow normalShadow = new DropShadow();
        normalShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        normalShadow.setWidth(10);
        normalShadow.setHeight(10);
        biletPane.setEffect(normalShadow);
    }

    // Bilet sayısı güncellendiğinde animasyon
    private void animateBiletUpdate() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), biletAdetLabel);
        scaleTransition.setFromX(1.2);
        scaleTransition.setFromY(1.2);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    public static String girisYapanKullaniciAdi = "";
    public static String girisYapanKullaniciTC = "";
    public static String girisYapanKullaniciSoyad = "";
    
    public void setKullaniciBilgileri(String ad, String tc, String soyad) {
        welcomeLabel.setText("Merhaba " + ad + " ");
        this.kullaniciTC = tc;
        girisYapanKullaniciAdi = ad;
        girisYapanKullaniciTC = tc;
        girisYapanKullaniciSoyad = soyad;
        guncelleBiletSayisi();
    }

    public void setKullaniciTC(String tc) {
        this.kullaniciTC = tc;
        girisYapanKullaniciTC = tc;
        guncelleBiletSayisi();
    }

    private void showNotification(String message) {
        // Eğer timeline varsa ve çalışıyorsa durdur
        if (hideNotificationTimeline != null) {
            hideNotificationTimeline.stop();
        }
        
        // Bildirim panelini hazırla
        if (notificationPane == null) {
            // Notification paneli oluştur
            notificationPane = new AnchorPane();
            notificationPane.setPrefHeight(60);
            notificationPane.setPrefWidth(300);
            notificationPane.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
            
            // İçerik için HBox
            HBox content = new HBox(10);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setPadding(new javafx.geometry.Insets(10));
            
            // İkon
            notificationIcon = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("../resimler/ticket.png")));
            notificationIcon.setFitHeight(30);
            notificationIcon.setFitWidth(30);
            
            // Label
            notificationLabel = new Label();
            notificationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
            
            content.getChildren().addAll(notificationIcon, notificationLabel);
            notificationPane.getChildren().add(content);
            
            // Ana panele ekle ve en üstte göster
            mainAnchorPane.getChildren().add(notificationPane);
            notificationPane.toFront();
            
            // Başlangıç pozisyonu (ekranın üstünde gizli)
            notificationPane.setLayoutX((mainAnchorPane.getPrefWidth() - 300) / 2);
            notificationPane.setLayoutY(-70);
        }
        
        // Mesajı güncelle
        notificationLabel.setText(message);
        
        // Aşağı kayma animasyonu
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), notificationPane);
        slideIn.setFromY(0);
        slideIn.setToY(90);
        slideIn.play();
        
        // 7 saniye sonra yukarı kayma animasyonu
        hideNotificationTimeline = new Timeline(
            new KeyFrame(Duration.seconds(7), e -> {
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), notificationPane);
                slideOut.setFromY(90);
                slideOut.setToY(0);
                slideOut.play();
            })
        );
        hideNotificationTimeline.play();
    }

    private void guncelleBiletSayisi() {
        try {
            Connection connection = DriverManager.getConnection(
                    "**", "root", "");
            String query = "SELECT Adet FROM biletler WHERE TC = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kullaniciTC);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int biletSayisi = resultSet.getInt("Adet");
                biletAdetLabel.setText("Bilet Adedi: " + biletSayisi);
                animateBiletUpdate();
            } else {
                biletAdetLabel.setText("Bilet Adedi: 0");
            }
            
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            biletAdetLabel.setText("Bilet Adedi: 0");
        }
    }

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        soyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        puanColumn.setCellValueFactory(new PropertyValueFactory<>("puan"));

        kampanyaIdColumn.setCellValueFactory(new PropertyValueFactory<>("kampanyaId"));
        kampanyaAciklamaColumn.setCellValueFactory(new PropertyValueFactory<>("aciklama"));

        loadPuanData();
        loadKampanyaData();
        
        // Her saniye yenileme için zamanlayıcı oluştur
        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Tabloları yenile
                loadPuanData();
                loadKampanyaData();
                
                // Kullanıcı adını yenile
                try {
                    Connection conn = DriverManager.getConnection(
                            "**", "root", "");
                    String sql = "SELECT Ad FROM uyeler WHERE TC = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, kullaniciTC);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String ad = rs.getString("Ad");
                        welcomeLabel.setText("Merhaba " + ad);
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            })
        );
        
        // Zamanlayıcıyı başlat
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        welcomeLabel.setText("Merhaba " + kullaniciAdi);
        girisYapanKullaniciAdi = kullaniciAdi;
    }

    public void loadPuanData() {
        ObservableList<Puan> puanList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(
                    "**", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM puanlar ORDER BY Puan DESC");

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String ad = resultSet.getString("Ad");
                String soyad = resultSet.getString("SoyAd");
                int puan = resultSet.getInt("Puan");
                puanList.add(new Puan(id, ad, soyad, puan));
            }
            puanTable.setItems(puanList);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadKampanyaData() {
        ObservableList<Kampanya> kampanyaList = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(
                    "**", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM kampanyabilgi");

            while (resultSet.next()) {
                int kampanyaId = resultSet.getInt("KampanyaID");
                String aciklama = resultSet.getString("Aciklama");
                kampanyaList.add(new Kampanya(kampanyaId, aciklama));
            }
            kampanyaTable.setItems(kampanyaList);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   @FXML
private void reservationButton_click(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rezervasyon.fxml"));
        Parent root = loader.load();
        
        // Controller'ı al ve TC'yi set et
        RezervasyonController controller = loader.getController();
        controller.setUserInfo(kullaniciTC); // tc değişkenini kullan
        // veya
        // controller.setUserInfo(getTc()); // eğer bir getter metodunuz varsa
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Rezervasyon");
        stage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void profileButton_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("uyeprofil.fxml"));
            Parent root = loader.load();
            
            // Controller'ı al ve TC'yi set et
            UyeProfilController controller = loader.getController();
            controller.setUserTC(kullaniciTC);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil Düzenle");
            
            // Profil penceresi kapandığında ana sayfadaki ismi güncelle
            stage.setOnHidden(e -> {
                // Veritabanından güncel ismi çek
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    conn = DriverManager.getConnection(
                            "**", "root", "");
                    String sql = "SELECT Ad FROM uyeler WHERE TC = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, kullaniciTC);
                    rs = ps.executeQuery();
                    
                    if(rs.next()) {
                        welcomeLabel.setText("Merhaba " + rs.getString("Ad"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if(rs != null) rs.close();
                        if(ps != null) ps.close();
                        if(conn != null) conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void bankaButton_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("banka.fxml"));
            Parent root = loader.load();
            
            BankaController bankaController = loader.getController();
            bankaController.setKullaniciBilgileri(girisYapanKullaniciAdi, girisYapanKullaniciTC, girisYapanKullaniciSoyad);
            
            Stage stage = new Stage();
            stage.setTitle("Banka İşlemleri");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Banka sayfası açılırken bir hata oluştu!");
            alert.showAndWait();
        }
    }

    @FXML
    private void siraButton_click() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/sira.fxml"));
            Parent root = loader.load();
            SiraController controller = loader.getController();
            
            // Kullanıcı bilgilerini aktar
            controller.setKullaniciBilgileri(girisYapanKullaniciTC, girisYapanKullaniciAdi, girisYapanKullaniciSoyad);
            
            Stage stage = new Stage();
            stage.setTitle("Sıra Takip");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Sıra sayfası açılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void yorumsikayetclick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sikayetler.fxml"));
            Parent root = loader.load();
            
            // Controller'ı al ve TC'yi set et
            SikayetlerController controller = loader.getController();
            controller.setKullaniciTC(kullaniciTC);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Yorum / Şikayet");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Sayfa açılırken bir hata oluştu: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Pencere kapatıldığında zamanlayıcıyı durdur
    @FXML
    private void handleClose() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }
}
