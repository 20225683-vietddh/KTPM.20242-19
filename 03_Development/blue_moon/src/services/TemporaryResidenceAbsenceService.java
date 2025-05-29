package services;

import java.time.LocalDate;
import java.util.List;

import dao.TemporaryResidenceAbsenceDAO;
import dto.TemporaryResidenceAbsenceDTO;
import models.TemporaryResidenceAbsence;

public class TemporaryResidenceAbsenceService {
    private TemporaryResidenceAbsenceDAO temporaryResidenceAbsenceDAO;
    
    public TemporaryResidenceAbsenceService() {
        this.temporaryResidenceAbsenceDAO = new TemporaryResidenceAbsenceDAO();
    }
    
    public List<TemporaryResidenceAbsence> getAllTemporaryResidenceAbsences() {
        return temporaryResidenceAbsenceDAO.getAll();
    }
    
    public List<TemporaryResidenceAbsence> getByMemberId(int memberId) {
        return temporaryResidenceAbsenceDAO.getByMemberId(memberId);
    }
    
    public boolean saveTemporaryResidenceAbsence(TemporaryResidenceAbsenceDTO tempRADTO) {
        if (!validate(tempRADTO)) {
            return false;
        }
        
        TemporaryResidenceAbsence tempRA = convertToTemporaryResidenceAbsence(tempRADTO);
        return save(tempRA);
    }
    
    public boolean updateTemporaryResidenceAbsence(TemporaryResidenceAbsenceDTO tempRADTO) {
        if (!validate(tempRADTO)) {
            return false;
        }
        
        TemporaryResidenceAbsence tempRA = convertToTemporaryResidenceAbsence(tempRADTO);
        return temporaryResidenceAbsenceDAO.update(tempRA);
    }
    
    public boolean deleteTemporaryResidenceAbsence(int id) {
        return temporaryResidenceAbsenceDAO.delete(id);
    }
    
    private boolean validate(TemporaryResidenceAbsenceDTO tempRADTO) {
        // Validate member ID
        if (tempRADTO.getMemberId() <= 0) {
            return false;
        }
        
        // Validate type
        if (tempRADTO.getType() == null || (!tempRADTO.getType().equals("RESIDENCE") && !tempRADTO.getType().equals("ABSENCE"))) {
            return false;
        }
        
        // Validate dates
        if (tempRADTO.getStartDate() == null || tempRADTO.getEndDate() == null) {
            return false;
        }
        
        // End date must be after start date
        if (tempRADTO.getEndDate().isBefore(tempRADTO.getStartDate())) {
            return false;
        }
        
        // Start date must not be in the past
        if (tempRADTO.getStartDate().isBefore(LocalDate.now())) {
            return false;
        }
        
        // Validate address
        if (tempRADTO.getAddress() == null || tempRADTO.getAddress().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    private TemporaryResidenceAbsence convertToTemporaryResidenceAbsence(TemporaryResidenceAbsenceDTO tempRADTO) {
        TemporaryResidenceAbsence tempRA = new TemporaryResidenceAbsence();
        tempRA.setId(tempRADTO.getId());
        tempRA.setMemberId(tempRADTO.getMemberId());
        tempRA.setMemberName(tempRADTO.getMemberName());
        tempRA.setType(tempRADTO.getType());
        tempRA.setReason(tempRADTO.getReason());
        tempRA.setStartDate(tempRADTO.getStartDate());
        tempRA.setEndDate(tempRADTO.getEndDate());
        tempRA.setAddress(tempRADTO.getAddress());
        tempRA.setStatus("PENDING"); // Default status for new entries
        
        return tempRA;
    }
    
    private boolean save(TemporaryResidenceAbsence tempRA) {
        return temporaryResidenceAbsenceDAO.save(tempRA);
    }
}