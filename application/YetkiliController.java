package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputDialog;

public class YetkiliController {
    
    private static final String DB_URL = "**";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    
    // Üye Yönetimi
    @FXML
    private TableView<Uye> uyeTableView;
    @FXML
    private TableColumn<Uye, String> uyeTcColumn;
    @FXML
    private TableColumn<Uye, String> uyeAdColumn;
    @FXML
    private TableColumn<Uye, String> uyeSoyadColumn;
    @FXML
    private TableColumn<Uye, String> uyeEmailColumn;
    @FXML
    private TableColumn<Uye, String> uyeTelefonColumn;
    
    // Puan İşlemleri
    @FXML
    private TableView<PuanIslem> puanTableView;
    @FXML
    private TableColumn<PuanIslem, String> puanTcColumn;
    @FXML
    private TableColumn<PuanIslem, String> puanAdColumn;
    @FXML
    private TableColumn<PuanIslem, String> puanSoyadColumn;
    @FXML
    private TableColumn<PuanIslem, Integer> puanMiktarColumn;
    @FXML
    private TextField puanAramaTextField;
    @FXML
    private TextField puanMiktarTextField;
    @FXML
    private Button puanEkleButton;
    @FXML
    private Button puanDusButton;
    
    // Rezervasyon
    @FXML
    private TableView<Rezervasyon> rezervasyonTableView;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonTcColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonAdColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonSoyadColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonTarihColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonSaatDilimiColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonPaketTipiColumn;
    @FXML
    private TableColumn<Rezervasyon, String> rezervasyonDurumColumn;
    @FXML
    private TextField rezervasyonAramaTextField;
    @FXML
    private Button rezervasyonIptalButton;
    
    // Sıra Tablosu
    @FXML
    private TableView<Sira> siraTableView;
    @FXML
    private TableColumn<Sira, String> siraTcColumn;
    @FXML
    private TableColumn<Sira, String> siraAdColumn;
    @FXML
    private TableColumn<Sira, String> siraSoyadColumn;
    @FXML
    private TableColumn<Sira, Integer> siraSiraColumn;
    @FXML
    private TableColumn<Sira, LocalDateTime> siraTarihColumn;
    @FXML
    private TextField siraAramaTextField;
    @FXML
    private TextField siraTcTextField;
    @FXML
    private TextField siraSiraTextField;
    @FXML
    private Button siraEkleButton;
    @FXML
    private Button siraSilButton;
    @FXML
    private ComboBox<String> siraOyuncakComboBox;
    
    // Şifre Sıfırlama
    @FXML
    private TableView<SifreSifirlama> sifreSifirlamaTableView;
    @FXML
    private TableColumn<SifreSifirlama, String> sifreTcColumn;
    @FXML
    private TableColumn<SifreSifirlama, String> sifreKodColumn;
    @FXML
    private TableColumn<SifreSifirlama, LocalDateTime> sifreSonGecerlilikColumn;
    @FXML
    private TableColumn<SifreSifirlama, Boolean> sifreKullanildiColumn;
    @FXML
    private TextField sifreAramaTextField;
    
    // Arama
    @FXML
    private TextField tf_ara;
    @FXML
    private Button uyeAramaButton;
    
    @FXML
    private Button uyeDuzenleButton;
    
    @FXML
    private Button uyeSilButton;
    
    private ObservableList<Uye> tumUyeler = FXCollections.observableArrayList();
    private ObservableList<PuanIslem> tumPuanIslemler = FXCollections.observableArrayList();
    private ObservableList<Rezervasyon> tumRezervasyonlar = FXCollections.observableArrayList();
    private ObservableList<Sira> tumSiralar = FXCollections.observableArrayList();
    private ObservableList<SifreSifirlama> tumSifreSifirlamalar = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        // Üye tablosu sütunlarını ayarla
        uyeTcColumn.setCellValueFactory(new PropertyValueFactory<>("tc"));
        uyeAdColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        uyeSoyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        uyeEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        uyeTelefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        
        // Puan tablosu sütunlarını ayarla
        puanTcColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        puanAdColumn.setCellValueFactory(new PropertyValueFactory<>("Ad"));
        puanSoyadColumn.setCellValueFactory(new PropertyValueFactory<>("SoyAd"));
        puanMiktarColumn.setCellValueFactory(new PropertyValueFactory<>("Puan"));
        
