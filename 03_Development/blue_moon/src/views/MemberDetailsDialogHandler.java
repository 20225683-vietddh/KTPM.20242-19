package views;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Household;
import models.Member;
import services.MemberService;
import services.MemberServiceImpl;
import utils.RelationshipHelper;
import utils.RelationshipType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import controllers.HouseholdController;
import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.MemberNotFoundException;
import exception.ServiceException;

public class MemberDetailsDialogHandler {
	private HouseholdController householdController;
	private MemberService memberService = new MemberServiceImpl();
	
	private Household household;
	private ObservableList<Member> memberList = FXCollections.observableArrayList();

	private Runnable onSuccessCallback;

	private TextField txtHouseholdSize;

	 
    @FXML private Label lblHouseholdInfo;
    @FXML private Label lblMemberCount;
    @FXML private TableView<Member> tblMembers;
    @FXML private TableColumn<Member, String> colMemberId;
    @FXML private TableColumn<Member, String> colFullName;
    @FXML private TableColumn<Member, String> colGender;
    @FXML private TableColumn<Member, String> colDateOfBirth;
    @FXML private TableColumn<Member, String> colIdCard;
    @FXML private TableColumn<Member, String> colRelationship;
    @FXML private TableColumn<Member, String> colIsHead;
    @FXML private Button btnClose;
    @FXML private Button btnMakeOwner;
    @FXML private Button btnRemoveMember;
    @FXML private Button btnAddMember;
    @FXML private TextField txtMemberId;
    
    
    
   
	public Runnable getOnSuccessCallback() {
		return onSuccessCallback;
	}

	public void setOnSuccessCallback(Runnable onSuccessCallback) {
		this.onSuccessCallback = onSuccessCallback;
	}


    
    public MemberDetailsDialogHandler() {
    	
    };
    

	
    
	// Interface for callback when changes are made
    public interface MemberChangeListener {
        void onMemberOwnerChanged(Member newOwner);
        void onMemberRemoved(Member removedMember);
        void onMemberAdded(Member newMember);
    }
    
    private MemberChangeListener changeListener;
    
