package views.room;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Room;
import services.RoomService;
import views.BaseScreenWithLogoutAndGoBackHandler;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RoomHandler extends BaseScreenWithLogoutAndGoBackHandler implements Initializable {
    
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private Button btnSearch;
    @FXML private Button btnRefresh;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox roomContainer;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblEmptyRooms;
    @FXML private Label lblOccupiedRooms;
    @FXML private Label lblOccupancyRate;
    
    private RoomService roomService;
    private ObservableList<Room> roomList;
    
    public RoomHandler(Stage stage, String userName) throws Exception {
        super(stage, utils.Configs.ROOM_SCREEN_PATH, utils.Configs.LOGO_PATH, "Quản lý phòng");
        this.roomService = new RoomService();
        this.roomList = FXCollections.observableArrayList();
        loader.setController(this);
        this.setContent();
        this.setScene();
        if (this.scene != null) {
            this.scene.setUserData(this);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(); // Kích hoạt nút đăng xuất và quay lại
        setupFilterComboBox();
        setupEventHandlers();
        loadRooms();
        updateStatistics();
    }
    
    private void setupFilterComboBox() {
        cmbFilter.getItems().addAll("Tất cả", "Phòng trống", "Có người ở");
        cmbFilter.setValue("Tất cả");
    }
    
    private void setupEventHandlers() {
        btnSearch.setOnAction(e -> handleSearch());
        btnRefresh.setOnAction(e -> handleRefresh());
        cmbFilter.setOnAction(e -> handleFilter());
        
        // Tìm kiếm khi nhấn Enter
        txtSearch.setOnAction(e -> handleSearch());
    }
    
    private void loadRooms() {
        try {
            List<Room> rooms = roomService.getAllRooms();
            roomList.setAll(rooms);
            displayRooms(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể tải danh sách phòng: " + e.getMessage());
        }
    }
    
    private void displayRooms(List<Room> rooms) {
        roomContainer.getChildren().clear();
        
        if (rooms.isEmpty()) {
            Label noDataLabel = new Label("Không có phòng nào được tìm thấy");
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20px;");
            roomContainer.getChildren().add(noDataLabel);
            return;
        }
        
        for (Room room : rooms) {
            try {
                RoomCell roomCell = new RoomCell(stage, room, this);
                roomContainer.getChildren().add(roomCell);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi tạo RoomCell cho phòng: " + room.getRoomNumber());
            }
        }
    }
    
    private void handleSearch() {
        String keyword = txtSearch.getText();
        try {
            List<Room> searchResults = roomService.searchRooms(keyword);
            displayRooms(searchResults);
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể tìm kiếm: " + e.getMessage());
        }
    }
    
    private void handleFilter() {
        String selectedFilter = cmbFilter.getValue();
        try {
            List<Room> filteredRooms;
            switch (selectedFilter) {
                case "Phòng trống":
                    filteredRooms = roomService.getEmptyRooms();
                    break;
                case "Có người ở":
                    filteredRooms = roomService.getOccupiedRooms();
                    break;
                default:
                    filteredRooms = roomService.getAllRooms();
                    break;
            }
            displayRooms(filteredRooms);
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void handleRefresh() {
        txtSearch.clear();
        cmbFilter.setValue("Tất cả");
        loadRooms();
        updateStatistics();
    }
    
    public void updateStatistics() {
        try {
            int totalRooms = roomService.getTotalRoomsCount();
            int emptyRooms = roomService.getEmptyRoomsCount();
            int occupiedRooms = roomService.getOccupiedRoomsCount();
            double occupancyRate = roomService.getOccupancyRate();
            
            lblTotalRooms.setText("Tổng số phòng: " + totalRooms);
            lblEmptyRooms.setText("Phòng trống: " + emptyRooms);
            lblOccupiedRooms.setText("Có người ở: " + occupiedRooms);
            lblOccupancyRate.setText(String.format("Tỷ lệ lấp đầy: %.1f%%", occupancyRate));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi cập nhật thống kê: " + e.getMessage());
        }
    }
    
    public void refreshData() {
        handleRefresh();
    }
} 