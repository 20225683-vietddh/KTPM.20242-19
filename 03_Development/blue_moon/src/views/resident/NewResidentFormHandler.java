package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Resident;
import utils.Configs;
import controllers.resident.ResidentController;
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
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblError;
    

    private final ResidentController controller;
    private final TableView<Resident> residentTable;
    private final Runnable refreshCallback;
    private final Stage stage;
    
    public NewResidentFormHandler(TableView<Resident> tableView, Runnable onSaveCallback) throws IOException, SQLException {
        this.residentTable = tableView;
        this.refreshCallback = onSaveCallback;
        this.stage = new Stage();
        this.controller = new ResidentController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.NEW_RESIDENT_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("Khong tim thay file FXML: /views/resident/NewResidentForm.fxml");
        }
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load(), 400, 650);
            stage.setScene(scene);
            stage.setTitle("Them nhan khau moi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Loi tai FXML: " + e.getMessage());
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
    }
    private void handleSave() {
        try {
            System.out.println("Bat dau luu nhan khau moi");
            if (lblError != null) {
                lblError.setText("");
            } else {
                System.err.println("lblError khong duoc khoi tao");
            }

            Resident resident = new Resident();

            String fullName = tfFullName.getText().trim();
            if (fullName.isEmpty()) {
                throw new InvalidInputException("Ho va ten khong duoc de trong.");
            }
            resident.setFullName(fullName);  
            
            if (dpDateOfBirth.getValue() == null) {
                throw new InvalidInputException("Ngay sinh phai duoc chon.");
            }
            resident.setDateOfBirth(dpDateOfBirth.getValue());
            
            String gender = cbGender.getSelectionModel().getSelectedItem();
            if (gender == null) throw new InvalidInputException("Gioi tinh phai duoc chon.");
            resident.setGender(gender);

            String ethnicity = cbEthnicity.getSelectionModel().getSelectedItem();
            if (ethnicity == null) throw new InvalidInputException("Dan toc phai duoc chon.");
            resident.setEthnicity(ethnicity);
            
            resident.setReligion(tfReligion.getText().trim());

            String citizenId = tfCitizenId.getText().trim();
            if (citizenId.isEmpty()) {
                throw new InvalidInputException("Can cuoc cong dan khong duoc de trong.");
            }
            if (!citizenId.matches("\\d{12}")) {
                throw new InvalidInputException("Can cuoc cong dan phai la 12 chu so.");
            }
            resident.setCitizenId(citizenId);

            if (dpDateOfIssue.getValue() == null) {
                throw new InvalidInputException("Ngay cap CCCD phai duoc chon.");
            }
            resident.setDateOfIssue(dpDateOfIssue.getValue());
            
            String placeOfIssue = cbPlaceOfIssue.getSelectionModel().getSelectedItem();
            if (placeOfIssue == null) throw new InvalidInputException("Noi cap CCCD phai duoc chon.");
            resident.setPlaceOfIssue(placeOfIssue);

            resident.setOccupation(tfOccupation.getText().trim());
            
            resident.setAddedDate(LocalDate.now());

            
           // resident.setisHouseholdHead(false); 


            System.out.println("Gọi addResident");
            controller.addResident(resident);
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
