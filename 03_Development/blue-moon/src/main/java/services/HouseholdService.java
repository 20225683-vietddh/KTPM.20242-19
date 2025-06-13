package services;

import dao.households.HouseholdDAO;
import dao.households.HouseholdDAOPostgreSQL;
import models.Household;
import models.Resident;
import java.sql.SQLException;
import java.util.List;

public class HouseholdService {
    private final HouseholdDAO householdDAO;
    private final ChangeHistoryService changeHistoryService;
    
    public HouseholdService() throws SQLException {
        this.householdDAO = new HouseholdDAOPostgreSQL();
        this.changeHistoryService = new ChangeHistoryService();
    }
    
    /**
     * Lấy tất cả hộ khẩu
     */
    public List<Household> getAllHouseholds() {
        return householdDAO.getAllHouseholds();
    }
    
    /**
     * Lấy thông tin chi tiết hộ khẩu theo ID (bao gồm danh sách cư dân)
     */
    public Household getHouseholdWithResidents(int householdId) {
        return householdDAO.getHouseholdById(householdId);
    }
    
    /**
     * Lấy thông tin cơ bản hộ khẩu theo ID
     */
    public Household getHouseholdById(int householdId) {
        Household household = householdDAO.getHouseholdById(householdId);
        if (household != null) {
            // Không load residents để tiết kiệm tài nguyên nếu chỉ cần thông tin cơ bản
            household.setResidents(null);
        }
        return household;
    }
    
    /**
     * Lấy danh sách cư dân của một hộ khẩu
     */
    public List<Resident> getResidentsByHouseholdId(int householdId) {
        return householdDAO.getResidentsByHouseholdId(householdId);
    }
    
