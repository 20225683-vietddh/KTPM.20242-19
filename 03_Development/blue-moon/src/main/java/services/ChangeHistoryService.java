package services;

import dao.changehistory.ChangeHistoryDAO;
import dao.changehistory.ChangeHistoryDAOPostgreSQL;
import models.ChangeHistoryRecord;
import models.Resident;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ChangeHistoryService {
    private ChangeHistoryDAO changeHistoryDAO;
    
    public ChangeHistoryService() throws SQLException {
        this.changeHistoryDAO = new ChangeHistoryDAOPostgreSQL();
    }
    
    /**
     * Ghi lại việc thêm nhân khẩu mới
     */
    public void recordResidentAdded(Resident resident) {
        ChangeHistoryRecord record = new ChangeHistoryRecord(
            ChangeHistoryRecord.CHANGE_TYPE_ADD,
            LocalDate.now(),
            resident.getResidentId(),
            resident.getHouseholdId()
        );
        
        boolean success = changeHistoryDAO.addChangeRecord(record);
        if (success) {
            System.out.println("✅ Đã ghi lại lịch sử: THÊM nhân khẩu " + resident.getFullName() + 
                             " (ID: " + resident.getResidentId() + ") vào hộ khẩu " + resident.getHouseholdId());
        } else {
            System.err.println("❌ Lỗi ghi lại lịch sử thêm nhân khẩu: " + resident.getFullName());
        }
    }
    
    /**
     * Ghi lại việc cập nhật nhân khẩu
     */
    public void recordResidentUpdated(Resident resident) {
        ChangeHistoryRecord record = new ChangeHistoryRecord(
            ChangeHistoryRecord.CHANGE_TYPE_UPDATE,
            LocalDate.now(),
            resident.getResidentId(),
            resident.getHouseholdId()
        );
        
        boolean success = changeHistoryDAO.addChangeRecord(record);
        if (success) {
            System.out.println("✅ Đã ghi lại lịch sử: CẬP NHẬT nhân khẩu " + resident.getFullName() + 
                             " (ID: " + resident.getResidentId() + ") trong hộ khẩu " + resident.getHouseholdId());
        } else {
            System.err.println("❌ Lỗi ghi lại lịch sử cập nhật nhân khẩu: " + resident.getFullName());
        }
    }
    
    /**
     * Ghi lại việc xóa nhân khẩu
     */
    public void recordResidentDeleted(int residentId, int householdId, String residentName) {
        ChangeHistoryRecord record = new ChangeHistoryRecord(
            ChangeHistoryRecord.CHANGE_TYPE_DELETE,
            LocalDate.now(),
            residentId,
            householdId
        );
        
        boolean success = changeHistoryDAO.addChangeRecord(record);
        if (success) {
            System.out.println("✅ Đã ghi lại lịch sử: XÓA nhân khẩu " + residentName + 
                             " (ID: " + residentId + ") khỏi hộ khẩu " + householdId);
        } else {
            System.err.println("❌ Lỗi ghi lại lịch sử xóa nhân khẩu: " + residentName);
        }
    }
    
    /**
     * Lấy lịch sử thay đổi của một nhân khẩu
     */
    public List<ChangeHistoryRecord> getResidentChangeHistory(int residentId) {
        return changeHistoryDAO.getChangeRecordsByResidentId(residentId);
    }
    
    /**
     * Lấy lịch sử thay đổi của một hộ khẩu
     */
    public List<ChangeHistoryRecord> getHouseholdChangeHistory(int householdId) {
        return changeHistoryDAO.getChangeRecordsByHouseholdId(householdId);
    }
    
    /**
     * Lấy tất cả lịch sử thay đổi
     */
    public List<ChangeHistoryRecord> getAllChangeHistory() {
        return changeHistoryDAO.getAllChangeRecords();
    }
    
    /**
     * Lấy lịch sử thay đổi theo loại
     */
    public List<ChangeHistoryRecord> getChangeHistoryByType(String changeType) {
        return changeHistoryDAO.getChangeRecordsByType(changeType);
    }
    
    /**
     * Xóa tất cả lịch sử của một hộ khẩu (khi xóa hộ khẩu)
     */
    public void deleteHouseholdChangeHistory(int householdId) {
        boolean success = changeHistoryDAO.deleteChangeRecordsByHouseholdId(householdId);
        if (success) {
            System.out.println("✅ Đã xóa tất cả lịch sử thay đổi của hộ khẩu " + householdId);
        } else {
            System.err.println("❌ Lỗi xóa lịch sử thay đổi của hộ khẩu " + householdId);
        }
    }
    
    /**
     * Xóa tất cả lịch sử của một nhân khẩu (khi xóa nhân khẩu)
     */
    public void deleteResidentChangeHistory(int residentId) {
        boolean success = changeHistoryDAO.deleteChangeRecordsByResidentId(residentId);
        if (success) {
            System.out.println("✅ Đã xóa tất cả lịch sử thay đổi của nhân khẩu " + residentId);
        } else {
            System.err.println("❌ Lỗi xóa lịch sử thay đổi của nhân khẩu " + residentId);
        }
    }
    
    /**
     * Thống kê số lượng thay đổi theo loại
     */
    public void printChangeStatistics() {
        try {
            List<ChangeHistoryRecord> allRecords = getAllChangeHistory();
            long addCount = allRecords.stream().filter(r -> ChangeHistoryRecord.CHANGE_TYPE_ADD.equals(r.getChangeType())).count();
            long updateCount = allRecords.stream().filter(r -> ChangeHistoryRecord.CHANGE_TYPE_UPDATE.equals(r.getChangeType())).count();
            long deleteCount = allRecords.stream().filter(r -> ChangeHistoryRecord.CHANGE_TYPE_DELETE.equals(r.getChangeType())).count();
            
            System.out.println("\n📊 THỐNG KÊ LỊCH SỬ THAY ĐỔI NHÂN KHẨU:");
            System.out.println("➕ Thêm mới: " + addCount + " lần");
            System.out.println("✏️ Cập nhật: " + updateCount + " lần");
            System.out.println("🗑️ Xóa: " + deleteCount + " lần");
            System.out.println("📈 Tổng cộng: " + allRecords.size() + " thay đổi\n");
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thống kê lịch sử thay đổi: " + e.getMessage());
        }
    }
} 