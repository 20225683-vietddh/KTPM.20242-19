package views.chargefee;

import views.BaseScreenHandler;
import views.messages.*;
import models.Fee;
import models.CampaignFee;
import models.Household;
import dto.campaignfee.FeeAmountRecordDTO;
import services.ChargeFeeService;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import utils.ReceiptGenerator;
import java.awt.Desktop;
import java.io.File;

public class CompulsoryChargeFeeHandler extends BaseScreenHandler {
	@FXML private Button btnClose;
    @FXML private Button btnChargeFee;
    @FXML private Label lblHouseNumber;
    @FXML private VBox vbFeeAmounts;
    @FXML private Label lblTotalExpectedAmount;
    @FXML private Label lblTotalPaidAmount;
    @FXML private Label lblOutstandingAmount;
    @FXML private Label lblOverpaidAmount;
    private CampaignFee campaignFee;
    private Household household;
    private ChargeFeeService service;
    
	public CompulsoryChargeFeeHandler(Stage ownerStage, CampaignFee campaignFee, Household household, ChargeFeeService service) throws Exception {
        super(new Stage(), utils.Configs.COMPULSORY_CHARGE_FEE_FORM, utils.Configs.LOGO_PATH, "Các khoản thu bắt buộc");
        loader.setController(this);
        this.campaignFee = campaignFee;
        this.household = household;
        this.service = service;
        this.setContent();  
        this.setScene();    

        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur); 

