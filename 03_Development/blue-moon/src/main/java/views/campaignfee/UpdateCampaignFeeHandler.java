package views.campaignfee;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
import views.messages.ErrorDialog;
import views.messages.InformationDialog;

public class UpdateCampaignFeeHandler extends CampaignFeeFormHandler {
    @FXML private ComboBox<String> cbStatus;
    private CampaignFee campaignFee;
    private final Stage ownerStage;
    
	public UpdateCampaignFeeHandler(Stage ownerStage, CampaignFee campaignFee) throws Exception {
        super(ownerStage, utils.Configs.UPDATE_CAMPAIGN_FORM, "Chỉnh sửa thông tin đợt thu");
        loader.setController(this);
        
        this.campaignFee = campaignFee;
        this.ownerStage = ownerStage;
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
			FeeCell feeCell = new FeeCell(List.of(fee), false, this::handleAddNewFee, super::handleDeleteFee);
			feeCell.getComboBox().setValue(fee);
			vbFeesList.getChildren().add(feeCell.getContainer());
			feeCells.add(feeCell);
		}
	}
	
	@Override
	protected void handleAddNewFee() {
		FeeCell lastCell = feeCells.get(feeCells.size() - 1);
		Fee selected = lastCell.getComboBox().getValue();

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

		FeeCell newRow = new FeeCell(remaining, true, this::handleAddNewFee, super::handleDeleteFee);
		vbFeesList.getChildren().add(newRow.getContainer());
		feeCells.add(newRow);
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
