package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Resident;
import utils.Configs;
import controllers.ManageResidentController;
import exception.InvalidInputException;
import views.messages.ErrorDialog;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;


public class NewResidentFormHandler {
    @FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbEthnicity;
    @FXML private ComboBox<String> cbPlaceOfIssue;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private TextField tfOccupation;
    @FXML private TextField tfRelationshipWithHead;
    @FXML private TextField tfHouseHoldId;
    @FXML private TextField tfNote;
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblError;
    

    private final ManageResidentController controller;
    private final TableView<Resident> residentTable;
    private final Runnable refreshCallback;
    private final Stage stage;
    
    public NewResidentFormHandler(TableView<Resident> tableView, Runnable onSaveCallback) throws IOException, SQLException {
        this.residentTable = tableView;
        this.refreshCallback = onSaveCallback;
        this.stage = new Stage();
        try {
            this.controller = new ManageResidentController();
        } catch (SQLException e) {
            ErrorDialog.showError("Lỗi kết nối", "Không thể khởi tạo database: " + e.getMessage());
            throw e;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.NEW_RESIDENT_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("Không tìm thấy file FXML: /views/resident/NewResidentForm.fxml");
        }
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load(), 400, 650);
            stage.setScene(scene);
            stage.setTitle("Thêm nhân khẩu mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Lỗi tải FXML: " + e.getMessage());
            throw e;
        }
    }
    
    public void show() {
        stage.showAndWait();
    }
    @FXML
    private void initialize() {
        btnSave.setOnAction(e -> handleSave());
        btnClose.setOnAction(e -> handleClose());
        
        cbGender.getItems().addAll(Configs.GENDER);
        cbEthnicity.getItems().addAll(Configs.ETHNICITY);
        cbPlaceOfIssue.getItems().addAll(Configs.PLACEOFISSUE);
        
     // Đảm bảo các TextField có thể chỉnh sửa (trừ tfId)
        if (tfFullName != null) tfFullName.setEditable(true);
        if (tfReligion != null) tfReligion.setEditable(true);
        if (tfCitizenId != null) tfCitizenId.setEditable(true);
        if (tfOccupation != null) tfOccupation.setEditable(true);
        if (tfRelationshipWithHead != null) tfRelationshipWithHead.setEditable(true);
        if (tfHouseHoldId != null) tfHouseHoldId.setEditable(true);
        if (tfNote != null) tfNote.setEditable(true);
    }
    private void handleSave() {
        try {
            System.out.println("Bắt đầu lưu nhân khẩu mới");
            if (lblError != null) {
                lblError.setText("");
            } else {
                System.err.println("lblError không được khởi tạo");
            }

            Resident resident = new Resident();

            String fullName = tfFullName.getText().trim();
            if (fullName.isEmpty()) {
                throw new InvalidInputException("Họ và tên không được để trống.");
            }
            resident.setFullName(fullName);  
            
            LocalDate dob = dpDateOfBirth.getValue();
            if (dob != null) resident.setDateOfBirth(dob);
            
            String gender = cbGender.getSelectionModel().getSelectedItem();
            if (gender == null) throw new InvalidInputException("Giới tính phải được chọn.");
            resident.setGender(gender);

            resident.setEthnicity(cbEthnicity.getSelectionModel().getSelectedItem());
            
            resident.setReligion(tfReligion.getText().trim());

            String citizenId = tfCitizenId.getText().trim();
            if (citizenId.isEmpty()) {
                throw new InvalidInputException("Căn cước công dân không được để trống.");
            }
            if (!citizenId.matches("\\d{12}")) {
                throw new InvalidInputException("Căn cước công dân phải là 12 chữ số.");
            }
            resident.setCitizenId(citizenId);

            LocalDate doi = dpDateOfIssue.getValue();
            if (doi != null) resident.setDateOfIssue(doi);
            
            resident.setPlaceOfIssue(cbPlaceOfIssue.getSelectionModel().getSelectedItem());

            resident.setOccupation(tfOccupation.getText().trim());
            
            resident.setAddedDate(LocalDate.now());
            
            String relationshipText = tfRelationshipWithHead.getText().trim();
            if (relationshipText.isEmpty()) {
                throw new InvalidInputException("Quan hệ với chủ hộ không được để trống.");
            }
            resident.setRelationshipWithHead(relationshipText);

            resident.setNotes(tfNote.getText().trim());

            String householdIdText = tfHouseHoldId.getText().trim();
            if (!householdIdText.isEmpty()) {
                try {
                    int householdId = Integer.parseInt(householdIdText);
                    resident.setHouseholdId(householdId);
                } catch (NumberFormatException e) {
                    throw new InvalidInputException("ID hộ phải là một số nguyên.");
                }
            }

            System.out.println("Gọi addResident");
            controller.handleAddResident(resident); // Giả định phương thức thêm mới
            System.out.println("Thêm nhân khẩu thành công");

            // Cập nhật TableView
            if (residentTable != null) {
                residentTable.getItems().add(new Resident(resident));
                residentTable.refresh();
                System.out.println("Đã thêm resident mới vào TableView");
            } else {
                System.err.println("residentTable không được khởi tạo");
            }

            if (refreshCallback != null) {
                System.out.println("Gọi refreshCallback");
                refreshCallback.run();
            } else {
                System.err.println("refreshCallback không được khởi tạo trong handleSave");
            }

            stage.close();
        } catch (InvalidInputException e) {
            if (lblError != null) {
                lblError.setText(e.getMessage());
            }
            System.err.println("Lỗi nhập liệu: " + e.getMessage());
        } catch (SQLException e) {
            if (lblError != null) {
                lblError.setText("Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
            System.err.println("Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            if (lblError != null) {
                lblError.setText("Lỗi không xác định: " + e.getMessage());
            }
            System.err.println("Lỗi không xác định trong handleSave: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClose() {
        System.out.println("Đóng form thêm nhân khẩu");
        stage.close();
    }
}
