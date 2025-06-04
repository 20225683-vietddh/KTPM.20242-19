package controllers;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Resident;
import services.MemberServiceImpl;

public class MemberService {
	private MemberServiceImpl memberService = new MemberServiceImpl();

    public MemberService() {
    }


    public MemberServiceImpl getMemberService() {
		return memberService;
	}


	public void setMemberService(MemberServiceImpl memberService) {
		this.memberService = memberService;
	}


	// Lấy member theo ID
    public Resident getMemberById(String memberId) {
        try {
            return memberService.getMemberById(memberId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get member by ID: " + e.getMessage());
            return null;
        }
    }

    // Lấy danh sách member của 1 hộ gia đình
    public List<Resident> getMembersByHouseholdId(int householdId) {
        try {
            return memberService.getMembersByHouseholdId(householdId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get members by household ID: " + e.getMessage());
            return null;
        }
    }

    // Kiểm tra tồn tại
    public boolean memberExists(String memberId) {
        return memberService.memberExists(memberId);
    }

    // Thêm member
    public boolean addMember(Resident member) throws SQLException {
        try {
            boolean result = memberService.addMember(member);
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
            boolean result = memberService.updateMember(member);
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
    public boolean deleteMember(String memberId) throws SQLException {
        try {
            boolean result = memberService.deleteMember(memberId);
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
            return memberService.getHouseholdHead(householdId);
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
