package views.statistics;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.PostgreSQLConnection;
import dao.statistic.ChangeHistoryDAO;
import dao.statistic.ResidentDAO;
import dao.statistic.StayAbsenceDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.statistic.AgeGroupStats;
import models.statistic.ChangeHistoryInfo;
import models.statistic.StayAbsenceInfo;
import views.BaseScreenWithLogoutAndGoBackHandler;

public class StatisticScreenHandler extends BaseScreenWithLogoutAndGoBackHandler{

    @FXML
    private TabPane statisticTabPane;

    @FXML
    private Tab DoTuoiMenuTab, GioiTinhMenuTab, KhoangThoiGianMenuTab, TamTruTamVangMenuTab, ThongKeMainMenuTab;

    @FXML
    private Button btnKhoangThoiGian, btnTamTruTamVang, btnTheoDoTuoi, btnTheoGioiTinh, btnThongKe;

    @FXML private ComboBox<Integer> cbMonthSA;
    @FXML private ComboBox<String> cbStayAbsence;
    @FXML private ComboBox<Integer> cbYearSA;

    
    @FXML private ComboBox<Integer> cbBeginMonthPeriod;
    @FXML private ComboBox<Integer> cbBeginYearPeriod;
    @FXML private ComboBox<Integer> cbEndMonthPeriod;
    @FXML private ComboBox<Integer> cbEndYearPeriod;

    @FXML
    private Label lblTotalResidents, lblPercentHousehold;

    @FXML
    private BarChart<String, Number> barChartPopulation, barChartAgeGroup;

    @FXML
    private PieChart pieChartGender;

    @FXML
    private Label lblTotalResidentsAge;

    @FXML
    private Label lblMale, lblFemale;

    @FXML 
    private TableColumn<AgeGroupStats, String> colAgeRange, colMale, colFemale, colTotal;

    @FXML 
    private TableView<AgeGroupStats> tableViewAgeGender;

    @FXML
    private Label labelAverageAge, labelMostCommonAgeGroup;

    @FXML private TableView<StayAbsenceInfo> tableViewStayAbsence;

    @FXML private TableColumn<StayAbsenceInfo, Integer> colResidentId;
    @FXML private TableColumn<StayAbsenceInfo, String> colFullName;
    @FXML private TableColumn<StayAbsenceInfo, String> colGender;
    @FXML private TableColumn<StayAbsenceInfo, LocalDate> colDateOfBirth;
    @FXML private TableColumn<StayAbsenceInfo, Integer> colHouseholdId;
    @FXML private TableColumn<StayAbsenceInfo, LocalDate> colCreatedDate;
    @FXML private TableColumn<StayAbsenceInfo, String> colPeriod;
    @FXML private TableColumn<StayAbsenceInfo, String> colRequestDesc;

    @FXML private TableView<ChangeHistoryInfo> tableViewChangeHistory;

    @FXML private TableColumn<ChangeHistoryInfo, Integer> colResidentIdGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, String> colFullNameGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, LocalDate> colDateOfBirthGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, String> colGenderGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, Integer> colHouseholdIdGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, String> colChangeTypeGapTime;
    @FXML private TableColumn<ChangeHistoryInfo, LocalDate> colChangeDateGapTime;


    public StatisticScreenHandler(Stage stage, String screenPath) throws Exception{
        super(stage, screenPath,  utils.Configs.LOGO_PATH, "Thống kê");
        loader.setController(this);
        this.setContent();  // nếu có nội dung cần set
        this.setScene();    // cần thiết để gán scene cho stage
    }


    @FXML
    @Override
    public void initialize(){
        super.initialize();

        cbStayAbsence.getItems().addAll("Tạm trú", "Tạm vắng");
        cbMonthSA.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        cbBeginMonthPeriod.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        cbEndMonthPeriod.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);

        int startYear = 2000;
        int currentYear = java.time.Year.now().getValue();
        List<Integer> years = new ArrayList<>();
        for (int year = currentYear; year >= startYear; year--) {
            years.add(year);
        }

        cbYearSA.getItems().setAll(years);
        cbBeginYearPeriod.getItems().setAll(years);
        cbEndYearPeriod.getItems().setAll(years);


        statisticTabPane.getTabs().clear(); 
        statisticTabPane.getTabs().add(ThongKeMainMenuTab);

        btnTheoDoTuoi.setOnAction(e -> showTab(DoTuoiMenuTab));
        btnTheoGioiTinh.setOnAction(e -> showTab(GioiTinhMenuTab));
        btnKhoangThoiGian.setOnAction(e -> showTab(KhoangThoiGianMenuTab));
        btnTamTruTamVang.setOnAction(e -> showTab(TamTruTamVangMenuTab));
        btnThongKe.setOnAction(e -> showTab(ThongKeMainMenuTab));

        

