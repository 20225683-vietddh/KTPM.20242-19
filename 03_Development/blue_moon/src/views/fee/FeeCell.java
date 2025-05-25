package views.fee;

import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import models.Fee;
import views.messages.*;
import views.ScreenNavigator;
import services.FeeService;
import javafx.scene.control.Alert.AlertType;
import exception.InvalidInputException;
import javafx.scene.Parent;
import javafx.scene.Node;

public class FeeCell extends HBox {
    private final Stage stage;
    private final Fee fee;
    private final FeeService service;
    private final FeeListPageHandler feeListHandler;
    
    public FeeCell(Stage stage, Fee fee, FeeService service, FeeListPageHandler feeListHandler) {
        this.stage = stage;
        this.fee = fee;
        this.service = service;
        this.feeListHandler = feeListHandler;
        setupFeeCell();
    }
    
    private void setupFeeCell() {
        this.setPrefSize(1019, 50);
        this.setMaxSize(1019, 50);
        this.setMinSize(1019, 50);
        this.setPadding(new Insets(15, 20, 15, 20));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-border-color: black; -fx-border-radius: 8px; -fx-background-color: #F7F7F7; -fx-background-radius: 8px;");
    
        Label nameLabel = new Label("Tên: ");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        
        Label feeName = new Label(fee.getName());
        feeName.setPrefWidth(200);
        feeName.setMaxWidth(200);
        feeName.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        
        Label createdDateLabel = new Label("Ngày tạo: ");
        createdDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        
        Label feeCreatedDate = new Label(fee.getCreatedDate().toString());
        feeCreatedDate.setPrefWidth(120);
        feeCreatedDate.setMaxWidth(120);
        feeCreatedDate.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        
        Label mandatoryLabel = new Label("Bắt buộc: ");
        mandatoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        
        Label feeMandatory = new Label(fee.getIsMandatory() ? "Có" : "Không");
        feeMandatory.setPrefWidth(80);
        feeMandatory.setMaxWidth(80);
        feeMandatory.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        
        Label descriptionLabel = new Label("Mô tả: ");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        
        String descText = fee.getDescription();
        if (descText == null || descText.isEmpty()) {
            descText = "Không có mô tả";
        } else if (descText.length() > 30) {
            descText = descText.substring(0, 30) + "...";
        }
        
        Label feeDescription = new Label(descText);
        feeDescription.setPrefWidth(230);
        feeDescription.setMaxWidth(230);
        feeDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        
        HBox hbButton = new HBox();
        hbButton.setPrefWidth(USE_COMPUTED_SIZE);
        hbButton.setPrefHeight(USE_COMPUTED_SIZE);
        hbButton.setSpacing(10);
        
        Button btnUpdate = new Button("Sửa");
        btnUpdate.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btnUpdate.setPrefWidth(80);
        btnUpdate.setOnAction(e -> handleUpdate(fee));
        
        Button btnDelete = new Button("Xóa");
        btnDelete.setStyle("-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btnDelete.setPrefWidth(80);
        btnDelete.setOnAction(e -> handleDelete(fee));
        
        hbButton.getChildren().addAll(btnUpdate, btnDelete);
        
        // Tạo một Region để đẩy các nút sang bên phải
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        spacer.setPrefWidth(USE_COMPUTED_SIZE);
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        this.getChildren().addAll(nameLabel, feeName, createdDateLabel, feeCreatedDate, 
                                mandatoryLabel, feeMandatory, descriptionLabel, feeDescription, 
                                spacer, hbButton);
    }
    
    private void handleUpdate(Fee fee) {
        try {
            Stage popupStage = new Stage();
            UpdateFeeFormHandler updateFeeHandler = new UpdateFeeFormHandler(
                popupStage, 
                utils.Configs.UPDATE_FEE_FORM_PATH, 
                utils.Configs.LOGO_PATH, 
                "Cập nhật khoản thu", 
                fee.getId()
            );
            updateFeeHandler.show();
            
            popupStage.setOnHiding(e -> refreshFeeList());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi biểu mẫu", "Không thể mở biểu mẫu cập nhật khoản thu!");
            e.printStackTrace();
        }
    }
    
    private void handleDelete(Fee fee) {
        String option = ConfirmationDialog.getOption("Bạn có chắc chắn muốn xóa khoản thu " + fee.getName() + " không?");
        if (option.equals("YES")) {
            try {
                boolean result = service.deleteFee(fee.getId());
                if (result) {
                    InformationDialog.showNotification("Xóa thành công", "Khoản thu " + fee.getName() + " đã được xóa thành công!");
                    refreshFeeList();
                } else {
                    ErrorDialog.showError("Lỗi", "Không thể xóa khoản thu");
                }
            } catch (InvalidInputException e) {
                ErrorDialog.showError("Lỗi hệ thống", e.getMessage());
            }
        }
    }
    
    private void refreshFeeList() {
        try {
            if (feeListHandler != null) {
                feeListHandler.loadFeeList();
            } else {
                reloadFeeListPage();
            }
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể làm mới danh sách khoản thu: " + e.getMessage());
            e.printStackTrace();
            reloadFeeListPage();
        }
    }
    
    private void reloadFeeListPage() {
        try {
            String userName = "Người dùng";
            
            if (stage != null && stage.getScene() != null && stage.getScene().getRoot() instanceof Parent) {
                Parent root = (Parent) stage.getScene().getRoot();
                for (Node node : root.lookupAll("Label")) {
                    if (node.getId() != null && node.getId().equals("lblUserName")) {
                        userName = ((Label) node).getText();
                        break;
                    }
                }
            }
            
            FeeListPageHandler feeListHandler = new FeeListPageHandler(stage, userName);
            feeListHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải lại trang: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 