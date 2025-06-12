package dao.households;

import models.Household;
import models.Resident;
import java.util.List;

public interface HouseholdDAO {
    // Lấy tất cả hộ khẩu
    public List<Household> getAllHouseholds();
    
    // Lấy hộ khẩu theo ID
    public Household getHouseholdById(int householdId);
    
    // Lấy danh sách cư dân của một hộ khẩu
    public List<Resident> getResidentsByHouseholdId(int householdId);
    
    // Tìm kiếm hộ khẩu theo từ khóa
    public List<Household> searchHouseholds(String keyword);
    
    // Thêm hộ khẩu mới
    public boolean addHousehold(Household household);
    
    // Cập nhật thông tin hộ khẩu
    public boolean updateHousehold(Household household);
    
    // Xóa hộ khẩu
    public boolean deleteHousehold(int householdId);
    
    // Cập nhật số nhân khẩu thực tế trong database (sync data)
    public boolean updateResidentsCount(int householdId);
} 