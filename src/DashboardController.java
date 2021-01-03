import com.mysql.cj.protocol.Resultset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private TableView<Forecast> mainTable;

    @FXML
    private TableColumn<Forecast, String> col_prod, col_p1, col_p2, col_p3, col_p4, col_p5, col_p6;

    @FXML
    private ComboBox<String> customerList;

    ObservableList<Forecast> oblist = FXCollections.observableArrayList();

    private String login;

    @FXML
    private Button btnExit;

    public DashboardController(String login)
    {
        this.login = login;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerList.setItems(getCustomersName());
        customerList.setValue(getCustomersName().get(0));

        getPeriodsName();
        getValuesInPivotView(customerList.getSelectionModel().getSelectedItem());

        col_prod.setCellValueFactory(new PropertyValueFactory<>("product"));
        col_p1.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod1"));
        col_p2.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod2"));
        col_p3.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod3"));
        col_p4.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod4"));
        col_p5.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod5"));
        col_p6.setCellValueFactory(new PropertyValueFactory<>("valueInPeriod6"));
        mainTable.setItems(oblist);

        mainTable.setEditable(true);
        col_p1.setCellFactory(TextFieldTableCell.forTableColumn());
        col_p2.setCellFactory(TextFieldTableCell.forTableColumn());
        col_p3.setCellFactory(TextFieldTableCell.forTableColumn());
        col_p4.setCellFactory(TextFieldTableCell.forTableColumn());
        col_p5.setCellFactory(TextFieldTableCell.forTableColumn());
        col_p6.setCellFactory(TextFieldTableCell.forTableColumn());
    }


    @FXML
    void clickExit(ActionEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void keyPressed(KeyEvent event) {
        if (event.getCode()== KeyCode.DIGIT1) {
            Forecast frcst = mainTable.getSelectionModel().getSelectedItem();
            int customerId = frcst.getCustomerId();
            int productId = frcst.productId;

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String time = formatter.format(date);

            int[] oldValue = new int[6];
            if (frcst.getValueInPeriod1()!=null) oldValue[0] = Integer.parseInt(frcst.getValueInPeriod1()); else oldValue[0] =0;
            if (frcst.getValueInPeriod2()!=null) oldValue[1] = Integer.parseInt(frcst.getValueInPeriod2()); else oldValue[1] =0;
            if (frcst.getValueInPeriod3()!=null) oldValue[2] = Integer.parseInt(frcst.getValueInPeriod3()); else oldValue[2] =0;
            if (frcst.getValueInPeriod4()!=null) oldValue[3] = Integer.parseInt(frcst.getValueInPeriod4()); else oldValue[3] =0;
            if (frcst.getValueInPeriod5()!=null) oldValue[4] = Integer.parseInt(frcst.getValueInPeriod5()); else oldValue[4] =0;
            if (frcst.getValueInPeriod6()!=null) oldValue[5] = Integer.parseInt(frcst.getValueInPeriod6()); else oldValue[5] =0;
            for(int i = 0;i<6;i++) {
                updateDB(customerId, productId, 1+i, time, oldValue[i], oldValue[0]);
            }
            getValuesInPivotView(customerList.getSelectionModel().getSelectedItem());
        }
        if (event.getCode()== KeyCode.DIGIT2) {
            Forecast frcst = mainTable.getSelectionModel().getSelectedItem();
            int customerId = frcst.getCustomerId();
            int productId = frcst.productId;
            int newValue = frcst.getSum()/6;

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String time = formatter.format(date);

            int[] oldValue = new int[6];
            if (frcst.getValueInPeriod1()!=null) oldValue[0] = Integer.parseInt(frcst.getValueInPeriod1()); else oldValue[0] =0;
            if (frcst.getValueInPeriod2()!=null) oldValue[1] = Integer.parseInt(frcst.getValueInPeriod2()); else oldValue[1] =0;
            if (frcst.getValueInPeriod3()!=null) oldValue[2] = Integer.parseInt(frcst.getValueInPeriod3()); else oldValue[2] =0;
            if (frcst.getValueInPeriod4()!=null) oldValue[3] = Integer.parseInt(frcst.getValueInPeriod4()); else oldValue[3] =0;
            if (frcst.getValueInPeriod5()!=null) oldValue[4] = Integer.parseInt(frcst.getValueInPeriod5()); else oldValue[4] =0;
            if (frcst.getValueInPeriod6()!=null) oldValue[5] = Integer.parseInt(frcst.getValueInPeriod6()); else oldValue[5] =0;
            for(int i = 0;i<6;i++) {
                updateDB(customerId, productId, 1+i, time, oldValue[i], newValue);
            }
            getValuesInPivotView(customerList.getSelectionModel().getSelectedItem());
        }

    }


    @FXML
    void valueChange(TableColumn.CellEditEvent event) {
        Forecast frcst = mainTable.getSelectionModel().getSelectedItem();
        int customerId = frcst.getCustomerId();
        int productId = frcst.productId;
        int periodId = findPeriodId(event.getTableColumn().getText());
        boolean update = true;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = formatter.format(date);

        int oldValue;
        try {
            oldValue = Integer.parseInt(event.getOldValue().toString());
        }
        catch(NullPointerException ex) {
            oldValue = 0;
        }

        int newValue = 0;
        try {
            newValue = Integer.parseInt(event.getNewValue().toString());
        }
        catch (NumberFormatException ex) {
            update = false;
            JOptionPane.showMessageDialog(null, "Only numbers are allowed!",
                    "Warning!", JOptionPane.WARNING_MESSAGE);
        }
        if(update) updateDB(customerId, productId, periodId, time, oldValue, newValue);
        getValuesInPivotView(customerList.getSelectionModel().getSelectedItem());
    }

    private void updateDB(int customerId, int productId, int periodId, String time, int oldValue, int newValue) {
        String sql = "INSERT INTO `Forecast` (`Id`, `CustomerId`, `ProductId`, `PeriodId`, `Time`, `OldValue`, `NewValue`)" +
                " VALUES (NULL, '" + customerId + "', '" + productId + "', '" + periodId + "', '" + time + "', '" +
                + oldValue + "', '" + newValue +"');";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();){
            statement.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getPeriodsName() {
        String[] tab = new String[6];
        int i = 0;
        String sql = "SELECT Name FROM `Periods`;";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                tab[i] = rs.getString("Name");
                i++;
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        col_p1.setText(tab[0]);
        col_p2.setText(tab[1]);
        col_p3.setText(tab[2]);
        col_p4.setText(tab[3]);
        col_p5.setText(tab[4]);
        col_p6.setText(tab[5]);
    }

    private void getValuesInPivotView(String customer) {
        oblist.clear();
        String valueInPeriods[] = new String[6];
        int i = -1;
        String sql = "SELECT Products.Id, Products.Name, Customers.Id, Periods.Id, Forecast.NewValue" +
                " FROM (Customers, Products, Periods) LEFT JOIN (SELECT * FROM Forecast WHERE Forecast.Id IN(SELECT MAX(Forecast.Id)" +
                " FROM Forecast GROUP BY Forecast.CustomerId, Forecast.ProductId, Forecast.PeriodId)) as Forecast" +
                " ON Customers.Id = Forecast.CustomerId AND Products.Id = Forecast.ProductId" +
                " AND Periods.Id = Forecast.PeriodId WHERE Customers.Name = '" + customer + "'" +
                " ORDER BY Products.Id ASC, Customers.Id ASC, Periods.Id ASC;";
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                i++;
                valueInPeriods[i] = rs.getString("Forecast.newValue");
                if (i==5) {
                    oblist.add(new Forecast(rs.getInt("Products.Id"), rs.getInt("Customers.Id"),
                            rs.getString("Products.Name"), valueInPeriods[0], valueInPeriods[1],
                            valueInPeriods[2],  valueInPeriods[3], valueInPeriods[4], valueInPeriods[5]));
                    i = -1;
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private ObservableList<String> getCustomersName() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql;
        if (getLogin().equals("Admin")) {
            sql = "SELECT Customers.Name FROM (Customers, Users) WHERE Customers.UserId = Users.Id;";
        }
        else {
            sql = "SELECT Customers.Name FROM (Customers, Users) WHERE Customers.UserId = Users.Id AND" +
                    " Users.Login = '" + getLogin() + "';";
        }
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

    @FXML
    void customerChange(ActionEvent event) {
        getValuesInPivotView(customerList.getSelectionModel().getSelectedItem());
    }

    private int findPeriodId(String periodName) {
        String sql = "SELECT * FROM `Periods` WHERE Name = '" + periodName + "';";
        int period = 0;
        try (Connection conn = DbConnector.getConnection(); Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                period = rs.getInt("Id");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return period;
    }

    public String getLogin() {
        return login;
    }
}
