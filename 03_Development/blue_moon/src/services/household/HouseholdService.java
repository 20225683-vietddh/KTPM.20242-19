package services.household;

import java.sql.SQLException;
import java.util.List;

import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;

public interface HouseholdService {
	
    
    
    List<Household> getAllHouseholds() throws ServiceException;
    
    Household getHouseholdById(int householdId) throws HouseholdNotExist, ServiceException;
  
    List<Resident> getResidents(int householdId) throws HouseholdNotExist, ServiceException;
    
    List<Integer> getResidentIds(int householdId) throws HouseholdNotExist;
    
   
    int getResidentCount(int id) throws HouseholdNotExist, ServiceException;

	void addHousehold(Household household) throws HouseholdAlreadyExistsException,ResidentNotFoundException,InvalidHouseholdDataException, HouseholdNotExist, SQLException;
   
    void addResidentToHousehold(Household h, int ResidentId) throws HouseholdNotExist, ServiceException, SQLException;

    void addResidentsToHousehold(Household h, List<Integer> residentIds) throws HouseholdNotExist, ServiceException, SQLException;
    
	void updateHousehold(Household household) throws HouseholdNotExist,HouseholdAlreadyExistsException,ResidentNotFoundException,
    	InvalidHouseholdDataException, SQLException;
    
  
    boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException;
    
    void removeResident(Household h, int ResidentId) throws HouseholdNotExist, ServiceException, SQLException;
}
