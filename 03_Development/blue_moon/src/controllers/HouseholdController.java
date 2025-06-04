package controllers;

import java.sql.SQLException;
import java.util.List;

import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.MemberNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.HouseholdService;
import services.HouseholdServiceImpl;
import services.MemberServiceImpl;


public class HouseholdController {
    private HouseholdService householdService = new HouseholdServiceImpl();
    private MemberServiceImpl memberService = new MemberServiceImpl();
    
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
    
    public void addHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException, MemberNotFoundException, InvalidHouseholdDataException, SQLException, ServiceException {
        householdService.addHousehold(household);
        
        System.out.println("Add completed successfully");
        getAllHouseholds();
         
    }
    
    public Household getHouseholdDetails(int id) throws HouseholdNotExist, ServiceException {
        return householdService.getHouseholdById(id);
    }
    
    
    public void updateHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException, MemberNotFoundException, InvalidHouseholdDataException, SQLException, ServiceException {
        householdService.updateHousehold(household);
        System.out.println("Update completed successfully");
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

    

	public void addMemberToHousehold(Household h, String id) throws HouseholdNotExist, ServiceException, SQLException {
		householdService.addMemberToHousehold(h,id);
	}
	
	public void addMembersToHousehold(Household h, List<String> ids) throws HouseholdNotExist, ServiceException, SQLException {
		for (String id : ids) householdService.addMemberToHousehold(h,id);
	}


	public int getMemberCount(int id) throws HouseholdNotExist, ServiceException {
		return householdService.getMemberCount(id);
	}

	public List<Resident> getMembers(int householdId) throws ServiceException {
		try {
			List<Resident> members = memberService.getMembersByHouseholdId(householdId);
			System.out.println("Found " + members.size() + " members for household " + householdId);
			return members;
		} catch (ServiceException e) {
			System.err.println("Error getting members for household " + householdId + ": " + e.getMessage());
			throw e;
		}
	}
	
	public List<String> getMemberIds(int householdId) throws HouseholdNotExist{
		return householdService.getMemberIds(householdId);
	}
	
	public void removeMember(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException {
		householdService.removeMember(h,memberId);
	}
}