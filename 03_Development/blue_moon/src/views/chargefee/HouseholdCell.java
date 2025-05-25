package views.chargefee;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Household;
import models.CampaignFee;
import services.ChargeFeeService;
import views.messages.ErrorDialog;

public class HouseholdCell extends HBox {
	private final Stage stage;
	private CampaignFee campaignFee;
	private Household household;
	private ChargeFeeService service;
	private Runnable onReload;
	
	public HouseholdCell(Stage stage, CampaignFee cf, Household household, ChargeFeeService service, Runnable onReload) {
		this.stage = stage;
		this.campaignFee = cf;
		this.household = household;
		this.service = service;
		this.onReload = onReload;
		setupCell();
	}
	
	private void setupCell() {
		this.setPrefSize(1019, 60);
		this.setMaxSize(1019, 70);
		this.setMinSize(1019, 60);
		this.setPadding(new Insets(15, 20, 15, 20));
		this.setAlignment(Pos.CENTER_LEFT);
		this.setStyle("-fx-border-color: black; -fx-border-radius: 8px; -fx-background-color: #F7F7F7; -fx-background-radius: 8px;");
		
		Label name = new Label("Hộ: ");
	    name.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
	    
	    Label householdName = new Label(household.getHouseNumber());
	    householdName.setPrefWidth(620);
	    householdName.setMaxWidth(620);
	    householdName.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
	    
	    
	    Button btnView = new Button("Cập nhật tình hình thu phí");
	    btnView.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
	    btnView.setPrefWidth(300);
	    btnView.setPrefHeight(60);
	    btnView.setOnAction(e -> handleUpdateChargeFee());
	    
	    this.getChildren().addAll(name, householdName, btnView);
	}
	
	private void handleUpdateChargeFee() {
		try {
			new UpdateChargeFeeHandler(this.stage, campaignFee, household, service);
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở form!");
			e.printStackTrace();
			if (onReload != null) {
			    onReload.run();
			}
		}
	}
}
