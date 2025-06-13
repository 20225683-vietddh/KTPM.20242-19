package services;

import dao.stayabsence.StayAbsenceDAO;
import dao.stayabsence.StayAbsenceDAOPostgreSQL;
import models.StayAbsenceRecord;
import models.Resident;
import models.Household;

import java.time.LocalDate;
import java.util.List;

public class StayAbsenceService {
    private StayAbsenceDAO stayAbsenceDAO;
    private ResidentService residentService;
    private HouseholdService householdService;
    
    public StayAbsenceService() throws Exception {
        this.stayAbsenceDAO = new StayAbsenceDAOPostgreSQL();
        this.residentService = new ResidentService();
        this.householdService = new HouseholdService();
    }
    
    /**
     * Add a new temporary stay record (for external people)
     */
    public boolean addTemporaryStayRecord(StayAbsenceRecord record) throws Exception {
        // Validate required fields for temporary stay
        if (record.getRecordType() == null || !record.getRecordType().equals(StayAbsenceRecord.TYPE_TEMPORARY_STAY)) {
            throw new IllegalArgumentException("Invalid record type for temporary stay");
        }
        
        if (record.getTempResidentName() == null || record.getTempResidentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Temporary resident name is required");
        }
        
        if (record.getHouseholdId() == null) {
            throw new IllegalArgumentException("Household ID is required");
        }
        
        if (record.getStartDate() == null || record.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        
        if (record.getEndDate().isBefore(record.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Verify household exists
        Household household = householdService.getHouseholdById(record.getHouseholdId());
        if (household == null) {
            throw new IllegalArgumentException("Household not found");
        }
        
        // Set default values
        record.setCreatedDate(LocalDate.now());
        record.setStatus(StayAbsenceRecord.STATUS_ACTIVE);
        
        // Calculate period
        long days = java.time.temporal.ChronoUnit.DAYS.between(record.getStartDate(), record.getEndDate());
        record.setPeriod(days + " ngày");
        
        // Save to database
        boolean success = stayAbsenceDAO.addStayAbsenceRecord(record);
        
        if (success) {
            System.out.println("✅ Successfully added temporary stay record for: " + record.getTempResidentName());
        }
        
        return success;
    }
    
    /**
     * Add a new temporary absence record (for existing residents)
     */
    public boolean addTemporaryAbsenceRecord(StayAbsenceRecord record) throws Exception {
        // Validate required fields for temporary absence
        if (record.getRecordType() == null || !record.getRecordType().equals(StayAbsenceRecord.TYPE_TEMPORARY_ABSENCE)) {
            throw new IllegalArgumentException("Invalid record type for temporary absence");
        }
        
        if (record.getResidentId() == null) {
            throw new IllegalArgumentException("Resident ID is required");
        }
        
        if (record.getTempAddress() == null || record.getTempAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination address is required");
        }
        
        if (record.getStartDate() == null || record.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        
        if (record.getEndDate().isBefore(record.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Verify resident exists
        Resident resident = residentService.getResidentById(record.getResidentId());
        if (resident == null) {
            throw new IllegalArgumentException("Resident not found");
        }
        
        // Set household ID from resident
        record.setHouseholdId(resident.getHouseholdId());
        
        // Set default values
        record.setCreatedDate(LocalDate.now());
        record.setStatus(StayAbsenceRecord.STATUS_ACTIVE);
        
        // Calculate period
        long days = java.time.temporal.ChronoUnit.DAYS.between(record.getStartDate(), record.getEndDate());
        record.setPeriod(days + " ngày");
        
        // Save to database
        boolean success = stayAbsenceDAO.addStayAbsenceRecord(record);
        
        if (success) {
            System.out.println("✅ Successfully added temporary absence record for: " + resident.getFullName());
        }
        
        return success;
    }
    
    /**
     * Get all stay/absence records
     */
    public List<StayAbsenceRecord> getAllRecords() throws Exception {
        return stayAbsenceDAO.getAllStayAbsenceRecords();
    }
    
    /**
     * Get records by type (TEMPORARY_STAY or TEMPORARY_ABSENCE)
     */
    public List<StayAbsenceRecord> getRecordsByType(String recordType) throws Exception {
        return stayAbsenceDAO.getStayAbsenceRecordsByType(recordType);
    }
    
    /**
     * Get records by household
     */
    public List<StayAbsenceRecord> getRecordsByHousehold(int householdId) throws Exception {
        return stayAbsenceDAO.getStayAbsenceRecordsByHouseholdId(householdId);
    }
    
    /**
     * Get records by resident
     */
    public List<StayAbsenceRecord> getRecordsByResident(int residentId) throws Exception {
        return stayAbsenceDAO.getStayAbsenceRecordsByResidentId(residentId);
    }
    
    /**
     * Get active records only
     */
    public List<StayAbsenceRecord> getActiveRecords() throws Exception {
        return stayAbsenceDAO.getActiveStayAbsenceRecords();
    }
    
    /**
     * Search records by keyword
     */
    public List<StayAbsenceRecord> searchRecords(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRecords();
        }
        return stayAbsenceDAO.searchStayAbsenceRecords(keyword.trim());
    }
    
    /**
     * Update record status
     */
    public boolean updateRecordStatus(int recordId, String status) throws Exception {
        if (!StayAbsenceRecord.STATUS_ACTIVE.equals(status) && 
            !StayAbsenceRecord.STATUS_EXPIRED.equals(status) && 
            !StayAbsenceRecord.STATUS_CANCELLED.equals(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        return stayAbsenceDAO.updateRecordStatus(recordId, status);
    }
    
    /**
     * Cancel a record
     */
    public boolean cancelRecord(int recordId) throws Exception {
        return updateRecordStatus(recordId, StayAbsenceRecord.STATUS_CANCELLED);
    }
    
    /**
     * Check and update expired records
     */
    public int updateExpiredRecords() throws Exception {
        List<StayAbsenceRecord> activeRecords = getActiveRecords();
        int updatedCount = 0;
        
        LocalDate today = LocalDate.now();
        
        for (StayAbsenceRecord record : activeRecords) {
            if (record.getEndDate() != null && record.getEndDate().isBefore(today)) {
                if (updateRecordStatus(record.getRecordId(), StayAbsenceRecord.STATUS_EXPIRED)) {
                    updatedCount++;
                }
            }
        }
        
        if (updatedCount > 0) {
            System.out.println("✅ Updated " + updatedCount + " expired records");
        }
        
        return updatedCount;
    }
    
    /**
     * Get record by ID
     */
    public StayAbsenceRecord getRecordById(int recordId) throws Exception {
        return stayAbsenceDAO.getStayAbsenceRecordById(recordId);
    }
    
    /**
     * Update a record
     */
    public boolean updateRecord(StayAbsenceRecord record) throws Exception {
        if (record.getRecordId() <= 0) {
            throw new IllegalArgumentException("Invalid record ID");
        }
        
        // Validate based on record type
        if (StayAbsenceRecord.TYPE_TEMPORARY_STAY.equals(record.getRecordType())) {
            if (record.getTempResidentName() == null || record.getTempResidentName().trim().isEmpty()) {
                throw new IllegalArgumentException("Temporary resident name is required");
            }
        } else if (StayAbsenceRecord.TYPE_TEMPORARY_ABSENCE.equals(record.getRecordType())) {
            if (record.getResidentId() == null) {
                throw new IllegalArgumentException("Resident ID is required");
            }
        }
        
        // Update period if dates changed
        if (record.getStartDate() != null && record.getEndDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(record.getStartDate(), record.getEndDate());
            record.setPeriod(days + " ngày");
        }
        
        return stayAbsenceDAO.updateStayAbsenceRecord(record);
    }
    
    /**
     * Delete a record
     */
    public boolean deleteRecord(int recordId) throws Exception {
        return stayAbsenceDAO.deleteStayAbsenceRecord(recordId);
    }
} 