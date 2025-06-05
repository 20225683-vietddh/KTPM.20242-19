package services.tempresidenceabsence;

import dto.TemporaryResidenceAbsenceDTO;
import exception.ServiceException;
import models.TemporaryResidenceAbsence;
import services.tempresidenceabsence.TemporaryResidenceAbsenceServiceImpl.RequestStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing temporary residence and absence requests
 * Provides business logic layer between controller and data access layer
 */
public interface TemporaryResidenceAbsenceService {
    
    // ==================== CRUD Operations ====================
    
    TemporaryResidenceAbsence createTemporaryRequest(TemporaryResidenceAbsenceDTO dto) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getAllTemporaryRequests() throws ServiceException;
    
    Optional<TemporaryResidenceAbsence> getTemporaryRequestById(int id) throws ServiceException;
    
    TemporaryResidenceAbsence updateTemporaryRequest(int id, TemporaryResidenceAbsenceDTO dto) throws ServiceException;
    
    boolean deleteTemporaryRequest(int id) throws ServiceException;
    
    // ==================== Status Management ====================
    
    
    TemporaryResidenceAbsence updateRequestStatus(int id, String status) throws ServiceException;
    
    // ==================== Search and Filter Operations ====================
    
    List<TemporaryResidenceAbsence> searchByMemberName(String memberName) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getRequestsByType(String type) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getRequestsByStatus(String status) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getRequestsByDateRange(LocalDate startDate, LocalDate endDate) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getRequestsByMemberId(int memberId) throws ServiceException;
    
    // ==================== Statistics ====================
    
    int getTotalRequestsCount() throws ServiceException;
    
    int getRequestsCountByStatus(String status) throws ServiceException;
    
    int getRequestsCountByType(String type) throws ServiceException;
    
    // ==================== Business Logic ====================
    
    List<TemporaryResidenceAbsence> getActiveRequestsByMemberId(int memberId) throws ServiceException;
    
    boolean validateRequestDates(int memberId, LocalDate startDate, LocalDate endDate, Integer excludeRequestId) throws ServiceException;
    
    List<TemporaryResidenceAbsence> getCurrentActiveRequests() throws ServiceException;
    
    List<TemporaryResidenceAbsence> getExpiringRequests(int daysAhead) throws ServiceException;
    
    boolean hasConflictingRequests(int memberId, LocalDate startDate, LocalDate endDate) throws ServiceException;
    
    RequestStatistics getRequestStatistics() throws ServiceException;
}