        // Rezervasyon tablosu sütunlarını ayarla
        rezervasyonTcColumn.setCellValueFactory(new PropertyValueFactory<>("TC"));
        rezervasyonAdColumn.setCellValueFactory(new PropertyValueFactory<>("Ad"));
        rezervasyonSoyadColumn.setCellValueFactory(new PropertyValueFactory<>("SoyAd"));
        rezervasyonTarihColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        rezervasyonSaatDilimiColumn.setCellValueFactory(new PropertyValueFactory<>("saat_dilimi"));
        rezervasyonPaketTipiColumn.setCellValueFactory(new PropertyValueFactory<>("paket_tipi"));
        rezervasyonDurumColumn.setCellValueFactory(new PropertyValueFactory<>("durum"));
        
        // Sıra tablosu sütunlarını ayarla
        siraTcColumn.setCellValueFactory(new PropertyValueFactory<>("TC"));
        siraAdColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        siraSoyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        siraSiraColumn.setCellValueFactory(new PropertyValueFactory<>("sıraNo"));
        siraTarihColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        
        // Şifre sıfırlama tablosu sütunlarını ayarla
        sifreTcColumn.setCellValueFactory(new PropertyValueFactory<>("tc"));
        sifreKodColumn.setCellValueFactory(new PropertyValueFactory<>("kod"));
        sifreSonGecerlilikColumn.setCellValueFactory(new PropertyValueFactory<>("sonGecerlilik"));
        sifreKullanildiColumn.setCellValueFactory(new PropertyValueFactory<>("kullanildi"));
        