        this.stage.setOnHidden(e -> parentRoot.setEffect(null));
        this.showPopup(ownerStage);
    }
	
	@FXML
	public void initialize() {
		int totalExpectedAmount = service.countTotalExpectedAmount(campaignFee.getId(), household.getHouseholdId());
        int totalPaidAmount = service.countTotalCompulsoryPaidAmount(campaignFee.getId(), household.getHouseholdId());
        int totalOutstandingAmount = totalExpectedAmount - totalPaidAmount;
		loadForm();
		btnClose.setOnAction(e -> handleClose());
		btnChargeFee.setOnAction(e -> handleChargeFee(totalExpectedAmount, totalOutstandingAmount));
	}
	
	private void loadForm() {
	    vbFeeAmounts.getChildren().clear();
	    lblHouseNumber.setText("Hộ " + household.getHouseNumber());
	    try {
	        List<Fee> compulsoryFees = getCompulsoryFees();
	        if (compulsoryFees.isEmpty()) {
	            Label lblEmpty = new Label("Chưa có khoản thu bắt buộc nào!");
	            lblEmpty.setPrefWidth(760);
	            lblEmpty.setStyle("-fx-font-size: 20px;");
	            vbFeeAmounts.getChildren().add(lblEmpty);
	        } else {
	            for (Fee fee : compulsoryFees) {
	                boolean isExisted = service.isRecordExisted(campaignFee.getId(), household.getHouseholdId(), fee.getId());
	                if (isExisted) {
	                    FeeAmountRecordDTO dto = service.getPaymentRecord(campaignFee.getId(), household.getHouseholdId(), fee.getId());
	                    setupCompulsoryFeeRow(dto.getFeeName(), dto.getFeeId(), dto.getAreas(), dto.getExpectedAmount());
	                }
	            }
	        }
	        int totalExpectedAmount = service.countTotalExpectedAmount(campaignFee.getId(), household.getHouseholdId());
	        int totalPaidAmount = service.countTotalCompulsoryPaidAmount(campaignFee.getId(), household.getHouseholdId());
	        int totalOutstandingAmount = totalExpectedAmount - totalPaidAmount;
	        int totalOverpaidAmount = totalPaidAmount - totalExpectedAmount;
	        lblTotalExpectedAmount.setText(utils.Utils.formatCurrency(totalExpectedAmount) + " đồng.");
	        lblTotalPaidAmount.setText(utils.Utils.formatCurrency(totalPaidAmount) + " đồng.");
	        if (totalOutstandingAmount > 0) {
	        	lblOutstandingAmount.setText(utils.Utils.formatCurrency(totalOutstandingAmount) + " đồng.");
	        	lblOverpaidAmount.setText("0 đồng.");
	        	btnChargeFee.setDisable(false);
	        } else {
	        	btnChargeFee.setDisable(true);
	        	lblOutstandingAmount.setText("0 đồng");
	        	lblOverpaidAmount.setText(utils.Utils.formatCurrency(totalOverpaidAmount) + " đồng.");
	        }
	    } catch (Exception e) {
	        ErrorDialog.showError("Lỗi hệ thống", "Không thể tải lại dữ liệu!");
	        e.printStackTrace();
	    }
	}

	
	private void handleClose() {
		Stage stage = (Stage) btnClose.getScene().getWindow();
		stage.close();
	}
	
	private List<Fee> getCompulsoryFees() {
		List<Fee> fees = campaignFee.getFees();
		List<Fee> compulsoryFees = new ArrayList<>();	
		for (Fee fee : fees) {
			if (fee.getIsMandatory()) {
				compulsoryFees.add(fee);
			}
		}
	    return compulsoryFees;
	}
	
	private List<Integer> getCompulsoryFeeIds() {
		List<Fee> compulsoryFees = getCompulsoryFees();
		List<Integer> compulsoryFeeIds = new ArrayList<>();
		for (Fee fee : compulsoryFees) {
			compulsoryFeeIds.add(fee.getId());
		}
		return compulsoryFeeIds;
	}
	
	private void setupCompulsoryFeeRow(String feeName, int feeId, int areas, int expectedAmount) {
		Label lblFeeName = new Label(feeName);
        lblFeeName.setPrefWidth(360);
        lblFeeName.setMaxWidth(360);
        lblFeeName.setStyle("-fx-font-size: 18px;");
        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
        
        TextField tfExpectedAmount = new TextField(utils.Utils.formatCurrency(expectedAmount));
        tfExpectedAmount.setPrefWidth(120);
        tfExpectedAmount.setStyle("-fx-font-size: 18px;");
        tfExpectedAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        HBox.setMargin(tfExpectedAmount, new Insets(10, 40, 10, 40));
        tfExpectedAmount.setDisable(true);
        
        Button btnDetailedAction = null;
        if (feeName.equals("Phí quản lý chung cư") || feeName.equals("Phí dịch vụ chung cư") || feeName.equals("Phí gửi xe")) {
        	btnDetailedAction = new Button("Xem chi tiết");
            btnDetailedAction.setStyle("-fx-background-color:  linear-gradient(to right, #43A5DC, #FF7BAC); -fx-font-size: 18px; -fx-text-fill: white; -fx-cursor: hand;");
            btnDetailedAction.setPrefWidth(145);
            HBox.setMargin(btnDetailedAction, new Insets(10, 25, 10, 25));
            btnDetailedAction.setOnAction(e -> handleViewDetail(feeName, areas));
        } else {
			btnDetailedAction = new Button("Nhập số tiền");
			btnDetailedAction.setStyle("-fx-background-color:  linear-gradient(to right, #43A5DC, #FF7BAC); -fx-font-size: 18px; -fx-text-fill: white; -fx-cursor: hand;");
			btnDetailedAction.setPrefWidth(145);
			HBox.setMargin(btnDetailedAction, new Insets(10, 25, 10, 25));
			btnDetailedAction.setOnAction(e -> handleEnterExpectedAmount(feeName, feeId));
        }       

        HBox row = new HBox(lblFeeName, tfExpectedAmount, btnDetailedAction);
        row.setStyle("-fx-background-color: #F8F8F8;");
        row.setPrefWidth(760);
        row.setAlignment(Pos.CENTER_LEFT);
        vbFeeAmounts.getChildren().add(row);
	}
	
	private void handleViewDetail(String feeName, int areas) {
		if (feeName.equals("Phí quản lý chung cư")) {
			CompulsoryFeeInformationDialog.showNotification("Chi tiết " + feeName, 
					"Theo quy định của ban quản lý chung cư Blue Moon, "
					+ feeName + " được tính bằng công thức:\n\n"
					+ "Diện tích căn hộ (mét vuông) ❌ 7.000 (đồng/mét vuông)\n\n"
					+ "Diện tích căn hộ của bạn là: " + areas + " (mét vuông)");
		} else if (feeName.equals("Phí dịch vụ chung cư")) {
			CompulsoryFeeInformationDialog.showNotification("Chi tiết " + feeName, 
					"Theo quy định của ban quản lý chung cư Blue Moon, "
					+ feeName + " được tính bằng công thức:\n\n"
					+ "Diện tích căn hộ (mét vuông) ❌ 10.000 (đồng/mét vuông)\n\n"
					+ "Diện tích căn hộ của bạn là: " + areas + " (mét vuông)");
		} else if (feeName.equals("Phí gửi xe")) {
			// TODO: Code
		}
	}
	
	private void handleEnterExpectedAmount(String feeName, int feeId) {
		while (true) {
            Optional<String> result = InputDialog.getInput("10000", "Nhập số tiền cần thu", "Hãy nhập số tiền cần thu cho khoản " + feeName + ":", "Số tiền: ");
            if (!result.isPresent()) {
                return;
            }
            String input = result.get().trim();
            if (!input.matches("\\d+")) {
                ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị số.");
                continue;
            }
            try {
				int expectedAmount = Integer.parseInt(input);
				if (expectedAmount < 0) {
					ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị nguyên dương.");
					continue;
				}
				service.updateRecord(campaignFee.getId(), household.getHouseholdId(), feeId, expectedAmount);
				loadForm();
				break;
            } catch (NumberFormatException e) {
            	ErrorDialog.showError("Số tiền không hợp lệ", "Bạn phải nhập một giá trị nguyên dương.");
            } catch (SQLException e) {
            	e.printStackTrace();
            	ErrorDialog.showError("Lỗi hệ thống", "Không thể lưu số tiền cho khoản " + feeName + " vào CSDL.");
            } catch (Exception e) {
            	ErrorDialog.showError("Lỗi hệ thống", "Không thể tải lại form các khoản thu bắt buộc.");
            	e.printStackTrace();
            }
		}
	}
	
	private void handleChargeFee(int totalExpectedAmount, int totalOutstandingAmount) {
		try {
			String option = ConfirmationDialog.getOption("Bạn có chắc chắn hộ dân đã nộp đủ toàn bộ số tiền còn thiếu " + utils.Utils.formatCurrency(totalOutstandingAmount) + " đồng không?");
			switch (option) {
			case "YES":
				service.updatePaidAmount(campaignFee.getId(), household.getHouseholdId(), getCompulsoryFeeIds());
				Map<String, Integer> feeWithExpectedAmounts = this.getFeeNameWithExpectedAmount();
				ReceiptGenerator generator = new ReceiptGenerator();
				String filePath = "output/bien_lai_cac_khoan_bat_buoc_ho_dan_" + household.getHouseholdId() + "_dot_thu_" + campaignFee.getId() + ".docx";
				generator.generateReceipt(filePath,
                        household.getHouseNumber(),
                        campaignFee.getName(),
                        feeWithExpectedAmounts,
                        totalExpectedAmount,
                        "Kế toán chung cư Blue Moon"
                        );
				InformationDialog.showNotification("Thành công", "Xác nhận thu tiền thành công. Biên lai đã được xuất dưới dạng file .docx.");
				Desktop.getDesktop().open(new File(filePath));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể cập nhật số tiền đã được thu trong CSDL!");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể tải biên lai nộp tiền!");
		} finally {
			handleClose();
		}
	}
	
	private Map<String, Integer> getFeeNameWithExpectedAmount() {
		List<Fee> compulsoryFees = getCompulsoryFees();
		Map<String, Integer> feeNameWithExpectedAmount = new HashMap<>();
		for (Fee fee : compulsoryFees) {
			try {
				boolean isExisted = service.isRecordExisted(campaignFee.getId(), household.getHouseholdId(), fee.getId());
				if (isExisted) {
					FeeAmountRecordDTO dto = service.getPaymentRecord(campaignFee.getId(), household.getHouseholdId(), fee.getId());
					feeNameWithExpectedAmount.put(dto.getFeeName(), dto.getExpectedAmount());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			}
		}
		return feeNameWithExpectedAmount;
	}
}
