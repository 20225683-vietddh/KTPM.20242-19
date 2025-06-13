package views.household;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.ChangeHistoryRecord;
import services.ChangeHistoryService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChangeHistoryHandler {
    private Stage stage;
    private int householdId;
    private ChangeHistoryService changeHistoryService;
    
    public ChangeHistoryHandler(Stage parentStage, int householdId) {
        this.householdId = householdId;
        try {
            this.changeHistoryService = new ChangeHistoryService();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo ChangeHistoryService: " + e.getMessage());
            e.printStackTrace();
        }
        createAndShowChangeHistoryWindow(parentStage);
    }
    
    private void createAndShowChangeHistoryWindow(Stage parentStage) {
        stage = new Stage();
        stage.setTitle("Lịch sử thay đổi nhân khẩu - Hộ khẩu ID: " + householdId);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(true);
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Table
        TableView<ChangeHistoryRecord> table = createTable();
        root.setCenter(table);
        
        // Buttons
        HBox buttonBox = createButtonBox();
        root.setBottom(buttonBox);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        
        Label titleLabel = new Label("📋 LỊCH SỬ THAY ĐỔI NHÂN KHẨU");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label descLabel = new Label("Hiển thị tất cả các thay đổi (thêm, sửa, xóa) của nhân khẩu trong hộ khẩu này");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(titleLabel, descLabel);
        return header;
    }
    
    private TableView<ChangeHistoryRecord> createTable() {
        TableView<ChangeHistoryRecord> table = new TableView<>();
        
        // Columns
        TableColumn<ChangeHistoryRecord, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        idCol.setPrefWidth(60);
        
        TableColumn<ChangeHistoryRecord, String> typeCol = new TableColumn<>("Loại thay đổi");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("changeType"));
        typeCol.setPrefWidth(120);
        typeCol.setCellFactory(column -> new TableCell<ChangeHistoryRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (item) {
                        case "ADD":
                            setText("➕ THÊM MỚI");
                            setStyle("-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60;");
                            break;
                        case "UPDATE":
                            setText("✏ CẬP NHẬT");
                            setStyle("-fx-background-color: #fef9e7; -fx-text-fill: #f39c12;");
                            break;
                        case "DELETE":
                            setText("🗑 XÓA");
                            setStyle("-fx-background-color: #fdedec; -fx-text-fill: #e74c3c;");
                            break;
                        default:
                            setText(item);
                            setStyle("");
                    }
                }
            }
        });
        
        TableColumn<ChangeHistoryRecord, LocalDate> dateCol = new TableColumn<>("Ngày thay đổi");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("changeDate"));
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(column -> new TableCell<ChangeHistoryRecord, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        
        TableColumn<ChangeHistoryRecord, String> residentNameCol = new TableColumn<>("Tên nhân khẩu");
        residentNameCol.setCellValueFactory(new PropertyValueFactory<>("residentName"));
        residentNameCol.setPrefWidth(150);
        residentNameCol.setCellFactory(column -> new TableCell<ChangeHistoryRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("(Đã xóa)");
                    setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });
        
        TableColumn<ChangeHistoryRecord, String> householdNameCol = new TableColumn<>("Tên hộ khẩu");
        householdNameCol.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        householdNameCol.setPrefWidth(180);
        householdNameCol.setCellFactory(column -> new TableCell<ChangeHistoryRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("(Đã xóa)");
                    setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });
        
        table.getColumns().addAll(idCol, typeCol, dateCol, residentNameCol, householdNameCol);
        
        // Load data
        loadHistoryData(table);
        
        return table;
    }
    
    private void loadHistoryData(TableView<ChangeHistoryRecord> table) {
        try {
            var records = changeHistoryService.getHouseholdChangeHistory(householdId);
            ObservableList<ChangeHistoryRecord> data = FXCollections.observableArrayList(records);
            table.setItems(data);
            
            if (records.isEmpty()) {
                table.setPlaceholder(new Label("Chưa có lịch sử thay đổi nào cho hộ khẩu này"));
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tải lịch sử thay đổi: " + e.getMessage());
            table.setPlaceholder(new Label("Không thể tải lịch sử thay đổi"));
        }
    }
    
    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button refreshBtn = new Button("🔄 Làm mới");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        refreshBtn.setOnAction(e -> {
            // Reload table
            TableView<ChangeHistoryRecord> table = (TableView<ChangeHistoryRecord>) stage.getScene().getRoot().lookup("TableView");
            if (table != null) {
                loadHistoryData(table);
            }
        });
        
        Button statisticsBtn = new Button("📊 Thống kê");
        statisticsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
        statisticsBtn.setOnAction(e -> {
            try {
                changeHistoryService.printChangeStatistics();
                views.messages.InformationDialog.showNotification("Thống kê", "Xem console để thấy thống kê chi tiết");
            } catch (Exception ex) {
                views.messages.ErrorDialog.showError("Lỗi", "Không thể hiển thị thống kê: " + ex.getMessage());
            }
        });
        
        Button closeBtn = new Button("✖ Đóng");
        closeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5;");
        closeBtn.setOnAction(e -> stage.close());
        
        buttonBox.getChildren().addAll(refreshBtn, statisticsBtn, closeBtn);
        return buttonBox;
    }
} 