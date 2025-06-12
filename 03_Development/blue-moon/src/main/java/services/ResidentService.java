package services;

import dao.residents.ResidentDAO;
import dao.residents.ResidentDAOPostgreSQL;
import models.Resident;
import java.sql.SQLException;
import java.util.List;

public class ResidentService {
    private final ResidentDAO residentDAO;
    private final ChangeHistoryService changeHistoryService;
    
    public ResidentService() throws SQLException {
        this.residentDAO = new ResidentDAOPostgreSQL();
        this.changeHistoryService = new ChangeHistoryService();
    }
    
    /**
     * Lấy tất cả nhân khẩu
     */
    public List<Resident> getAllResidents() {
        try {
            return residentDAO.getAllResidents();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách nhân khẩu: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Lấy nhân khẩu theo ID
     */
    public Resident getResidentById(int residentId) {
        try {
            return residentDAO.getResidentById(residentId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy nhân khẩu theo ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy danh sách nhân khẩu theo household ID
     */
    public List<Resident> getResidentsByHouseholdId(int householdId) {
        try {
            return residentDAO.getResidentsByHouseholdId(householdId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy nhân khẩu theo hộ khẩu: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Tìm kiếm nhân khẩu theo từ khóa
     */
    public List<Resident> searchResidents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllResidents();
        }
        try {
            return residentDAO.searchResidents(keyword.trim());
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm nhân khẩu: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Thêm nhân khẩu mới (có ghi lại lịch sử)
     */
    public boolean addResident(Resident resident) {
        if (resident == null) {
            return false;
        }
        
        try {
            System.out.println("🔍 DEBUG ResidentService: Bắt đầu thêm nhân khẩu " + resident.getFullName());
            
            // Thêm nhân khẩu vào database
            boolean success = residentDAO.addResident(resident);
            
            System.out.println("🔍 DEBUG ResidentService: addResident DAO result = " + success);
            System.out.println("🔍 DEBUG ResidentService: Resident ID sau insert = " + resident.getResidentId());
            
            if (success) {
                System.out.println("🔍 DEBUG ResidentService: Chuẩn bị ghi lịch sử...");
                // ID đã được set trong DAO, bây giờ ghi lại lịch sử
                changeHistoryService.recordResidentAdded(resident);
                System.out.println("✅ Đã thêm nhân khẩu: " + resident.getFullName() + " (ID: " + resident.getResidentId() + ")");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ DEBUG ResidentService: Lỗi khi thêm nhân khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật thông tin nhân khẩu (có ghi lại lịch sử)
     */
    public boolean updateResident(Resident resident) {
        if (resident == null || resident.getResidentId() <= 0) {
            return false;
        }
        
        try {
            // Cập nhật nhân khẩu trong database
            boolean success = residentDAO.updateResident(resident);
            
            if (success) {
                // Ghi lại lịch sử CẬP NHẬT nhân khẩu
                changeHistoryService.recordResidentUpdated(resident);
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật nhân khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa nhân khẩu (có ghi lại lịch sử)
     */
    public boolean deleteResident(int residentId) {
        if (residentId <= 0) {
            return false;
        }
        
        try {
            // Lấy thông tin nhân khẩu TRƯỚC khi xóa để ghi lại lịch sử
            Resident resident = residentDAO.getResidentById(residentId);
            if (resident == null) {
                return false;
            }
            
            // Ghi lại lịch sử XÓA nhân khẩu TRƯỚC khi xóa
            changeHistoryService.recordResidentDeleted(
                resident.getResidentId(),
                resident.getHouseholdId(),
                resident.getFullName()
            );
            
            // Xóa nhân khẩu từ database
            boolean success = residentDAO.deleteResident(residentId);
            
            if (success) {
                System.out.println("✅ Đã xóa nhân khẩu: " + resident.getFullName() + " (ID: " + residentId + ")");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa nhân khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra nhân khẩu có tồn tại không
     */
    public boolean residentExists(int residentId) {
        return getResidentById(residentId) != null;
    }
    
    /**
     * Kiểm tra CCCD đã tồn tại chưa
     */
    public boolean citizenIdExists(String citizenId) {
        if (citizenId == null || citizenId.trim().isEmpty()) {
            return false;
        }
        List<Resident> residents = searchResidents(citizenId);
        return !residents.isEmpty();
    }
    
    /**
     * Lấy tổng số nhân khẩu
     */
    public int getTotalResidents() {
        return getAllResidents().size();
    }
    
    /**
     * Lấy số nhân khẩu trong một hộ khẩu cụ thể
     */
    public int getResidentsCountByHouseholdId(int householdId) {
        return getResidentsByHouseholdId(householdId).size();
    }
    
    /**
     * Lấy lịch sử thay đổi của một nhân khẩu
     */
    public List<models.ChangeHistoryRecord> getResidentChangeHistory(int residentId) {
        return changeHistoryService.getResidentChangeHistory(residentId);
    }
    
    /**
     * In thống kê lịch sử thay đổi
     */
    public void printChangeStatistics() {
        changeHistoryService.printChangeStatistics();
    }
} 