    @FXML
    private void initialize() {
        setupTableColumns();
        tblMembers.setItems(memberList);
        
        // Initially disable buttons until a member is selected
        btnMakeOwner.setDisable(true);
        btnRemoveMember.setDisable(true);
        
        // Enable/disable buttons based on selection
        tblMembers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            btnMakeOwner.setDisable(!hasSelection || newSelection.isHouseholdHead());
            btnRemoveMember.setDisable(!hasSelection);
        });
    }
    
    @FXML
    private void handleAddMember() {
        String memberId = txtMemberId.getText().trim();
        
        // Validate input
        if (memberId.isEmpty()) {
            showWarningDialog("Lỗi", "Vui lòng nhập ID thành viên.");
            return;
        }
        
        try {
            // Check if member exists
            Member memberToAdd = memberService.getMemberById(memberId);
            
            // Check if member already belongs to this household
            if (memberToAdd.getHouseholdId() == household.getId()) {
                showWarningDialog("Lỗi", "Thành viên này đã thuộc hộ khẩu hiện tại.");
                txtMemberId.clear();
                return;
            }
            
            // Check if member belongs to another household
            if (memberToAdd.getHouseholdId() != 0) {
                showWarningDialog("Lỗi", 
                    String.format("Thành viên '%s' đã thuộc hộ khẩu khác (ID: %d). " +
                                "Không thể thêm vào hộ khẩu này.", 
                                memberToAdd.getFullName(), 
                                memberToAdd.getHouseholdId()));
                txtMemberId.clear();
                return;
            }
            
            // Check for duplicate in current member list (additional safety check)
            boolean isDuplicate = memberList.stream()
                    .anyMatch(member -> member.getId().equals(memberId));
            
            if (isDuplicate) {
                showWarningDialog("Lỗi", "Thành viên này đã có trong danh sách.");
                txtMemberId.clear();
                return;
            }
            
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xác nhận thêm thành viên", 
                String.format("Bạn có chắc chắn muốn thêm thành viên '%s' vào hộ khẩu này?", 
                             memberToAdd.getFullName())
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
            	//update local + server member
                memberToAdd.setHouseholdId(household.getId());
                if (memberToAdd.getRelationship() == null ) {
                    
                    RelationshipType relationship = RelationshipHelper.determineRelationship(memberToAdd.getDateOfBirth().toString(), memberToAdd.getGender());
                    memberToAdd.setRelationship(relationship);
                }
                memberToAdd.setHouseholdHead(false);
                memberService.updateMember(memberToAdd);
                
                // Add to local list and refresh UI
                memberList.add(memberToAdd);
                lblMemberCount.setText(String.valueOf(memberList.size()));
                
                //update local + server household
                household.addMember(memberToAdd);
                householdController.addMemberToHousehold(household, memberId);
                
                //TODO
//                txtHouseholdSize.setText(memberList.size()+"");
                //update view household dialog
    			if (onSuccessCallback != null) {
    				onSuccessCallback.run();
    			}
    			
    			
                // Clear input field
                txtMemberId.clear();
                
                // Notify listener if set
                if (changeListener != null) {
                    changeListener.onMemberAdded(memberToAdd);
                }
                
                showInfoDialog("Thành công", 
                    String.format("Đã thêm thành viên '%s' vào hộ khẩu thành công.", 
                                 memberToAdd.getFullName()));
            }
            
        } catch (ServiceException e) {
            showWarningDialog("Lỗi", 
                String.format("Không tìm thấy thành viên với ID '%s'. Vui lòng kiểm tra lại.", memberId));
            txtMemberId.clear();
        } catch (Exception e) {
            showWarningDialog("Lỗi", "Có lỗi xảy ra khi thêm thành viên: " + e.getMessage());
            txtMemberId.clear();
        };
        
        // Add Enter key support for member ID text field
        txtMemberId.setOnAction(e -> handleAddMember());
    }
    
    private void setupTableColumns() {
        // Set up table columns with cell value factories
        colMemberId.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getId()));
            
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
        colMemberId.setStyle("-fx-alignment: CENTER;");
        colGender.setStyle("-fx-alignment: CENTER;");
        colDateOfBirth.setStyle("-fx-alignment: CENTER;");
        colIsHead.setStyle("-fx-alignment: CENTER;");
        
        // Make table rows selectable with nice styling
        tblMembers.setRowFactory(tv -> {
            TableRow<Member> row = new javafx.scene.control.TableRow<Member>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Member selectedMember = row.getItem();
                    showMemberDetails(selectedMember);
                }
            });
            return row;
        });
    }
    
    @FXML
    private void handleMakeOwner() throws ServiceException, HouseholdNotExist, HouseholdAlreadyExistsException, MemberNotFoundException, InvalidHouseholdDataException, SQLException {
        Member selectedMember = tblMembers.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarningDialog("Lỗi", "Vui lòng chọn một thành viên để đặt làm chủ hộ.");
            return;
        }
        
        if (selectedMember.isHouseholdHead()) {
            showWarningDialog("Thông báo", "Thành viên này đã là chủ hộ.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xác nhận thay đổi chủ hộ", 
            String.format("Bạn có chắc chắn muốn đặt '%s' làm chủ hộ mới?\n\nViệc này sẽ thay đổi chủ hộ hiện tại.", 
                         selectedMember.getFullName())
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update household head status
            for (Member member : memberList) {
            	if (member.isHouseholdHead()) {
            		System.out.println("before: hh head = "+member.getFullName());
            		member.setHouseholdHead(false);
            		memberService.updateMember(member);
            	}
            }
            selectedMember.setHouseholdHead(true);
            memberService.updateMember(selectedMember);
            
            household.setOwnerId(selectedMember.getId());
            household.setOwnerName(selectedMember.getFullName());
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
                changeListener.onMemberOwnerChanged(selectedMember);
            }
            
            showInfoDialog("Thành công", "Đã cập nhật chủ hộ mới thành công.");
        }
    }
    
    @FXML
    private void handleRemoveMember() throws HouseholdNotExist, ServiceException, SQLException {
        Member selectedMember = tblMembers.getSelectionModel().getSelectedItem();
        
        if (selectedMember == null) {
            showWarningDialog("Lỗi", "Vui lòng chọn một thành viên để xóa.");
            return;
        }
        
        // Special case: Last member (who must be household head) - Delete entire household
        if (memberList.size() == 1 && selectedMember.isHouseholdHead()) {
            // Confirm action
            Optional<ButtonType> result = showConfirmDialog(
                "Xác nhận xóa thành viên cuối cùng, đồng thời xóa hộ khẩu này!", 
                String.format("Bạn có chắc chắn muốn xóa hộ khẩu?\n\nHành động này không thể hoàn tác.")
            );
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Remove member from household (set household ID to 0)
                    selectedMember.setHouseholdId(0);
                    selectedMember.setHouseholdHead(false);
                    selectedMember.setRelationship(RelationshipType.UNKNOWN);
                    memberService.updateMember(selectedMember);
                    
                    // Delete the household from database (you'll need a HouseholdController for this)
                     householdController.deleteHousehold(household.getId());
                    
                    // Clear the local member list
                    memberList.clear();
                    
                    // Update UI
                    lblMemberCount.setText("0");
                    if (txtHouseholdSize != null) {
                    	//TODO 
//                        txtHouseholdSize.setText("0");
                    }
                    
                    // Notify listener if set
                    if (changeListener != null) {
                        changeListener.onMemberRemoved(selectedMember);
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
        
        
        if (selectedMember.isHouseholdHead()) {
            showWarningDialog("Không thể xóa", "Không thể xóa chủ hộ. Vui lòng chỉ định chủ hộ mới trước khi xóa.");
            return;
        }
        
        if (memberList.size() <= 1) {
            showWarningDialog("Không thể xóa", "Không thể xóa thành viên cuối cùng trong hộ khẩu.");
            return;
        }
        
        // Confirm action
        Optional<ButtonType> result = showConfirmDialog(
            "Xác nhận xóa thành viên", 
            String.format("Bạn có chắc chắn muốn xóa thành viên '%s' khỏi hộ khẩu?\n\nHành động này không thể hoàn tác.", 
                         selectedMember.getFullName())
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove member from list + household
        	selectedMember.setHouseholdId(0);
        	selectedMember.setRelationship(RelationshipType.UNKNOWN);
        	try {
				memberService.updateMember(selectedMember);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	household.removeMember(selectedMember.getId());
        	householdController.removeMember(household, selectedMember.getId()); //rm trong db
        	
            memberList.remove(selectedMember); //rm trong cache
            
            //TODO
//            txtHouseholdSize.setText(memberList.size()+"");
            
            
            // Update member count
            lblMemberCount.setText(String.valueOf(memberList.size()));
            
            
          //update view household dialog
			if (onSuccessCallback != null) {
				onSuccessCallback.run();
			}
            
			
            // Notify listener if set
            if (changeListener != null) {
                changeListener.onMemberRemoved(selectedMember);
            }
            
            showInfoDialog("Thành công", "Đã xóa thành viên thành công.");
        }
    }
    
    public void setMembers(List<Member> members) {
        memberList.clear();
        if (members != null) {
            memberList.addAll(members);
            lblMemberCount.setText(String.valueOf(members.size()));
        } else {
            lblMemberCount.setText("0");
        }
    }
    
    // Get the updated member list (useful if parent needs to sync changes)
	public List<Member> getMembers() {
	    return memberList;
	}

	public void setHouseholdInfo(String householdNumber, String ownerName) {
        String info = String.format("Hộ khẩu: %s - Chủ hộ: %s", 
                                   householdNumber != null ? householdNumber : "N/A",
                                   ownerName != null ? ownerName : "N/A");
        lblHouseholdInfo.setText(info);
    }
    
	//update the household info line in the head of the dialog
    private void updateHouseholdInfo() {
        // Find current household head and update display
        Member currentHead = memberList.stream()
                                      .filter(Member::isHouseholdHead)
                                      .findFirst()
                                      .orElse(null);
        
        if (currentHead != null) {
            // Extract household number from current label (if needed)
            String currentText = lblHouseholdInfo.getText();
            String householdNumber = "N/A";
            if (currentText.contains("Hộ khẩu: ") && currentText.contains(" - ")) {
                householdNumber = currentText.substring(
                    currentText.indexOf("Hộ khẩu: ") + 9,
                    currentText.indexOf(" - ")
                );
            }
            
            setHouseholdInfo(householdNumber, currentHead.getFullName());
        }
    }
    
    public void setChangeListener(MemberChangeListener listener) {
        this.changeListener = listener;
    }
    
    private void showMemberDetails(Member member) {
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
            member.getId(),
            member.getFullName(),
            member.getGender(),
            member.getDateOfBirth(),
            member.getIdCard(),
            member.getRelationship(),
            member.getOccupation() != null ? member.getOccupation() : "Chưa cập nhật",
            member.isHouseholdHead() ? "Có" : "Không"
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
        tblMembers.refresh();
    }
    
    public void setHousehold(Household household) {
		this.household = household;
	}

	public Household getHousehold() {
		return household;
	}

	public Member getSelectedMember() {
        return tblMembers.getSelectionModel().getSelectedItem();
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