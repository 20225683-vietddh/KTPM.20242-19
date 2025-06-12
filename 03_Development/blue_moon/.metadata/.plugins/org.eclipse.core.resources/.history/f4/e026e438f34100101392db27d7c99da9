package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Resident;
import utils.Configs;
import controllers.resident.ManageResidentController;
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
            ErrorDialog.showError("Lá»—i káº¿t ná»‘i", "KhÃ´ng thá»ƒ khá»Ÿi táº¡o database: " + e.getMessage());
            throw e;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Configs.NEW_RESIDENT_FORM));
        if (loader.getLocation() == null) {
            throw new IOException("KhÃ´ng tÃ¬m tháº¥y file FXML: /views/resident/NewResidentForm.fxml");
        }
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load(), 400, 650);
            stage.setScene(scene);
            stage.setTitle("ThÃªm nhÃ¢n kháº©u má»›i");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Lá»—i táº£i FXML: " + e.getMessage());
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
        
     // Ä�áº£m báº£o cÃ¡c TextField cÃ³ thá»ƒ chá»‰nh sá»­a (trá»« tfId)
        if (tfFullName != null) tfFullName.setEditable(true);
        if (tfReligion != null) tfReligion.setEditable(true);
        if (tfCitizenId != null) tfCitizenId.setEditable(true);
        if (tfOccupation != null) tfOccupation.setEditable(true);
        //if (tfRelationshipWithHead != null) tfRelationshipWithHead.setEditable(true);
       // if (tfHouseHoldId != null) tfHouseHoldId.setEditable(true);
        if (tfNote != null) tfNote.setEditable(true);
    }
    private void handleSave() {
        try {
            System.out.println("Báº¯t Ä‘áº§u lÆ°u nhÃ¢n kháº©u má»›i");
            if (lblError != null) {
                lblError.setText("");
            } else {
                System.err.println("lblError khÃ´ng Ä‘Æ°á»£c khá»Ÿi táº¡o");
            }

            Resident resident = new Resident();

            String fullName = tfFullName.getText().trim();
            if (fullName.isEmpty()) {
                throw new InvalidInputException("Há»� vÃ  tÃªn khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
            }
            resident.setFullName(fullName);  
            
            LocalDate dob = dpDateOfBirth.getValue();
            if (dob != null) resident.setDateOfBirth(dob);
            
            String gender = cbGender.getSelectionModel().getSelectedItem();
            if (gender == null) throw new InvalidInputException("Giá»›i tÃ­nh pháº£i Ä‘Æ°á»£c chá»�n.");
            resident.setGender(gender);

            resident.setEthnicity(cbEthnicity.getSelectionModel().getSelectedItem());
            
            resident.setReligion(tfReligion.getText().trim());

            String citizenId = tfCitizenId.getText().trim();
            if (citizenId.isEmpty()) {
                throw new InvalidInputException("CÄƒn cÆ°á»›c cÃ´ng dÃ¢n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
            }
            if (!citizenId.matches("\\d{12}")) {
                throw new InvalidInputException("CÄƒn cÆ°á»›c cÃ´ng dÃ¢n pháº£i lÃ  12 chá»¯ sá»‘.");
            }
            resident.setCitizenId(citizenId);

            LocalDate doi = dpDateOfIssue.getValue();
            if (doi != null) resident.setDateOfIssue(doi);
            
            resident.setPlaceOfIssue(cbPlaceOfIssue.getSelectionModel().getSelectedItem());

            resident.setOccupation(tfOccupation.getText().trim());
            
            resident.setAddedDate(LocalDate.now());

            resident.setNotes(tfNote.getText().trim());
            
           // resident.setisHouseholdHead(false); 


            System.out.println("Gá»�i addResident");
            controller.handleAddResident(resident); // Giáº£ Ä‘á»‹nh phÆ°Æ¡ng thá»©c thÃªm má»›i
            System.out.println("ThÃªm nhÃ¢n kháº©u thÃ nh cÃ´ng");

            // Cáº­p nháº­t TableView
            if (residentTable != null) {
                residentTable.getItems().add(new Resident(resident));
                residentTable.refresh();
                System.out.println("Ä�Ã£ thÃªm resident má»›i vÃ o TableView");
            } else {
                System.err.println("residentTable khÃ´ng Ä‘Æ°á»£c khá»Ÿi táº¡o");
            }

            if (refreshCallback != null) {
                System.out.println("Gá»�i refreshCallback");
                refreshCallback.run();
            } else {
                System.err.println("refreshCallback khÃ´ng Ä‘Æ°á»£c khá»Ÿi táº¡o trong handleSave");
            }

            stage.close();
        } catch (InvalidInputException e) {
            if (lblError != null) {
                lblError.setText(e.getMessage());
            }
            System.err.println("Lá»—i nháº­p liá»‡u: " + e.getMessage());
        } catch (SQLException e) {
            if (lblError != null) {
                lblError.setText("Lá»—i cÆ¡ sá»Ÿ dá»¯ liá»‡u: " + e.getMessage());
            }
            System.err.println("Lá»—i SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            if (lblError != null) {
                lblError.setText("Lá»—i khÃ´ng xÃ¡c Ä‘á»‹nh: " + e.getMessage());
            }
            System.err.println("Lá»—i khÃ´ng xÃ¡c Ä‘á»‹nh trong handleSave: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClose() {
        System.out.println("Ä�Ã³ng form thÃªm nhÃ¢n kháº©u");
        stage.close();
    }
}
