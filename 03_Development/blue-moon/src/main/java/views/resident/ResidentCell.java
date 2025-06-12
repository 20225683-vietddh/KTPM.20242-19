package views.resident;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import models.Resident;
import services.ResidentService;
import views.messages.ErrorDialog;
import views.messages.ConfirmationDialog;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;

public class ResidentCell extends HBox {
	private final Stage stage;
	private Resident resident;
	private ResidentHandler parentHandler;
	private ResidentService residentService;
	
	public ResidentCell(Stage stage, Resident resident, ResidentHandler parentHandler) {
		this.stage = stage;
		this.resident = resident;
		this.parentHandler = parentHandler;
		this.residentService = new ResidentService();
		setupCell();
	}
	
	private void setupCell() {
		this.setAlignment(Pos.CENTER_LEFT);
		this.setSpacing(20);
		this.setPadding(new Insets(15));
		this.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
		this.setPrefHeight(100);

		VBox infoSection = new VBox(5);
		infoSection.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(infoSection, Priority.ALWAYS);
		
		Label lblFullName = new Label(resident.getFullName());
		lblFullName.setFont(Font.font("System", FontWeight.BOLD, 16));
		lblFullName.setStyle("-fx-text-fill: #2c3e50;");

		String dateOfBirth = resident.getDateOfBirth() != null ? 
			resident.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
		Label lblInfo = new Label("Sinh: " + dateOfBirth + " | Giới tính: " + 
			(resident.getGender() != null ? resident.getGender() : "N/A"));
		lblInfo.setFont(Font.font("System", 14));
		lblInfo.setStyle("-fx-text-fill: #7f8c8d;");

		Label lblDetails = new Label("CCCD: " + (resident.getCitizenId() != null ? resident.getCitizenId() : "N/A") + 
			" | Nghề nghiệp: " + (resident.getOccupation() != null ? resident.getOccupation() : "N/A"));
		lblDetails.setFont(Font.font("System", 12));
		lblDetails.setStyle("-fx-text-fill: #95a5a6;");
		
		Label lblRelationship = new Label("Quan hệ: " + 
			(resident.getRelationshipWithHead() != null ? resident.getRelationshipWithHead() : "N/A") + 
			" | Hộ khẩu ID: " + resident.getHouseholdId());
		lblRelationship.setFont(Font.font("System", 12));
		lblRelationship.setStyle("-fx-text-fill: #95a5a6;");
		
		infoSection.getChildren().addAll(lblFullName, lblInfo, lblDetails, lblRelationship);
		
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
			ResidentDetailHandler detailHandler = new ResidentDetailHandler(stage, resident);
			System.out.println("Viewing details for resident ID: " + resident.getResidentId());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Không thể mở chi tiết nhân khẩu!");
		}
	}
	
	private void handleEdit() {
		try {
			UpdateResidentHandler updateHandler = new UpdateResidentHandler(stage, resident, parentHandler);
			System.out.println("Opening edit form for resident ID: " + resident.getResidentId());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Không thể mở form chỉnh sửa nhân khẩu!");
		}
	}
	
	private void handleDelete() {
		try {
			String warningMessage = "Bạn có chắc chắn muốn xóa nhân khẩu \"" + resident.getFullName() + "\" không?\n\n" +
									"⚠️ CẢNH BÁO: Hành động này không thể hoàn tác!\n\n";
			
			// Thêm cảnh báo đặc biệt nếu là chủ hộ
			if ("Chủ hộ".equals(resident.getRelationshipWithHead())) {
				warningMessage += "🚨 Đây là CHỦ HỘ! Nếu hộ khẩu không còn thành viên nào khác,\n" +
								 "toàn bộ hộ khẩu sẽ bị xóa và phòng sẽ trở về trạng thái trống!\n\n";
			}
			
			warningMessage += "Bạn có muốn tiếp tục?";
			
			String option = ConfirmationDialog.getOption(warningMessage);
			boolean confirmed = "YES".equals(option);
			
			if (confirmed) {
				boolean success = residentService.deleteResidentSafely(resident.getResidentId());
				
				if (success) {
					views.messages.InformationDialog.showNotification("Thành công", 
						"Xóa nhân khẩu thành công!\nHệ thống đã tự động cập nhật thông tin hộ khẩu và phòng.");
					
					// Refresh parent list
					if (parentHandler != null) {
						parentHandler.loadResidentList("", "Tất cả hộ khẩu");
					}
				} else {
					ErrorDialog.showError("Lỗi", "Không thể xóa nhân khẩu!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", e.getMessage());
		}
	}
} 