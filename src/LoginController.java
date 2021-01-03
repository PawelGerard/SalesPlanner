import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Label lbl1;

    @FXML
    private Label lbl2;

    @FXML
    private TextField txt1;

    @FXML
    private PasswordField txt2;

    @FXML
    private Button btn1;

    @FXML
    private Text txtMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void Clickbtn1(ActionEvent event) {
        searchForUser(txt1.getText(), txt2.getText());
    }


    public void searchForUser(String log, String pass) {

        String sql = "SELECT * FROM Users";
        boolean exist = false;

        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    if (rs.getString("Login").equals(log)) {
                        exist = true;
                        if (rs.getString("Password").equals(pass)) {
                            Stage currentStage = (Stage) btn1.getScene().getWindow();
                            currentStage.close();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Panel.fxml"));
                            Parent root = fxmlLoader.load();
                            PanelController panelController = (PanelController) fxmlLoader.getController();
                            panelController.setLogin(log);
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Sales Planner");
                            stage.show();
                        }
                    }
                }
            if (exist) {
                setInfoMessage("wrong password", Color.RED);
            } else {
                setInfoMessage("wrong login", Color.RED);
            }
        }
        catch (Exception ex) {
            setInfoMessage("SQL error", Color.RED);
            ex.printStackTrace();
        }
    }

    public void setInfoMessage(String text, Color color) {
        txtMessage.setText(text);
        txtMessage.setFill(color);
    }
}