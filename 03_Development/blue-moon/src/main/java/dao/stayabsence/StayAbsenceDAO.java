package dao.stayabsence;

import models.StayAbsenceRecord;
import java.util.List;

public interface StayAbsenceDAO {
    /**
     * Thêm bản ghi tạm trú/tạm vắng mới
     */
    boolean addStayAbsenceRecord(StayAbsenceRecord record) throws Exception;
    
    /**
     * Cập nhật bản ghi tạm trú/tạm vắng
     */
    boolean updateStayAbsenceRecord(StayAbsenceRecord record) throws Exception;
    
    /**
     * Xóa bản ghi tạm trú/tạm vắng
     */
    boolean deleteStayAbsenceRecord(int recordId) throws Exception;
    
    /**
     * Lấy bản ghi theo ID
     */
    StayAbsenceRecord getStayAbsenceRecordById(int recordId) throws Exception;
    
    /**
     * Lấy tất cả bản ghi tạm trú/tạm vắng
     */
    List<StayAbsenceRecord> getAllStayAbsenceRecords() throws Exception;
    
    /**
     * Lấy bản ghi theo loại (TEMPORARY_STAY hoặc TEMPORARY_ABSENCE)
     */
    List<StayAbsenceRecord> getStayAbsenceRecordsByType(String recordType) throws Exception;
    
    /**
     * Lấy bản ghi theo hộ khẩu
     */
    List<StayAbsenceRecord> getStayAbsenceRecordsByHouseholdId(int householdId) throws Exception;
    
    /**
     * Lấy bản ghi theo nhân khẩu (chỉ cho tạm vắng)
     */
    List<StayAbsenceRecord> getStayAbsenceRecordsByResidentId(int residentId) throws Exception;
    
    /**
     * Lấy bản ghi đang hoạt động
     */
    List<StayAbsenceRecord> getActiveStayAbsenceRecords() throws Exception;
    
    /**
     * Tìm kiếm bản ghi theo từ khóa
     */
    List<StayAbsenceRecord> searchStayAbsenceRecords(String keyword) throws Exception;
    
    /**
     * Cập nhật trạng thái record
     */
    boolean updateRecordStatus(int recordId, String status) throws Exception;
} 