    /**
     * Tìm kiếm hộ khẩu theo từ khóa
     */
    public List<Household> searchHouseholds(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHouseholds();
        }
        return householdDAO.searchHouseholds(keyword.trim());
    }
    
    /**
     * Thêm hộ khẩu mới
     */
    public boolean addHousehold(Household household) {
        if (household == null) {
            return false;
        }
        return householdDAO.addHousehold(household);
    }
    
    /**
     * Cập nhật thông tin hộ khẩu
     */
    public boolean updateHousehold(Household household) {
        if (household == null || household.getHouseholdId() <= 0) {
            return false;
        }
        return householdDAO.updateHousehold(household);
    }
    
    /**
     * Xóa hộ khẩu
     */
    public boolean deleteHousehold(int householdId) {
        if (householdId <= 0) {
            return false;
        }
        return householdDAO.deleteHousehold(householdId);
    }
    
    /**
     * Xóa hộ khẩu một cách an toàn (xóa tất cả nhân khẩu và reset phòng)
     * Đây là hành động nguy hiểm và không thể hoàn tác!
     */
    public boolean deleteHouseholdSafely(int householdId) throws Exception {
        if (householdId <= 0) {
            throw new Exception("ID hộ khẩu không hợp lệ");
        }
        
        // Lấy thông tin hộ khẩu trước khi xóa
        Household household = householdDAO.getHouseholdById(householdId);
        if (household == null) {
            throw new Exception("Không tìm thấy hộ khẩu với ID: " + householdId);
        }
        
        String roomNumber = household.getHouseNumber();
        
        try {
            // Bước 1: Xóa tất cả nhân khẩu trong hộ khẩu
            dao.residents.ResidentDAO residentDAO = new dao.residents.ResidentDAOPostgreSQL();
            List<Resident> residents = residentDAO.getResidentsByHouseholdId(householdId);
            
            for (Resident resident : residents) {
                // Ghi lại lịch sử TRƯỚC khi xóa
                changeHistoryService.recordResidentDeleted(
                    resident.getResidentId(), 
                    resident.getHouseholdId(), 
                    resident.getFullName()
                );
                
                boolean residentDeleted = residentDAO.deleteResident(resident.getResidentId());
                if (!residentDeleted) {
                    throw new Exception("Không thể xóa nhân khẩu: " + resident.getFullName());
                }
            }
            
            // Xóa tất cả lịch sử thay đổi của hộ khẩu sau khi xóa hết nhân khẩu
            changeHistoryService.deleteHouseholdChangeHistory(householdId);
            
            // Bước 2: Xóa hộ khẩu
            boolean householdDeleted = householdDAO.deleteHousehold(householdId);
            if (!householdDeleted) {
                throw new Exception("Không thể xóa hộ khẩu");
            }
            
            // Bước 3: Reset trạng thái phòng về trống
            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                dao.rooms.RoomDAO roomDAO = new dao.rooms.RoomDAOPostgreSQL();
                boolean roomUpdated = roomDAO.updateRoomStatus(roomNumber, true);
                if (!roomUpdated) {
                    System.err.println("Warning: Không thể cập nhật trạng thái phòng " + roomNumber + " về trống");
                    // Không throw exception vì dữ liệu chính đã được xóa thành công
                }
            }
            
            return true;
            
        } catch (Exception e) {
            // Log lỗi chi tiết
            System.err.println("Lỗi khi xóa hộ khẩu ID " + householdId + ": " + e.getMessage());
            throw new Exception("Không thể xóa hộ khẩu: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra hộ khẩu có tồn tại không
     */
    public boolean householdExists(int householdId) {
        return getHouseholdById(householdId) != null;
    }
    
    /**
     * Lấy tổng số hộ khẩu
     */
    public int getTotalHouseholds() {
        return getAllHouseholds().size();
    }
    
    /**
     * Lấy số nhân khẩu thực tế của một hộ khẩu (tính từ database)
     */
    public int getActualResidentsCount(int householdId) {
        List<Resident> residents = getResidentsByHouseholdId(householdId);
        return residents != null ? residents.size() : 0;
    }
    
    /**
     * Đồng bộ số nhân khẩu trong database với số nhân khẩu thực tế
     */
    public boolean syncResidentsCount(int householdId) {
        return householdDAO.updateResidentsCount(householdId);
    }
    
    /**
     * Đồng bộ số nhân khẩu cho tất cả hộ khẩu
     */
    public boolean syncAllResidentsCount() {
        try {
            List<Household> households = getAllHouseholds();
            boolean allSuccess = true;
            for (Household household : households) {
                if (!syncResidentsCount(household.getHouseholdId())) {
                    allSuccess = false;
                }
            }
            return allSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Thêm hộ khẩu mới cùng với danh sách thành viên
     */
    public boolean addHouseholdWithResidents(Household household, List<Resident> residents) {
        try {
            // Đặt head_resident_id = null trước khi thêm
            household.setHeadResidentId(0);
            
            // Thêm hộ khẩu trước (với head_resident_id = null)
            boolean householdAdded = householdDAO.addHousehold(household);
            if (!householdAdded) {
                return false;
            }
            
            // Lấy ID của hộ khẩu vừa thêm
            List<Household> households = householdDAO.searchHouseholds(household.getHouseNumber());
            if (households.isEmpty()) {
                return false;
            }
            
            Household addedHousehold = households.get(0);
            int householdId = addedHousehold.getHouseholdId();
            
            // Thêm từng thành viên
            dao.residents.ResidentDAO residentDAO = new dao.residents.ResidentDAOPostgreSQL();
            int headResidentId = 0;
            
            for (Resident resident : residents) {
                resident.setHouseholdId(householdId);
                boolean residentAdded = residentDAO.addResident(resident);
                
                if (!residentAdded) {
                    // Rollback nếu có lỗi
                    householdDAO.deleteHousehold(householdId);
                    return false;
                }
                
                // Lưu ID chủ hộ ngay sau khi thêm thành công
                if ("Chủ hộ".equals(resident.getRelationshipWithHead())) {
                    // Lấy ID của resident vừa thêm bằng cách tìm theo CCCD
                    List<Resident> addedResidents = residentDAO.getResidentsByHouseholdId(householdId);
                    for (Resident r : addedResidents) {
                        if (r.getCitizenId().equals(resident.getCitizenId())) {
                            headResidentId = r.getResidentId();
                            break;
                        }
                    }
                }
            }
            
            // Cập nhật head_resident_id cho hộ khẩu sau khi đã thêm tất cả residents
            if (headResidentId > 0) {
                addedHousehold.setHeadResidentId(headResidentId);
                boolean updateSuccess = householdDAO.updateHousehold(addedHousehold);
                if (!updateSuccess) {
                    System.err.println("Warning: Could not update head_resident_id for household " + householdId);
                    // Không rollback vì dữ liệu cơ bản đã được thêm thành công
                }
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 