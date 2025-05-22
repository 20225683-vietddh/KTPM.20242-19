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
        
        HBox hbCreatedDate = new HBox(10);
        Label createdDate = new Label("Ngày tạo (yyyy/mm/dd):");
        createdDate.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cfCreatedDate = new Label(campaignFee.getCreatedDate().toString());
        cfCreatedDate.setStyle("-fx-font-size: 18px;");
        hbCreatedDate.getChildren().addAll(createdDate, cfCreatedDate);

        HBox hbStartDate = new HBox(10);
        Label startDate = new Label("Ngày bắt đầu (yyyy/mm/dd):");
        startDate.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cfStartDate = new Label(campaignFee.getStartDate().toString());
        cfStartDate.setStyle("-fx-font-size: 18px;");
        hbStartDate.getChildren().addAll(startDate, cfStartDate);
        
        HBox hbDueDate = new HBox(10);
        Label dueDate = new Label("Ngày kết thúc (yyyy/mm/dd):");
        dueDate.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cfDueDate = new Label(campaignFee.getDueDate().toString());
        cfDueDate.setStyle("-fx-font-size: 18px;");
        hbDueDate.getChildren().addAll(dueDate, cfDueDate);
        
        Button closeBtn = new Button("Đóng");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setPrefWidth(150);
        closeBtn.setPrefHeight(40);
        VBox.setMargin(closeBtn, new Insets(0, 225, 0, 225));
        closeBtn.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(campaignFeeName, hbCreatedDate, hbStartDate, hbDueDate, closeBtn);

        Scene scene = new Scene(layout, 600, 500);

        popupStage.setTitle("Thông tin chi tiết đợt thu");
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); 
        popupStage.setResizable(false);
	}
	
	private HBox contentBox(String title, String content) {
		HBox hBox = new HBox(10);
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
