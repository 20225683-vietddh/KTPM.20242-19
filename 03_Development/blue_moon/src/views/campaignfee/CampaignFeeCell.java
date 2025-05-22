package views.campaignfee;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import models.CampaignFee;
import views.messages.ErrorDialog;

public class CampaignFeeCell extends HBox {
	private final CampaignFee campaignFee;
	
	public CampaignFeeCell(CampaignFee cf) {
		campaignFee = cf;
		setupCampaignFeeCell();
	}
	
	private void setupCampaignFeeCell() {
		this.setPrefSize(1019, 50);
		this.setMaxSize(1019, 50);
		this.setMinSize(1019, 50);
		this.setPadding(new Insets(15, 20, 15, 20));
		this.setAlignment(Pos.CENTER_LEFT);
		this.setStyle("-fx-border-color: black; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px;");
	
	    Label name = new Label("Tên: ");
	    name.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
	    
	    Label campaignFeeName = new Label(campaignFee.getName());
	    campaignFeeName.setPrefWidth(270);
	    campaignFeeName.setMaxWidth(270);
	    campaignFeeName.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
	    
	    Label startDate = new Label("Ngày bắt đầu: ");
	    startDate.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
	    
	    Label campaignFeeStartDate = new Label(campaignFee.getStartDate().toString());
	    campaignFeeStartDate.setPrefWidth(105);
	    campaignFeeStartDate.setMaxWidth(105);
	    campaignFeeStartDate.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
	    
	    Label status = new Label("Trạng thái: ");
	    status.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
	    
	    Label campaignFeeStatus = new Label(campaignFee.getStatus());
	    campaignFeeStatus.setPrefWidth(110);
	    campaignFeeStatus.setMaxWidth(110);
	    campaignFeeStatus.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
	    
	    HBox hbButton = new HBox();
	    hbButton.setPrefWidth(USE_COMPUTED_SIZE);
	    hbButton.setPrefHeight(USE_COMPUTED_SIZE);
	    hbButton.setSpacing(10);
	    
	    Button btnView = new Button("Xem");
	    btnView.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
	    btnView.setPrefWidth(80);
	    btnView.setOnAction(e -> handleViewCampaignFee(campaignFee));
	    
	    Button btnUpdate = new Button("Sửa");
	    btnUpdate.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
	    btnUpdate.setPrefWidth(80);
	    
	    Button btnDelete = new Button("Xóa");
	    btnDelete.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
	    btnDelete.setPrefWidth(80);
	    
	    hbButton.getChildren().addAll(btnView, btnUpdate, btnDelete);
	    
	    this.getChildren().addAll(name, campaignFeeName, startDate, campaignFeeStartDate, status, campaignFeeStatus, hbButton);
	}
	
	private void handleViewCampaignFee(CampaignFee campaignFee) {
		try {
			(new CampaignFeeDetailedBox(campaignFee)).show();
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị thông tin chi tiết của đợt thu " + campaignFee.getName());
			e.printStackTrace();
		}
	}
}
