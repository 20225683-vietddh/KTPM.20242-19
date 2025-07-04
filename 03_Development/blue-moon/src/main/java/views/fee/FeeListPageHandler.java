package views.fee;

import java.util.List;
import exception.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.Fee;
import services.FeeService;

public class FeeListPageHandler extends BaseScreenWithLogoutAndGoBackHandler {
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