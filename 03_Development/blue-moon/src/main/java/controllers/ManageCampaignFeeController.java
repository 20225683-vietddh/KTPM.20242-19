package controllers;

import java.util.ArrayList;
import java.util.List;
import dto.campaignfee.*;
import exception.*;
import services.CampaignFeeService;
import java.time.DateTimeException;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ManageCampaignFeeController extends BaseController {
	private final CampaignFeeService service ;
	
	public ManageCampaignFeeController() throws SQLException {
		service = new CampaignFeeService();
	}
	
	public void handleAddCampaignFee(CampaignFeeDTO requestDTO) throws InvalidInputException, DateTimeException, InvalidDateRangeException, SQLException {
		validateInputs(requestDTO);
		validateStartAndDueDate(requestDTO);
		addNewCampaignFee(requestDTO);
	}
	
	public void handleUpdateCampaignFee(CampaignFeeDTO requestDTO, List<Integer> originalSelectedFeeIds) throws InvalidInputException, DateTimeException, InvalidDateRangeException, SQLException {
		validateInputs(requestDTO);
		validateStartAndDueDate(requestDTO);
		
		try {
			int campaignFeeId = requestDTO.getId();
			List<Integer> updatedSelectedFeeIds = requestDTO.getFeeIds();
			List<Integer> removedFeeIds = getRemovedFeeIds(originalSelectedFeeIds, updatedSelectedFeeIds);
			System.out.println(campaignFeeId + "\n" + originalSelectedFeeIds + "\n" + updatedSelectedFeeIds + "\n" + removedFeeIds);
			
			if (removedFeeIds.isEmpty() || !hasCollectionBeenProcessed(campaignFeeId, removedFeeIds)) {
				updateCampaignFee(requestDTO);
			} else {
				throw new IllegalStateException("Không thể cập nhật đợt thu phí vì một số khoản thu mà bạn xóa " + removedFeeIds + " đã được thu!");
			}
		} catch (SQLException e) {
			throw e;
		}
	}
	
	private List<Integer> getRemovedFeeIds(List<Integer> originalSelectedFeeIds, List<Integer> updatedSelectedFeeIds) {
	    Set<Integer> updatedSet = new HashSet<>(updatedSelectedFeeIds);
	    
	    return originalSelectedFeeIds.stream()
	        .filter(feeId -> !updatedSet.contains(feeId))
	        .collect(Collectors.toList());
	}
	
	private boolean hasCollectionBeenProcessed(int campaignFeeId, List<Integer> removedFeeIds) throws SQLException {
		return service.isFeesExisted(campaignFeeId, removedFeeIds);
	}
	
	private void validateInputs(CampaignFeeDTO dto) throws InvalidInputException {
		List<String> errorMessages = new ArrayList<>();

		if (dto.getName() == null || dto.getName().isEmpty()) {
		    errorMessages.add("Vui lòng nhập tên cho đợt thu!");
		}

		if (dto.getStartDay() == null || dto.getStartMonth() == null || dto.getStartYear() == null) {
		    errorMessages.add("Vui lòng chọn đầy đủ ngày/tháng/năm bắt đầu đợt thu!");
		}
		
		if (dto.getDueDay() == null || dto.getDueMonth() == null || dto.getDueYear() == null) {
		    errorMessages.add("Vui lòng chọn đầy đủ ngày/tháng/năm kết thúc đợt thu!");
		}
		
		if (dto.getFeeIds().isEmpty()) {
			errorMessages.add("Vui lòng chọn ít nhất 1 khoản thu cho đợt thu!");
		}
		
		if (!errorMessages.isEmpty()) {
		    throw new InvalidInputException(String.join("\n", errorMessages));
		}
	}
	
	private void validateStartAndDueDate(CampaignFeeDTO dto) throws DateTimeException, InvalidDateRangeException {
		String sDay = dto.getStartDay();
		String sMonth = dto.getStartMonth();
		String sYear = dto.getStartYear();
		String dDay = dto.getDueDay();
		String dMonth = dto.getDueMonth();
		String dYear = dto.getDueYear();
		
		if (sDay != null && sMonth != null && sYear != null &&
				dDay != null && dMonth != null && dYear != null) {
			utils.Utils.validateDate(sDay, sMonth, sYear);
			utils.Utils.validateDate(dDay, dMonth, dYear);
			utils.Utils.validateStartAndDueDate(sDay, sMonth, sYear, dDay, dMonth, dYear);
		}
	}
	
	private void addNewCampaignFee(CampaignFeeDTO dto) throws SQLException {
		service.addNewCampaignFee(dto);
	}
	
	private void updateCampaignFee(CampaignFeeDTO dto) throws SQLException {
		service.updateCampaignFee(dto);
	}
}
