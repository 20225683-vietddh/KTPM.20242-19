package views;

import controllers.HouseholdController;
import controllers.TemporaryResidenceAbsenceController;
import dto.TemporaryResidenceAbsenceDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Household;
import models.Member;

public class FormIssueTemporaryResidenceAbsenceHandler {
    private Stage stage;
    private TemporaryResidenceAbsenceController controller;
    private ComboBox<String> typeComboBox;
    private TextField personNameField;
    private TextField idNumberField;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField addressField;
    private TextArea reasonField;
    
    public FormIssueTemporaryResidenceAbsenceHandler(TemporaryResidenceAbsenceController controller) {
        this.controller = controller;
        this.stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        createIssueForm();
    }
    
    private void createIssueForm() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Cấp giấy tạm trú/tạm vắng");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        
        Label typeLabel = new Label("Loại giấy:");
        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Tạm trú", "Tạm vắng");
        typeComboBox.setValue("Tạm trú"); // Default value
        
        Label personLabel = new Label("Họ tên người đăng ký:");
        personNameField = new TextField();
        
        Label idLabel = new Label("Số CMND/CCCD:");
        idNumberField = new TextField();
        
        Label startDateLabel = new Label("Ngày bắt đầu:");
        startDatePicker = new DatePicker();
        
        Label endDateLabel = new Label("Ngày kết thúc:");
        endDatePicker = new DatePicker();
        
        Label addressLabel = new Label("Địa chỉ tạm trú/vắng:");
        addressField = new TextField();
        
        Label reasonLabel = new Label("Lý do:");
        reasonField = new TextArea();
        reasonField.setPrefRowCount(3);
        
        formGrid.add(typeLabel, 0, 0);
        formGrid.add(typeComboBox, 1, 0);
        formGrid.add(personLabel, 0, 1);
        formGrid.add(personNameField, 1, 1);
        formGrid.add(idLabel, 0, 2);
        formGrid.add(idNumberField, 1, 2);
        formGrid.add(startDateLabel, 0, 3);
        formGrid.add(startDatePicker, 1, 3);
        formGrid.add(endDateLabel, 0, 4);
        formGrid.add(endDatePicker, 1, 4);
        formGrid.add(addressLabel, 0, 5);
        formGrid.add(addressField, 1, 5);
        formGrid.add(reasonLabel, 0, 6);
        formGrid.add(reasonField, 1, 6);
        
        // Buttons
        HBox buttonBar = new HBox(10);
        
        Button saveButton = new Button("Lưu");
        saveButton.setOnAction(e -> submitForm());
        
        Button cancelButton = new Button("Hủy");
        cancelButton.setOnAction(e -> stage.close());
        
        buttonBar.getChildren().addAll(saveButton, cancelButton);
        
        root.getChildren().addAll(titleLabel, formGrid, buttonBar);
        
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Cấp giấy tạm trú/tạm vắng");
    }
    
    public void displayIssueForm() {
        stage.show();
    }
    
    private void submitForm() {
        // Create DTO from form fields
        TemporaryResidenceAbsenceDTO tempRADTO = new TemporaryResidenceAbsenceDTO();
        tempRADTO.setType(typeComboBox.getValue());
        tempRADTO.setPersonName(personNameField.getText());
        tempRADTO.setIdNumber(idNumberField.getText());
        tempRADTO.setStartDate(startDatePicker.getValue());
        tempRADTO.setEndDate(endDatePicker.getValue());
        tempRADTO.setAddress(addressField.getText());
        tempRADTO.setReason(reasonField.getText());
        
        // Submit to controller
        controller.submitIssueRequest(tempRADTO);
    }
    
    public void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Cấp giấy " + typeComboBox.getValue() + " thành công!");
        alert.showAndWait();
        
        stage.close();
    }
}