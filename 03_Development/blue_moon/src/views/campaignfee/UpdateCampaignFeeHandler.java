package views.campaignfee;

import java.sql.SQLException;
import java.time.DateTimeException;
import controllers.ManageCampaignFeeController;
import dto.campaignfee.CampaignFeeDTO;
import exception.InvalidDateRangeException;
import exception.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import models.CampaignFee;
import models.Fee;
import views.ScreenNavigator;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;

public class UpdateCampaignFeeHandler extends CampaignFeeFormHandler {
    @FXML
    private ComboBox<String> cbStatus;
    
    private CampaignFee campaignFee;
    
	public UpdateCampaignFeeHandler(Stage ownerStage, CampaignFee campaignFee) throws Exception {
        super(ownerStage, utils.Configs.UPDATE_CAMPAIGN_FORM, "Chỉnh sửa thông tin đợt thu");
        loader.setController(this);
        
        this.campaignFee = campaignFee;
        this.setContent();  
        this.setupUI();
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
		super.initialize();
		cbStatus.getItems().addAll(utils.Configs.STATUS);
		
		for (Fee fee : campaignFee.getFees()) {
			FeeCell feeCell = new FeeCell(allFees, super::handleAddNewFee);
			feeCell.getComboBox().setValue(fee);
			vbFeesList.getChildren().add(feeCell.getContainer());
			feeCells.add(feeCell);
		}
	}
	
	@Override 
	protected void handleSave() {
		CampaignFeeDTO requestDTO = this.getUserInputs();

		try {
			this.controller = new ManageCampaignFeeController();
			((ManageCampaignFeeController) controller).handleUpdateCampaignFee(requestDTO);
			Stage stage = (Stage) btnSave.getScene().getWindow();
			stage.close();
			InformationDialog.showNotification("Cập nhật thành công",
					"Chúc mừng bạn! Đợt thu phí " + requestDTO.getName() + " đã được cập nhật thành công!");
			ScreenNavigator.goBack();
		} catch (InvalidInputException e) {
			ErrorDialog.showError("Lỗi nhập liệu", e.getMessage());
		} catch (DateTimeException e) {
			ErrorDialog.showError("Lỗi ngày tháng", e.getMessage());
		} catch (InvalidDateRangeException e) {
			ErrorDialog.showError("Lỗi ngày tháng", e.getMessage());
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", e.getMessage());
		}
	}
	
	@Override 
	protected CampaignFeeDTO getUserInputs() {
		CampaignFeeDTO dto = super.getUserInputs();
		String selectedStatus = cbStatus.getValue();
		dto.setStatus(selectedStatus);
		dto.setId(campaignFee.getId());
		return dto;
	}
	
	private void setupUI() {
		tfName.setText(campaignFee.getName());
		cbStartDay.setValue(String.valueOf(campaignFee.getStartDate().getDayOfMonth()));
		cbStartMonth.setValue(String.valueOf(campaignFee.getStartDate().getMonthValue()));
		cbStartYear.setValue(String.valueOf(campaignFee.getStartDate().getYear()));
		cbDueDay.setValue(String.valueOf(campaignFee.getDueDate().getDayOfMonth()));
		cbDueMonth.setValue(String.valueOf(campaignFee.getDueDate().getMonthValue()));
		cbDueYear.setValue(String.valueOf(campaignFee.getDueDate().getYear()));
		cbStatus.setValue(campaignFee.getStatus());
		taDescription.setText(campaignFee.getDescription());
	}
}
