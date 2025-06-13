package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import models.Fee;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import views.messages.*;
import dto.campaignfee.CampaignFeeDTO;
import controllers.ManageCampaignFeeController;
import exception.*;
import java.time.DateTimeException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.SQLException;

public class NewCampaignFeeHandler extends CampaignFeeFormHandler {
	private final Stage ownerStage;
	
	public NewCampaignFeeHandler(Stage ownerStage) throws Exception {
        super(ownerStage, utils.Configs.NEW_CAMPAIGN_FORM, "Thêm đợt thu mới");
        loader.setController(this);
        this.ownerStage = ownerStage;
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
		FeeCell firstCell = new FeeCell(allFees, true, this::handleAddNewFee, super::handleDeleteFee);
		vbFeesList.getChildren().add(firstCell.getContainer());
		feeCells.add(firstCell);
	}
	
	@Override
	protected void handleAddNewFee() {
	    FeeCell lastCell = feeCells.get(feeCells.size() - 1);
	    Fee selected = lastCell.getSelectedFee();

	    if (selected == null) {
	        ErrorDialog.showError("Lỗi", "Vui lòng chọn khoản thu trước!");
	        return;
	    }

	    Set<Fee> selectedFees = feeCells.stream()
	        .map(FeeCell::getSelectedFee)
	        .filter(Objects::nonNull)
	        .collect(Collectors.toSet());

	    List<Fee> remaining = allFees.stream()
	        .filter(f -> !selectedFees.contains(f))
	        .collect(Collectors.toList());

	    if (remaining.isEmpty()) {
	        ErrorDialog.showError("Lỗi", "Không còn khoản thu nào!");
	        return;
	    }

	    FeeCell newRow = new FeeCell(remaining, true, this::handleAddNewFee, this::handleDeleteFee);
	    vbFeesList.getChildren().add(newRow.getContainer());
	    feeCells.add(newRow);
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
			refreshCampaignFeeList();
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
	
	private void refreshCampaignFeeList() {
		try {
			CampaignFeeListHandler listHandler = CampaignFeeListHandler.getHandlerFromStage(ownerStage);
			if (listHandler != null) {
				listHandler.loadCampaignFeeList("");
			}
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi", "Không thể làm mới danh sách đợt thu: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
