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
	
    
    
    List<Household> getAll() throws ServiceException;
    
    Household getHouseholdById(int householdId) throws HouseholdNotExist, ServiceException;
  
    List<Resident> getResidents(int householdId) throws HouseholdNotExist, ServiceException;
    
    List<Integer> getResidentIds(int householdId) throws HouseholdNotExist, ServiceException;
    
   
	int getResidentCount(int id) throws HouseholdNotExist, ServiceException;

	void addHousehold(Household household) throws HouseholdAlreadyExistsException,ResidentNotFoundException,InvalidHouseholdDataException, HouseholdNotExist, SQLException, ServiceException;
   
    void addResidentToHousehold(Household h, String ResidentCitizenId) throws HouseholdNotExist, ServiceException, SQLException;

    void addResidentsToHousehold(Household h, List<String> residentCitizenIds) throws HouseholdNotExist, ServiceException, SQLException;
    
	void updateHousehold(Household household) throws HouseholdNotExist,HouseholdAlreadyExistsException,ResidentNotFoundException,
    	InvalidHouseholdDataException, SQLException, ServiceException;
    
  
    boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException;
    
    void removeResident(Household h, String ResidentCitizenId) throws HouseholdNotExist, ServiceException, SQLException;
    
    boolean phoneExists(String phone) throws ServiceException;
    
    boolean emailExists(String email) throws ServiceException;
    
    boolean roomExists(String roomNumber) throws ServiceException;
}
