module com.gestionstock {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jbcrypt;

    opens com.gestionstock to javafx.fxml, javafx.graphics;
    opens com.gestionstock.controller to javafx.fxml;
    opens com.gestionstock.model to javafx.fxml, javafx.base, org.hibernate.orm.core;
}