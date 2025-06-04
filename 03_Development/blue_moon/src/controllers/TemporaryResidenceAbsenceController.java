package controllers;

import dto.TemporaryResidenceAbsenceDTO;
import services.TemporaryResidenceAbsenceService;


import dto.TemporaryResidenceAbsenceDTO;
import exception.ServiceException;
import models.TemporaryResidenceAbsence;
import services.TemporaryResidenceAbsenceService;
import services.TemporaryResidenceAbsenceServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TemporaryResidenceAbsenceController {
    
    private final TemporaryResidenceAbsenceServiceImpl temporaryResidenceAbsenceService;
    
    public TemporaryResidenceAbsenceController() {
        this.temporaryResidenceAbsenceService = new TemporaryResidenceAbsenceServiceImpl();
    }
    
    public TemporaryResidenceAbsenceController(TemporaryResidenceAbsenceServiceImpl service) {
        this.temporaryResidenceAbsenceService = service;
    }
    
    // ==================== CRUD Operations ====================
    
    public TemporaryResidenceAbsence createTemporaryRequest(TemporaryResidenceAbsenceDTO dto) throws ServiceException {
        try {
            validateRequestDTO(dto);
            return temporaryResidenceAbsenceService.createTemporaryRequest(dto);
        } catch (Exception e) {
            throw new ServiceException("Failed to create temporary request: " + e.getMessage(), e);
        }
    }
    
    public List<TemporaryResidenceAbsence> getAllTemporaryRequests() throws ServiceException {
        try {
            return temporaryResidenceAbsenceService.getAllTemporaryRequests();
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve temporary requests: " + e.getMessage(), e);
        }
    }
    
    public Optional<TemporaryResidenceAbsence> getTemporaryRequestById(int id) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            return temporaryResidenceAbsenceService.getTemporaryRequestById(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve temporary request by ID: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an existing temporary request
     * @param id The request ID to update
     * @param dto The updated request data
     * @return The updated request
     * @throws ServiceException if update fails
     */
    public TemporaryResidenceAbsence updateTemporaryRequest(int id, TemporaryResidenceAbsenceDTO dto) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            validateRequestDTO(dto);
            return temporaryResidenceAbsenceService.updateTemporaryRequest(id, dto);
        } catch (Exception e) {
            throw new ServiceException("Failed to update temporary request: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteRequest(int id) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            return temporaryResidenceAbsenceService.deleteTemporaryRequest(id);
        } catch (Exception e) {
            throw new ServiceException("Failed to delete temporary request: " + e.getMessage(), e);
        }
    }
    
    // ==================== Status Management ====================
    
    public TemporaryResidenceAbsence approveRequest(int id) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            return temporaryResidenceAbsenceService.updateRequestStatus(id, "APPROVED");
        } catch (Exception e) {
            throw new ServiceException("Failed to approve temporary request: " + e.getMessage(), e);
        }
    }
    
    public TemporaryResidenceAbsence rejectRequest(int id) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            return temporaryResidenceAbsenceService.updateRequestStatus(id, "REJECTED");
        } catch (Exception e) {
            throw new ServiceException("Failed to reject temporary request: " + e.getMessage(), e);
        }
    }
    public TemporaryResidenceAbsence setPendingStatus(int id) throws ServiceException {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("Request ID must be positive");
            }
            return temporaryResidenceAbsenceService.updateRequestStatus(id, "PENDING");
        } catch (Exception e) {
            throw new ServiceException("Failed to set pending status: " + e.getMessage(), e);
        }
    }
    
    // ==================== Search and Filter Operations ====================
    
    public List<TemporaryResidenceAbsence> searchByMemberName(String memberName) throws ServiceException {
        try {
            if (memberName == null || memberName.trim().isEmpty()) {
                throw new IllegalArgumentException("Member name cannot be empty");
            }
            return temporaryResidenceAbsenceService.searchByMemberName(memberName.trim());
        } catch (Exception e) {
            throw new ServiceException("Failed to search by member name: " + e.getMessage(), e);
        }
    }
    public List<TemporaryResidenceAbsence> getRequestsByType(String type) throws ServiceException {
        try {
            if (type == null || (!type.equals("RESIDENCE") && !type.equals("ABSENCE"))) {
                throw new IllegalArgumentException("Type must be either 'RESIDENCE' or 'ABSENCE'");
            }
            return temporaryResidenceAbsenceService.getRequestsByType(type);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by type: " + e.getMessage(), e);
        }
    }
    
    public List<TemporaryResidenceAbsence> getRequestsByStatus(String status) throws ServiceException {
        try {
            if (status == null || (!status.equals("PENDING") && !status.equals("APPROVED") && !status.equals("REJECTED"))) {
                throw new IllegalArgumentException("Status must be PENDING, APPROVED, or REJECTED");
            }
            return temporaryResidenceAbsenceService.getRequestsByStatus(status);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by status: " + e.getMessage(), e);
        }
    }
    public List<TemporaryResidenceAbsence> getRequestsByDateRange(LocalDate startDate, LocalDate endDate) throws ServiceException {
        try {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start date and end date cannot be null");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            return temporaryResidenceAbsenceService.getRequestsByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by date range: " + e.getMessage(), e);
        }
    }
    public List<TemporaryResidenceAbsence> getRequestsByMemberId(int memberId) throws ServiceException {
        try {
            if (memberId <= 0) {
                throw new IllegalArgumentException("Member ID must be positive");
            }
            return temporaryResidenceAbsenceService.getRequestsByMemberId(memberId);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests by member ID: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistics ====================
    
    public int getTotalRequestsCount() throws ServiceException {
        try {
            return temporaryResidenceAbsenceService.getTotalRequestsCount();
        } catch (Exception e) {
            throw new ServiceException("Failed to get total requests count: " + e.getMessage(), e);
        }
    }
    
    public int getRequestsCountByStatus(String status) throws ServiceException {
        try {
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Status cannot be empty");
            }
            return temporaryResidenceAbsenceService.getRequestsCountByStatus(status);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests count by status: " + e.getMessage(), e);
        }
    }
    public int getRequestsCountByType(String type) throws ServiceException {
        try {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Type cannot be empty");
            }
            return temporaryResidenceAbsenceService.getRequestsCountByType(type);
        } catch (Exception e) {
            throw new ServiceException("Failed to get requests count by type: " + e.getMessage(), e);
        }
    }
    
    // ==================== Business Logic ====================
    
    public boolean hasActiveRequests(int memberId) throws ServiceException {
        try {
            if (memberId <= 0) {
                throw new IllegalArgumentException("Member ID must be positive");
            }
            List<TemporaryResidenceAbsence> activeRequests = temporaryResidenceAbsenceService.getActiveRequestsByMemberId(memberId);
            return !activeRequests.isEmpty();
        } catch (Exception e) {
            throw new ServiceException("Failed to check active requests: " + e.getMessage(), e);
        }
    }
    
    public List<TemporaryResidenceAbsence> getActiveRequestsByMemberId(int memberId) throws ServiceException {
        try {
            if (memberId <= 0) {
                throw new IllegalArgumentException("Member ID must be positive");
            }
            return temporaryResidenceAbsenceService.getActiveRequestsByMemberId(memberId);
        } catch (Exception e) {
            throw new ServiceException("Failed to get active requests by member ID: " + e.getMessage(), e);
        }
    }
    
    public boolean validateRequestDates(int memberId, LocalDate startDate, LocalDate endDate, Integer excludeRequestId) throws ServiceException {
        try {
            if (memberId <= 0) {
                throw new IllegalArgumentException("Member ID must be positive");
            }
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start date and end date cannot be null");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            
            return temporaryResidenceAbsenceService.validateRequestDates(memberId, startDate, endDate, excludeRequestId);
        } catch (Exception e) {
            throw new ServiceException("Failed to validate request dates: " + e.getMessage(), e);
        }
    }
    
    // ==================== Validation Methods ====================
    
    private void validateRequestDTO(TemporaryResidenceAbsenceDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Request data cannot be null");
        }
        
        if (dto.getMemberId() <= 0) {
            throw new IllegalArgumentException("Member ID must be positive");
        }
        
        if (dto.getMemberName() == null || dto.getMemberName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }
        
        if (dto.getType() == null || (!dto.getType().equals("RESIDENCE") && !dto.getType().equals("ABSENCE"))) {
            throw new IllegalArgumentException("Type must be either 'RESIDENCE' or 'ABSENCE'");
        }
        
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        
        // Validate date is not in the past (for new requests)
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
    
    public String getTypeDisplayName(String type) {
        if ("RESIDENCE".equals(type)) {
            return "Tạm trú";
        } else if ("ABSENCE".equals(type)) {
            return "Tạm vắng";
        }
        return type;
    }
    
    public String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Từ chối";
            default: return status;
        }
    }
}