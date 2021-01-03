import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {

    public static Connection getConnection() {
        Connection connection = null;
        String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://pawelgerard.cba.pl:3306/pawelgerard", "pawelgerard", "Test121");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
