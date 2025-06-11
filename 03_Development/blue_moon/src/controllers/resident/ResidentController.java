package controllers.resident;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Resident;
import services.resident.ResidentServiceImpl;

public class ResidentController {
	private ResidentServiceImpl residentService;

    public ResidentController() throws SQLException {
        this.residentService = new ResidentServiceImpl();
    }

    public List<Resident> getAllResidents() throws ServiceException {
        return residentService.getAllResidents();
    }

    public ResidentServiceImpl getResidentService() {
		return residentService;
	}
    
	public void setResidentService(ResidentServiceImpl residentService) {
		this.residentService = residentService;
	}

	// Lay resident theo ID
    public Resident getResidentByCitizenId(String residentCitizenId) {
        try {
            return residentService.getResidentByCitizenId(residentCitizenId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get resident by ID: " + e.getMessage());
            return null;
        }
    }

    // Lay danh sach resident cua 1 ho gia dinh
    public List<Resident> getResidentsByHouseholdId(int householdId) {
        try {
            return residentService.getResidentsByHouseholdId(householdId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get residents by household ID: " + e.getMessage());
            return null;
        }
    }

    // Kiem tra ton tai bang id
    public boolean residentExists(int residentId) {
        return residentService.residentExists(residentId);
    }

    // Them resident
    public boolean addResident(Resident resident) throws SQLException {
        try {
            boolean result = residentService.addResident(resident);
            if (result) {
                System.out.println("Resident added successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to add resident: " + e.getMessage());
            return false;
        }
    }

    // Cap nhat resident
    public boolean updateResident(Resident resident) throws SQLException {
        try {
            boolean result = residentService.updateResident(resident);
            if (result) {
                System.out.println("Resident updated successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to update resident: " + e.getMessage());
            return false;
        }
    }

    // Xoa resident
    public boolean deleteResident(String residentCitizenId) throws SQLException {
        try {
            boolean result = residentService.deleteResident(residentCitizenId);
            if (result) {
                System.out.println("Resident deleted successfully.");
            }
            return result;
        } catch (ServiceException e) {
            showErrorMessage("Failed to delete resident: " + e.getMessage());
            return false;
        }
    }

    // Lay chu ho
    public Resident getHouseholdHead(int householdId) {
        try {
            return residentService.getHouseholdHead(householdId);
        } catch (ServiceException e) {
            showErrorMessage("Failed to get household head: " + e.getMessage());
            return null;
        }
    }

    // In loi
    public void showErrorMessage(String message) {
        // Thay the bang UI dialog neu co
        System.err.println("Error: " + message);
    }
}
