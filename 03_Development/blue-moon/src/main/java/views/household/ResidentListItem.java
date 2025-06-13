package views.household;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import models.Resident;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ResidentListItem extends HBox {
    private Resident resident;
    private int index;
    private AddHouseholdHandler parentHandler;
    
    public ResidentListItem(Resident resident, int index, AddHouseholdHandler parentHandler) {
        this.resident = resident;
        this.index = index;
        this.parentHandler = parentHandler;
        setupItem();
    }
    
    private void setupItem() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.setPadding(new Insets(10));
        this.setPrefHeight(80);
        
        // Màu nền khác nhau cho chủ hộ
        if ("Chủ hộ".equals(resident.getRelationshipWithHead())) {
            this.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #27ae60; -fx-border-radius: 5; -fx-background-radius: 5;");
        } else {
            this.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5; -fx-background-radius: 5;");
        }

        VBox infoSection = new VBox(3);
        infoSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoSection, Priority.ALWAYS);
        
        // Thông tin cơ bản
        Label lblName = new Label(resident.getFullName());
        lblName.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblName.setStyle("-fx-text-fill: #2c3e50;");

        Label lblRelationship = new Label("Quan hệ: " + resident.getRelationshipWithHead());
        lblRelationship.setFont(Font.font("System", 12));
        if ("Chủ hộ".equals(resident.getRelationshipWithHead())) {
            lblRelationship.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            lblRelationship.setStyle("-fx-text-fill: #7f8c8d;");
        }
        
        Label lblInfo = new Label("CCCD: " + resident.getCitizenId() + " | " + 
                                 resident.getGender() + " | " + 
                                 (resident.getDateOfBirth() != null ? resident.getDateOfBirth().toString() : "N/A"));
        lblInfo.setFont(Font.font("System", 11));
        lblInfo.setStyle("-fx-text-fill: #95a5a6;");
        
        infoSection.getChildren().addAll(lblName, lblRelationship, lblInfo);
        
        // Nút xóa
        Button btnRemove = new Button("Xóa");
        btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3; -fx-cursor: hand;");
        btnRemove.setPrefWidth(60);
        btnRemove.setOnAction(e -> handleRemove());
        
        this.getChildren().addAll(infoSection, btnRemove);
    }
    
    private void handleRemove() {
        if (parentHandler != null) {
            parentHandler.removeResident(index);
        }
    }
} 