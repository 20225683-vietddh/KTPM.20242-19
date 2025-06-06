package views.chargefee;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Optional;
import dto.campaignfee.FeeAmountRecordDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import models.CampaignFee;
import models.Fee;
import models.Household;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InputDialog;
import services.ChargeFeeService;

public class OptionalChargeFeeHandler extends BaseScreenHandler {
	@FXML private Button btnClose;
	@FXML private Label lblHouseNumber;
	@FXML private Label lblTotalPaidAmount;
	@FXML private VBox vbFeeAmounts;
	private CampaignFee campaignFee;
	private Household household;
	private ChargeFeeService service;
    
	public OptionalChargeFeeHandler(Stage ownerStage, CampaignFee campaignFee, Household household, ChargeFeeService service) throws Exception {
        super(new Stage(), utils.Configs.OPTIONAL_CHARGE_FEE_FORM, utils.Configs.LOGO_PATH, "Các khoản thu tự nguyện");
        loader.setController(this);
        this.campaignFee = campaignFee;
        this.household = household;
        this.service = service;
        this.setContent();  
        this.setScene();    

        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur); 

        this.stage.setOnHidden(e -> parentRoot.setEffect(null));
        this.showPopup(ownerStage);
    }
	
	@FXML
	public void initialize() {
		loadForm();
		btnClose.setOnAction(e -> handleClose());
		int totalOptionalPaidAmount = service.countTotalOptionalPaidAmount(campaignFee.getId(), household.getHouseholdId());
		lblTotalPaidAmount.setText(utils.Utils.formatCurrency(totalOptionalPaidAmount));
	}
	
	private void loadForm() {
		vbFeeAmounts.getChildren().clear();
		lblHouseNumber.setText("Hộ " + household.getHouseNumber());
		try {
			List<Fee> optionalFees = getOptionalFees();
			if (optionalFees.isEmpty()) {
				Label lblEmpty = new Label("Đợt này không có khoản thu tự nguyện nào!");
				lblEmpty.setPrefWidth(760);
				lblEmpty.setStyle("-fx-font-size: 20px;");
				vbFeeAmounts.getChildren().add(lblEmpty);
			} else {
				for (Fee fee : optionalFees) {
					boolean isExisted = service.isRecordExisted(campaignFee.getId(), household.getHouseholdId(), fee.getId());
					if (isExisted) {
						FeeAmountRecordDTO dto = service.getPaymentRecord(campaignFee.getId(), household.getHouseholdId(), fee.getId());
						setupOptionalFeeRow(dto.getFeeName(), fee.getId(), dto.getPaidAmount());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Không thể tại lại danh sách các khoản thu tự nguyện");
		}
	}
	
	private void setupOptionalFeeRow(String feeName, int feeId, int paidAmount) {
		Label lblFeeName = new Label(feeName);
        lblFeeName.setPrefWidth(360);
        lblFeeName.setMaxWidth(360);
        lblFeeName.setStyle("-fx-font-size: 18px;");
        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
            
        TextField tfExpectedAmount = new TextField(utils.Utils.formatCurrency(paidAmount));
        tfExpectedAmount.setPrefWidth(120);
        tfExpectedAmount.setStyle("-fx-font-size: 18px;");
        tfExpectedAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        HBox.setMargin(tfExpectedAmount, new Insets(10, 40, 10, 40));
        tfExpectedAmount.setDisable(true);
        
        Button btnDetailedAction = new Button("Nhập số tiền");
        btnDetailedAction.setStyle("-fx-background-color:  linear-gradient(to right, #43A5DC, #FF7BAC); -fx-font-size: 18px; -fx-text-fill: white; -fx-cursor: hand;");
        btnDetailedAction.setPrefWidth(145);
        HBox.setMargin(btnDetailedAction, new Insets(10, 25, 10, 25));
        btnDetailedAction.setOnAction(e -> handleEnterPaidAmount(feeName, feeId));
        
        HBox row = new HBox(lblFeeName, tfExpectedAmount, btnDetailedAction);
        row.setStyle("-fx-background-color: #F8F8F8;");
        row.setPrefWidth(760);
        row.setAlignment(Pos.CENTER_LEFT);
        vbFeeAmounts.getChildren().add(row);
	}
	
	private void handleEnterPaidAmount(String feeName, int feeId) {
		while (true) {
            Optional<String> result = InputDialog.getInput("10000", "Nhập số tiền mà hộ dân đã nộp", "Hãy nhập số tiền mà hộ dân đã nộp cho khoản " + feeName + ":", "Số tiền: ");
            if (!result.isPresent()) {
                return;
            }
            String input = result.get().trim();
            if (!input.matches("\\d+")) {
                ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị số.");
                continue;
            }
            try {
				int paidAmount = Integer.parseInt(input);
				if (paidAmount < 0) {
					ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị nguyên dương.");
					continue;
				}
				service.updateRecord(campaignFee.getId(), household.getHouseholdId(), feeId, paidAmount);
				loadForm();
				break;
            } catch (NumberFormatException e) {
            	ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị nguyên dương.");
            } catch (SQLException e) {
            	e.printStackTrace();
            	ErrorDialog.showError("Lỗi hệ thống", "Không thể lưu số tiền đã nộp cho khoản " + feeName + " vào CSDL.");
            } catch (Exception e) {
            	ErrorDialog.showError("Lỗi hệ thống", "Không thể tải lại form các khoản thu bắt buộc.");
            	e.printStackTrace();
            }
		}
	}
	
	private List<Fee> getOptionalFees() {
		List<Fee> fees = campaignFee.getFees();
		List<Fee> optionalFees = new ArrayList<>();
		for (Fee fee : fees) {
			if (!fee.getIsMandatory()) {
				optionalFees.add(fee);
			}
		}
		return optionalFees;
	}
	
	private void handleClose() {
		Stage stage = (Stage) btnClose.getScene().getWindow();
		stage.close();
	}
}
