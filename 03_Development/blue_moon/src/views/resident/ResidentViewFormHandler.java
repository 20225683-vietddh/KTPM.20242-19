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
import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class ResidentViewFormHandler {
	@FXML private TextField tfId;
	@FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbEthnicity;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private ComboBox<String> cbPlaceOfIssue;
    @FXML private TextField tfOccupation;
    @FXML private TextField tfAdded_date;
    @FXML private TextField tfRelationshipWithHead;
    @FXML private Button btnClose;

    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ResidentController controller;
    private Resident resident;
    private Stage stage;

    public ResidentViewFormHandler(Stage stage, Resident resident) throws IOException, SQLException {
        this.resident = resident;
        this.stage = stage;
        try {
            this.controller = new ResidentController();
        } catch (SQLException e) {
            ErrorDialog.showError("Loi ket noi", "Khong the khoi tao controller: " + e.getMessage());
            throw e;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.RESIDENT_VIEW_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("Khong tim thay tep FXML: /views/resident/ResidentViewForm.fxml");
        }
        loader.setController(this);
        Scene scene = new Scene(loader.load(), 400, 650);
        stage.setScene(scene);
        stage.setTitle("Xem thong tin nhan khau");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
    }

    public void show() {
        stage.show();
    }

    @FXML
    public void initialize() {
        // Kiem tra null cho cac thanh phan
        if (tfId == null || tfFullName == null || btnClose == null) {
            System.err.println("One or more FXML components are null: " +
                    "\ntfId: " + (tfId == null ? "null" : "not null") +
                    "\ntfFullName: " + (tfFullName == null ? "null" : "not null") +
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

        // Gan su kien cho nut dong
        btnClose.setOnAction(e -> stage.close());
    }

    private void loadResidentData() {
        if (resident == null) {
            System.err.println("Resident object is null");
            return;
        }

        // Thiet lap cac truong thong tin
        tfId.setText(String.valueOf(resident.getId()));
        tfFullName.setText(resident.getFullName());
        dpDateOfBirth.setValue(resident.getDateOfBirth());
        dpDateOfIssue.setValue(resident.getDateOfIssue());
        cbGender.setValue(resident.getGender().toString());
        cbEthnicity.setValue(resident.getEthnicity());
        cbPlaceOfIssue.setValue(resident.getPlaceOfIssue());
        tfReligion.setText(resident.getReligion());
        tfCitizenId.setText(resident.getCitizenId());
        tfOccupation.setText(resident.getOccupation());
        tfRelationshipWithHead.setText(resident.getRelationship().toString());

        // Vo hieu hoa cac truong nhap lieu
        tfId.setEditable(false);
        tfFullName.setEditable(false);
        dpDateOfBirth.setEditable(false);
        dpDateOfIssue.setEditable(false);
        cbGender.setEditable(false);
        cbEthnicity.setEditable(false);
        cbPlaceOfIssue.setEditable(false);
        tfReligion.setEditable(false);
        tfCitizenId.setEditable(false);
        tfOccupation.setEditable(false);
        tfRelationshipWithHead.setEditable(false);
    }
}
