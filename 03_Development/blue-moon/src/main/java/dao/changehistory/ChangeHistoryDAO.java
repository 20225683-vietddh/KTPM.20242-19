package dao.changehistory;

import models.ChangeHistoryRecord;
import java.util.List;

public interface ChangeHistoryDAO {
    
    /**
     * Thêm một bản ghi lịch sử thay đổi mới
     * @param record Bản ghi lịch sử cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    boolean addChangeRecord(ChangeHistoryRecord record);
    
    /**
     * Lấy tất cả bản ghi lịch sử thay đổi
     * @return Danh sách tất cả bản ghi
     */
    List<ChangeHistoryRecord> getAllChangeRecords();
    
    /**
     * Lấy bản ghi lịch sử thay đổi theo resident_id
     * @param residentId ID của nhân khẩu
     * @return Danh sách bản ghi lịch sử của nhân khẩu
     */
    List<ChangeHistoryRecord> getChangeRecordsByResidentId(int residentId);
    
    /**
     * Lấy bản ghi lịch sử thay đổi theo household_id
     * @param householdId ID của hộ khẩu
     * @return Danh sách bản ghi lịch sử của hộ khẩu
     */
    List<ChangeHistoryRecord> getChangeRecordsByHouseholdId(int householdId);
    
    /**
     * Lấy bản ghi lịch sử thay đổi theo loại thay đổi
     * @param changeType Loại thay đổi (ADD, UPDATE, DELETE)
     * @return Danh sách bản ghi theo loại
     */
    List<ChangeHistoryRecord> getChangeRecordsByType(String changeType);
    
    /**
     * Xóa bản ghi lịch sử theo ID
     * @param recordId ID của bản ghi cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deleteChangeRecord(int recordId);
    
    /**
     * Xóa tất cả bản ghi lịch sử của một nhân khẩu
     * @param residentId ID của nhân khẩu
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deleteChangeRecordsByResidentId(int residentId);
    
    /**
     * Xóa tất cả bản ghi lịch sử của một hộ khẩu
     * @param householdId ID của hộ khẩu
     * @return true nếu thành công, false nếu thất bại
     */
    boolean deleteChangeRecordsByHouseholdId(int householdId);
} 