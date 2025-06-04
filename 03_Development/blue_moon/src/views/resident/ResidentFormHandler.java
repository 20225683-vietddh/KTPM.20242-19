package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Resident;
import utils.Configs;
import utils.enums.RelationshipType;
import controllers.ManageResidentController;
import exception.InvalidInputException;
import views.messages.ErrorDialog;
import java.io.IOException;
import java.sql.SQLException;

public class ResidentFormHandler {
    @FXML private TextField tfId;
    @FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private DatePicker dpAddedDate;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbEthnicity;
    @FXML private ComboBox<String> cbPlaceOfIssue;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private TextField tfOccupation;
    @FXML private TextField tfRelationshipWithHead;
    @FXML private TextField tfNote;
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblError;


    private ManageResidentController controller;
    private Resident resident;
    private Stage stage;
    private Runnable onSaveCallback; // Callback để làm mới bảng
    
    public ResidentFormHandler(Resident resident, Runnable onSaveCallback) throws IOException, SQLException {
        this.resident = resident;
        this.onSaveCallback = onSaveCallback;
        this.stage = new Stage();
        try {
            this.controller = new ManageResidentController();
        } catch (SQLException e) {
            ErrorDialog.showError("Lỗi kết nối", "Không thể khởi tạo controller: " + e.getMessage());
            throw e;
        }

        // Tải FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.RESIDENT_EDIT_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("Không tìm thấy tệp FXML: /views/resident/ResidentEditForm.fxml");
        }
        loader.setController(this);
        Scene scene = new Scene(loader.load(), 400, 650);
        stage.setScene(scene);
        stage.setTitle("Chỉnh sửa thông tin nhân khẩu");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
    }

    public void show() {
        stage.showAndWait();
    }

    @FXML
    public void initialize() {
        // Kiểm tra null cho các thành phần
        if (tfId == null || tfFullName == null || btnSave == null || btnClose == null) {
            System.err.println("One or more FXML components are null: " +
                    "tfId=" + tfId + ", tfFullName=" + tfFullName + ", btnSave=" + btnSave + ", btnClose=" + btnClose);
        }
        
        cbGender.getItems().addAll(Configs.GENDER);
        cbEthnicity.getItems().addAll(Configs.ETHNICITY);
        cbPlaceOfIssue.getItems().addAll(Configs.PLACEOFISSUE);

        loadResidentData();

        btnSave.setOnAction(e -> handleSave());
        btnClose.setOnAction(e -> stage.close());

        // Đảm bảo các TextField có thể chỉnh sửa (trừ tfId)
        if (tfId != null) tfId.setEditable(false);
        if (tfFullName != null) tfFullName.setEditable(true);
        if (tfReligion != null) tfReligion.setEditable(true);
        if (tfCitizenId != null) tfCitizenId.setEditable(true);
        if (tfOccupation != null) tfOccupation.setEditable(true);
        if (tfRelationshipWithHead != null) tfRelationshipWithHead.setEditable(true);
        if (tfNote != null) tfNote.setEditable(true);
    }

    private void loadResidentData() {
        if (resident != null) {
        	tfId.setText(String.valueOf(resident.getId()));
            tfFullName.setText(resident.getFullName() != null ? resident.getFullName() : "");
            dpDateOfBirth.setValue(resident.getDateOfBirth());
            dpDateOfIssue.setValue(resident.getDateOfIssue());
            cbGender.setValue(resident.getGender());
            cbEthnicity.setValue(resident.getEthnicity());
            cbPlaceOfIssue.setValue(resident.getPlaceOfIssue());
            tfReligion.setText(resident.getReligion() != null ? resident.getReligion() : "");
            tfCitizenId.setText(resident.getCitizenId() != null ? resident.getCitizenId() : "");
            tfOccupation.setText(resident.getOccupation() != null ? resident.getOccupation() : "");
            tfRelationshipWithHead.setText(resident.getRelationship() != null ? resident.getRelationship().toString() : "");
            tfNote.setText(resident.getNotes() != null ? resident.getNotes() : "");
        }
    }

    private void handleSave() {
        try {
            lblError.setText("");
            resident.setFullName(tfFullName.getText().trim());
            resident.setDateOfBirth(dpDateOfBirth.getValue());
            resident.setDateOfIssue(dpDateOfIssue.getValue());
            resident.setGender(cbGender.getSelectionModel().getSelectedItem());
            resident.setEthnicity(cbEthnicity.getSelectionModel().getSelectedItem());
            resident.setPlaceOfIssue(cbPlaceOfIssue.getSelectionModel().getSelectedItem());
            resident.setReligion(tfReligion.getText().trim());
            resident.setCitizenId(tfCitizenId.getText().trim());
            resident.setOccupation(tfOccupation.getText().trim());
            resident.setRelationship(RelationshipType.valueOf(tfRelationshipWithHead.getText().trim()));
            resident.setNotes(tfNote.getText().trim());


            controller.handleUpdateResident(resident);

            if (onSaveCallback != null) onSaveCallback.run();

            stage.close();
        } catch (InvalidInputException e) {
            lblError.setText(e.getMessage());
        } catch (SQLException e) {
            lblError.setText("Lỗi cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            lblError.setText("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }

}