package views.tempresidenceabsence;



import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import controllers.TemporaryResidenceAbsenceController;
import controllers.MemberService;
import exception.ServiceException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.TemporaryResidenceAbsence;
import services.MemberServiceImpl;
import utils.AlertUtils;
import utils.Configs;
import utils.SceneUtils;
import views.homepage.HomePageHandler;


public class TemporaryResidenceAbsenceHandler extends HomePageHandler implements Initializable {
    
    // Service layer
    private TemporaryResidenceAbsenceController temporaryController = new TemporaryResidenceAbsenceController();
    private MemberServiceImpl memberService = new MemberServiceImpl();
    
    // Data collections
    private ObservableList<TemporaryResidenceAbsence> requestList;
    private FilteredList<TemporaryResidenceAbsence> filteredRequestList;
    private SortedList<TemporaryResidenceAbsence> sortedRequestList;
    
    private final String userName;
    
    // UI Controls
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterType;
    @FXML private ComboBox<String> cmbFilterStatus;
    
    // Statistics labels
    @FXML private Label lblTotalRequests;
    @FXML private Label lblResidenceRequests;
    @FXML private Label lblAbsenceRequests;
    @FXML private Label lblPendingRequests;
    
    // Action buttons
    @FXML private Button btnAddRequest;
    @FXML private Button btnRefresh;
    
    // Table and columns
    @FXML private TableView<TemporaryResidenceAbsence> tblTemporaryRequests;
    @FXML private TableColumn<TemporaryResidenceAbsence, Integer> colId;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colMemberName;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colType;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colReason;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colStartDate;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colEndDate;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colAddress;
    @FXML private TableColumn<TemporaryResidenceAbsence, String> colStatus;
    @FXML private TableColumn<TemporaryResidenceAbsence, Void> colActions;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public TemporaryResidenceAbsenceHandler(Stage stage, String userName) throws Exception {
        super(stage, Configs.TEMPORARY_RESIDENCE_ABSENCE_PATH);
        this.temporaryController = new TemporaryResidenceAbsenceController();
        this.userName = userName;
        
        // Set this instance as the controller before loading FXML
        super.loader.setController(this);
        
        // Load FXML content
        this.setContent();
        this.setScene();
        
        // Set the username
        super.lblUserName.setText(userName);
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        super.initialize();
        // Initialize components
        setupComboBoxes();
        setupTableColumns();
        setupSearch();
        setupFilters();
        setupButtonActions();
        loadTemporaryRequestData();
        updateStatistics();
    }
    
    private void setupComboBoxes() {
        // Type filter
        cmbFilterType.setItems(FXCollections.observableArrayList(
            "Tất cả", "Tạm trú", "Tạm vắng"
        ));
        cmbFilterType.setValue("Tất cả");
        
        // Status filter
        cmbFilterStatus.setItems(FXCollections.observableArrayList(
            "Tất cả", "Chờ duyệt", "Đã duyệt", "Từ chối"
        ));
        cmbFilterStatus.setValue("Tất cả");
    }
    
