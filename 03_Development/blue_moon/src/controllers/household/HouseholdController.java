package controllers.household;

import java.sql.SQLException;
import java.util.List;

import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.household.HouseholdService;
import services.household.HouseholdServiceImpl;
import services.resident.ResidentServiceImpl;


public class HouseholdController {
    private HouseholdService householdService = new HouseholdServiceImpl();
    private ResidentServiceImpl memberService = new ResidentServiceImpl();
    
    public HouseholdService getHouseholdService() {
		return householdService;
	}

	public void setHouseholdService(HouseholdServiceImpl householdService) {
		this.householdService = householdService;
	}

	public HouseholdController() {
    }
    
    public List<Household> getAllHouseholds() throws ServiceException {
        return householdService.getAllHouseholds();
    }
    
    public void addHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, SQLException, ServiceException {
        householdService.addHousehold(household);
        
        System.out.println("Add completed successfully");
        getAllHouseholds();
         
    }
    
    public Household getHouseholdDetails(int id) throws HouseholdNotExist, ServiceException {
        return householdService.getHouseholdById(id);
    }
    
    
    public void updateHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, SQLException, ServiceException {
        householdService.updateHousehold(household);
        getAllHouseholds();
    }
    
    public boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException {
        try {
			householdService.deleteHousehold(id);
	         System.out.println("Delete completed successfully");
	         return true;
		} catch (HouseholdNotExist e) {
			e.printStackTrace();
		}
		return false;
         
      
       
    }
    
    public void showErrorMessage(String message) {
        // Implementation would show error message in UI
        System.err.println("Error: " + message);
    }

    

	public void addResidentToHousehold(Household h, int residentId) throws HouseholdNotExist, ServiceException, SQLException {
		householdService.addResidentToHousehold(h,residentId);
	}
	
	public void addResidentsToHousehold(Household h, List<Integer> ids) throws HouseholdNotExist, ServiceException, SQLException {
		householdService.addResidentsToHousehold(h,ids);
	}


	public int getResidentCount(int id) throws HouseholdNotExist, ServiceException {
		return householdService.getResidentCount(id);
	}

	public List<Resident> getResidents(int householdId) throws ServiceException {
		try {
			List<Resident> residents = memberService.getResidentsByHouseholdId(householdId);
			System.out.println("Found " + residents.size() + " residents for household " + householdId);
			return residents;
		} catch (ServiceException e) {
			System.err.println("Error getting residents for household " + householdId + ": " + e.getMessage());
			throw e;
		}
	}
	
	public List<Integer> getResidentIds(int householdId) throws HouseholdNotExist{
		return householdService.getResidentIds(householdId);
	}
	
	public void removeResident(Household h, int memberId) throws HouseholdNotExist, ServiceException, SQLException {
		householdService.removeResident(h,memberId);
	}
}