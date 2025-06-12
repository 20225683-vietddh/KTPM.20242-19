package views.household;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import models.Household;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import services.HouseholdService;
import views.messages.ConfirmationDialog;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;

public class HouseholdCell extends HBox {
	private final Stage stage;
	private Household household;
	private HouseholdHandler parentHandler;
	private HouseholdService householdService;
	
	public HouseholdCell(Stage stage, Household household, HouseholdHandler parentHandler) {
		this.stage = stage;
		this.household = household;
		this.parentHandler = parentHandler;
		try {
			this.householdService = new HouseholdService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setupCell();
	}
	
	private void setupCell() {
		this.setAlignment(Pos.CENTER_LEFT);
		this.setSpacing(20);
		this.setPadding(new Insets(15));
		this.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
		this.setPrefHeight(80);

		VBox infoSection = new VBox(5);
		infoSection.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(infoSection, Priority.ALWAYS);
		
		Label lblHouseNumber = new Label("Số nhà: " + household.getHouseNumber());
		lblHouseNumber.setFont(Font.font("System", FontWeight.BOLD, 16));
		lblHouseNumber.setStyle("-fx-text-fill: #2c3e50;");

		String address = household.getStreet() + ", " + household.getWard() + ", " + household.getDistrict();
		Label lblAddress = new Label("Địa chỉ: " + address);
		lblAddress.setFont(Font.font("System", 14));
		lblAddress.setStyle("-fx-text-fill: #7f8c8d;");

		Label lblId = new Label("ID: " + household.getHouseholdId());
		lblId.setFont(Font.font("System", 12));
		lblId.setStyle("-fx-text-fill: #95a5a6;");
		
		infoSection.getChildren().addAll(lblHouseNumber, lblAddress, lblId);
		
		HBox actionSection = new HBox(10);
		actionSection.setAlignment(Pos.CENTER_RIGHT);
		
		Button btnView = new Button("Xem chi tiết");
		btnView.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
		btnView.setPrefWidth(100);
		btnView.setOnAction(e -> handleViewDetails());
		
		Button btnEdit = new Button("Sửa");
		btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
		btnEdit.setPrefWidth(80);
		btnEdit.setOnAction(e -> handleEdit());
		
		Button btnDelete = new Button("Xóa");
		btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
		btnDelete.setPrefWidth(80);
		btnDelete.setOnAction(e -> handleDelete());
		
		actionSection.getChildren().addAll(btnView, btnEdit, btnDelete);
		
		this.getChildren().addAll(infoSection, actionSection);
	}
	
	private void handleViewDetails() {
		try {
			HouseholdDetailHandler detailHandler = new HouseholdDetailHandler(stage, household);
			System.out.println("Viewing details for household ID: " + household.getHouseholdId());
		} catch (Exception e) {
			e.printStackTrace();
			views.messages.ErrorDialog.showError("Lỗi", "Không thể mở chi tiết hộ khẩu!");
		}
	}
	
	private void handleEdit() {
		try {
			UpdateHouseholdHandler updateHandler = new UpdateHouseholdHandler(stage, household, parentHandler);
			System.out.println("Opening edit form for household ID: " + household.getHouseholdId());
		} catch (Exception e) {
			e.printStackTrace();
			views.messages.ErrorDialog.showError("Lỗi", "Không thể mở form chỉnh sửa hộ khẩu!");
		}
	}
	
	private void handleDelete() {
		try {
			String warningMessage = "🚨 CẢNH BÁO NGHIÊM TRỌNG! 🚨\n\n" +
									"Bạn có chắc chắn muốn xóa hộ khẩu \"" + household.getHouseNumber() + "\" không?\n\n" +
									"⚠️ HÀNH ĐỘNG NÀY SẼ:\n" +
									"• Xóa VĨNH VIỄN tất cả " + household.getNumberOfResidents() + " nhân khẩu trong hộ\n" +
									"• Xóa toàn bộ thông tin hộ khẩu\n" +
									"• Reset phòng \"" + household.getHouseNumber() + "\" về trạng thái trống\n" +
									"• KHÔNG THỂ HOÀN TÁC!\n\n" +
									"Đây là hành động cực kỳ nguy hiểm. Bạn có chắc chắn muốn tiếp tục?";
			
			String option = ConfirmationDialog.getOption(warningMessage);
			boolean confirmed = "YES".equals(option);
			
			if (confirmed) {
				// Xác nhận lần 2 để đảm bảo
				String finalConfirm = "LẦN XÁC NHẬN CUỐI CÙNG!\n\n" +
									 "Bạn thực sự muốn xóa vĩnh viễn hộ khẩu \"" + household.getHouseNumber() + "\" " +
									 "và tất cả " + household.getNumberOfResidents() + " nhân khẩu bên trong?\n\n" +
									 "Nhập \"XÁC NHẬN\" để tiếp tục:";
				
				String finalOption = ConfirmationDialog.getOption(finalConfirm);
				if ("YES".equals(finalOption)) {
					boolean success = householdService.deleteHouseholdSafely(household.getHouseholdId());
					
					if (success) {
						InformationDialog.showNotification("Xóa thành công", 
							"Đã xóa hộ khẩu \"" + household.getHouseNumber() + "\" và tất cả nhân khẩu!\n" +
							"Phòng đã được reset về trạng thái trống.");
						
						// Refresh parent list
						if (parentHandler != null) {
							parentHandler.loadHouseholdList("");
						}
					} else {
						ErrorDialog.showError("Lỗi", "Không thể xóa hộ khẩu!");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", e.getMessage());
		}
	}
} 