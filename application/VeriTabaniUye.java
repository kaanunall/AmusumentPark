package application;
import java.sql.*;
import javax.swing.JOptionPane;

import javax.swing.JOptionPane;
public class VeriTabaniUye {
	public Connection baglanti;
	private String databaseismi="**";
	private String kullaniciadi="**";
	private String kullanicisifre="";
	
	public Connection baglan() {
		String url="**"+databaseismi;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			baglanti=DriverManager.getConnection(url,kullaniciadi,kullanicisifre);
		}	catch (Exception e) {
			JOptionPane.showMessageDialog(null,e);
		}
		return baglanti;
	}
	public void kapat() {
		
		
		try {
			baglanti.close();
		}	catch (Exception e) {
			JOptionPane.showMessageDialog(null,e);
		}
		
	}
	

}