        // Tarih formatını ayarla
        siraTarihColumn.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<Sira, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });
        
        sifreSonGecerlilikColumn.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<SifreSifirlama, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });
        
        // Kullanıldı sütununu ayarla
        sifreKullanildiColumn.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<SifreSifirlama, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item ? "Evet" : "Hayır");
                    }
                }
            };
        });
        
        // Arama alanları için listener'lar
        tf_ara.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                uyeleriYukle(); // Arama kutusu boşsa tüm üyeleri yükle
            } else {
                uyeleriAra(newValue); // Veritabanında ara
            }
        });
        
        puanAramaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                puanIslemleriniYukle(); // Arama kutusu boşsa tüm puan işlemlerini yükle
            } else {
                puanIslemleriniAra(newValue); // Veritabanında ara
            }
        });
        
        rezervasyonAramaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                rezervasyonlariYukle(); // Arama kutusu boşsa tüm rezervasyonları yükle
            } else {
                rezervasyonlariAra(newValue); // Veritabanında ara
            }
        });
        
        siraAramaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                siralariYukle(); // Arama kutusu boşsa tüm sıraları yükle
            } else {
                siralariAra(newValue); // Veritabanında ara
            }
        });
        
        sifreAramaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                sifreSifirlamaVerileriniYukle(); // Arama kutusu boşsa tüm şifre sıfırlama verilerini yükle
            } else {
                sifreSifirlamaAra(newValue); // Veritabanında ara
            }
        });
        
        // Verileri yükle
        uyeleriYukle();
        puanIslemleriniYukle();
        rezervasyonlariYukle();
        siralariYukle();
        sifreSifirlamaVerileriniYukle();
        
        // Otomatik yenileme için timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    // Arama kutusu boşsa tüm verileri yükle
                    if (tf_ara.getText() == null || tf_ara.getText().isEmpty()) {
                        uyeleriYukle();
                    }
                    if (puanAramaTextField.getText() == null || puanAramaTextField.getText().isEmpty()) {
                        puanIslemleriniYukle();
                    }
                    if (rezervasyonAramaTextField.getText() == null || rezervasyonAramaTextField.getText().isEmpty()) {
                        rezervasyonlariYukle();
                    }
                    if (siraAramaTextField.getText() == null || siraAramaTextField.getText().isEmpty()) {
                        siralariYukle();
                    }
                    if (sifreAramaTextField.getText() == null || sifreAramaTextField.getText().isEmpty()) {
                        sifreSifirlamaVerileriniYukle();
                    }
                });
            }
        }, 5000, 5000); // 5 saniyede bir yenile
        
        // Sıra Yönetimi için oyuncak listesini yükle
        oyuncakListesiniYukle();
        
        // Oyuncak seçimi değiştiğinde sıraları güncelle
        siraOyuncakComboBox.setOnAction(event -> {
            siralariYukle();
        });
    }
    
    @FXML
    public void tf_arama_uye() {
        String aramaMetni = tf_ara.getText();
        if (aramaMetni == null || aramaMetni.isEmpty()) {
            uyeleriYukle(); // Arama kutusu boşsa tüm üyeleri yükle
        } else {
            uyeleriAra(aramaMetni); // Veritabanında ara
        }
    }
    
    @FXML
    public void uyeAramaButton_click() {
        tf_arama_uye();
    }
    
    @FXML
    public void uyeDuzenleButton_click() {
        Uye seciliUye = uyeTableView.getSelectionModel().getSelectedItem();
        if (seciliUye != null) {
            try {
                // Düzenleme penceresini aç
                FXMLLoader loader = new FXMLLoader(getClass().getResource("uyeDuzenle.fxml"));
                Parent root = loader.load();
                UyeDuzenleController controller = loader.getController();
                controller.setUye(seciliUye);
                
                Stage stage = new Stage();
                stage.setTitle("Üye Düzenle");
                stage.setScene(new Scene(root));
                stage.show();
                
                // Pencere kapandığında tabloyu güncelle
                stage.setOnCloseRequest(event -> {
                    uyeleriYukle();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Kullanıcıya bir uyarı göster
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen düzenlemek istediğiniz üyeyi seçin.");
            alert.showAndWait();
        }
    }
    
    @FXML
    public void uyeSilButton_click() {
        Uye seciliUye = uyeTableView.getSelectionModel().getSelectedItem();
        if (seciliUye != null) {
            // Kullanıcıya onay sor
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Onay");
            alert.setHeaderText(null);
            alert.setContentText(seciliUye.getAd() + " " + seciliUye.getSoyad() + " isimli üyeyi silmek istediğinize emin misiniz?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                uyeSil(seciliUye.getTc());
                uyeleriYukle();
            }
        } else {
            // Kullanıcıya bir uyarı göster
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen silmek istediğiniz üyeyi seçin.");
            alert.showAndWait();
        }
    }
    
    @FXML
    public void puanEkleButton_click() {
        PuanIslem seciliKisi = puanTableView.getSelectionModel().getSelectedItem();
        if (seciliKisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen puan eklenecek kişiyi seçin.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Puan Ekle");
        dialog.setHeaderText(null);
        dialog.setContentText("Eklenecek puan miktarını girin:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int eklenecekPuan = Integer.parseInt(result.get());
                if (eklenecekPuan <= 0) {
                    throw new NumberFormatException("Puan pozitif bir sayı olmalıdır.");
                }

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    String sql = "UPDATE puan_sistemi.puanlar SET Puan = Puan + ? WHERE ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, eklenecekPuan);
                    pstmt.setString(2, seciliKisi.getID());
                    pstmt.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Başarılı");
                    alert.setHeaderText(null);
                    alert.setContentText(seciliKisi.getAd() + " " + seciliKisi.getSoyAd() + 
                                       " kişisine " + eklenecekPuan + " puan eklendi.");
                    alert.showAndWait();

                    puanIslemleriniYukle(); // Tabloyu güncelle
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hata");
                    alert.setHeaderText(null);
                    alert.setContentText("Puan eklenirken bir hata oluştu: " + e.getMessage());
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText(null);
                alert.setContentText("Lütfen geçerli bir sayı girin.");
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    public void puanDusButton_click() {
        PuanIslem seciliKisi = puanTableView.getSelectionModel().getSelectedItem();
        if (seciliKisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen puan düşülecek kişiyi seçin.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Puan Düş");
        dialog.setHeaderText(null);
        dialog.setContentText("Düşülecek puan miktarını girin:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int dusulecekPuan = Integer.parseInt(result.get());
                if (dusulecekPuan <= 0) {
                    throw new NumberFormatException("Puan pozitif bir sayı olmalıdır.");
                }

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    // Önce mevcut puanı kontrol et
                    String checkSql = "SELECT Puan FROM puan_sistemi.puanlar WHERE ID = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, seciliKisi.getID());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        int mevcutPuan = rs.getInt("Puan");
                        if (mevcutPuan < dusulecekPuan) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Uyarı");
                            alert.setHeaderText(null);
                            alert.setContentText("Düşülecek puan miktarı mevcut puandan fazla olamaz.\nMevcut Puan: " + mevcutPuan);
                            alert.showAndWait();
                            return;
                        }

                        String sql = "UPDATE puan_sistemi.puanlar SET Puan = Puan - ? WHERE ID = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, dusulecekPuan);
                        pstmt.setString(2, seciliKisi.getID());
                        pstmt.executeUpdate();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Başarılı");
                        alert.setHeaderText(null);
                        alert.setContentText(seciliKisi.getAd() + " " + seciliKisi.getSoyAd() + 
                                           " kişisinden " + dusulecekPuan + " puan düşüldü.");
                        alert.showAndWait();

                        puanIslemleriniYukle(); // Tabloyu güncelle
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hata");
                    alert.setHeaderText(null);
                    alert.setContentText("Puan düşülürken bir hata oluştu: " + e.getMessage());
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText(null);
                alert.setContentText("Lütfen geçerli bir sayı girin.");
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    public void rezervasyonIptalButton_click() {
        Rezervasyon seciliRezervasyon = rezervasyonTableView.getSelectionModel().getSelectedItem();
        if (seciliRezervasyon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen iptal edilecek rezervasyonu seçin.");
            alert.showAndWait();
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false); // Transaction başlat
            
            try {
                // Önce rezervasyonun detaylarını al
                String sqlRezervasyon = "SELECT HesapNo, fiyat FROM uyegiris.rezervasyonlar WHERE TC = ? AND tarih = ? AND saat_dilimi = ?";
                PreparedStatement pstmtRezervasyon = conn.prepareStatement(sqlRezervasyon);
                pstmtRezervasyon.setString(1, seciliRezervasyon.getTC());
                pstmtRezervasyon.setString(2, seciliRezervasyon.getTarih());
                pstmtRezervasyon.setString(3, seciliRezervasyon.getSaat_dilimi());
                ResultSet rsRezervasyon = pstmtRezervasyon.executeQuery();
                
                if (rsRezervasyon.next()) {
                    String hesapNo = rsRezervasyon.getString("HesapNo");
                    double fiyat = rsRezervasyon.getDouble("fiyat");
                    
                    // Banka hesabına iade işlemi
                    String sqlBankaGuncelle = "UPDATE uyegiris.banka SET Bakiye = Bakiye + ? WHERE HesapNo = ?";
                    PreparedStatement pstmtBanka = conn.prepareStatement(sqlBankaGuncelle);
                    pstmtBanka.setDouble(1, fiyat);
                    pstmtBanka.setString(2, hesapNo);
                    pstmtBanka.executeUpdate();
                    
                    // Rezervasyonu sil
                    String sqlRezervasyonSil = "DELETE FROM uyegiris.rezervasyonlar WHERE TC = ? AND tarih = ? AND saat_dilimi = ?";
                    PreparedStatement pstmtSil = conn.prepareStatement(sqlRezervasyonSil);
                    pstmtSil.setString(1, seciliRezervasyon.getTC());
                    pstmtSil.setString(2, seciliRezervasyon.getTarih());
                    pstmtSil.setString(3, seciliRezervasyon.getSaat_dilimi());
                    pstmtSil.executeUpdate();
                    
                    conn.commit(); // Transaction'ı onayla
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Başarılı");
                    alert.setHeaderText(null);
                    alert.setContentText("Rezervasyon silindi ve " + fiyat + " TL iade işlemi gerçekleştirildi.\nHesap No: " + hesapNo);
                    alert.showAndWait();
                    
                    rezervasyonlariYukle(); // Tabloyu güncelle
                } else {
                    throw new SQLException("Rezervasyon bilgileri bulunamadı.");
                }
            } catch (SQLException e) {
                conn.rollback(); // Hata durumunda transaction'ı geri al
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Rezervasyon iptal edilirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void siraEkleButton_click() {
        String tc = siraTcTextField.getText();
        String siraNoStr = siraSiraTextField.getText();
        String seciliOyuncak = siraOyuncakComboBox.getValue();
        
        if (tc == null || tc.isEmpty() || siraNoStr == null || siraNoStr.isEmpty() || seciliOyuncak == null) {
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen tüm alanları doldurun ve bir oyuncak seçin.");
            alert.showAndWait();
            return;
        }
        
        try {
            int siraNo = Integer.parseInt(siraNoStr);
            
            // Check if the member already exists in the queue
            if (uyeSiraVarMi(tc, seciliOyuncak)) {
                // If exists, update the queue
                siraGuncelle(tc, siraNo, seciliOyuncak);
            } else {
                // If not exists, add to queue
                siraEkle(tc, siraNo, seciliOyuncak);
            }
            
            siraTcTextField.clear();
            siraSiraTextField.clear();
            siralariYukle();
        } catch (NumberFormatException e) {
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen sıra numarası için geçerli bir sayı girin.");
            alert.showAndWait();
        }
    }
    
    private boolean uyeSiraVarMi(String tc, String oyuncakAdi) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT COUNT(*) FROM oyuncaklar." + oyuncakAdi + " WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void siraGuncelle(String tc, int siraNo, String oyuncakAdi) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Önce üye bilgilerini al
            String sql = "SELECT * FROM uyegiris.uyeler WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String ad = rs.getString("Ad");
                String soyad = rs.getString("SoyAd");
                
                // Sıra güncelle
                sql = "UPDATE oyuncaklar." + oyuncakAdi + " SET sıra = ? WHERE TC = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, siraNo);
                pstmt.setString(2, tc);
                pstmt.executeUpdate();
                
                // Başarılı mesajı göster
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText(null);
                alert.setContentText("Sıra başarıyla güncellendi.");
                alert.showAndWait();
            } else {
                // Üye bulunamadı mesajı göster
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uyarı");
                alert.setHeaderText(null);
                alert.setContentText("Bu TC numarasına sahip bir üye bulunamadı.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Sıra güncellenirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void siraSilButton_click() {
        // Seçili sırayı sil
        Sira seciliSira = siraTableView.getSelectionModel().getSelectedItem();
        String seciliOyuncak = siraOyuncakComboBox.getValue();
        
        if (seciliSira != null && seciliOyuncak != null) {
            siraSil(seciliSira.getTC(), seciliOyuncak);
            siralariYukle();
        } else {
            // Kullanıcıya bir uyarı göster
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen silmek istediğiniz sırayı seçin ve bir oyuncak seçin.");
            alert.showAndWait();
        }
    }
    
    private void uyeleriYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.uyeler";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            tumUyeler.clear();
            while (rs.next()) {
                tumUyeler.add(new Uye(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("email"),
                    rs.getString("Telefon"),
                    0 // puan değeri şimdilik 0 olarak ayarlandı
                ));
            }
            uyeTableView.setItems(tumUyeler);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void uyeleriAra(String aramaMetni) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.uyeler WHERE TC LIKE ? OR Ad LIKE ? OR SoyAd LIKE ? OR email LIKE ? OR Telefon LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String aramaPattern = "%" + aramaMetni + "%";
            pstmt.setString(1, aramaPattern);
            pstmt.setString(2, aramaPattern);
            pstmt.setString(3, aramaPattern);
            pstmt.setString(4, aramaPattern);
            pstmt.setString(5, aramaPattern);

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Uye> aramaSonuclari = FXCollections.observableArrayList();
            while (rs.next()) {
                aramaSonuclari.add(new Uye(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("email"),
                    rs.getString("Telefon"),
                    0 // puan değeri şimdilik 0 olarak ayarlandı
                ));
            }
            uyeTableView.setItems(aramaSonuclari);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void puanIslemleriniYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT ID, Ad, SoyAd, Puan FROM puan_sistemi.puanlar ORDER BY ID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            tumPuanIslemler.clear();
            while (rs.next()) {
                tumPuanIslemler.add(new PuanIslem(
                    rs.getString("ID"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getInt("Puan")
                ));
            }
            puanTableView.setItems(tumPuanIslemler);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Puan işlemleri yüklenirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void puanIslemleriniAra(String aramaMetni) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT ID, Ad, SoyAd, Puan FROM puan_sistemi.puanlar WHERE ID LIKE ? OR Ad LIKE ? OR SoyAd LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String aramaPattern = "%" + aramaMetni + "%";
            pstmt.setString(1, aramaPattern);
            pstmt.setString(2, aramaPattern);
            pstmt.setString(3, aramaPattern);

            ResultSet rs = pstmt.executeQuery();
            ObservableList<PuanIslem> aramaSonuclari = FXCollections.observableArrayList();
            while (rs.next()) {
                aramaSonuclari.add(new PuanIslem(
                    rs.getString("ID"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getInt("Puan")
                ));
            }
            puanTableView.setItems(aramaSonuclari);
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Puan işlemleri aranırken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void rezervasyonlariYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.rezervasyonlar ORDER BY tarih, saat_dilimi";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("tarih"),
                    rs.getString("saat_dilimi"),
                    rs.getString("paket_tipi"),
                    rs.getString("durum")
                );
                rezervasyonlar.add(rezervasyon);
            }
            
            rezervasyonTableView.setItems(rezervasyonlar);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Rezervasyonlar yüklenirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void rezervasyonlariAra(String aramaMetni) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.rezervasyonlar WHERE TC LIKE ? OR Ad LIKE ? OR SoyAd LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String aramaParametresi = "%" + aramaMetni + "%";
            pstmt.setString(1, aramaParametresi);
            pstmt.setString(2, aramaParametresi);
            pstmt.setString(3, aramaParametresi);
            
            ResultSet rs = pstmt.executeQuery();
            ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("tarih"),
                    rs.getString("saat_dilimi"),
                    rs.getString("paket_tipi"),
                    rs.getString("durum")
                );
                rezervasyonlar.add(rezervasyon);
            }
            
            rezervasyonTableView.setItems(rezervasyonlar);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Rezervasyonlar aranırken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void siralariYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String seciliOyuncak = siraOyuncakComboBox.getValue();
            if (seciliOyuncak == null) {
                return;
            }
            
            String sql = "SELECT TC, Ad, SoyAd, sıra FROM oyuncaklar." + seciliOyuncak + " WHERE sıra > 0 ORDER BY sıra";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            tumSiralar.clear();
            while (rs.next()) {
                tumSiralar.add(new Sira(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getInt("sıra"),
                    LocalDateTime.now()
                ));
            }
            siraTableView.setItems(tumSiralar);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Sıralar yüklenirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void siralariAra(String aramaMetni) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String seciliOyuncak = siraOyuncakComboBox.getValue();
            if (seciliOyuncak == null) {
                return;
            }
            
            String sql = "SELECT * FROM oyuncaklar." + seciliOyuncak + " WHERE TC LIKE ? OR Ad LIKE ? OR SoyAd LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String aramaPattern = "%" + aramaMetni + "%";
            pstmt.setString(1, aramaPattern);
            pstmt.setString(2, aramaPattern);
            pstmt.setString(3, aramaPattern);

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Sira> aramaSonuclari = FXCollections.observableArrayList();
            while (rs.next()) {
                aramaSonuclari.add(new Sira(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getInt("sıra"),
                    LocalDateTime.now() // Tarih sütunu olmadığı için şu anki zamanı kullanıyoruz
                ));
            }
            siraTableView.setItems(aramaSonuclari);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void siraEkle(String tc, int siraNo, String oyuncakAdi) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Önce üye bilgilerini al
            String sql = "SELECT * FROM uyegiris.uyeler WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String ad = rs.getString("Ad");
                String soyad = rs.getString("SoyAd");
                
                // Sıra ekle
                sql = "INSERT INTO oyuncaklar." + oyuncakAdi + " (TC, Ad, SoyAd, sıra) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, tc);
                pstmt.setString(2, ad);
                pstmt.setString(3, soyad);
                pstmt.setInt(4, siraNo);
                pstmt.executeUpdate();
                
                // Başarılı mesajı göster
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText(null);
                alert.setContentText("Sıra başarıyla eklendi.");
                alert.showAndWait();
            } else {
                // Üye bulunamadı mesajı göster
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uyarı");
                alert.setHeaderText(null);
                alert.setContentText("Bu TC numarasına sahip bir üye bulunamadı.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Sıra eklenirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void siraSil(String tc, String oyuncakAdi) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Önce silinecek kişinin sıra numarasını al
            String sql = "SELECT sıra FROM oyuncaklar." + oyuncakAdi + " WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int silinecekSira = rs.getInt("sıra");
                
                // Kişiyi sil
                sql = "DELETE FROM oyuncaklar." + oyuncakAdi + " WHERE TC = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, tc);
                pstmt.executeUpdate();
                
                // Silinen kişinin sıra numarasından sonraki tüm kişilerin sıra numaralarını bir azalt
                sql = "UPDATE oyuncaklar." + oyuncakAdi + " SET sıra = sıra - 1 WHERE sıra > ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, silinecekSira);
                pstmt.executeUpdate();
                
                // Başarılı mesajı göster
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText(null);
                alert.setContentText("Sıra başarıyla silindi ve diğer sıralar güncellendi.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Sıra silinirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void sifreSifirlamaVerileriniYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.sifre_sifirlama ORDER BY son_gecerlilik DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            tumSifreSifirlamalar.clear();
            while (rs.next()) {
                tumSifreSifirlamalar.add(new SifreSifirlama(
                    rs.getString("TC"),
                    rs.getString("kod"),
                    rs.getTimestamp("son_gecerlilik").toLocalDateTime(),
                    rs.getBoolean("kullanildi")
                ));
            }
            sifreSifirlamaTableView.setItems(tumSifreSifirlamalar);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void sifreSifirlamaAra(String aramaMetni) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM uyegiris.sifre_sifirlama WHERE TC LIKE ? OR kod LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String aramaPattern = "%" + aramaMetni + "%";
            pstmt.setString(1, aramaPattern);
            pstmt.setString(2, aramaPattern);

            ResultSet rs = pstmt.executeQuery();
            ObservableList<SifreSifirlama> aramaSonuclari = FXCollections.observableArrayList();
            while (rs.next()) {
                aramaSonuclari.add(new SifreSifirlama(
                    rs.getString("TC"),
                    rs.getString("kod"),
                    rs.getTimestamp("son_gecerlilik").toLocalDateTime(),
                    rs.getBoolean("kullanildi")
                ));
            }
            sifreSifirlamaTableView.setItems(aramaSonuclari);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void uyeSil(String tc) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Önce üyenin ilişkili kayıtlarını kontrol et
            String sql = "SELECT COUNT(*) FROM uyegiris.uyeler WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int uyeSayisi = rs.getInt(1);
            
            if (uyeSayisi == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uyarı");
                alert.setHeaderText(null);
                alert.setContentText("Bu TC numarasına sahip bir üye bulunamadı.");
                alert.showAndWait();
                return;
            }
            
            // Rezervasyon kontrolü
            sql = "SELECT COUNT(*) FROM uyegiris.rezervasyonlar WHERE TC = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            rs = pstmt.executeQuery();
            rs.next();
            int rezervasyonSayisi = rs.getInt(1);
            
            // Sıra kontrolü - tüm oyuncaklar için kontrol et
            sql = "SHOW TABLES FROM oyuncaklar";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            int toplamSiraSayisi = 0;
            while (rs.next()) {
                String oyuncakAdi = rs.getString(1);
                sql = "SELECT COUNT(*) FROM oyuncaklar." + oyuncakAdi + " WHERE TC = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, tc);
                ResultSet siraRs = pstmt.executeQuery();
                siraRs.next();
                toplamSiraSayisi += siraRs.getInt(1);
            }
            
            // Şifre sıfırlama kontrolü
            sql = "SELECT COUNT(*) FROM uyegiris.sifre_sifirlama WHERE TC = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            rs = pstmt.executeQuery();
            rs.next();
            int sifreSifirlamaSayisi = rs.getInt(1);
            
            // İlişkili kayıtlar varsa kullanıcıya uyarı göster
            if (rezervasyonSayisi > 0 || toplamSiraSayisi > 0 || sifreSifirlamaSayisi > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uyarı");
                alert.setHeaderText(null);
                alert.setContentText("Bu üyenin ilişkili kayıtları var. Önce bu kayıtları silmelisiniz.");
                alert.showAndWait();
                return;
            }
            
            // Üyeyi sil
            sql = "DELETE FROM uyegiris.uyeler WHERE TC = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tc);
            pstmt.executeUpdate();
            
            // Başarılı mesajı göster
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bilgi");
            alert.setHeaderText(null);
            alert.setContentText("Üye başarıyla silindi.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Hata mesajı göster
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Üye silinirken bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void oyuncakListesiniYukle() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SHOW TABLES FROM oyuncaklar";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            ObservableList<String> oyuncaklar = FXCollections.observableArrayList();
            while (rs.next()) {
                oyuncaklar.add(rs.getString(1));
            }
            
            siraOyuncakComboBox.setItems(oyuncaklar);
            
            // Varsayılan olarak ilk oyuncakı seç
            if (!oyuncaklar.isEmpty()) {
                siraOyuncakComboBox.setValue(oyuncaklar.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Uye sınıfı
    public static class Uye {
        private String tc;
        private String ad;
        private String soyad;
        private String email;
        private String telefon;
        private int puan;
        
        public Uye(String tc, String ad, String soyad, String email, String telefon, int puan) {
            this.tc = tc;
            this.ad = ad;
            this.soyad = soyad;
            this.email = email;
            this.telefon = telefon;
            this.puan = puan;
        }
        
        public String getTc() { return tc; }
        public void setTc(String tc) { this.tc = tc; }
        public String getAd() { return ad; }
        public void setAd(String ad) { this.ad = ad; }
        public String getSoyad() { return soyad; }
        public void setSoyad(String soyad) { this.soyad = soyad; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTelefon() { return telefon; }
        public void setTelefon(String telefon) { this.telefon = telefon; }
        public int getPuan() { return puan; }
        public void setPuan(int puan) { this.puan = puan; }
    }
    
    // PuanIslem sınıfı
    public static class PuanIslem {
        private String ID;
        private String Ad;
        private String SoyAd;
        private int Puan;
        
        public PuanIslem(String ID, String Ad, String SoyAd, int Puan) {
            this.ID = ID;
            this.Ad = Ad;
            this.SoyAd = SoyAd;
            this.Puan = Puan;
        }
        
        public String getID() { return ID; }
        public String getAd() { return Ad; }
        public String getSoyAd() { return SoyAd; }
        public int getPuan() { return Puan; }
    }
    
    // Rezervasyon sınıfı
    public static class Rezervasyon {
        private String TC;
        private String Ad;
        private String SoyAd;
        private String tarih;
        private String saat_dilimi;
        private String paket_tipi;
        private String durum;
        
        public Rezervasyon(String TC, String Ad, String SoyAd, String tarih, String saat_dilimi, String paket_tipi, String durum) {
            this.TC = TC;
            this.Ad = Ad;
            this.SoyAd = SoyAd;
            this.tarih = tarih;
            this.saat_dilimi = saat_dilimi;
            this.paket_tipi = paket_tipi;
            this.durum = durum;
        }
        
        public String getTC() { return TC; }
        public String getAd() { return Ad; }
        public String getSoyAd() { return SoyAd; }
        public String getTarih() { return tarih; }
        public String getSaat_dilimi() { return saat_dilimi; }
        public String getPaket_tipi() { return paket_tipi; }
        public String getDurum() { return durum; }
    }
    
    // Sira sınıfı
    public static class Sira {
        private String TC;
        private String ad;
        private String soyad;
        private int sıra;
        private LocalDateTime tarih;

        public Sira(String TC, String ad, String soyad, int sıra, LocalDateTime tarih) {
            this.TC = TC;
            this.ad = ad;
            this.soyad = soyad;
            this.sıra = sıra;
            this.tarih = tarih;
        }

        public String getTC() {
            return TC;
        }

        public void setTC(String TC) {
            this.TC = TC;
        }

        public String getAd() {
            return ad;
        }

        public void setAd(String ad) {
            this.ad = ad;
        }

        public String getSoyad() {
            return soyad;
        }

        public void setSoyad(String soyad) {
            this.soyad = soyad;
        }

        public int getSıraNo() {
            return sıra;
        }

        public void setSıraNo(int sıra) {
            this.sıra = sıra;
        }

        public LocalDateTime getTarih() {
            return tarih;
        }

        public void setTarih(LocalDateTime tarih) {
            this.tarih = tarih;
        }
    }
    
    // SifreSifirlama sınıfı
    public static class SifreSifirlama {
        private String tc;
        private String kod;
        private LocalDateTime sonGecerlilik;
        private boolean kullanildi;
        
        public SifreSifirlama(String tc, String kod, LocalDateTime sonGecerlilik, boolean kullanildi) {
            this.tc = tc;
            this.kod = kod;
            this.sonGecerlilik = sonGecerlilik;
            this.kullanildi = kullanildi;
        }
        
        public String getTc() { return tc; }
        public void setTc(String tc) { this.tc = tc; }
        public String getKod() { return kod; }
        public void setKod(String kod) { this.kod = kod; }
        public LocalDateTime getSonGecerlilik() { return sonGecerlilik; }
        public void setSonGecerlilik(LocalDateTime sonGecerlilik) { this.sonGecerlilik = sonGecerlilik; }
        public boolean isKullanildi() { return kullanildi; }
        public void setKullanildi(boolean kullanildi) { this.kullanildi = kullanildi; }
    }
} 