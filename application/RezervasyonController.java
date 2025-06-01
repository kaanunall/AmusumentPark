package application;

import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RezervasyonController implements Initializable {
    @FXML private VBox reservationForm;
    @FXML private VBox existingReservation;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeSlotCombo;
    @FXML private RadioButton standardPackage;
    @FXML private RadioButton ultraPackage;
    @FXML private Label reservationDetails;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox<String> hesapComboBox;
    @FXML private Label bakiyeLabel;

    private ToggleGroup packageGroup;
    private String userTC;
    private String userAd;
    private String userSoyad;
    private final double STANDART_FIYAT = 200.0;
    private final double ULTRA_FIYAT = 400.0;
    private VeriTabaniUye vt = new VeriTabaniUye();
    private String selectedHesapNo;
    private double selectedHesapBakiye;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        packageGroup = new ToggleGroup();
        standardPackage.setToggleGroup(packageGroup);
        ultraPackage.setToggleGroup(packageGroup);

        timeSlotCombo.getItems().addAll(
            "10:00 - 14:00",
            "14:00 - 18:00",
            "18:00 - 22:00"
        );

        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });

        // Hesap seçimi değiştiğinde bakiyeyi güncelle
        hesapComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    String hesapNo = newValue.split(" ")[0];
                    updateSelectedAccountInfo(hesapNo);
                }
            }
        );
    }

    public void setUserInfo(String tc) {
        this.userTC = tc;
        loadUserData();
        loadUserAccounts();
        checkExistingReservation();
    }

    private void loadUserData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vt.baglan();
            String sql = "SELECT Ad, SoyAd FROM uyeler WHERE TC = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userTC);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                this.userAd = rs.getString("Ad");
                this.userSoyad = rs.getString("SoyAd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void loadUserAccounts() {
        try (Connection conn = vt.baglan()) {
            String sql = "SELECT HesapNo, bakiye FROM banka WHERE TC = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userTC);
            ResultSet rs = ps.executeQuery();

            ObservableList<String> hesaplar = FXCollections.observableArrayList();
            while (rs.next()) {
                String hesapNo = rs.getString("HesapNo");
                double bakiye = rs.getDouble("bakiye");
                hesaplar.add(hesapNo + " (Bakiye: " + bakiye + " TL)");
            }
            hesapComboBox.setItems(hesaplar);

            if (!hesaplar.isEmpty()) {
                hesapComboBox.getSelectionModel().selectFirst();
                String firstHesapNo = hesaplar.get(0).split(" ")[0];
                updateSelectedAccountInfo(firstHesapNo);
            }
        } catch (SQLException e) {
            showAlert("Hata", "Hesaplar yüklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    private void updateSelectedAccountInfo(String hesapNo) {
        try (Connection conn = vt.baglan()) {
            String sql = "SELECT bakiye FROM banka WHERE TC = ? AND HesapNo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userTC);
            ps.setString(2, hesapNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                selectedHesapNo = hesapNo;
                selectedHesapBakiye = rs.getDouble("bakiye");
                bakiyeLabel.setText("Seçili Hesap Bakiyesi: " + selectedHesapBakiye + " TL");
            }
        } catch (SQLException e) {
            showAlert("Hata", "Hesap bilgileri alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    private void checkExistingReservation() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vt.baglan();
            String sql = "SELECT * FROM rezervasyonlar WHERE TC = ? AND durum = 'Rezervasyon Yapıldı'";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userTC);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                reservationForm.setVisible(false);
                existingReservation.setVisible(true);
                
                String reservationInfo = String.format(
                    "Tarih: %s\n" +
                    "Saat: %s\n" +
                    "Paket: %s\n" +
                    "Fiyat: %.2f₺\n\n" +
                    "Durum: Rezervasyon Yapıldı",
                    rs.getDate("tarih"),
                    rs.getString("saat_dilimi"),
                    rs.getString("paket_tipi"),
                    rs.getDouble("fiyat")
                );
                reservationDetails.setText(reservationInfo);
            } else {
                reservationForm.setVisible(true);
                existingReservation.setVisible(false);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
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

    private double getSelectedPackagePrice() {
        RadioButton selected = (RadioButton) packageGroup.getSelectedToggle();
        return selected == standardPackage ? STANDART_FIYAT : ULTRA_FIYAT;
    }

    private String getSelectedPackageType() {
        RadioButton selected = (RadioButton) packageGroup.getSelectedToggle();
        return selected == standardPackage ? "Standart Paket" : "Ultra Paket";
    }

    @FXML
    private void confirmReservation() {
        if (!validateInputs()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun!");
            return;
        }

        double fiyat = getSelectedPackagePrice();
        
        if (!checkAndUpdateBalance(fiyat)) {
            return; // Hata mesajı checkAndUpdateBalance içinde gösteriliyor
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = vt.baglan();
            String sql = "INSERT INTO rezervasyonlar (TC, Ad, SoyAd, tarih, saat_dilimi, paket_tipi, fiyat, durum, HesapNo) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'Rezervasyon Yapıldı', ?)";
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, userTC);
            ps.setString(2, userAd);
            ps.setString(3, userSoyad);
            ps.setDate(4, java.sql.Date.valueOf(datePicker.getValue()));
            ps.setString(5, timeSlotCombo.getValue());
            ps.setString(6, getSelectedPackageType());
            ps.setDouble(7, fiyat);
            ps.setString(8, selectedHesapNo); // Ödeme yapılan hesap numarası
            
            ps.executeUpdate();
            showAlert("Başarılı", "Rezervasyonunuz başarıyla oluşturuldu!");
            
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
            
        } catch (SQLException e) {
            showAlert("Hata", "Rezervasyon oluşturma hatası: " + e.getMessage());
        } finally {
            try {
                if(ps != null) ps.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void cancelReservation() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = vt.baglan();
            
            // Rezervasyon bilgilerini al (fiyat ve hesap no)
            String selectSql = "SELECT fiyat, HesapNo FROM rezervasyonlar WHERE TC = ? AND durum = 'Rezervasyon Yapıldı'";
            ps = conn.prepareStatement(selectSql);
            ps.setString(1, userTC);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                double fiyat = rs.getDouble("fiyat");
                String hesapNo = rs.getString("HesapNo");
                
                // Bakiyeyi orijinal hesaba iade et
                String refundSql = "UPDATE banka SET bakiye = bakiye + ? WHERE TC = ? AND HesapNo = ?";
                PreparedStatement refundPs = conn.prepareStatement(refundSql);
                refundPs.setDouble(1, fiyat);
                refundPs.setString(2, userTC);
                refundPs.setString(3, hesapNo);
                refundPs.executeUpdate();
                
                // Rezervasyonu sil
                ps = conn.prepareStatement("DELETE FROM rezervasyonlar WHERE TC = ? AND durum = 'Rezervasyon Yapıldı'");
                ps.setString(1, userTC);
                ps.executeUpdate();
                
                showAlert("Başarılı", "Rezervasyonunuz iptal edildi ve ücret " + hesapNo + " numaralı hesaba iade edildi.");
                
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            }
            
        } catch (SQLException e) {
            showAlert("Hata", "Rezervasyon iptali sırasında hata: " + e.getMessage());
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

    private boolean checkAndUpdateBalance(double fiyat) {
        if (selectedHesapNo == null) {
            showAlert("Uyarı", "Lütfen bir hesap seçiniz!");
            return false;
        }

        try (Connection conn = vt.baglan()) {
            // Güncel bakiyeyi kontrol et
            String checkSql = "SELECT bakiye FROM banka WHERE TC = ? AND HesapNo = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, userTC);
            checkPs.setString(2, selectedHesapNo);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                double currentBakiye = rs.getDouble("bakiye");
                if (currentBakiye >= fiyat) {
                    // Bakiyeyi güncelle
                    String updateSql = "UPDATE banka SET bakiye = bakiye - ? WHERE TC = ? AND HesapNo = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setDouble(1, fiyat);
                    updatePs.setString(2, userTC);
                    updatePs.setString(3, selectedHesapNo);
                    updatePs.executeUpdate();
                    return true;
                }
            }
            showAlert("Uyarı", "Seçili hesapta yeterli bakiye bulunmamaktadır!");
            return false;
        } catch (SQLException e) {
            showAlert("Hata", "Bakiye kontrolü sırasında bir hata oluştu: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInputs() {
        if (datePicker.getValue() == null || 
            timeSlotCombo.getValue() == null || 
            packageGroup.getSelectedToggle() == null ||
            hesapComboBox.getValue() == null) {
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 