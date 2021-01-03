import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<String> cbTypes, cbLogin;

    @FXML
    private Button btnSave, btnCancel;

    String action;

    ManagementTableRecord mtr;

    ManagementController parentController;

    public CustomerController(ManagementController parentController, ManagementTableRecord mtr, String action) {
        this.mtr = mtr;
        this.parentController = parentController;
        this.action = action;
    }

    public CustomerController(ManagementController parentController, String action) {
        this.parentController = parentController;
        this.action = action;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (mtr != null) {
            txtName.setText(mtr.getValueC2());
            cbTypes.setItems(getCustomerTypesName());
            cbTypes.setValue(mtr.getValueC3());
            cbLogin.setItems(getUsersName());
            cbLogin.setValue(mtr.getValueC4());
        }
        else {
            cbTypes.setItems(getCustomerTypesName());
            cbTypes.setValue(getCustomerTypesName().get(0));
            cbLogin.setItems(getUsersName());
            cbLogin.setValue(getUsersName().get(0));
        }
    }

    private void insertToDB() {
        String sql = "INSERT INTO `Customers` (`Id`, `Name`, `CustomerTypeId`, `UserId`)" +
                " VALUES (NULL, '"  + txtName.getText() + "', " + findCustomerTypeId() + ", " + findUserId() + ");";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateInDB() {
        String sql = "UPDATE `Customers` SET `Name` = '" + txtName.getText() + "', `CustomerTypeId` = " + findCustomerTypeId() + ", `UserId` = " + findUserId()
        + " WHERE `Id` = " + mtr.getValueC1() + ";";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int findCustomerTypeId() {
        String sql = "SELECT * FROM `CustomerType` WHERE Name = '" + cbTypes.getSelectionModel().getSelectedItem() + "';";
        int category = 0;
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                category = rs.getInt("Id");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return category;
    }

    private int findUserId() {
        String sql = "SELECT * FROM `Users` WHERE Login = '" + cbLogin.getSelectionModel().getSelectedItem() + "';";
        int category = 0;
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                category = rs.getInt("Id");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return category;
    }

    @FXML
    void clickCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSave(ActionEvent event) {
        if (isDataOk()) {
            switch(action) {
                case "insert":
                    insertToDB();
                    break;
                case "update":
                    updateInDB();
                    break;
            }
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
            parentController.createReport();
        }
        else {
            JOptionPane.showMessageDialog(null, "Please insert valid data!",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private ObservableList<String> getCustomerTypesName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT Name FROM `CustomerType`;";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("Name"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private ObservableList<String> getUsersName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT Login FROM `Users` WHERE Login != 'Admin';";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("Login"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private boolean isDataOk() {
        boolean result = false;
        if (txtName.getText() != null && !txtName.getText().equals("") )
            result = true;
        return result;
    }

}