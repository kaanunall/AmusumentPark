package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Duration;
import java.sql.*;

public class MusterilerController {

    @FXML
    private TableView<Uye> musteriTablosu;
    @FXML
    private TableColumn<Uye, String> tcSutun;
    @FXML
    private TableColumn<Uye, String> adSutun;
    @FXML
    private TableColumn<Uye, String> soyadSutun;
    @FXML
    private TableColumn<Uye, String> emailSutun;
    @FXML
    private TableColumn<Uye, String> telefonSutun;
    @FXML
    private TextField aramaKutusu;

    private ObservableList<Uye> uyelerListesi = FXCollections.observableArrayList();
    private Timeline refreshTimeline;

    @FXML
    public void initialize() {
        tcSutun.setCellValueFactory(new PropertyValueFactory<>("tc"));
        adSutun.setCellValueFactory(new PropertyValueFactory<>("ad"));
        soyadSutun.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        emailSutun.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonSutun.setCellValueFactory(new PropertyValueFactory<>("telefon"));

        aramaKutusu.textProperty().addListener((observable, oldValue, newValue) -> aramaYap(newValue));

        uyeleriYukle();
        startAutoRefresh();
    }

    private void uyeleriYukle() {
        uyelerListesi.clear();
        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "SELECT * FROM uyeler";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                uyelerListesi.add(new Uye(
                    rs.getString("TC"),
                    rs.getString("Ad"),
                    rs.getString("SoyAd"),
                    rs.getString("Email"),
                    rs.getString("Telefon")
                ));
            }

            musteriTablosu.setItems(uyelerListesi);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> uyeleriYukle()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void aramaYap(String aramaMetni) {
        if (aramaMetni.isEmpty()) {
            musteriTablosu.setItems(uyelerListesi);
        } else {
            ObservableList<Uye> filtrelenmisListe = FXCollections.observableArrayList();
            for (Uye uye : uyelerListesi) {
                if (uye.getTc().contains(aramaMetni) || 
                    uye.getAd().toLowerCase().contains(aramaMetni.toLowerCase()) || 
                    uye.getSoyad().toLowerCase().contains(aramaMetni.toLowerCase()) || 
                    uye.getEmail().toLowerCase().contains(aramaMetni.toLowerCase()) || 
                    uye.getTelefon().contains(aramaMetni)) {
                    filtrelenmisListe.add(uye);
                }
            }
            musteriTablosu.setItems(filtrelenmisListe);
        }
    }

    @FXML
    private void uyeSil() {
        Uye secilenUye = musteriTablosu.getSelectionModel().getSelectedItem();
        if (secilenUye != null) {
            try (Connection conn = DriverManager.getConnection("**", "root", "")) {
                String sql = "DELETE FROM uyeler WHERE TC = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, secilenUye.getTc());
                pstmt.executeUpdate();

                uyelerListesi.remove(secilenUye);
                musteriTablosu.refresh();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void uyeDuzenle() {
        Uye secilenUye = musteriTablosu.getSelectionModel().getSelectedItem();
        if (secilenUye != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("musteriduzenle.fxml"));
                Parent root = loader.load();

                MusteriDuzenleController controller = loader.getController();
                controller.setUye(secilenUye);

                Stage stage = new Stage();
                stage.setTitle("Üye Düzenle");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}