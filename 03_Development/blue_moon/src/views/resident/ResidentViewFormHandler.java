package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Resident;
import controllers.ManageResidentController;
import exception.InvalidInputException;
import views.messages.ErrorDialog;
import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class ResidentViewFormHandler {
	@FXML private TextField tfId;
	@FXML private TextField tfFullName;
    @FXML private TextField tfDateOfBirth;
    @FXML private TextField tfGender;
    @FXML private TextField tfEthnicity;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private TextField tfDateOfIssue;
    @FXML private TextField tfPlaceOfIssue;
    @FXML private TextField tfOccupation;
    @FXML private TextField tfAdded_date;
    @FXML private TextField tfRelationship_with_head;
    @FXML private TextField tfNote; 
    @FXML private Button btnClose;

    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ManageResidentController controller;
    private Resident resident;
    private Stage stage;

    public ResidentViewFormHandler(Stage stage, Resident resident) throws IOException, SQLException {
        this.stage = stage;
        this.resident = resident;
        try {
            this.controller = new ManageResidentController(); // Xử lý lỗi SQL khi khởi tạo controller
        } catch (SQLException e) {
            ErrorDialog.showError("Lỗi kết nối", "Không thể khởi tạo controller: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Tải FXML và thiết lập Stage
        URL fxmlLocation = getClass().getResource("/views/resident/ResidentViewForm.fxml");
        if (fxmlLocation == null) {
            throw new IOException("Không tìm thấy tệp FXML: /ResidentViewForm.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setController(this);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Thông tin nhân khẩu");
    }

    public void show() {
        stage.show();
    }

    @FXML
    public void initialize() {
        loadResidentData();
        btnClose.setOnAction(e -> stage.close());
    }

    private void loadResidentData() {
        if (resident != null) {
        	tfId.setText(String.valueOf(resident.getId()));
            tfFullName.setText(resident.getFullName());
            if (resident.getDateOfBirth() != null) {
                tfDateOfBirth.setText(resident.getDateOfBirth().format(DATE_FORMATTER));
            }

            if (resident.getDateOfIssue() != null) {
                tfDateOfIssue.setText(resident.getDateOfIssue().format(DATE_FORMATTER));
            }
            if (resident.getAddedDate() != null) {
            	tfAdded_date.setText(resident.getAddedDate().format(DATE_FORMATTER));
            }
            tfGender.setText(resident.getGender());
            tfEthnicity.setText(resident.getEthnicity());
            tfReligion.setText(resident.getReligion());
            tfCitizenId.setText(resident.getCitizenId());
            tfPlaceOfIssue.setText(resident.getPlaceOfIssue());
            tfOccupation.setText(resident.getOccupation());
            tfRelationship_with_head.setText(resident.getRelationshipWithHead());
            tfNote.setText(resident.getNotes());
        }
    }
}
