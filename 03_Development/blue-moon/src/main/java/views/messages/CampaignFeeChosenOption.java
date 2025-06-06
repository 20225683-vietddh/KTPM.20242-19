package views.messages;

import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.util.List;
import services.CampaignFeeService;
import models.CampaignFee;

public class CampaignFeeChosenOption {
	private final Stage popupStage;
	private ComboBox<CampaignFee> cbCampaignFee;
	private CampaignFee selected;
	private final CampaignFeeService service = new CampaignFeeService();
	
	public CampaignFeeChosenOption() throws Exception {
		popupStage = new Stage();
		setupUI();
	}
	
	private void setupUI() {
		VBox layout = new VBox(10);
        layout.setPadding(new Insets(0, 20, 20, 20));
        layout.setStyle("-fx-background-color: white;");
        
        Label lblTitle = new Label("Thu phí");
        lblTitle.setPrefWidth(360);
        lblTitle.setPadding(new Insets(20, 0, 20, 0));
        lblTitle.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-alignment: CENTER;");
        VBox.setMargin(lblTitle, new Insets(0, 0, 10, 0));
        
        cbCampaignFee = new ComboBox<>();
        cbCampaignFee.setPromptText("Chọn đợt thu phí");
        cbCampaignFee.setPrefWidth(360);
        cbCampaignFee.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-radius: 5px; -fx-font-size: 18px;");

        List<CampaignFee> campaignFees = service.getAllCampaignFees();
        cbCampaignFee.getItems().addAll(campaignFees);
        
        Button btnOK = new Button("Xác nhận");
        btnOK.setOnAction(e -> {
            selected = cbCampaignFee.getValue(); 
            popupStage.close();
        });
        
        btnOK.setPrefWidth(120);
        btnOK.setMaxWidth(120);
        btnOK.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-size: 18px; -fx-text-fill: white;");
        VBox.setMargin(btnOK, new Insets(300, 0, 10, 120));
        
        layout.getChildren().addAll(lblTitle, cbCampaignFee, btnOK);
        
		Scene scene = new Scene(layout, 400, 510);
		popupStage.setTitle("Chọn đợt thu phí");
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); 
        popupStage.setResizable(false);
	}
	
	public void show() {
		popupStage.showAndWait();
	}
	
	public CampaignFee getSelectedOption() {
		return this.selected;
	}
}
