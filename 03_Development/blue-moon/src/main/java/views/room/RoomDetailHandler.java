package views.room;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Room;
import models.Household;
import services.HouseholdService;

import java.time.format.DateTimeFormatter;

public class RoomDetailHandler {
    @FXML private Label lblRoomNumber;
    @FXML private Label lblStatus;
    @FXML private Label lblHouseholdId;
    @FXML private Label lblHouseNumber;
    @FXML private Label lblAddress;
    @FXML private Label lblRegistrationDate;
    @FXML private Label lblHeadResident;
    @FXML private Label lblResidentsCount;
    @FXML private Label lblAreas;
    @FXML private Button btnClose;
    
    private Stage stage;
    private Stage parentStage;
    private Room room;
    private HouseholdService householdService;
    
    public RoomDetailHandler(Stage parentStage, Room room) throws Exception {
        this.parentStage = parentStage;
        this.room = room;
        this.householdService = new HouseholdService();
        
        // Tạo stage mới cho popup
        this.stage = new Stage();
        
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/room/RoomDetailPopup.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        
        // Thiết lập scene và stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Chi tiết phòng - " + room.getRoomNumber());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(false);
        
        // Áp dụng hiệu ứng blur cho cửa sổ cha
        parentStage.getScene().getRoot().setEffect(new GaussianBlur(10));
        
        // Hiển thị thông tin phòng
        populateRoomDetails();
        
        // Hiển thị popup
        stage.showAndWait();
        
        // Xóa hiệu ứng blur khi đóng popup
        parentStage.getScene().getRoot().setEffect(null);
    }
    
    @FXML
    private void initialize() {
        btnClose.setOnAction(e -> handleClose());
    }
    
    private void populateRoomDetails() {
        try {
            // Thông tin cơ bản của phòng
            lblRoomNumber.setText(room.getRoomNumber());
            lblStatus.setText(room.isEmpty() ? "Trống" : "Có người ở");
            
            if (room.isEmpty()) {
                lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                // Ẩn thông tin hộ khẩu nếu phòng trống
                hideHouseholdInfo();
            } else {
                lblStatus.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                
                // Hiển thị thông tin hộ khẩu
                Household household = room.getHousehold();
                if (household != null) {
                    lblHouseholdId.setText(String.valueOf(household.getHouseholdId()));
                    lblHouseNumber.setText(household.getHouseNumber());
                    
                    String fullAddress = household.getStreet() + ", " + 
                                       household.getWard() + ", " + 
                                       household.getDistrict();
                    lblAddress.setText(fullAddress);
                    
                    if (household.getRegistrationDate() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        lblRegistrationDate.setText(household.getRegistrationDate().format(formatter));
                    } else {
                        lblRegistrationDate.setText("Chưa có thông tin");
                    }
                    
                    // Lấy thông tin chủ hộ
                    if (household.getHeadResidentId() > 0) {
                        // Có thể lấy tên chủ hộ từ database nếu cần
                        lblHeadResident.setText("ID: " + household.getHeadResidentId());
                    } else {
                        lblHeadResident.setText("Chưa có thông tin");
                    }
                    
                    lblResidentsCount.setText(String.valueOf(household.getNumberOfResidents()));
                    lblAreas.setText(household.getAreas() + " m²");
                } else {
                    hideHouseholdInfo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể hiển thị thông tin phòng!");
        }
    }
    
    private void hideHouseholdInfo() {
        lblHouseholdId.setText("Không có");
        lblHouseNumber.setText("Không có");
        lblAddress.setText("Không có");
        lblRegistrationDate.setText("Không có");
        lblHeadResident.setText("Không có");
        lblResidentsCount.setText("0");
        lblAreas.setText("Không có");
    }
    
    private void handleClose() {
        // Xóa hiệu ứng blur trước khi đóng
        if (parentStage != null && parentStage.getScene() != null) {
            parentStage.getScene().getRoot().setEffect(null);
        }
        stage.close();
    }
} 