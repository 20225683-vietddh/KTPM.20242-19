package views.fee;

import java.util.List;

import controllers.FeeListController;
import exception.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import views.homepage.AccountantHomePageHandler;
import models.Fee;
import services.FeeService;

public class FeeListPageHandler extends BaseScreenWithLogoutAndGoBackHandler {
    @FXML private Button btnHome;
    @FXML private Button btnCampaignFees;
    @FXML private Button btnFees;
    @FXML private Button btnHouseholds;
    @FXML private Button btnStatistics;

    @FXML private Button btnAddFee;
    @FXML private TextField tfSearch;
    @FXML private Label lblUserName;
    @FXML private VBox vbFeeList;

    private final FeeService service = new FeeService();

    public FeeListPageHandler(Stage stage, String userName) throws Exception {
        super(stage, utils.Configs.FEE_LIST_PATH, utils.Configs.LOGO_PATH, "Danh sách khoản thu");
        loader.setController(this);
        this.setContent();
        this.setScene();
        if (lblUserName != null) {
            this.lblUserName.setText(userName);
        }
        
        // Lưu trữ tham chiếu đến FeeListPageHandler trong userData của Scene
        if (this.scene != null) {
            this.scene.setUserData(this);
        }
    }

    @FXML
    public void initialize() {
        super.initialize();
        loadFeeList();
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
        }
        
        if (btnAddFee != null) {
            btnAddFee.setOnAction(e -> handleAddFee());
        }
        
        setupNavbarEvents();
    }

    private void setupNavbarEvents() {
        if (btnHome != null) btnHome.setOnAction(e -> handleHome());
        if (btnCampaignFees != null) btnCampaignFees.setOnAction(e -> handleCampaignFees());
        if (btnFees != null) btnFees.setOnAction(e -> {}); // Đang ở trang này
        if (btnHouseholds != null) btnHouseholds.setOnAction(e -> handleHouseholds());
        if (btnStatistics != null) btnStatistics.setOnAction(e -> handleStatistics());
    }

    public void loadFeeList() {
        try {
            List<Fee> fees = service.getAllFeesAsModel();
            
            if (vbFeeList != null) {
                vbFeeList.getChildren().clear();
                for (Fee fee : fees) {
                    FeeCell cell = new FeeCell(this.stage, fee, service, this);
                    vbFeeList.getChildren().add(cell);
                    vbFeeList.setSpacing(20);
                }
            }
        } catch (InvalidInputException e) {
            ErrorDialog.showError("Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private void applyFilter(String keyword) {
        try {
            List<Fee> allFees = service.getAllFeesAsModel();
            
            if (vbFeeList != null) {
                vbFeeList.getChildren().clear();
                
                if (keyword == null || keyword.trim().isEmpty()) {
                    for (Fee fee : allFees) {
                        FeeCell cell = new FeeCell(this.stage, fee, service, this);
                        vbFeeList.getChildren().add(cell);
                    }
                } else {
                    String searchTerm = keyword.toLowerCase().trim();
                    for (Fee fee : allFees) {
                        if (fee.getName().toLowerCase().contains(searchTerm) || 
                            String.valueOf(fee.getId()).contains(searchTerm) ||
                            (fee.getDescription() != null && fee.getDescription().toLowerCase().contains(searchTerm))) {
                            FeeCell cell = new FeeCell(this.stage, fee, service, this);
                            vbFeeList.getChildren().add(cell);
                        }
                    }
                }
                
                vbFeeList.setSpacing(20);
            }
        } catch (InvalidInputException e) {
            ErrorDialog.showError("Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private void handleHome() {
        try {
            AccountantHomePageHandler homePage = new AccountantHomePageHandler(this.stage);
            homePage.show();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể quay về trang chủ!");
        }
    }

    private void handleCampaignFees() {
        try {
            // BaseScreenHandler handler = new CampaignFeeListHandler(this.stage, lblUserName.getText());
            // handler.show();
            ErrorDialog.showError("Thông báo", "Chức năng đang được phát triển!");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách đợt thu!");
        }
    }

    private void handleHouseholds() {
        try {
            // BaseScreenHandler handler = new HouseholdsPageHandler(this.stage, lblUserName.getText());
            // handler.show();
            ErrorDialog.showError("Thông báo", "Chức năng đang được phát triển!");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách hộ dân!");
        }
    }

    private void handleStatistics() {
        try {
            // BaseScreenHandler handler = new StatisticsPageHandler(this.stage, utils.Configs.STATISTICS_PAGE_PATH);
            // handler.show();
            ErrorDialog.showError("Thông báo", "Chức năng đang được phát triển!");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang thống kê!");
        }
    }

    private void handleAddFee() {
        try {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(this.stage);
            
            AddFeeFormHandler addFeeHandler = new AddFeeFormHandler(
                popupStage, 
                utils.Configs.ADD_FEE_FORM_PATH, 
                utils.Configs.LOGO_PATH, 
                "Thêm khoản thu mới"
            );
            
            addFeeHandler.show();
            
            popupStage.setOnHiding(e -> loadFeeList());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi biểu mẫu", "Không thể mở biểu mẫu thêm khoản thu!");
        }
    }
} 