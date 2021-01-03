import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    private Label lblNumber, lblName, lblStatus, lblWeight, lblUnits, lblPrice, lblCategory;

    @FXML
    private TextField txtPrice, txtUnits, txtWeight, txtName, txtNumber;

    @FXML
    private ComboBox<String> cbCategory, cbStatus;

    @FXML
    private Button btnSave, btnCancel;

    ManagementTableRecord mtr;
    ManagementController parentController;
    String action;

    public ProductController(ManagementController parentController, ManagementTableRecord mtr, String action) {
        this.mtr = mtr;
        this.parentController = parentController;
        this.action = action;
    }

    public ProductController(ManagementController parentController, String action) {
        this.parentController = parentController;
        this.action = action;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (mtr != null) {
            txtNumber.setText(mtr.getValueC2());
            txtName.setText(mtr.getValueC3());
            txtWeight.setText(mtr.getValueC4());
            txtUnits.setText(mtr.getValueC5());
            txtPrice.setText(mtr.getValueC6());
            cbCategory.setItems(getCategoriesName());
            cbCategory.setValue(mtr.getValueC7());
            cbStatus.setItems(getStatusName());
            cbStatus.setValue(mtr.getValueC8());
        }
        else {
            cbCategory.setItems(getCategoriesName());
            cbCategory.setValue(getCategoriesName().get(0));
            cbStatus.setItems(getStatusName());
            cbStatus.setValue(getStatusName().get(0));
        }
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
                    insertProductToDB();
                    break;
                case "update":
                    updateProductInDB();
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

    private ObservableList<String> getCategoriesName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT Name FROM `Categories`;";
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

    private ObservableList<String> getStatusName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Active");
        list.add("Inactive");
        return list;
    }

    private void insertProductToDB() {
        String sql = "INSERT INTO `Products` (`Id`, `Number`, `Name`, `Weight in kg`, `Units in carton`, `Price`, `CategoryId`, `Status`)" +
                " VALUES (NULL, '" + txtNumber.getText() + "', '" + txtName.getText() + "', '" + txtWeight.getText() + "', '" + txtUnits.getText() + "', '" +
                txtPrice.getText() + "', '" + findCategoryId() + "', '" + cbStatus.getSelectionModel().getSelectedItem() + "');";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProductInDB() {
        String sql = "UPDATE `Products` SET `Number`= " + txtNumber.getText() + ", `Name` = '" + txtName.getText() + "', `Weight in kg` = " + txtWeight.getText() +
                ", `Units in carton` = " + txtUnits.getText() + ", `Price` = " + txtPrice.getText() + ", `CategoryId` = " + findCategoryId() +
                ", `Status` = '" + cbStatus.getSelectionModel().getSelectedItem() + "' WHERE `Id` = " + mtr.getValueC1() + ";";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int findCategoryId() {
        String sql = "SELECT * FROM `Categories` WHERE Name = '" + cbCategory.getSelectionModel().getSelectedItem() + "';";
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


    private boolean isDataOk() {
        boolean result = false;
        if (txtName.getText() != null && !txtName.getText().equals("") && isNumeric(txtNumber.getText()) && isNumeric(txtWeight.getText()) &&
                isNumeric(txtUnits.getText()) && isNumeric(txtPrice.getText()))
           result = true;
        return result;
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

}
