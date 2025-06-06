package views.household;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Household;
import models.Resident;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import utils.RelationshipHelper;
import utils.enums.RelationshipType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import controllers.household.HouseholdController;
import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import utils.FieldVerifier;

public class ResidentDetailsDialogHandler {
	private HouseholdController householdController;
	private ResidentService residentService = new ResidentServiceImpl();
	
	private Household household;
	private ObservableList<Resident> residentList = FXCollections.observableArrayList();

	private Runnable onSuccessCallback;

	private TextField txtHouseholdSize;

	 
    @FXML private Label lblHouseholdInfo;
    @FXML private Label lblResidentCount;
    @FXML private TableView<Resident> tblResidents;
    @FXML private TableColumn<Resident, String> colResidentId;
    @FXML private TableColumn<Resident, String> colFullName;
    @FXML private TableColumn<Resident, String> colGender;
    @FXML private TableColumn<Resident, String> colDateOfBirth;
    @FXML private TableColumn<Resident, String> colIdCard;
    @FXML private TableColumn<Resident, String> colRelationship;
    @FXML private TableColumn<Resident, String> colIsHead;
    @FXML private Button btnClose;
    @FXML private Button btnMakeOwner;
    @FXML private Button btnRemoveResident;
    @FXML private Button btnAddResident;
    @FXML private TextField txtResidentCitizenId;
    @FXML private ComboBox<RelationshipType> cboRelationship;
    
    
    
   
	public Runnable getOnSuccessCallback() {
		return onSuccessCallback;
	}

	public void setOnSuccessCallback(Runnable onSuccessCallback) {
		this.onSuccessCallback = onSuccessCallback;
	}


    
    public ResidentDetailsDialogHandler() {
    	
    };
    

	
    
	// Interface for callback when changes are made
    public interface ResidentChangeListener {
        void onResidentOwnerChanged(Resident newOwner);
        void onResidentRemoved(Resident removedResident);
        void onResidentAdded(Resident newResident);
    }
    
    private ResidentChangeListener changeListener;
    
