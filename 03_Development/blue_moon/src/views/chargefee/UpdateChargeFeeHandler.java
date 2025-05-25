package views.chargefee;

import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import models.Fee;
import models.CampaignFee;
import models.Household;
import dto.campaignfee.FeeAmountRecordDTO;
import services.ChargeFeeService;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateChargeFeeHandler extends BaseScreenHandler {
	@FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblHouseNumber;

    @FXML
    private VBox vbFeeAmounts;
    
    @FXML
    private Label lblTotalExpectedAmount;
    
    @FXML
    private Label lblTotalPaidAmount;
    
    private CampaignFee campaignFee;
    private Household household;
    private ChargeFeeService service;
    
	public UpdateChargeFeeHandler(Stage ownerStage, CampaignFee campaignFee, Household household, ChargeFeeService service) throws Exception {
        super(new Stage(), utils.Configs.UPDATE_AMOUNT_BOX, utils.Configs.LOGO_PATH, "Cập nhật tình hình thu phí");
        loader.setController(this);
        this.campaignFee = campaignFee;
        this.household = household;
        this.service = service;
        
        this.setContent();  
        this.setScene();    

        // Apply the blur effect to the parent stage
        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur); 

        // Delete the blur effect after closing the pop up stage
        this.stage.setOnHidden(e -> parentRoot.setEffect(null));

        this.showPopup(ownerStage);
    }
	
	@FXML
	public void initialize() {
		this.lblHouseNumber.setText("Hộ " + household.getHouseNumber());
		try {
			List<Fee> fees = campaignFee.getFees();

		    for (Fee fee : fees) {
		        Label lblFeeName = new Label(fee.getName());
		        lblFeeName.setPrefWidth(200);
		        lblFeeName.setMaxWidth(200);
		        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
		        if (!fee.getIsMandatory()) {
		        	lblFeeName.setStyle("-fx-font-size: 18px; -fx-font-style: italic;");
		        	System.out.println(fee.getIsMandatory());
		        } else {
		        	lblFeeName.setStyle("-fx-font-size: 18px; -fx-font-style: regular;");
		        }
		        
		        TextField tfExpectedAmount = new TextField();
		        tfExpectedAmount.setPrefWidth(140);
		        tfExpectedAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
		        
		        Label lblPaidAmount = new Label();
		        lblPaidAmount.setPrefWidth(140);
		        lblPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
		        
		        Label lblPaidDate = new Label();
		        lblPaidDate.setPrefWidth(140);
		        lblPaidDate.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
		        
		        TextField tfNewPaidAmount = new TextField();
		        tfNewPaidAmount.setPrefWidth(135);
		        tfNewPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
		        
		        boolean existed = service.isRecordExisted(campaignFee.getId(), household.getHouseholdId(), fee.getId());

		        if (!existed) {
		            tfExpectedAmount.setText("0");
		            lblPaidAmount.setText("0");
		            lblPaidDate.setText("Chưa có");
		            tfNewPaidAmount.setDisable(true);
		        } else {
		        	FeeAmountRecordDTO dto = service.getPaymentRecord(campaignFee.getId(), household.getHouseholdId(), fee.getId());
		        	tfExpectedAmount.setText(utils.Utils.formatCurrency(dto.getExpectedAmount()));
		        	lblPaidAmount.setText(utils.Utils.formatCurrency(dto.getPaidAmount()));
		        	lblPaidDate.setText(dto.getPaidDate() == null ? "Chưa có" : dto.getPaidDate().toString());
		            if (dto.isFullyPaid()) {
		            	tfNewPaidAmount.setDisable(true);
		            	tfNewPaidAmount.setPromptText("Đã nộp đủ");
		            } else {
		            	tfNewPaidAmount.setDisable(false);
		            }
		        }

		        HBox row = new HBox(lblFeeName, tfExpectedAmount, lblPaidAmount, lblPaidDate, tfNewPaidAmount);
		        row.setAlignment(Pos.CENTER_LEFT);
		        vbFeeAmounts.getChildren().add(row);
		    }
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			e.printStackTrace();
		}
		
		lblTotalExpectedAmount.setText(utils.Utils.formatCurrency(service.countTotalExpectedAmount(campaignFee.getId(), household.getHouseholdId())) + " đồng.");
		lblTotalPaidAmount.setText(utils.Utils.formatCurrency(service.countTotalPaidAmount(campaignFee.getId(), household.getHouseholdId())) + " đồng.");
		btnSave.setOnAction(e -> handleSave());
		btnDelete.setOnAction(e -> handleDelete());
	}
	
	private void handleSave() {
		try { 
			for (Node node : vbFeeAmounts.getChildren()) {
		        if (node instanceof HBox hbox) {
		            Label feeNameLabel = (Label) hbox.getChildren().get(0);
		            TextField expectedAmountField = (TextField) hbox.getChildren().get(1);
		            TextField additionalAmountField = (TextField) hbox.getChildren().get(4);

		            String feeName = feeNameLabel.getText();
		            Fee fee = campaignFee.getFees().stream()
		                    .filter(f -> f.getName().equals(feeName))
		                    .findFirst()
		                    .orElse(null);

		            if (fee == null) continue; 

		            int feeId = fee.getId();
		            int campaignFeeId = campaignFee.getId();
		            int householdId = household.getHouseholdId();

		            Integer expectedAmount = null;
		            try {
		                expectedAmount = utils.Utils.parseCurrency(expectedAmountField.getText().trim());
		                if (expectedAmount <= 0) {
		                    ErrorDialog.showError("Lỗi", "Vui lòng nhập số tiền cần thu là một số nguyên dương cho khoản thu " + fee.getName());
		                    return;
		                }
		            } catch (NumberFormatException e) {
		                ErrorDialog.showError("Lỗi", "Vui lòng nhập số tiền cần thu hợp lệ cho khoản thu " + fee.getName());
		                return;
		            }

		            Integer additionalAmount = 0;
		            try {
		                String input = additionalAmountField.getText().trim();
		                if (!input.isEmpty()) {
		                    additionalAmount = Integer.parseInt(input);
		                    if (additionalAmount <= 0) {
		                        ErrorDialog.showError("Lỗi", "Vui lòng nhập số tiền đã nộp là một số nguyên dương cho khoản thu " + fee.getName());
		                        return;
		                    }
		                }
		            } catch (NumberFormatException e) {
		                ErrorDialog.showError("Lỗi", "Vui lòng nhập số tiền đã nộp hợp lệ cho khoản thu " + fee.getName());
		                return;
		            }

		            boolean existed = service.isRecordExisted(campaignFeeId, householdId, feeId);

		            if (!existed) {
		                service.insertRecord(campaignFeeId, householdId, feeId, expectedAmount, additionalAmount, null);
		            } else {
		                FeeAmountRecordDTO existingRecord = service.getPaymentRecord(campaignFeeId, householdId, feeId);

		                Integer updatedPaidAmount = (existingRecord.getPaidAmount() != 0 ? existingRecord.getPaidAmount() : 0) + additionalAmount;
		                LocalDate paidDate = additionalAmount > 0 ? LocalDate.now() : existingRecord.getPaidDate();

		                service.updateRecord(campaignFeeId, householdId, feeId, expectedAmount, updatedPaidAmount, paidDate);
		            }
		        }
		    }
			InformationDialog.showNotification("Thành công", "Cập nhật tình hình thu phí thành công");
		    this.stage.close();
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi", "Không truy cập được vào CSDL");
			e.printStackTrace();
		}
	}
	
	private void handleDelete() {
		Stage stage = (Stage) btnDelete.getScene().getWindow();
		stage.close();
	}
}
