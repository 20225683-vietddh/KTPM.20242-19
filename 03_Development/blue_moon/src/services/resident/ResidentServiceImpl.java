package services.resident;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.resident.ResidentDAO;
import exception.ServiceException;
import models.Resident;

public class ResidentServiceImpl  implements ResidentService {

	private final ResidentDAO residentDAO = new ResidentDAO();

	@Override
	public Resident getResidentById(int residentId) throws ServiceException {
		return residentDAO.findById(residentId) ;
	}


	@Override
	public Resident getResidentByCitizenId(String citizenId) throws ServiceException {
		return residentDAO.findByCitizenId(citizenId);
	}

    @Override
	public List<Resident> getResidentsByHouseholdId(int householdId) throws ServiceException {
    	return residentDAO.findByHouseholdId(householdId);
	}


	@Override
	public List<Resident> getResidentsByIds(List<Integer> residentIds) throws ServiceException {
		List<Resident> residents = new ArrayList<>();
    	for (int residentId : residentIds) {
    		Resident r = residentDAO.findById(residentId);
    		residents.add(r);
    	}
    	return residents;
	}


	@Override
	public List<Resident> getAll() throws ServiceException {
		return residentDAO.findAll();
	}

	@Override
    public boolean residentExists(int residentId) {
    	return residentDAO.residentExists(residentId);
    }

    @Override
    public boolean citizenIdExists(String citizenId) {
        return residentDAO.citizenIdExists(citizenId);
    }

	@Override
	public boolean addResident(Resident resident) throws ServiceException, SQLException {
		residentDAO.add(resident);
		return true;
	}

    @Override
	public boolean updateResident(Resident resident) throws ServiceException, SQLException {
    	residentDAO.update(resident);
    	return true;
	}


	@Override
	public void updateResidents(List<Resident> residents) throws ServiceException, SQLException {
		residentDAO.updateMembers(residents);
		
	}

    @Override
	public boolean deleteResident(int residentId) throws ServiceException, SQLException {
    	residentDAO.delete(residentId);
    	return true;
	}


	@Override
    public Resident getHouseholdHead(int householdId) throws ServiceException {
    	return residentDAO.getHouseholdHead(householdId);
    	
    }

	public void setHouseholdOwnerByResidentId(int ownerId) throws ServiceException, SQLException {
		residentDAO.setHouseholdOwnerByResidentId(ownerId);
	}





}
