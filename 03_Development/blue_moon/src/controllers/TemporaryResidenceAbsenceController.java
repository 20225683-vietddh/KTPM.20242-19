package controllers;

import dto.TemporaryResidenceAbsenceDTO;
import services.TemporaryResidenceAbsenceService;
import views.FormIssueTemporaryResidenceAbsenceHandler;

public class TemporaryResidenceAbsenceController {
    private TemporaryResidenceAbsenceService temporaryResidenceAbsenceService;
    
    public TemporaryResidenceAbsenceController() {
        this.temporaryResidenceAbsenceService = new TemporaryResidenceAbsenceService();
    }
    
    public void openIssueForm() {
        FormIssueTemporaryResidenceAbsenceHandler formIssueHandler = new FormIssueTemporaryResidenceAbsenceHandler(this);
        formIssueHandler.createIssueForm();
    }
    
    public void submitIssueRequest(TemporaryResidenceAbsenceDTO tempRADTO) {
        boolean success = temporaryResidenceAbsenceService.saveTemporaryResidenceAbsence(tempRADTO);
        if (success) {
            showSuccessMessage();
        } else {
            showErrorMessage("Failed to issue temporary residence/absence. Please check your input.");
        }
    }
    
    public void showSuccessMessage() {
        // Implementation would show success message in UI
        System.out.println("Temporary residence/absence request submitted successfully");
    }
    
    public void showErrorMessage(String message) {
        // Implementation would show error message in UI
        System.err.println("Error: " + message);
    }
}