        try {
            Connection conn = PostgreSQLConnection.getInstance().getConnection();
            ResidentDAO residentDAO = new ResidentDAO();

            int totalResidents = residentDAO.getTotalResidents(conn);
            double percentHousehold = residentDAO.getPercentHousehole(conn);

            lblTotalResidents.setText(String.valueOf(totalResidents));
            lblTotalResidentsAge.setText(String.valueOf(totalResidents));
            lblPercentHousehold.setText(String.format(" %.1f%%", percentHousehold));
            showPopulationBarChart(conn);
            showGenderPieChart(conn);
            showAgeGroupTable(conn);
            showAgeOverview(conn);
            showAgeGroupBarChart(conn);
            showStayAbsenceInfoTableView(conn);

            cbStayAbsence.setOnAction(e -> showStayAbsenceInfoTableView(conn));
            cbMonthSA.setOnAction(e -> showStayAbsenceInfoTableView(conn));
            cbYearSA.setOnAction(e -> showStayAbsenceInfoTableView(conn));

            handleSearchChangeHistory(conn);
            cbBeginMonthPeriod.setOnAction(e -> handleSearchChangeHistory(conn));
            cbBeginYearPeriod.setOnAction(e -> handleSearchChangeHistory(conn));
            cbEndMonthPeriod.setOnAction(e -> handleSearchChangeHistory(conn));
            cbEndYearPeriod.setOnAction(e -> handleSearchChangeHistory(conn));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void handleSearchChangeHistory(Connection conn) {
        Integer beginMonth = cbBeginMonthPeriod.getValue();
        Integer beginYear = cbBeginYearPeriod.getValue();
        Integer endMonth = cbEndMonthPeriod.getValue();
        Integer endYear = cbEndYearPeriod.getValue();

        if (beginMonth == null || beginYear == null || endMonth == null || endYear == null) {
            System.out.println("ComboBox chưa được chọn đủ!");
            return; 
        }


        LocalDate startDate = LocalDate.of(beginYear, beginMonth, 1);
        LocalDate endDate = LocalDate.of(endYear, endMonth, YearMonth.of(endYear, endMonth).lengthOfMonth());

        showChangeHistoryTable(conn, startDate, endDate);
    }

    private void showTab(Tab selectedTab) {
        statisticTabPane.getTabs().clear();           
        statisticTabPane.getTabs().add(selectedTab);  
    }


    public void showPopulationBarChart(Connection conn) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        ResidentDAO dao = new ResidentDAO();
        Map<String, Integer> data = dao.getPopulationPerYear(conn);

        int cumulativeTotal = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            cumulativeTotal += entry.getValue();
            series.getData().add(new XYChart.Data<>(entry.getKey(), cumulativeTotal));
        }

        barChartPopulation.getData().clear(); 
        barChartPopulation.getData().add(series);
    }


    public void showGenderPieChart(Connection conn) {
        ResidentDAO dao = new ResidentDAO();
        Map<String, Integer> genderStats = dao.getGenderStatistics(conn);

        int total = genderStats.values().stream().mapToInt(Integer::intValue).sum();
        int maleCount = genderStats.getOrDefault("Nam", 0);
        int femaleCount = genderStats.getOrDefault("Nữ", 0);

    
        lblMale.setText(String.valueOf(maleCount));
        lblFemale.setText(String.valueOf(femaleCount));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : genderStats.entrySet()) {
            String gender = entry.getKey();
            int count = entry.getValue();
            double percent = (count * 100.0) / total;
            pieChartData.add(new PieChart.Data(gender + String.format(" (%.1f%%)", percent), count));
        }

        pieChartGender.setData(pieChartData);
    }


    public void showAgeGroupTable(Connection conn) {
        List<AgeGroupStats> stats = new ResidentDAO().getAgeGroupStatistics(conn);
        ObservableList<AgeGroupStats> data = FXCollections.observableArrayList(stats);

        colAgeRange.setCellValueFactory(new PropertyValueFactory<>("ageRange"));
        colMale.setCellValueFactory(new PropertyValueFactory<>("maleCount"));
        colFemale.setCellValueFactory(new PropertyValueFactory<>("femaleCount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCount"));

        tableViewAgeGender.setItems(data);
    }


    public void showAgeOverview(Connection conn) {
        ResidentDAO dao = new ResidentDAO();

        double avgAge = dao.getAverageAge(conn);
        labelAverageAge.setText(String.format("%.1f tuổi", avgAge));

        String commonGroup = dao.getMostCommonAgeGroup(conn);
        labelMostCommonAgeGroup.setText(commonGroup);
    }


    public void showAgeGroupBarChart(Connection conn) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        

        ResidentDAO dao = new ResidentDAO();
        Map<String, Integer> ageGroups = dao.getAgeGroupDistribution(conn);

        for (Map.Entry<String, Integer> entry : ageGroups.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartAgeGroup.getData().clear();
        barChartAgeGroup.getData().add(series);
    }


    public void showStayAbsenceInfoTableView(Connection conn) {
        String type = cbStayAbsence.getValue();
        Integer month = cbMonthSA.getValue();
        Integer year = cbYearSA.getValue();

        if (type == null || month == null || year == null) {
            tableViewStayAbsence.setItems(FXCollections.observableArrayList());
            return;
        }

        List<StayAbsenceInfo> records = new StayAbsenceDAO().getStayAbsenceRecords(conn, type, month, year);
        ObservableList<StayAbsenceInfo> data = FXCollections.observableArrayList(records);

        colResidentId.setCellValueFactory(new PropertyValueFactory<>("residentId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colHouseholdId.setCellValueFactory(new PropertyValueFactory<>("householdId"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        colRequestDesc.setCellValueFactory(new PropertyValueFactory<>("requestDesc"));

        tableViewStayAbsence.setItems(data);
    }


    public void showChangeHistoryTable(Connection conn, LocalDate startDate, LocalDate endDate) {
        List<ChangeHistoryInfo> list = new ChangeHistoryDAO().getChangeHistoryByPeriod(conn, startDate, endDate);
        ObservableList<ChangeHistoryInfo> data = FXCollections.observableArrayList(list);

        colResidentIdGapTime.setCellValueFactory(new PropertyValueFactory<>("residentId"));
        colFullNameGapTime.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDateOfBirthGapTime.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colGenderGapTime.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colHouseholdIdGapTime.setCellValueFactory(new PropertyValueFactory<>("householdId"));
        colChangeTypeGapTime.setCellValueFactory(new PropertyValueFactory<>("changeType"));
        colChangeDateGapTime.setCellValueFactory(new PropertyValueFactory<>("changeDate"));

        tableViewChangeHistory.setItems(data);
    }

   
}
