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
    @FXML private TextField txtResidentId;
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

        

        // Add all enum values except UNKNOWN (or include it if needed)

        for (RelationshipType type : RelationshipType.values()) {

            if (type != RelationshipType.UNKNOWN) { // Skip UNKNOWN if you don't want it as an option

                relationships.add(type);

            }

        }

        

        cboRelationship.setItems(relationships);

        

        // Set custom string converter if needed to display Vietnamese text

        cboRelationship.setConverter(new javafx.util.StringConverter<RelationshipType>() {

            @Override

            public String toString(RelationshipType relationshipType) {

                if (relationshipType == null) return "";

                

                // Convert enum to Vietnamese display text

                switch (relationshipType) {

                    case FATHER: return "Cha";

                    case MOTHER: return "Mẹ";

                    case SON: return "Con trai";

                    case DAUGHTER: return "Con gái";

                    case HUSBAND: return "Chồng";

                    case WIFE: return "Vợ";

                    case BROTHER: return "Anh/Em trai";

                    case SISTER: return "Chị/Em gái";

                    case GRANDFATHER: return "Ông";

                    case GRANDMOTHER: return "Bà";

                    case GRANDSON: return "Cháu trai";

                    case GRANDDAUGHTER: return "Cháu gái";

                    case UNCLE: return "Chú/Bác";

                    case AUNT: return "Cô/Dì";

                    case NEPHEW: return "Cháu trai (anh chị em)";

                    case NIECE: return "Cháu gái (anh chị em)";

                    case COUSIN: return "Anh/Chị/Em họ";

                    case FATHER_IN_LAW: return "Bố chồng/vợ";

                    case MOTHER_IN_LAW: return "Mẹ chồng/vợ";

                    case SON_IN_LAW: return "Con rể";

                    case DAUGHTER_IN_LAW: return "Con dâu";

                    case BROTHER_IN_LAW: return "Anh/Em rể";

                    case SISTER_IN_LAW: return "Chị/Em dâu";

                    case OTHER: return "Khác";

                    default: return relationshipType.toString();

                }

            }



            @Override

            public RelationshipType fromString(String string) {

                // This method is used when converting back from string to enum

                // Usually not needed for ComboBox, but implement if necessary

                return null;

            }

        });

        

        // Set prompt text

        cboRelationship.setPromptText("Chọn mối quan hệ với chủ hộ");

    }

    

    private void setupAddResidentValidation() {

        // Initially disable add button
        
        // Disable add button if relationship not selected
        btnAddResident.setDisable(true);
        // Add listeners for validation

		Runnable validateAddButton = () -> {

			boolean isValidResidentId = txtResidentId.getText() != null && !txtResidentId.getText().trim().isEmpty();

			boolean isValidRelationship = cboRelationship.getValue() != null;

			btnAddResident.setDisable(!(isValidResidentId && isValidRelationship));

		};

		txtResidentId.textProperty().addListener((obs, oldVal, newVal) -> {
			validateAddButton.run();

		});

		// Listen to changes in relationship combo box

		cboRelationship.valueProperty().addListener((obs, oldVal, newVal) -> {

			validateAddButton.run();
		});
	}

    private void updateResidentCount() {
        if (lblResidentCount != null) {
            lblResidentCount.setText(String.valueOf(residentList != null ? residentList.size() : 0));
        }
    }
    
    @FXML
    private void handleAddResident() {
        String textId = txtResidentId.getText().trim();
        RelationshipType selectedRelationship = cboRelationship.getValue();
        
        // Validate input
        if (textId.isEmpty()) {
            showWarningDialog("Lỗi", "Vui lòng nhập ID thành viên.");
            return;
        }
        
        if (selectedRelationship == null) {
            showWarningDialog("Lỗi", "Vui lòng chọn quan hệ với chủ hộ.");
            return;
        }
        int residentId  = Integer.parseInt(textId);
        
        try {
            Resident residentToAdd = residentService.getResidentById(residentId);
            
            // Check if resident already belongs to this household
            if (residentToAdd.getHouseholdId() == household.getId()) {
                showWarningDialog("Lỗi", "Thành viên này đã thuộc hộ khẩu hiện tại.");
                txtResidentId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Check if resident belongs to another household
            if (residentToAdd.getHouseholdId() != 0) {
                showWarningDialog("Lỗi", 
                    String.format("Thành viên '%s' đã thuộc hộ khẩu khác (ID: %d). " +
                                "Không thể thêm vào hộ khẩu này.", 
                                residentToAdd.getFullName(), 
                                residentToAdd.getHouseholdId()));
                txtResidentId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Check for duplicate in current resident list (additional safety check)
            boolean isDuplicate = residentList.stream()
                    .anyMatch(resident -> resident.getId() == residentId);
            
            if (isDuplicate) {
                showWarningDialog("Lỗi", "Thành viên này đã có trong danh sách.");
                txtResidentId.clear();
                cboRelationship.setValue(null);
                return;
            }
            
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xác nhận thêm thành viên", 
                String.format("Bạn có chắc chắn muốn thêm thành viên '%s' vào hộ khẩu này với quan hệ '%s'?", 

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
                householdController.addResidentToHousehold(household, residentId);
                
                //update view household dialog
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                
                // Clear input fields
                txtResidentId.clear();
                cboRelationship.setValue(null);
                
                // Notify listener if set
                if (changeListener != null) {
                    changeListener.onResidentAdded(residentToAdd);
                }
                
                showInfoDialog("Thành công", 
                    String.format("Đã thêm thành viên '%s' vào hộ khẩu thành công.", 
                                 residentToAdd.getFullName()));
            }
            
        } catch (ServiceException e) {
            showWarningDialog("Lỗi", 
                String.format("Không tìm thấy thành viên với ID '%s'. Vui lòng kiểm tra lại.", residentId));
            txtResidentId.clear();
            cboRelationship.setValue(null);
        } catch (Exception e) {
            showWarningDialog("Lỗi", "Có lỗi xảy ra khi thêm thành viên: " + e.getMessage());
            txtResidentId.clear();
            cboRelationship.setValue(null);
        }
    }
    
    private void setupColumns() {
        // Set up table columns with cell value factories
        colResidentId.setCellValueFactory(cellData -> 
        //TODO : sua lai neu sau nay nhap vao cccd
            new SimpleStringProperty(cellData.getValue().getIdString()));
            
        colFullName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));
            
        colGender.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGenderString()));
            
        colDateOfBirth.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateOfBirth().toString()));
            
        colIdCard.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getIdCard()));
            
        colRelationship.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRelationshipString()));
            
        colIsHead.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isHouseholdHead() ? "Có" : "Không"));
        
        // Set column styles and alignment
        setupColumnStyles();
    }
    
    private void setupColumnStyles() {
        // Center align some columns
        colResidentId.setStyle("-fx-alignment: CENTER;");
        colGender.setStyle("-fx-alignment: CENTER;");
        colDateOfBirth.setStyle("-fx-alignment: CENTER;");
        colIsHead.setStyle("-fx-alignment: CENTER;");
        
        // Make table rows selectable with nice styling
        tblResidents.setRowFactory(tv -> {
            TableRow<Resident> row = new javafx.scene.control.TableRow<Resident>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Resident selectedResident = row.getItem();
                    showResidentDetails(selectedResident);
                }
            });
            return row;
        });
    }
    
    private void setupButtonHandlers() {
        // Set up event handlers for buttons
        btnClose.setOnAction(event -> handleClose());
        btnMakeOwner.setOnAction(event -> {
            try {
                handleMakeOwner();
            } catch (Exception e) {
                showWarningDialog("Lỗi", "Có lỗi xảy ra khi thay đổi chủ hộ: " + e.getMessage());
            }
        });
        btnRemoveResident.setOnAction(event -> {
            try {
                handleRemoveResident();
            } catch (Exception e) {
                showWarningDialog("Lỗi", "Có lỗi xảy ra khi xóa thành viên: " + e.getMessage());
            }
        });
        btnAddResident.setOnAction(event -> handleAddResident());
        
        // Add Enter key support for resident ID text field
        txtResidentId.setOnAction(event -> {

            if (!btnAddResident.isDisabled()) {

                handleAddResident();

            }

        });
    }
    
    @FXML
    private void handleMakeOwner() throws ServiceException, HouseholdNotExist, HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, SQLException {
        Resident selectedResident = tblResidents.getSelectionModel().getSelectedItem();
        if (selectedResident == null) {
            showWarningDialog("Lỗi", "Vui lòng chọn một thành viên để đặt làm chủ hộ.");
            return;
        }
        
        if (selectedResident.isHouseholdHead()) {
            showWarningDialog("Thông báo", "Thành viên này đã là chủ hộ.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xác nhận thay đổi chủ hộ", 
            String.format("Bạn có chắc chắn muốn đặt '%s' làm chủ hộ mới?\n\nViệc này sẽ thay đổi chủ hộ hiện tại.", 
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
            household.setOwnerName(selectedResident.getFullName());
            householdController.updateHousehold(household);
            
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
            
            showInfoDialog("Thành công", "Đã cập nhật chủ hộ mới thành công.");
        }
    }
    
    @FXML
    private void handleRemoveResident() throws HouseholdNotExist, ServiceException, SQLException {
        Resident selectedResident = tblResidents.getSelectionModel().getSelectedItem();
        
        if (selectedResident == null) {
            showWarningDialog("Lỗi", "Vui lòng chọn một thành viên để xóa.");
            return;
        }
        
        // Special case: Last resident (who must be household head) - Delete entire household
        if (residentList.size() == 1 && selectedResident.isHouseholdHead()) {
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xác nhận xóa thành viên cuối cùng, đồng thời xóa hộ khẩu này!", 
                String.format("Bạn có chắc chắn muốn xóa hộ khẩu?\n\nHành động này không thể hoàn tác.")
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
                    
                    showInfoDialog("Thành công", "Đã xóa hộ khẩu thành công.");
                    
                    // Close the dialog since household no longer exists
                    handleClose();
                    
                } catch (Exception e) {
                    showWarningDialog("Lỗi", "Có lỗi xảy ra khi xóa hộ khẩu: " + e.getMessage());
                }
            }
            return; // Exit method after handling this case
        }
        
        
        if (selectedResident.isHouseholdHead()) {
            showWarningDialog("Không thể xóa", "Không thể xóa chủ hộ. Vui lòng chỉ định chủ hộ mới trước khi xóa.");
            return;
        }
        
        if (residentList.size() <= 1) {
            showWarningDialog("Không thể xóa", "Không thể xóa thành viên cuối cùng trong hộ khẩu.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xác nhận xóa thành viên", 
            String.format("Bạn có chắc chắn muốn xóa thành viên '%s' khỏi hộ khẩu?\n\nHành động này không thể hoàn tác.", 
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
        	
        	household.removeResident(selectedResident.getId());
        	householdController.removeResident(household, selectedResident.getId()); //rm trong db
        	
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
            
            showInfoDialog("Thành công", "Đã xóa thành viên thành công.");
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
        String info = String.format("Chủ hộ: %s", ownerName != null ? ownerName : "N/A");
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
            "Thông tin chi tiết:\n\n" +
            "ID: %s\n" +
            "Họ tên: %s\n" +
            "Giới tính: %s\n" +
            "Ngày sinh: %s\n" +
            "CCCD/CMND: %s\n" +
            "Quan hệ với chủ hộ: %s\n" +
            "Nghề nghiệp: %s\n" +
            "Là chủ hộ: %s",
            resident.getId(),
            resident.getFullName(),
            resident.getGender(),
            resident.getDateOfBirth(),
            resident.getIdCard(),
            resident.getRelationship(),
            resident.getOccupation() != null ? resident.getOccupation() : "Chưa cập nhật",
            resident.isHouseholdHead() ? "Có" : "Không"
        );
        
        showInfoDialog("Chi tiết thành viên", details);
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
            if (household.getOwnerName() != null) {
                setHouseholdInfo(household.getOwnerName());
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