    @FXML
    private void initialize() {
        // Set up table columns first
        setupColumns();
        setupColumnStyles();
        
        // Set up button handlers
        setupButtonHandlers();
        
        // Initialize resident count label
        updateResidentCount();
        
        // Set up relationship combo box
        setupRelationshipComboBox();
        
        // Set up table selection listener
        tblResidents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnMakeOwner.setDisable(newSelection == null);
            btnRemoveResident.setDisable(newSelection == null);
        });
        

        setupAddResidentValidation();

    }

    

    private void setupRelationshipComboBox() {
        // Clear existing items first
        cboRelationship.getItems().clear();
        
        // Add all relationship types to combo box
        ObservableList<RelationshipType> relationships = FXCollections.observableArrayList();
        
        // Add all enum values except UNKNOWN
        for (RelationshipType type : RelationshipType.values()) {
            if (type != RelationshipType.UNKNOWN) {
                relationships.add(type);
            }
        }
        
        cboRelationship.setItems(relationships);
        
        // Set custom string converter with non-accented Vietnamese text
        cboRelationship.setConverter(new javafx.util.StringConverter<RelationshipType>() {
            @Override
            public String toString(RelationshipType relationshipType) {
                if (relationshipType == null) return "";
                
                // Convert enum to non-accented Vietnamese display text
                switch (relationshipType) {
                    case FATHER: return "Cha";
                    case MOTHER: return "Me";
                    case SON: return "Con trai";
                    case DAUGHTER: return "Con gai";
                    case HUSBAND: return "Chong";
                    case WIFE: return "Vo";
                    case BROTHER: return "Anh/Em trai";
                    case SISTER: return "Chi/Em gai";
                    case GRAND_FATHER: return "Ong";
                    case GRAND_MOTHER: return "Ba";
                    case GRAND_SON: return "Chau trai";
                    case GRAND_DAUGHTER: return "Chau gai";
                    case UNCLE: return "Chu/Bac";
                    case AUNT: return "Co/Di";
                    case NEPHEW: return "Chau trai (anh chi em)";
                    case NIECE: return "Chau gai (anh chi em)";
                    case COUSIN: return "Anh/Chi/Em ho";
                    case FATHER_IN_LAW: return "Bo chong/vo";
                    case MOTHER_IN_LAW: return "Me chong/vo";
                    case SON_IN_LAW: return "Con re";
                    case DAUGHTER_IN_LAW: return "Con dau";
                    case BROTHER_IN_LAW: return "Anh/Em re";
                    case SISTER_IN_LAW: return "Chi/Em dau";
                    case OTHER: return "Khac";
                    default: return relationshipType.toString();
                }
            }

            @Override
            public RelationshipType fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                // Convert non-accented Vietnamese text back to enum
                string = string.toLowerCase().trim();
                switch (string) {
                    case "cha": return RelationshipType.FATHER;
                    case "me": return RelationshipType.MOTHER;
                    case "con trai": return RelationshipType.SON;
                    case "con gai": return RelationshipType.DAUGHTER;
                    case "chong": return RelationshipType.HUSBAND;
                    case "vo": return RelationshipType.WIFE;
                    case "anh/em trai": return RelationshipType.BROTHER;
                    case "chi/em gai": return RelationshipType.SISTER;
                    case "ong": return RelationshipType.GRAND_FATHER;
                    case "ba": return RelationshipType.GRAND_MOTHER;
                    case "chau trai": return RelationshipType.GRAND_SON;
                    case "chau gai": return RelationshipType.GRAND_DAUGHTER;
                    case "chu/bac": return RelationshipType.UNCLE;
                    case "co/di": return RelationshipType.AUNT;
                    case "chau trai (anh chi em)": return RelationshipType.NEPHEW;
                    case "chau gai (anh chi em)": return RelationshipType.NIECE;
                    case "anh/chi/em ho": return RelationshipType.COUSIN;
                    case "bo chong/vo": return RelationshipType.FATHER_IN_LAW;
                    case "me chong/vo": return RelationshipType.MOTHER_IN_LAW;
                    case "con re": return RelationshipType.SON_IN_LAW;
                    case "con dau": return RelationshipType.DAUGHTER_IN_LAW;
                    case "anh/em re": return RelationshipType.BROTHER_IN_LAW;
                    case "chi/em dau": return RelationshipType.SISTER_IN_LAW;
                    case "khac": return RelationshipType.OTHER;
                    default: return RelationshipType.UNKNOWN;
                }
            }
        });
    }

    

    private void setupAddResidentValidation() {
        // Initially disable add button
        btnAddResident.setDisable(true);

        Runnable validateAddButton = () -> {
            String citizenId = txtResidentCitizenId.getText();
            boolean isValidResidentId = false;
            boolean isValidRelationship = cboRelationship.getValue() != null;

            if (citizenId != null && !citizenId.trim().isEmpty()) {
                // Sử dụng FieldVerifier để kiểm tra CCCD
                FieldVerifier.ValidationResult result = FieldVerifier.verifyCitizenId(citizenId);
                
                isValidResidentId = result.isValid();
                
                // Hiển thị thông báo lỗi nếu có
                if (!result.isValid() && !citizenId.trim().isEmpty()) {
                    showWarningDialog("Cảnh báo", result.getMessage());
                }
            }

            btnAddResident.setDisable(!(isValidResidentId && isValidRelationship));
        };

        // Thêm listener cho textfield CCCD
        txtResidentCitizenId.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                // Chỉ validate khi người dùng đã nhập xong (độ dài = 12)
                if (newVal.trim().length() == 12) {
                    validateAddButton.run();
                } else {
                    btnAddResident.setDisable(true);
                }
            } else {
                btnAddResident.setDisable(true);
            }
        });

        // Thêm listener cho combobox quan hệ
        cboRelationship.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (txtResidentCitizenId.getText() != null && txtResidentCitizenId.getText().trim().length() == 12) {
                validateAddButton.run();
            }
        });
    }

    private void updateResidentCount() {
        if (lblResidentCount != null) {
            lblResidentCount.setText(String.valueOf(residentList != null ? residentList.size() : 0));
        }
    }
    
    @FXML
    private void handleAddResident() {
        String txtCitizenId = txtResidentCitizenId.getText().trim();
        RelationshipType selectedRelationship = cboRelationship.getValue();
        
        // Validate input
        if (txtCitizenId.isEmpty()) {
            showWarningDialog("Canh bao", "Vui long nhap CCCD thanh vien.");
            return;
        }
        
        if (selectedRelationship == null) {
            showWarningDialog("Canh bao", "Vui long chon quan he voi chu ho.");
            return;
        }
        try {
            Resident residentToAdd = residentService.getResidentByCitizenId(txtCitizenId);
            
            // Check if resident already belongs to this household
            if (residentToAdd.getHouseholdId() == household.getId()) {
                showWarningDialog("Canh bao", "Thanh vien nay da thuoc ho khau hien tai.");
                txtResidentCitizenId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Check if resident belongs to another household
            if (residentToAdd.getHouseholdId() != 0) {
                showWarningDialog("Canh bao", 
                    String.format("Thanh vien '%s' da thuoc ho khau khac (ID: %d). " +
                                "Khong the them vao ho khau nay.", 
                                residentToAdd.getFullName(), 
                                residentToAdd.getHouseholdId()));
                txtResidentCitizenId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Check for duplicate in current resident list (additional safety check)
            boolean isDuplicate = residentList.stream()
                    .anyMatch(resident -> resident.getCitizenId().equals(txtCitizenId));
            
            if (isDuplicate) {
                showWarningDialog("Canh bao", "Thanh vien nay da co trong danh sach.");
                txtResidentCitizenId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xac nhan them thanh vien", 
                String.format("Ban co chac chan muon them thanh vien '%s' vao ho khau nay voi quan he '%s'?", 

                        residentToAdd.getFullName(), 

                        cboRelationship.getConverter().toString(selectedRelationship))
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //update local + server resident
                residentToAdd.setHouseholdId(household.getId());
                residentToAdd.setRelationship(selectedRelationship);
                residentToAdd.setHouseholdHead(false);
                residentService.updateResident(residentToAdd);
                
                // Add to local list and refresh UI
                residentList.add(residentToAdd);
                lblResidentCount.setText(String.valueOf(residentList.size()));
                
                //update local + server household
                household.addResident(residentToAdd);
                householdController.addResidentToHousehold(household, txtCitizenId);
                
                //update view household dialog
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                
                // Clear input fields
                txtResidentCitizenId.clear();
                cboRelationship.setValue(null);
                
                // Notify listener if set
                if (changeListener != null) {
                    changeListener.onResidentAdded(residentToAdd);
                }
                
                showInfoDialog("Thong bao", 
                    String.format("Da them thanh vien '%s' vao ho khau thanh cong.", 
                                 residentToAdd.getFullName()));
            }
            
        } catch (ServiceException e) {
            showWarningDialog("Loi", 
                String.format("Khong tim thay thanh vien voi ID '%s'. Vui long kiem tra lai.", txtCitizenId));
            txtResidentCitizenId.clear();
            cboRelationship.setValue(null);
        } catch (Exception e) {
            showWarningDialog("Loi", "Co loi xay ra khi them thanh vien: " + e.getMessage());
            txtResidentCitizenId.clear();
            cboRelationship.setValue(null);
        }
    }
    
    private void setupColumns() {
        // Set up table columns first
        colResidentId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colFullName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender().toString()));
        colDateOfBirth.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateOfBirth().toString()));
        colIdCard.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCitizenId()));
        colRelationship.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRelationship().toString()));
        colIsHead.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId() == household.getOwnerId()  ? "Chủ hộ" : ""));

        // Add double click handler to copy citizen ID
        tblResidents.setRowFactory(tv -> {
            TableRow<Resident> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Resident resident = row.getItem();
                    String citizenId = resident.getCitizenId();
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(citizenId);
                    clipboard.setContent(content);
                    
                    // Show notification
                    showInfoDialog("Thông báo", 
                        String.format("Đã sao chép CCCD: %s của %s vào clipboard", 
                            citizenId, resident.getFullName()));
                }
            });
            return row;
        });
    }
    
    private void setupColumnStyles() {
        // Set column widths
        colResidentId.setPrefWidth(50);
        colFullName.setPrefWidth(150);
        colGender.setPrefWidth(80);
        colDateOfBirth.setPrefWidth(100);
        colIdCard.setPrefWidth(120);
        colRelationship.setPrefWidth(100);
        colIsHead.setPrefWidth(80);

        // Set column styles
        String columnStyle = "-fx-alignment: CENTER;";
        colResidentId.setStyle(columnStyle);
        colGender.setStyle(columnStyle);
        colDateOfBirth.setStyle(columnStyle);
        colIdCard.setStyle(columnStyle);
        colRelationship.setStyle(columnStyle);
        colIsHead.setStyle(columnStyle);
    }
    
    private void setupButtonHandlers() {
        // Set up event handlers for buttons
        btnClose.setOnAction(event -> handleClose());
        btnMakeOwner.setOnAction(event -> {
            try {
                handleMakeOwner();
            } catch (Exception e) {
                showWarningDialog("Loi", "Co loi xay ra khi thay doi chu ho: " + e.getMessage());
            }
        });
        btnRemoveResident.setOnAction(event -> {
            try {
                handleRemoveResident();
            } catch (Exception e) {
                showWarningDialog("Loi", "Co loi xay ra khi xoa thanh vien: " + e.getMessage());
            }
        });
        btnAddResident.setOnAction(event -> handleAddResident());
        
        // Add Enter key support for resident ID text field
        txtResidentCitizenId.setOnAction(event -> {

            if (!btnAddResident.isDisabled()) {

                handleAddResident();

            }

        });
    }
    
    @FXML
    private void handleMakeOwner() throws ServiceException, HouseholdNotExist, HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, SQLException {
        Resident selectedResident = tblResidents.getSelectionModel().getSelectedItem();
        if (selectedResident == null) {
            showWarningDialog("Loi", "Vui long chon mot thanh vien de dat lam chu ho.");
            return;
        }
        
        if (selectedResident.isHouseholdHead()) {
            showWarningDialog("Thong bao", "Thanh vien nay da lam chu ho.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xac nhan thay doi chu ho", 
            String.format("Ban co chac chan muon dat '%s' lam chu ho moi?\n\nHanh dong nay se thay doi chu ho hien tai.", 
                         selectedResident.getFullName())
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update household head status
            for (Resident resident : residentList) {
            	if (resident.isHouseholdHead()) {
            		System.out.println("before: hh head = "+resident.getFullName());
            		resident.setHouseholdHead(false);
            		residentService.updateResident(resident);
            	}
            }
            selectedResident.setHouseholdHead(true);
            residentService.updateResident(selectedResident);
            
            household.setOwnerId(selectedResident.getId());
            //TODO :ddd
            
            household.setOwnerName(selectedResident.getFullName());
            
            householdController.updateHousehold(household, household.getHouseNumber());
            
            // Refresh table and update UI
            refreshTable();
            updateHouseholdInfo();
            
          //update view household dialog
			if (onSuccessCallback != null) {
				onSuccessCallback.run();
			}
            
            // Notify listener if set
            if (changeListener != null) {
                changeListener.onResidentOwnerChanged(selectedResident);
            }
            
            showInfoDialog("Thong bao", "Da cap nhat chu ho moi thanh cong.");
        }
    }
    
    @FXML
    private void handleRemoveResident() throws HouseholdNotExist, ServiceException, SQLException {
        Resident selectedResident = tblResidents.getSelectionModel().getSelectedItem();
        
        if (selectedResident == null) {
            showWarningDialog("Loi", "Vui long chon mot thanh vien de xoa.");
            return;
        }
        
        // Special case: Last resident (who must be household head) - Delete entire household
        if (residentList.size() == 1 && selectedResident.isHouseholdHead()) {
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xac nhan xoa thanh vien cuoi cung, dong thoi xoa ho khau nay!", 
                String.format("Ban co chac chan muon xoa ho khau?\n\nHanh dong nay khong the hoan tac.")
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Remove resident from household (set household ID to 0)
                    selectedResident.setHouseholdId(0);
                    selectedResident.setHouseholdHead(false);
                    selectedResident.setRelationship(RelationshipType.UNKNOWN);
                    residentService.updateResident(selectedResident);
                    
                    // Delete the household from database (you'll need a HouseholdController for this)
                     householdController.deleteHousehold(household.getId());
                    
                    // Clear the local resident list
                    residentList.clear();
                    
                    // Update UI
                    lblResidentCount.setText("0");
                    if (txtHouseholdSize != null) {
                    	//TODO 
//                        txtHouseholdSize.setText("0");
                    }
                    
                    // Notify listener if set
                    if (changeListener != null) {
                        changeListener.onResidentRemoved(selectedResident);
                    }
                    
                 	//update view household dialog
        			if (onSuccessCallback != null) {
        				onSuccessCallback.run();
        			}
                    
                    showInfoDialog("Thong bao", "Da xoa ho khau thanh cong.");
                    
                    // Close the dialog since household no longer exists
                    handleClose();
                    
                } catch (Exception e) {
                    showWarningDialog("Loi", "Co loi xay ra khi xoa ho khau: " + e.getMessage());
                }
            }
            return; // Exit method after handling this case
        }
        
        
        if (selectedResident.isHouseholdHead()) {
            showWarningDialog("Khong the xoa", "Khong the xoa chu ho. Vui long chon chu ho moi truoc khi xoa.");
            return;
        }
        
        if (residentList.size() <= 1) {
            showWarningDialog("Khong the xoa", "Khong the xoa thanh vien cuoi cung trong ho khau.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xac nhan xoa thanh vien", 
            String.format("Ban co chac chan muon xoa thanh vien '%s' khoi ho khau?\n\nHanh dong nay khong the hoan tac.", 
                         selectedResident.getFullName())
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove resident from list + household
        	selectedResident.setHouseholdId(0);
        	selectedResident.setRelationship(RelationshipType.UNKNOWN);
        	try {
				residentService.updateResident(selectedResident);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	household.removeResident(selectedResident.getCitizenId());
        	householdController.removeResident(household, selectedResident.getCitizenId()); //rm trong db
        	
            residentList.remove(selectedResident); //rm trong cache
            
            //TODO
//            txtHouseholdSize.setText(memberList.size()+"");
            
            
            // Update resident count
            lblResidentCount.setText(String.valueOf(residentList.size()));
            
            
          //update view household dialog
			if (onSuccessCallback != null) {
				onSuccessCallback.run();
			}
            
			
            // Notify listener if set
            if (changeListener != null) {
                changeListener.onResidentRemoved(selectedResident);
            }
            
            showInfoDialog("Thong bao", "Da xoa thanh vien thanh cong.");
        }
    }
    
    public void setResidents(List<Resident> residents) {
        residentList.clear();
        if (residents != null) {
            residentList.addAll(residents);
            tblResidents.setItems(residentList);  // Set the items to the table view
            lblResidentCount.setText(String.valueOf(residents.size()));
        } else {
            lblResidentCount.setText("0");
        }
        
        // Make sure the table is refreshed
        tblResidents.refresh();
    }
    
    // Get the updated resident list (useful if parent needs to sync changes)
	public List<Resident> getResidents() {
	    return residentList;
	}

	public void setHouseholdInfo(String ownerName) {
        String info = String.format("Chu ho: %s", ownerName != null ? ownerName : "N/A");
        lblHouseholdInfo.setText(info);
    }
    
	//update the household info line in the head of the dialog
    private void updateHouseholdInfo() {
        // Find current household head and update display
        Resident currentHead = residentList.stream()
                                      .filter(Resident::isHouseholdHead)
                                      .findFirst()
                                      .orElse(null);
        
        if (currentHead != null) {
            setHouseholdInfo(currentHead.getFullName());
        }
    }
    
    public void setChangeListener(ResidentChangeListener listener) {
        this.changeListener = listener;
    }
    
    private void showResidentDetails(Resident resident) {
        String details = String.format(
            "Thong tin chi tiet:\n\n" +
            "ID: %s\n" +
            "Ho ten: %s\n" +
            "Gioi tinh: %s\n" +
            "Ngay sinh: %s\n" +
            "CCCD/CMND: %s\n" +
            "Quan he voi chu ho: %s\n" +
            "Nghe nghiep: %s\n" +
            "La chu ho: %s",
            resident.getId(),
            resident.getFullName(),
            resident.getGender(),
            resident.getDateOfBirth(),
            resident.getIdCard(),
            resident.getRelationship(),
            resident.getOccupation() != null ? resident.getOccupation() : "Chua cap nhat",
            resident.isHouseholdHead() ? "Co" : "Khong"
        );
        
        showInfoDialog("Chi tiet thanh vien", details);
    }
    
    @FXML
    private void handleClose() {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
    
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(400);
        alert.setResizable(true);
        alert.showAndWait();
    }
    
    private void showWarningDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(400);
        alert.setResizable(true);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(400);
        alert.setResizable(true);
        return alert.showAndWait();
    }
    
    public void refreshTable() {
        tblResidents.refresh();
    }
    
    public void setHousehold(Household household) {
        this.household = household;
        
        // Update UI with household info
        if (household != null) {
            // Set household info
            try {
				if (household.getOwnerName() != null) {
				    setHouseholdInfo(household.getOwnerName());
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // Load members if available
            if (household.getResidents() != null) {
                residentList.clear();
                residentList.addAll(household.getResidents());
                tblResidents.setItems(residentList);
            }
            
            // Update resident count
            updateResidentCount();
            
            // Initialize button states
            btnMakeOwner.setDisable(true);
            btnRemoveResident.setDisable(true);
        }
    }

	public Household getHousehold() {
		return household;
	}

	public Resident getSelectedResident() {
        return tblResidents.getSelectionModel().getSelectedItem();
    }
    
    public TextField getTxtHouseholdSize() {
		return txtHouseholdSize;
	}

	public void setTxtHouseholdSize(TextField txtHouseholdSize) {
		this.txtHouseholdSize = txtHouseholdSize;
	}

	public HouseholdController getHouseholdController() {
		return householdController;
	}

	public void setHouseholdController(HouseholdController householdController) {
		this.householdController = householdController;
	}

}