    private void setupTableColumns() {
        try {
            if (colId == null) {
                System.err.println("Error: Column ID is null. FXML not properly loaded.");
                AlertUtils.showErrorAlert("Error", "UI Initialization Failed", 
                    "Table columns were not properly initialized. Please report this issue.");
                return;
            }
            
            // Set up basic columns
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
            colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
            colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            
            // Format type column
            colType.setCellValueFactory(cellData -> {
                String type = cellData.getValue().getType();
                String displayType = "RESIDENCE".equals(type) ? "Tạm trú" : "Tạm vắng";
                return new ReadOnlyStringWrapper(displayType);
            });
            
            // Format status column
            colStatus.setCellValueFactory(cellData -> {
                String status = cellData.getValue().getStatus();
                String displayStatus;
                switch (status) {
                    case "PENDING": displayStatus = "Chờ duyệt"; break;
                    case "APPROVED": displayStatus = "Đã duyệt"; break;
                    case "REJECTED": displayStatus = "Từ chối"; break;
                    default: displayStatus = status;
                }
                return new ReadOnlyStringWrapper(displayStatus);
            });
            
            // Format date columns
            colStartDate.setCellValueFactory(cellData -> {
                LocalDate date = cellData.getValue().getStartDate();
                return new ReadOnlyStringWrapper(date != null ? date.format(dateFormatter) : "");
            });
            
            colEndDate.setCellValueFactory(cellData -> {
                LocalDate date = cellData.getValue().getEndDate();
                return new ReadOnlyStringWrapper(date != null ? date.format(dateFormatter) : "");
            });
            
            // Set column widths proportionally
            colId.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.06));
            colMemberName.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.15));
            colType.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.08));
            colReason.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.18));
            colStartDate.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.1));
            colEndDate.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.1));
            colAddress.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.15));
            colStatus.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.08));
            colActions.prefWidthProperty().bind(tblTemporaryRequests.widthProperty().multiply(0.1));
            
            // Set up action column
            setupActionColumn();
            
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Table Setup Failed", e.getMessage());
        }
    }
    
    private void setupActionColumn() {
        Callback<TableColumn<TemporaryResidenceAbsence, Void>, TableCell<TemporaryResidenceAbsence, Void>> cellFactory = 
            new Callback<TableColumn<TemporaryResidenceAbsence, Void>, TableCell<TemporaryResidenceAbsence, Void>>() {
                @Override
                public TableCell<TemporaryResidenceAbsence, Void> call(final TableColumn<TemporaryResidenceAbsence, Void> param) {
                    return new TableCell<TemporaryResidenceAbsence, Void>() {
                        
                        private final Button btnView = new Button("Xem");
                        private final Button btnApprove = new Button("Duyệt");
                        private final Button btnReject = new Button("Từ chối");
                        private final Button btnEdit = new Button("Sửa");
                        private final Button btnDelete = new Button("Xóa");
                        
                        {
                            // Style buttons
                            btnView.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                            btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                            btnReject.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-cursor: hand;");
                            btnEdit.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand;");
                            btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-cursor: hand;");
                            
                            // Set button sizes
                            btnView.setPrefSize(50, 25);
                            btnApprove.setPrefSize(50, 25);
                            btnReject.setPrefSize(60, 25);
                            btnEdit.setPrefSize(40, 25);
                            btnDelete.setPrefSize(40, 25);
                            
                            // Set button actions
                            btnView.setOnAction(event -> {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                viewRequestDetails(request);
                            });
                            
                            btnApprove.setOnAction(event -> {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                approveRequest(request);
                            });
                            
                            btnReject.setOnAction(event -> {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                rejectRequest(request);
                            });
                            
                            btnEdit.setOnAction(event -> {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                editRequest(request);
                            });
                            
                            btnDelete.setOnAction(event -> {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                deleteRequest(request);
                            });
                        }
                        
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                TemporaryResidenceAbsence request = getTableView().getItems().get(getIndex());
                                HBox buttons = new HBox(5);
                                
                                // Always show view button
                                buttons.getChildren().add(btnView);
                                
                                // Show approve/reject buttons only for pending requests
                                if ("PENDING".equals(request.getStatus())) {
                                    buttons.getChildren().addAll(btnApprove, btnReject);
                                }
                                
                                // Show edit button for pending and approved requests
                                if (!"REJECTED".equals(request.getStatus())) {
                                    buttons.getChildren().add(btnEdit);
                                }
                                
                                // Always show delete button
                                buttons.getChildren().add(btnDelete);
                                
                                setGraphic(buttons);
                            }
                        }
                    };
                }
            };
        
        colActions.setCellFactory(cellFactory);
    }
    
    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRequestList.setPredicate(request -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                // Search in member name, reason, and address
                return request.getMemberName().toLowerCase().contains(lowerCaseFilter) ||
                       request.getReason().toLowerCase().contains(lowerCaseFilter) ||
                       request.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
    
    private void setupFilters() {
        // Type filter
        cmbFilterType.setOnAction(event -> applyFilters());
        
        // Status filter
        cmbFilterStatus.setOnAction(event -> applyFilters());
    }
    
    private void applyFilters() {
        filteredRequestList.setPredicate(request -> {
            boolean matchesType = true;
            boolean matchesStatus = true;
            boolean matchesSearch = true;
            
            // Type filter
            String selectedType = cmbFilterType.getValue();
            if (selectedType != null && !"Tất cả".equals(selectedType)) {
                String requestType = "RESIDENCE".equals(request.getType()) ? "Tạm trú" : "Tạm vắng";
                matchesType = selectedType.equals(requestType);
            }
            
            // Status filter
            String selectedStatus = cmbFilterStatus.getValue();
            if (selectedStatus != null && !"Tất cả".equals(selectedStatus)) {
                String requestStatus;
                switch (request.getStatus()) {
                    case "PENDING": requestStatus = "Chờ duyệt"; break;
                    case "APPROVED": requestStatus = "Đã duyệt"; break;
                    case "REJECTED": requestStatus = "Từ chối"; break;
                    default: requestStatus = request.getStatus();
                }
                matchesStatus = selectedStatus.equals(requestStatus);
            }
            
            // Search filter
            String searchText = txtSearch.getText();
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                matchesSearch = request.getMemberName().toLowerCase().contains(lowerCaseFilter) ||
                               request.getReason().toLowerCase().contains(lowerCaseFilter) ||
                               request.getAddress().toLowerCase().contains(lowerCaseFilter);
            }
            
            return matchesType && matchesStatus && matchesSearch;
        });
        
        updateStatistics();
    }
    
    private void setupButtonActions() {
        btnAddRequest.setOnAction(this::handleAddRequest);
        btnRefresh.setOnAction(this::handleRefresh);
    }
    
    private void loadTemporaryRequestData() {
        try {
            List<TemporaryResidenceAbsence> requests = temporaryController.getAllTemporaryRequests();
            requestList = FXCollections.observableArrayList(requests);
            filteredRequestList = new FilteredList<>(requestList);
            sortedRequestList = new SortedList<>(filteredRequestList);
            sortedRequestList.comparatorProperty().bind(tblTemporaryRequests.comparatorProperty());
            tblTemporaryRequests.setItems(sortedRequestList);
            
            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Data Loading Failed", 
                "Failed to load temporary residence/absence data: " + e.getMessage());
        }
    }
    
    private void updateStatistics() {
        if (requestList == null) return;
        
        int total = filteredRequestList.size();
        int residenceCount = 0;
        int absenceCount = 0;
        int pendingCount = 0;
        
        for (TemporaryResidenceAbsence request : filteredRequestList) {
            if ("RESIDENCE".equals(request.getType())) {
                residenceCount++;
            } else {
                absenceCount++;
            }
            
            if ("PENDING".equals(request.getStatus())) {
                pendingCount++;
            }
        }
        
        lblTotalRequests.setText(String.valueOf(total));
        lblResidenceRequests.setText(String.valueOf(residenceCount));
        lblAbsenceRequests.setText(String.valueOf(absenceCount));
        lblPendingRequests.setText(String.valueOf(pendingCount));
    }
    
    // Action handlers
    @FXML
    private void handleAddRequest(ActionEvent event) {
        try {
            // Navigate to add request form
            // Implementation depends on your form structure
            AlertUtils.showInfoAlert("Info", "Add Request", "Add request functionality to be implemented");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Failed", e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadTemporaryRequestData();
        txtSearch.clear();
        cmbFilterType.setValue("Tất cả");
        cmbFilterStatus.setValue("Tất cả");
    }
    
    private void viewRequestDetails(TemporaryResidenceAbsence request) {
        try {
            String details = String.format(
                "Chi tiết đơn #%d\n\n" +
                "Tên nhân khẩu: %s\n" +
                "Loại: %s\n" +
                "Lý do: %s\n" +
                "Ngày bắt đầu: %s\n" +
                "Ngày kết thúc: %s\n" +
                "Địa chỉ: %s\n" +
                "Trạng thái: %s",
                request.getId(),
                request.getMemberName(),
                "RESIDENCE".equals(request.getType()) ? "Tạm trú" : "Tạm vắng",
                request.getReason(),
                request.getStartDate() != null ? request.getStartDate().format(dateFormatter) : "N/A",
                request.getEndDate() != null ? request.getEndDate().format(dateFormatter) : "N/A",
                request.getAddress(),
                getDisplayStatus(request.getStatus())
            );
            
            AlertUtils.showInfoAlert("Chi tiết đơn", "Thông tin đơn tạm trú/tạm vắng", details);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "View Failed", e.getMessage());
        }
    }
    
    private void approveRequest(TemporaryResidenceAbsence request) {
        try {
            Optional<ButtonType> result = AlertUtils.showConfirmationAlert(
                "Xác nhận", 
                "Duyệt đơn", 
                "Bạn có chắc chắn muốn duyệt đơn #" + request.getId() + " không?"
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                temporaryController.approveRequest(request.getId());
                loadTemporaryRequestData();
                AlertUtils.showInfoAlert("Thành công", "Duyệt đơn", "Đơn đã được duyệt thành công");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Approval Failed", e.getMessage());
        }
    }
    
    private void rejectRequest(TemporaryResidenceAbsence request) {
        try {
            Optional<ButtonType> result = AlertUtils.showConfirmationAlert(
                "Xác nhận", 
                "Từ chối đơn", 
                "Bạn có chắc chắn muốn từ chối đơn #" + request.getId() + " không?"
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                temporaryController.rejectRequest(request.getId());
                loadTemporaryRequestData();
                AlertUtils.showInfoAlert("Thành công", "Từ chối đơn", "Đơn đã được từ chối");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Rejection Failed", e.getMessage());
        }
    }
    
    private void editRequest(TemporaryResidenceAbsence request) {
        try {
            // Navigate to edit form
            // Implementation depends on your form structure
            AlertUtils.showInfoAlert("Info", "Edit Request", "Edit request functionality to be implemented");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Edit Failed", e.getMessage());
        }
    }
    
    private void deleteRequest(TemporaryResidenceAbsence request) {
        try {
            Optional<ButtonType> result = AlertUtils.showConfirmationAlert(
                "Xác nhận xóa", 
                "Xóa đơn", 
                "Bạn có chắc chắn muốn xóa đơn #" + request.getId() + " không?\nThao tác này không thể hoàn tác."
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                temporaryController.deleteRequest(request.getId());
                loadTemporaryRequestData();
                AlertUtils.showInfoAlert("Thành công", "Xóa đơn", "Đơn đã được xóa thành công");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Deletion Failed", e.getMessage());
        }
    }
    
    private String getDisplayStatus(String status) {
        switch (status) {
            case "PENDING": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Từ chối";
            default: return status;
        }
    }
}