import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    private TableView<ReportTableRecord> mainTable;

    @FXML
    private TableColumn<?, String> c1, c2, c3, c4, c5, c6;

    @FXML
    private ComboBox<String> cbPeriods, cbCustomers, cbProducts;

    @FXML
    private Button btnLastChanges, btnProductDetails, btnWeightByCustomer,
            btnWeightByProduct, btnValueByProduct, btnValueByCustomer, btnCustomerDetails, btnExit;

    String Login;

    String lastRaport = "noReportClicked";

    String[] sqlColumnNames;

    ObservableList<ReportTableRecord> oblist = FXCollections.observableArrayList();

    ArrayList<TableColumn> columnList = new ArrayList<>();

    //one model for all reports
    //based on class ReportTableController
    String[] tableModelVariables = {"valueC1", "valueC2", "valueC3", "valueC4", "valueC5", "valueC6"};

    //constructor to pass login from previous controllers
    public ReportController(String login) {
        Login = login;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbCustomers.setItems(getCustomersName());
        cbCustomers.setValue(getCustomersName().get(0));


        cbPeriods.setItems(getPeriodsName());
        cbPeriods.setValue(getPeriodsName().get(0));

        cbProducts.setItems(getProductsName());
        cbProducts.setValue(getProductsName().get(0));

        //adding all table columns to the list
        columnList.add(c1);
        columnList.add(c2);
        columnList.add(c3);
        columnList.add(c4);
        columnList.add(c5);
        columnList.add(c6);
    }


    @FXML
    void clickExit(ActionEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    //if any report is clicked method createReport will call out
    @FXML
    void changePeriod(ActionEvent event) {
        if (!lastRaport.equals("noReportClicked"))
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickLastChanges(ActionEvent event) {
        String[] sqlColumnNames = {"Customer", "Product", "Period", "Time", "Old Value", "New Value"};
        this.sqlColumnNames = sqlColumnNames;
        lastRaport = "clickLastChanges";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickProductDetails(ActionEvent event) {
        String[] sqlColumnNames = {"Number", "Name", "Weight in kg", "Units in carton", "Price", "Category"};
        this.sqlColumnNames = sqlColumnNames;;
        lastRaport = "clickProductDetails";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickCustomerDetails(ActionEvent event) {
        String[] sqlColumnNames = {"Name", "Type", "User"};
        this.sqlColumnNames = sqlColumnNames;;
        lastRaport = "clickCustomerDetails";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickWeightByCustomer(ActionEvent event) {
        String[] sqlColumnNames = {"Customer", "Cartons", "Weight in kg"};
        this.sqlColumnNames = sqlColumnNames;
        lastRaport = "clickWeightByCustomer";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickWeightByProduct(ActionEvent event) {
        String[] sqlColumnNames = {"Product", "Cartons", "Units in carton", "Unit weight", "Weight in kg"};
        this.sqlColumnNames = sqlColumnNames;
        lastRaport = "clickWeightByProduct";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickValueByCustomer(ActionEvent event) {
        String[] sqlColumnNames = {"Customer", "Cartons", "Value"};
        this.sqlColumnNames = sqlColumnNames;
        lastRaport = "clickValueByCustomer";
        createReport(createStatement(), sqlColumnNames);
    }

    @FXML
    void clickValueByProduct(ActionEvent event) {
        String[] sqlColumnNames = {"Product", "Cartons", "Units in carton", "Price", "Value"};
        this.sqlColumnNames = sqlColumnNames;
        lastRaport = "clickValueByProduct";
        createReport(createStatement(), sqlColumnNames);
    }

    //Statement based on report previously clicked
    private String createStatement() {
        String sqlStatement = null;
        switch (lastRaport) {
            case "clickLastChanges":
                sqlStatement = "SELECT Customers.Name as Customer, Products.Name as Product, Periods.Name as Period," +
                        " Forecast.Time, Forecast.oldValue as 'Old Value', Forecast.newValue as 'New Value' " +
                        "FROM (Forecast, Customers, Products, Periods) WHERE Forecast.CustomerId = Customers.Id " +
                        "AND Forecast.ProductId = Products.Id AND Forecast.PeriodId = Periods.Id " +
                        "AND Periods.Name Like '" + cbPeriods.getSelectionModel().getSelectedItem() +
                        "' AND Customers.Name Like '" + cbCustomers.getSelectionModel().getSelectedItem() +
                        "' AND Products.Name Like '" + cbProducts.getSelectionModel().getSelectedItem() +
                        "' ORDER BY Forecast.Id DESC LIMIT 10;";
                break;
            case "clickProductDetails":
                sqlStatement = "SELECT Products.Number, Products.Name, Products.`Weight in kg`, Products.`Units in carton`, Products.Price," +
                        " Categories.Name as 'Category' FROM (Products, Categories)" +
                        " WHERE Products.CategoryId = Categories.Id AND Products.Status = 'Active' ORDER BY Products.Number ASC;";
                break;
            case "clickCustomerDetails":
                sqlStatement = "SELECT Customers.Name, CustomerType.Name as 'Type', Users.Login as 'User' FROM " +
                        "(Customers, CustomerType, Users) WHERE Customers.CustomerTypeId = CustomerType.Id AND " +
                        "Customers.UserId = Users.Id;";
                break;
            case "clickWeightByCustomer":
                sqlStatement = sqlStatement = "SELECT Customer, SUM(Cartons) as 'Cartons', SUM(Weight) as 'Weight in kg' FROM " +
                        "(SELECT Customers.Name as Customer, Products.Name as Product, SUM(Forecast.NewValue) as 'Cartons'," +
                        " SUM(Forecast.NewValue*Products.`Units in carton`*Products.`Weight in kg`) as 'Weight' FROM " +
                        "(Forecast, Customers, Products, Periods) WHERE Forecast.Id IN(SELECT MAX(Forecast.Id) " +
                        "FROM Forecast GROUP BY Forecast.CustomerId, Forecast.ProductId, Forecast.PeriodId) AND " +
                        "Forecast.PeriodId = Periods.Id AND Customers.Id = Forecast.CustomerId AND Forecast.ProductId = Products.Id " +
                        "AND Periods.Name Like '" + cbPeriods.getSelectionModel().getSelectedItem() +
                        "' AND Customers.Name Like '" + cbCustomers.getSelectionModel().getSelectedItem() +
                        "' AND Products.Name Like '" + cbProducts.getSelectionModel().getSelectedItem() +
                        "' GROUP BY Customers.Name, Products.Name) as TempTable GROUP BY Customer ORDER BY SUM(Weight) DESC;";
                break;
            case "clickWeightByProduct":
                sqlStatement = "SELECT Products.Name as Product, SUM(Forecast.NewValue) as 'Cartons', Products.`Units in carton`," +
                        " Products.`Weight in kg` as 'Unit weight', SUM(Forecast.NewValue*Products.`Units in carton`*Products.`Weight in kg`) as 'Weight in kg' FROM " +
                        "(Forecast, Products, Periods, Customers) WHERE Forecast.ProductId = Products.Id AND Forecast.PeriodId = Periods.Id AND Forecast.CustomerId = Customers.Id AND " +
                        "Periods.Name Like '" + cbPeriods.getSelectionModel().getSelectedItem() +
                        "' AND Customers.Name Like '" + cbCustomers.getSelectionModel().getSelectedItem() +
                        "' AND Products.Name Like '" + cbProducts.getSelectionModel().getSelectedItem() +
                        "' AND Forecast.Id IN(SELECT MAX(Forecast.Id) FROM Forecast GROUP BY Forecast.CustomerId, Forecast.ProductId, Forecast.PeriodId) " +
                        "GROUP BY Products.Name ORDER BY SUM(Forecast.NewValue*Products.`Units in carton`*Products.`Weight in kg`) DESC;";
                break;
            case "clickValueByCustomer":
                sqlStatement = "SELECT Customer, SUM(Cartons) as 'Cartons', SUM(Value) as 'Value' FROM " +
                        "(SELECT Customers.Name as Customer, Products.Name as Product, SUM(Forecast.NewValue) as 'Cartons'," +
                        " SUM(Forecast.NewValue*Products.`Units in carton`*Products.Price) as 'Value' FROM " +
                        "(Forecast, Customers, Products, Periods) WHERE Forecast.Id IN(SELECT MAX(Forecast.Id) " +
                        "FROM Forecast GROUP BY Forecast.CustomerId, Forecast.ProductId, Forecast.PeriodId) AND " +
                        "Forecast.PeriodId = Periods.Id AND Customers.Id = Forecast.CustomerId AND Forecast.ProductId = Products.Id " +
                        "AND Periods.Name Like '" + cbPeriods.getSelectionModel().getSelectedItem() +
                        "' AND Customers.Name Like '" + cbCustomers.getSelectionModel().getSelectedItem() +
                        "' AND Products.Name Like '" + cbProducts.getSelectionModel().getSelectedItem() +
                        "' GROUP BY Customers.Name, Products.Name) as TempTable GROUP BY Customer ORDER BY Value DESC;";
                break;
            case "clickValueByProduct":
                sqlStatement = "SELECT Products.Name as Product, SUM(Forecast.NewValue) as 'Cartons', Products.`Units in carton`," +
                        " Products.Price, SUM(Forecast.NewValue*Products.`Units in carton`*Products.Price) as 'Value' FROM " +
                        "(Forecast, Products, Periods, Customers) WHERE Forecast.ProductId = Products.Id AND Forecast.PeriodId = Periods.Id AND Forecast.CustomerId = Customers.Id AND " +
                        "Periods.Name Like '" + cbPeriods.getSelectionModel().getSelectedItem() +
                        "' AND Customers.Name Like '" + cbCustomers.getSelectionModel().getSelectedItem() +
                        "' AND Products.Name Like '" + cbProducts.getSelectionModel().getSelectedItem() +
                        "' AND Forecast.Id IN(SELECT MAX(Forecast.Id) FROM Forecast GROUP BY Forecast.CustomerId, Forecast.ProductId, Forecast.PeriodId) " +
                        "GROUP BY Products.Name ORDER BY Value DESC;";
                break;
        }
        return sqlStatement;
    }
    //one method for all reports
    public void createReport(String sqlStatement, String[] sqlColumnNames) {
        //clear list
        oblist.clear();
        //delete column headings
        for(int i = 0; i<6;i++) {
            columnList.get(i).setText("");
        }
        //if less than 6 columns are required the rest should be empty
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sqlStatement)) {
            while (rs.next()) {
                String[] tab = new String[8];
                for (int i = 0; i<sqlColumnNames.length;i++) {
                    tab[i] = rs.getString(sqlColumnNames[i]);
                }
                oblist.add(new ReportTableRecord(tab[0], tab[1], tab[2], tab[3], tab[4], tab[5]));
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
    //'%' is universal symbol, which stands for all periods
    //the rest of periods is added by sql statement
    private ObservableList<String> getPeriodsName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("%");
        String sql = "SELECT Name FROM `Periods`;";
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

    private ObservableList<String> getCustomersName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("%");
        String sql = "SELECT Name FROM `Customers`;";
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

    private ObservableList<String> getProductsName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("%");
        String sql = "SELECT Name FROM `Products`;";
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

    public String getLogin() {
        return Login;
    }
}
