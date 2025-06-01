package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;

public class MusteriDuzenleController {

    @FXML
    private TextField tcField;
    @FXML
    private TextField adField;
    @FXML
    private TextField soyadField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telefonField;

    private Uye secilenUye;

    public void setUye(Uye uye) {
        this.secilenUye = uye;
        tcField.setText(uye.getTc());
        adField.setText(uye.getAd());
        soyadField.setText(uye.getSoyad());
        emailField.setText(uye.getEmail());
        telefonField.setText(uye.getTelefon());
    }

    @FXML
    private void kaydet() {
        String yeniAd = adField.getText();
        String yeniSoyad = soyadField.getText();
        String yeniEmail = emailField.getText();
        String yeniTelefon = telefonField.getText();

        try (Connection conn = DriverManager.getConnection("**", "root", "")) {
            String sql = "UPDATE uyeler SET Ad = ?, SoyAd = ?, Email = ?, Telefon = ? WHERE TC = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, yeniAd);
            pstmt.setString(2, yeniSoyad);
            pstmt.setString(3, yeniEmail);
            pstmt.setString(4, yeniTelefon);
            pstmt.setString(5, secilenUye.getTc());
            pstmt.executeUpdate();

            // Pencereyi kapat
            Stage stage = (Stage) tcField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}