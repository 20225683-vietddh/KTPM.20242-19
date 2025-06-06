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
	public Resident getResidentById(int rId) throws ServiceException {
		return residentDAO.findById(rId);
	}


	@Override
	public Resident getResidentByCitizenId(String citizenId) throws ServiceException {
		return residentDAO.findByCitizenId(citizenId);
	}

    @Override
	public List<Resident> getResidentByCitizenIds(List<String> citizenIds) throws ServiceException {
        if (citizenIds == null || citizenIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Resident> residents = new ArrayList<>();
        List<String> notFoundIds = new ArrayList<>();
        
        for (String citizenId : citizenIds) {
            try {
                Resident resident = getResidentByCitizenId(citizenId);
                residents.add(resident);
            } catch (ServiceException e) {
                notFoundIds.add(citizenId);
            }
        }
        
        if (!notFoundIds.isEmpty()) {
            throw new ServiceException("Không tìm thấy cư dân với CCCD: " + String.join(", ", notFoundIds));
        }
        
        return residents;
	}


	@Override
	public List<Resident> getResidentsByHouseholdId(int householdId) throws ServiceException {
    	return residentDAO.findByHouseholdId(householdId);
	}
//
//
//	@Override
//	public List<Resident> getResidentsByResidentsCitizenIds(List<String> residentCitizenIds) throws ServiceException {
//		List<Resident> residents = new ArrayList<>();
//    	for (String residentId : residentCitizenIds) {
//    		Resident r = residentDAO.findByCitizenId(residentId);
//    		residents.add(r);
//    	}
//    	return residents;
//	}


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
	public boolean deleteResident(String residentCitizenId) throws ServiceException, SQLException {
    	residentDAO.delete(residentCitizenId);
    	return true;
	}


	@Override
    public Resident getHouseholdHead(int householdId) throws ServiceException {
    	return residentDAO.getHouseholdHead(householdId);
    	
    }

	public void setHouseholdOwnerByResidentCitizenId(String ownerCitizenId) throws ServiceException, SQLException {
		residentDAO.setHouseholdOwnerByResidentCitizenId(ownerCitizenId);
	}

    public List<Resident> getAllResidents() throws ServiceException {
        return residentDAO.findAll();
    }


}
