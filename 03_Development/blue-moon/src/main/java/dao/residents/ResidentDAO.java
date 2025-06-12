package dao.residents;

import models.Resident;
import java.util.List;

public interface ResidentDAO {
    List<Resident> getAllResidents() throws Exception;
    List<Resident> getResidentsByHouseholdId(int householdId) throws Exception;
    List<Resident> searchResidents(String searchTerm) throws Exception;
    Resident getResidentById(int residentId) throws Exception;
    boolean addResident(Resident resident) throws Exception;
    boolean updateResident(Resident resident) throws Exception;
    boolean deleteResident(int residentId) throws Exception;
    int getResidentCountByHouseholdId(int householdId) throws Exception;
} 