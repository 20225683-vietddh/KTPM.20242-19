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
import controllers.resident.ResidentController;
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
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblError;

    private ResidentController controller;
    private Resident resident;
    private Stage stage;
    private Runnable onSaveCallback; // Callback Ä‘á»ƒ lÃ m má»›i báº£ng
    
    public ResidentFormHandler(Resident resident, Runnable onSaveCallback) throws IOException, SQLException {
        this.resident = resident;
        this.onSaveCallback = onSaveCallback;
        this.stage = new Stage();
        this.controller = new ResidentController();

        // Tai FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.RESIDENT_EDIT_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("Khong tim thay tep FXML: /views/resident/ResidentEditForm.fxml");
        }
        loader.setController(this);
        Scene scene = new Scene(loader.load(), 400, 650);
        stage.setScene(scene);
        stage.setTitle("Chinh sua thong tin nhan khau");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
    }

    public void show() {
        stage.showAndWait();
    }

    @FXML
    public void initialize() {
        // Kiem tra null cho cac thanh phan
        if (tfId == null || tfFullName == null || btnSave == null || btnClose == null) {
            System.err.println("One or more FXML components are null: " +
                    "\ntfId: " + (tfId == null ? "null" : "not null") +
                    "\ntfFullName: " + (tfFullName == null ? "null" : "not null") +
                    "\nbtnSave: " + (btnSave == null ? "null" : "not null") +
                    "\nbtnClose: " + (btnClose == null ? "null" : "not null"));
            return;
        }

        // Khoi tao cac ComboBox
        if (cbGender != null) {
            cbGender.getItems().addAll(Configs.GENDER);
            cbGender.setEditable(false);
        }
        if (cbEthnicity != null) {
            cbEthnicity.getItems().addAll(Configs.ETHNICITY);
            cbEthnicity.setEditable(false);
        }
        if (cbPlaceOfIssue != null) {
            cbPlaceOfIssue.getItems().addAll(Configs.PLACEOFISSUE);
            cbPlaceOfIssue.setEditable(false);
        }

        // Load du lieu hien tai
        loadResidentData();

        // Gan su kien cho cac nut
        btnSave.setOnAction(e -> handleSave());
        btnClose.setOnAction(e -> stage.close());

        // Dam bao cac TextField co the chinh sua (tru tfId)
        if (tfId != null) tfId.setEditable(false);
        if (tfFullName != null) tfFullName.setEditable(true);
        if (tfReligion != null) tfReligion.setEditable(true);
        if (tfCitizenId != null) tfCitizenId.setEditable(true);
        if (tfOccupation != null) tfOccupation.setEditable(true);
        if (tfRelationshipWithHead != null) tfRelationshipWithHead.setEditable(true);
    }

    private void loadResidentData() {
        if (resident != null) {
            tfId.setText(String.valueOf(resident.getId()));
            tfFullName.setText(resident.getFullName() != null ? resident.getFullName() : "");
            dpDateOfBirth.setValue(resident.getDateOfBirth());
            dpDateOfIssue.setValue(resident.getDateOfIssue());
            cbGender.setValue(resident.getGender().toString());
            cbEthnicity.setValue(resident.getEthnicity());
            cbPlaceOfIssue.setValue(resident.getPlaceOfIssue());
            tfReligion.setText(resident.getReligion() != null ? resident.getReligion() : "");
            tfCitizenId.setText(resident.getCitizenId() != null ? resident.getCitizenId() : "");
            tfOccupation.setText(resident.getOccupation() != null ? resident.getOccupation() : "");
            tfRelationshipWithHead.setText(resident.getRelationship() != null ? resident.getRelationship().toString() : "");
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

            controller.updateResident(resident);

            if (onSaveCallback != null) onSaveCallback.run();

            stage.close();
        } catch (SQLException e) {
            lblError.setText("Loi co so du lieu: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            lblError.setText("Loi khong xac dinh: " + e.getMessage());
            e.printStackTrace();
        }
    }

}