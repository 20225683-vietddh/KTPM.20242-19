package views.campaignfee;

import javafx.stage.Stage; 
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import models.CampaignFee;
import models.Fee;

public class CampaignFeeDetailedBox {
	private final CampaignFee campaignFee;
	private final Stage popupStage;
	
	public CampaignFeeDetailedBox(CampaignFee cf) {
		campaignFee = cf;
		popupStage = new Stage();
		setupUI();
	}
	
	private void setupUI() {
		VBox layout = new VBox(10);
        layout.setPadding(new Insets(0, 20, 20, 20));
        layout.setStyle("-fx-background-color: white;");
        
        Label campaignFeeName = new Label(campaignFee.getName());
        campaignFeeName.setPrefWidth(600);
        campaignFeeName.setPadding(new Insets(20, 0, 20, 0));
        campaignFeeName.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-alignment: CENTER;");
        VBox.setMargin(campaignFeeName, new Insets(0, 0, 10, 0));
        
        HBox hbCreatedDate = contentBox("Ngày tạo (yyyy/mm/dd):", campaignFee.getCreatedDate().toString());
        HBox hbStartDate = contentBox("Ngày bắt đầu (yyyy/mm/dd):", campaignFee.getStartDate().toString());
        HBox hbDueDate = contentBox("Ngày kết thúc (yyyy/mm/dd):", campaignFee.getDueDate().toString());
        HBox hbStatus = contentBox("Trạng thái:", campaignFee.getStatus());
        HBox hbDescription = contentBox("Mô tả:", campaignFee.getDescription());
        
        Label lblFees = new Label("Danh sách các khoản thu:");
        lblFees.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox vbFees = new VBox(0);
        for (Fee fee : campaignFee.getFees()) {
        	Label lblFee = new Label("• " + fee.getName());
        	lblFee.setStyle("-fx-font-size: 18px; -fx-padding: 0 0 0 25;");
            vbFees.getChildren().add(lblFee);
        }
        
        Button closeBtn = new Button("Đóng");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setPrefWidth(150);
        closeBtn.setPrefHeight(40);
        VBox.setMargin(closeBtn, new Insets(0, 225, 0, 225));
        closeBtn.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(campaignFeeName, hbCreatedDate, hbStartDate, hbDueDate, hbStatus, hbDescription, lblFees, vbFees, closeBtn);

        Scene scene = new Scene(layout, 600, 600);

        popupStage.setTitle("Thông tin chi tiết đợt thu");
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); 
        popupStage.setResizable(false);
	}
	
	private HBox contentBox(String title, String content) {
		HBox hBox = new HBox(5);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblContent = new Label(content);
        lblContent.setStyle("-fx-font-size: 18px;");
        hBox.getChildren().addAll(lblTitle, lblContent);
        
        return hBox;
	}
	
	public void show() {
        popupStage.showAndWait(); 
    }
}
