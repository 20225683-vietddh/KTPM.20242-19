package controllers.resident;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Resident;
import services.resident.ResidentServiceImpl;

public class ResidentController {
	private ResidentServiceImpl residentService = new ResidentServiceImpl();

    public ResidentController() {
    }


    public ResidentServiceImpl getMemberService() {
		return residentService;
	}
    
	public void setMemberService(ResidentServiceImpl memberService) {
		this.residentService = memberService;
	}

	// Lấy member theo ID
    public Resident getMemberById(int memberId) {
        try {
            return residentService.getResidentById(memberId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get member by ID: " + e.getMessage());
            return null;
        }
    }

    // Lấy danh sách member của 1 hộ gia đình
    public List<Resident> getMembersByHouseholdId(int householdId) {
        try {
            return residentService.getResidentsByHouseholdId(householdId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get members by household ID: " + e.getMessage());
            return null;
        }
    }

    // Kiểm tra tồn tại
    public boolean memberExists(int memberId) {
        return residentService.residentExists(memberId);
    }

    // Thêm member
    public boolean addMember(Resident member) throws SQLException {
        try {
            boolean result = residentService.addResident(member);
            if (result) {
                System.out.println("Member added successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to add member: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật member
    public boolean updateMember(Resident member) throws SQLException {
        try {
            boolean result = residentService.updateResident(member);
            if (result) {
                System.out.println("Member updated successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to update member: " + e.getMessage());
            return false;
        }
    }

    // Xoá member
    public boolean deleteMember(int memberId) throws SQLException {
        try {
            boolean result = residentService.deleteResident(memberId);
            if (result) {
                System.out.println("Member deleted successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to delete member: " + e.getMessage());
            return false;
        }
    }

    // Lấy chủ hộ
    public Resident getHouseholdHead(int householdId) {
        try {
            return residentService.getHouseholdHead(householdId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get household head: " + e.getMessage());
            return null;
        }
    }

    // In lỗi
    public void showErrorMessage(String message) {
        // Thay thế bằng UI dialog nếu có
        System.err.println("Error: " + message);
    }
}
