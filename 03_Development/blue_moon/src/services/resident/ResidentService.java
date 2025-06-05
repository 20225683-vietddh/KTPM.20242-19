package services.resident;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Resident;

//MemberService interface
public interface ResidentService {
	
	List<Resident> getAll() throws ServiceException; //
	
	Resident getResidentById(int residentId) throws ServiceException; //
	
	Resident getResidentByCitizenId(String citizenId) throws ServiceException; //

	List<Resident> getResidentsByHouseholdId(int householdId) throws ServiceException;
	
	List<Resident> getResidentsByIds(List<Integer> residentIds) throws ServiceException;

	boolean addResident(Resident resident) throws ServiceException, SQLException; //

	boolean updateResident(Resident resident) throws ServiceException, SQLException; //
	
	void updateResidents(List<Resident> residents) throws ServiceException, SQLException;
	

	boolean deleteResident(int residentId) throws ServiceException, SQLException; //

	Resident getHouseholdHead(int householdId) throws ServiceException;

	boolean residentExists(int residentId);

	boolean citizenIdExists(String citizenId);

	
	
}