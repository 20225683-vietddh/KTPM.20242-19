package services;

import java.sql.SQLException;
import java.util.List;

import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.MemberNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;

public interface HouseholdService {
	
    
    
    List<Household> getAllHouseholds() throws ServiceException;
    
    Household getHouseholdById(int householdId) throws HouseholdNotExist, ServiceException;
  
    List<Resident> getMembers(int householdId) throws HouseholdNotExist, ServiceException;
    
    List<String> getMemberIds(int householdId) throws HouseholdNotExist;
    
    
   
    void addHousehold(Household household) throws HouseholdAlreadyExistsException,MemberNotFoundException,InvalidHouseholdDataException, HouseholdNotExist, SQLException;
   
    void updateHousehold(Household household) throws HouseholdNotExist,HouseholdAlreadyExistsException,MemberNotFoundException,
    	InvalidHouseholdDataException, SQLException;
    
  
    boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException;
    
    int getMemberCount(int id) throws HouseholdNotExist, ServiceException;

	void removeMember(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException;


	void addMemberToHousehold(Household h, String householdId) throws HouseholdNotExist, ServiceException, SQLException;
}
