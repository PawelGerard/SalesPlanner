import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelController implements Initializable {

    @FXML
    private Button btnPlan;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnManagment;

    private String login;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @FXML
    void clickManagment(ActionEvent event) {
        if (getLogin().equals("Admin")) {
            ManagementController managementController = new ManagementController();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Management.fxml"));
            fxmlLoader.setController(managementController);
            loadStage(fxmlLoader, "Management");
        }
          else {
            JOptionPane.showMessageDialog(null, "This module in only for admin!",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FXML
    void clickPlan(ActionEvent event) {
        DashboardController dashboardController = new DashboardController(login);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Dashboard.fxml"));
        fxmlLoader.setController(dashboardController);
        loadStage(fxmlLoader, "Plan");
    }

    @FXML
    void clickReports(ActionEvent event) {
            ReportController reportController = new ReportController(login);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Report.fxml"));
            fxmlLoader.setController(reportController);
            loadStage(fxmlLoader, "Reports");
    }


    public void loadStage(FXMLLoader fxmlLoader, String title) {
        Parent root = null;
        try {
            root = fxmlLoader.load();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.show();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}

