package views.room;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import models.Room;
import models.Household;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RoomCell extends HBox {
    private final Stage stage;
    private Room room;
    private RoomHandler parentHandler;
    
    public RoomCell(Stage stage, Room room, RoomHandler parentHandler) {
        this.stage = stage;
        this.room = room;
        this.parentHandler = parentHandler;
        setupCell();
    }
    
    private void setupCell() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);
        this.setPadding(new Insets(15));
        this.setPrefHeight(100);
        
        // Màu nền khác nhau cho phòng trống và có người ở
        if (room.isEmpty()) {
            this.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #27ae60; -fx-border-radius: 8; -fx-background-radius: 8;");
        } else {
            this.setStyle("-fx-background-color: #fdf2e9; -fx-border-color: #e67e22; -fx-border-radius: 8; -fx-background-radius: 8;");
        }

        VBox infoSection = new VBox(5);
        infoSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoSection, Priority.ALWAYS);
        
        // Thông tin phòng
        Label lblRoomNumber = new Label("Phòng: " + room.getRoomNumber());
        lblRoomNumber.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblRoomNumber.setStyle("-fx-text-fill: #2c3e50;");

        Label lblStatus = new Label("Trạng thái: " + (room.isEmpty() ? "Trống" : "Có người ở"));
        lblStatus.setFont(Font.font("System", 14));
        if (room.isEmpty()) {
            lblStatus.setStyle("-fx-text-fill: #27ae60;");
        } else {
            lblStatus.setStyle("-fx-text-fill: #e67e22;");
        }
        
        infoSection.getChildren().addAll(lblRoomNumber, lblStatus);
        
        // Thông tin hộ khẩu (nếu có)
        if (!room.isEmpty() && room.getHousehold() != null) {
            Household household = room.getHousehold();
            
            Label lblHouseholdInfo = new Label("Hộ khẩu: " + household.getHouseholdId());
            lblHouseholdInfo.setFont(Font.font("System", 12));
            lblHouseholdInfo.setStyle("-fx-text-fill: #7f8c8d;");
            
            String address = household.getStreet() + ", " + household.getWard() + ", " + household.getDistrict();
            Label lblAddress = new Label("Địa chỉ: " + address);
            lblAddress.setFont(Font.font("System", 12));
            lblAddress.setStyle("-fx-text-fill: #7f8c8d;");
            
            Label lblResidents = new Label("Số nhân khẩu: " + household.getNumberOfResidents());
            lblResidents.setFont(Font.font("System", 12));
            lblResidents.setStyle("-fx-text-fill: #7f8c8d;");
            
            infoSection.getChildren().addAll(lblHouseholdInfo, lblAddress, lblResidents);
        }
        
        HBox actionSection = new HBox(10);
        actionSection.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnViewDetails = new Button("Xem chi tiết");
        btnViewDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        btnViewDetails.setPrefWidth(100);
        btnViewDetails.setOnAction(e -> handleViewDetails());
        
        actionSection.getChildren().add(btnViewDetails);
        
        this.getChildren().addAll(infoSection, actionSection);
    }
    
    private void handleViewDetails() {
        try {
            RoomDetailHandler detailHandler = new RoomDetailHandler(stage, room);
            System.out.println("Viewing details for room: " + room.getRoomNumber());
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể mở chi tiết phòng!");
        }
    }
} 