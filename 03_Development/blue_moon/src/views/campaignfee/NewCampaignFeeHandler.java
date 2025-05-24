package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import views.messages.*;
import views.ScreenNavigator;
import dto.campaignfee.CampaignFeeDTO;
import controllers.ManageCampaignFeeController;
import exception.*;
import java.time.DateTimeException;
import java.sql.SQLException;

public class NewCampaignFeeHandler extends CampaignFeeFormHandler {
	public NewCampaignFeeHandler(Stage ownerStage) throws Exception {
        super(ownerStage, utils.Configs.NEW_CAMPAIGN_FORM, "Thêm đợt thu mới");
        loader.setController(this);
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
		super.initialize();
		FeeCell firstCell = new FeeCell(allFees, super::handleAddNewFee);
		vbFeesList.getChildren().add(firstCell.getContainer());
		feeCells.add(firstCell);
	}
	
	@Override
	protected void handleSave() {
		CampaignFeeDTO requestDTO = super.getUserInputs();
		
		try {
			this.controller = new ManageCampaignFeeController();
			((ManageCampaignFeeController) controller).handleAddCampaignFee(requestDTO);
			Stage stage = (Stage) btnSave.getScene().getWindow(); 
			stage.close();
			InformationDialog.showNotification("Thêm vào CSDL thành công",  "Chúc mừng bạn! Đợt thu phí " + requestDTO.getName() + " đã được thêm thành công!");
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
}
