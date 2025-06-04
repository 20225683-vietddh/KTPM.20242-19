package services;

import dao.tempresidenceabsence.*;
//import dao.TemporaryResidenceAbsenceDAOImpl;
import dto.TemporaryResidenceAbsenceDTO;
import exception.ServiceException;
import models.TemporaryResidenceAbsence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TemporaryResidenceAbsenceServiceImpl implements TemporaryResidenceAbsenceService {
    
    private final TemporaryResidenceAbsenceDAO dao;
    
    public TemporaryResidenceAbsenceServiceImpl() {
        this.dao = new TemporaryResidenceAbsenceDAO();
    }
    
    public TemporaryResidenceAbsenceServiceImpl(TemporaryResidenceAbsenceDAO dao) {
        this.dao = dao;
    }
    
    // ==================== CRUD Operations ====================
    
    @Override
    public TemporaryResidenceAbsence createTemporaryRequest(TemporaryResidenceAbsenceDTO dto) throws ServiceException {
        try {
            // Validate business rules before creation
            if (!validateRequestDates(dto.getMemberId(), dto.getStartDate(), dto.getEndDate(), null)) {
                throw new ServiceException("Request dates conflict with existing active requests");
            }
            
            // Convert DTO to Entity
            TemporaryResidenceAbsence request = new TemporaryResidenceAbsence();
            request.setMemberId(dto.getMemberId());
            request.setMemberName(dto.getMemberName());
            request.setType(dto.getType());
            request.setReason(dto.getReason());
            request.setStartDate(dto.getStartDate());
            request.setEndDate(dto.getEndDate());
            request.setAddress(dto.getAddress());
            request.setStatus("PENDING"); // Default status
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());
            
            return dao.save(request);
        } catch (Exception e) {
            throw new ServiceException("Failed to create temporary request", e);
        }
    }
    
    @Override
    public List<TemporaryResidenceAbsence> getAllTemporaryRequests() throws ServiceException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve all temporary requests", e);
        }
    }
    
    @Override
    public Optional<TemporaryResidenceAbsence> getTemporaryRequestById(int id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to find temporary request by ID", e);
        }
    }
    
    @Override
    public TemporaryResidenceAbsence updateTemporaryRequest(int id, TemporaryResidenceAbsenceDTO dto) throws ServiceException {
        try {
            Optional<TemporaryResidenceAbsence> existingOpt = dao.findById(id);
            if (!existingOpt.isPresent()) {
                throw new ServiceException("Temporary request not found with ID: " + id);
            }
            
            TemporaryResidenceAbsence existing = existingOpt.get();
            
            // Validate business rules before update (exclude current request from validation)
            if (!validateRequestDates(dto.getMemberId(), dto.getStartDate(), dto.getEndDate(), id)) {
                throw new ServiceException("Updated request dates conflict with existing active requests");
            }
            
            // Update fields
            existing.setMemberId(dto.getMemberId());
            existing.setMemberName(dto.getMemberName());
            existing.setType(dto.getType());
            existing.setReason(dto.getReason());
            existing.setStartDate(dto.getStartDate());
            existing.setEndDate(dto.getEndDate());
            existing.setAddress(dto.getAddress());
            existing.setUpdatedAt(LocalDateTime.now());
            
            return dao.update(existing);
        } catch (Exception e) {
            throw new ServiceException("Failed to update temporary request", e);
        }
    }
    
    @Override
    public boolean deleteTemporaryRequest(int id) throws ServiceException {
        try {
            Optional<TemporaryResidenceAbsence> existing = dao.findById(id);
            if (!existing.isPresent()) {
                return false;
            }
            
            return dao.delete(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to delete temporary request", e);
        }
    }
    
    // ==================== Status Management ====================
    
    @Override
    public TemporaryResidenceAbsence updateRequestStatus(int id, String status) throws ServiceException {
        try {
            Optional<TemporaryResidenceAbsence> existingOpt = dao.findById(id);
            if (!existingOpt.isPresent()) {
                throw new ServiceException("Temporary request not found with ID: " + id);
            }
            
            TemporaryResidenceAbsence existing = existingOpt.get();
            existing.setStatus(status);
            existing.setUpdatedAt(LocalDateTime.now());
            
            return dao.update(existing);
        } catch (Exception e) {
            throw new ServiceException("Failed to update request status", e);
        }
    }
    
    // ==================== Search and Filter Operations ====================
    
    @Override
    public List<TemporaryResidenceAbsence> searchByMemberName(String memberName) throws ServiceException {
        try {
            return dao.findByMemberNameContaining(memberName);
        } catch (Exception e) {
            throw new ServiceException("Failed to search by member name", e);
        }
    }
    
    @Override
    public List<TemporaryResidenceAbsence> getRequestsByType(String type) throws ServiceException {
        try {
            return dao.findByType(type);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by type", e);
        }
    }
    
    @Override
    public List<TemporaryResidenceAbsence> getRequestsByStatus(String status) throws ServiceException {
        try {
            return dao.findByStatus(status);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by status", e);
        }
    }
    
    @Override
    public List<TemporaryResidenceAbsence> getRequestsByDateRange(LocalDate startDate, LocalDate endDate) throws ServiceException {
        try {
            return dao.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by date range", e);
        }
    }
    
    @Override
    public List<TemporaryResidenceAbsence> getRequestsByMemberId(int memberId) throws ServiceException {
        try {
            return dao.findByMemberId(memberId);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by member ID", e);
        }
    }
    
    // ==================== Statistics ====================
    
    @Override
    public int getTotalRequestsCount() throws ServiceException {
        try {
            return dao.countAll();
        } catch (Exception e) {
            throw new ServiceException("Failed to get total requests count", e);
        }
    }
    
    @Override
    public int getRequestsCountByStatus(String status) throws ServiceException {
        try {
            return dao.countByStatus(status);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests count by status", e);
        }
    }
    
    @Override
    public int getRequestsCountByType(String type) throws ServiceException {
        try {
            return dao.countByType(type);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests count by type", e);
        }
    }
    
    // ==================== Business Logic ====================
    
    @Override
    public List<TemporaryResidenceAbsence> getActiveRequestsByMemberId(int memberId) throws ServiceException {
        try {
            List<TemporaryResidenceAbsence> memberRequests = dao.findByMemberId(memberId);
            
            // Filter active requests (APPROVED and current/future dates)
            LocalDate today = LocalDate.now();
            return memberRequests.stream()
                    .filter(request -> "APPROVED".equals(request.getStatus()))
                    .filter(request -> !request.getEndDate().isBefore(today))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Failed to get active requests by member ID", e);
        }
    }
    
    @Override
    public boolean validateRequestDates(int memberId, LocalDate startDate, LocalDate endDate, Integer excludeRequestId) throws ServiceException {
        try {
            List<TemporaryResidenceAbsence> existingRequests = dao.findByMemberId(memberId);
            
            // Filter out the request being updated (if any) and only check APPROVED/PENDING requests
            List<TemporaryResidenceAbsence> relevantRequests = existingRequests.stream()
                    .filter(request -> excludeRequestId == null || !request.getId().equals(excludeRequestId))
                    .filter(request -> "APPROVED".equals(request.getStatus()) || "PENDING".equals(request.getStatus()))
                    .collect(Collectors.toList());
            
            // Check for date overlaps
            for (TemporaryResidenceAbsence existing : relevantRequests) {
                if (datesOverlap(startDate, endDate, existing.getStartDate(), existing.getEndDate())) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            throw new ServiceException("Failed to validate request dates", e);
        }
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Check if two date ranges overlap
     */
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
    
    /**
     * Get requests that are currently active (approved and within date range)
     */
    @Override
    public List<TemporaryResidenceAbsence> getCurrentActiveRequests() throws ServiceException {
        try {
            List<TemporaryResidenceAbsence> allRequests = dao.findAll();
            LocalDate today = LocalDate.now();
            
            return allRequests.stream()
                    .filter(request -> "APPROVED".equals(request.getStatus()))
                    .filter(request -> !request.getStartDate().isAfter(today))
                    .filter(request -> !request.getEndDate().isBefore(today))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Failed to get current active requests", e);
        }
    }
    
    /**
     * Get requests that will expire within the specified number of days
     */
    @Override
    public List<TemporaryResidenceAbsence> getExpiringRequests(int daysAhead) throws ServiceException {
        try {
            List<TemporaryResidenceAbsence> activeRequests = getCurrentActiveRequests();
            LocalDate cutoffDate = LocalDate.now().plusDays(daysAhead);
            
            return activeRequests.stream()
                    .filter(request -> !request.getEndDate().isAfter(cutoffDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Failed to get expiring requests", e);
        }
    }
    
    /**
     * Check if a member has conflicting requests
     */
    @Override
    public boolean hasConflictingRequests(int memberId, LocalDate startDate, LocalDate endDate) throws ServiceException {
        try {
            return !validateRequestDates(memberId, startDate, endDate, null);
        } catch (Exception e) {
            throw new ServiceException("Failed to check conflicting requests", e);
        }
    }
    
    /**
     * Get statistics summary
     */
    @Override
    public RequestStatistics getRequestStatistics() throws ServiceException {
        try {
            RequestStatistics stats = new RequestStatistics();
            stats.setTotalRequests(getTotalRequestsCount());
            stats.setPendingRequests(getRequestsCountByStatus("PENDING"));
            stats.setApprovedRequests(getRequestsCountByStatus("APPROVED"));
            stats.setRejectedRequests(getRequestsCountByStatus("REJECTED"));
            stats.setResidenceRequests(getRequestsCountByType("RESIDENCE"));
            stats.setAbsenceRequests(getRequestsCountByType("ABSENCE"));
            
            return stats;
        } catch (Exception e) {
            throw new ServiceException("Failed to get request statistics", e);
        }
    }
    
    // ==================== Inner Class for Statistics ====================
    
    public static class RequestStatistics {
        private int totalRequests;
        private int pendingRequests;
        private int approvedRequests;
        private int rejectedRequests;
        private int residenceRequests;
        private int absenceRequests;
        
        // Getters and Setters
        public int getTotalRequests() { return totalRequests; }
        public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }
        
        public int getPendingRequests() { return pendingRequests; }
        public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }
        
        public int getApprovedRequests() { return approvedRequests; }
        public void setApprovedRequests(int approvedRequests) { this.approvedRequests = approvedRequests; }
        
        public int getRejectedRequests() { return rejectedRequests; }
        public void setRejectedRequests(int rejectedRequests) { this.rejectedRequests = rejectedRequests; }
        
        public int getResidenceRequests() { return residenceRequests; }
        public void setResidenceRequests(int residenceRequests) { this.residenceRequests = residenceRequests; }
        
        public int getAbsenceRequests() { return absenceRequests; }
        public void setAbsenceRequests(int absenceRequests) { this.absenceRequests = absenceRequests; }
        
        @Override
        public String toString() {
            return "RequestStatistics{" +
                    "totalRequests=" + totalRequests +
                    ", pendingRequests=" + pendingRequests +
                    ", approvedRequests=" + approvedRequests +
                    ", rejectedRequests=" + rejectedRequests +
                    ", residenceRequests=" + residenceRequests +
                    ", absenceRequests=" + absenceRequests +
                    '}';
        }
    }
}