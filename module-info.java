module LunaParkOtomasyon {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
    requires java.mail;
    requires java.activation;
	requires java.desktop;	
    requires java.xml;
	opens application to javafx.graphics, javafx.fxml, javafx.base;
    exports application;

}
