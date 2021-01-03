import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ManagementController implements Initializable {

    @FXML
    private TableView<ManagementTableRecord> mainTable;

    @FXML
    private Button btnProducts, btnCustomers, btnAdd, btnEdit, bDelete, btnExit;

    @FXML
    private TableColumn<ManagementTableRecord, String> c1, c2, c3, c4, c5, c6, c7, c8;

    String[] sqlColumnNames;

    ObservableList<ManagementTableRecord> oblist = FXCollections.observableArrayList();

    ArrayList<TableColumn> columnList = new ArrayList<>();

    String lastButton;

    String lastTable;

    String sqlStatement;

    String[] getSqlColumnNames;

    //one model for all reports
    //based on class ReportTableController
    String[] tableModelVariables = {"valueC1", "valueC2", "valueC3", "valueC4", "valueC5", "valueC6", "valueC7", "valueC8"};


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnList.add(c1);
        columnList.add(c2);
        columnList.add(c3);
        columnList.add(c4);
        columnList.add(c5);
        columnList.add(c6);
        columnList.add(c7);
        columnList.add(c8);
    }

    @FXML
    void clickExit(ActionEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }


    @FXML
    void clickAdd(ActionEvent event) {
        FXMLLoader fxmlLoader = null;
        ManagementTableRecord mtr = mainTable.getSelectionModel().getSelectedItem();
        if (mtr == null) {
            JOptionPane.showMessageDialog(null, "Please select item",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
        else {
            switch (lastTable) {
                case "Products":
                    fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Product.fxml"));
                    ProductController productController = new ProductController(this, "insert");
                    fxmlLoader.setController(productController);
                    loadStage(fxmlLoader);
                    break;
                case "Customers":
                    fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Customer.fxml"));
                    CustomerController customerController = new CustomerController(this, "insert");
                    fxmlLoader.setController(customerController);
                    loadStage(fxmlLoader);
                    break;
            }
        }
    }

    @FXML
    void clickEdit(ActionEvent event) {
        FXMLLoader fxmlLoader = null;
        ManagementTableRecord mtr = mainTable.getSelectionModel().getSelectedItem();
        if (mtr == null) {
            JOptionPane.showMessageDialog(null, "Please select item",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
        else {
            switch (lastTable) {
                case "Products":
                    fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Product.fxml"));
                    ProductController productController = new ProductController(this, mtr,"update");
                    fxmlLoader.setController(productController);
                    loadStage(fxmlLoader);
                    break;
                case "Customers":
                    fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Customer.fxml"));
                    CustomerController customerController = new CustomerController(this, mtr,"update");
                    fxmlLoader.setController(customerController);
                    loadStage(fxmlLoader);
                    break;
            }
        }
    }

    @FXML
    void clickDelete(ActionEvent event) {
        ManagementTableRecord mtr = mainTable.getSelectionModel().getSelectedItem();
        int result = 0;
        if (mtr == null) {
            JOptionPane.showMessageDialog(null, "Please select item",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
        else {
            result = optionResult(mtr);
            if (result == 0) {
                deleteProductInDB(mtr);
                createReport();
            }
        }
    }

    private int optionResult(ManagementTableRecord mtr) {
        String questionValue = null;
        switch(lastTable) {
            case "Products":
                questionValue = mtr.getValueC3();
                break;
            case "Customers":
                questionValue = mtr.getValueC2();
                break;
        }
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(null,"Are you sure to delete " + questionValue + "?",
                         "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[1]);
        return n;
    }

    private void deleteProductInDB(ManagementTableRecord mtr) {
        String sql = null;
        switch (lastTable) {
            case "Products":
                sql = "DELETE FROM `Products` WHERE `Id` = " + mtr.getValueC1() + ";";
                break;
            case "Customers":
                sql = "DELETE FROM `Customers` WHERE `Id` = " + mtr.getValueC1() + ";";
                break;
        }
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void clickCustomers(ActionEvent event) {
        lastTable = "Customers";
        String[] sqlColumnNames = {"Id", "Customer", "Customer Type", "Login"};
        this.sqlColumnNames = sqlColumnNames;
        sqlStatement = "SELECT Customers.Id, Customers.Name as Customer, CustomerType.Name as 'Customer Type' , Users.Login as Login FROM (Customers, CustomerType, Users)" +
                " WHERE Customers.CustomerTypeId = CustomerType.Id AND Customers.UserId = Users.Id;  ";
        createReport();
    }

    @FXML
    private void clickProducts(ActionEvent event) {
        lastTable = "Products";
        String[] sqlColumnNames = {"Id", "Number", "Name", "Weight in kg", "Units in carton", "Price", "Category", "Status"};
        this.sqlColumnNames = sqlColumnNames;
        sqlStatement = "SELECT Products.Id, Products.Number, Products.Name, Products.`Weight in kg`," +
                " Products.`Units in carton`, Products.Price, Categories.Name as 'Category', Products.Status FROM (Products, Categories)" +
                " WHERE Products.CategoryId = Categories.Id ORDER BY Products.Id ASC;";
        createReport();
    }


    public void createReport() {
        //clear list
        oblist.clear();
        //delete column headings
        for(int i = 0; i<8;i++) {
            columnList.get(i).setText("");
        }
        //if less than 8 columns are required the rest will be empty
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sqlStatement)) {
            while (rs.next()) {
                String[] tab = new String[8];
                for (int i = 0; i<sqlColumnNames.length;i++) {
                    tab[i] = rs.getString(sqlColumnNames[i]);
                }
                oblist.add(new ManagementTableRecord(tab[0], tab[1], tab[2], tab[3], tab[4], tab[5], tab[6], tab[7]));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        //adding headings and values to all required columns
        for(int i = 0; i<sqlColumnNames.length;i++) {
            columnList.get(i).setCellValueFactory(new PropertyValueFactory<>(tableModelVariables[i]));
            columnList.get(i).setText(sqlColumnNames[i]);
        }
        mainTable.setItems(oblist);
    }

    public void loadStage(FXMLLoader fxmlLoader) {
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
        stage.show();
    }
}
