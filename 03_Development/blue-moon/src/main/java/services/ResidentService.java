package services;

import models.Resident;
import dao.residents.ResidentDAO;
import dao.residents.ResidentDAOPostgreSQL;
import java.sql.SQLException;
import java.util.List;

public class ResidentService {
    private ResidentDAO residentDAO;
    
    public ResidentService() {
        try {
            this.residentDAO = new ResidentDAOPostgreSQL();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối database cho ResidentService: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo ResidentService: " + e.getMessage());
        }
    }
    
    public List<Resident> getAllResidents() throws Exception {
        return residentDAO.getAllResidents();
    }
    
    public List<Resident> getResidentsByHouseholdId(int householdId) throws Exception {
        return residentDAO.getResidentsByHouseholdId(householdId);
    }
    
    public List<Resident> searchResidents(String searchTerm) throws Exception {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllResidents();
        }
        return residentDAO.searchResidents(searchTerm.trim());
    }
    
    public Resident getResidentById(int residentId) throws Exception {
        return residentDAO.getResidentById(residentId);
    }
    
    public boolean addResident(Resident resident) throws Exception {
        // Validation
        if (resident.getFullName() == null || resident.getFullName().trim().isEmpty()) {
            throw new Exception("Tên không được để trống");
        }
        
        if (resident.getDateOfBirth() == null) {
            throw new Exception("Ngày sinh không được để trống");
        }
        
        if (resident.getCitizenId() == null || resident.getCitizenId().trim().isEmpty()) {
            throw new Exception("Số CCCD không được để trống");
        }
        
        if (resident.getHouseholdId() <= 0) {
            throw new Exception("Phải chọn hộ khẩu");
        }
        
        return residentDAO.addResident(resident);
    }
    
    public boolean updateResident(Resident resident) throws Exception {
        // Validation
        if (resident.getResidentId() <= 0) {
            throw new Exception("ID nhân khẩu không hợp lệ");
        }
        
        if (resident.getFullName() == null || resident.getFullName().trim().isEmpty()) {
            throw new Exception("Tên không được để trống");
        }
        
        if (resident.getDateOfBirth() == null) {
            throw new Exception("Ngày sinh không được để trống");
        }
        
        if (resident.getCitizenId() == null || resident.getCitizenId().trim().isEmpty()) {
            throw new Exception("Số CCCD không được để trống");
        }
        
        if (resident.getHouseholdId() <= 0) {
            throw new Exception("Phải chọn hộ khẩu");
        }
        
        return residentDAO.updateResident(resident);
    }
    
    public boolean deleteResident(int residentId) throws Exception {
        if (residentId <= 0) {
            throw new Exception("ID nhân khẩu không hợp lệ");
        }
        
        return residentDAO.deleteResident(residentId);
    }
    
    /**
     * Xóa nhân khẩu một cách an toàn (kiểm tra và cập nhật hộ khẩu)
     */
    public boolean deleteResidentSafely(int residentId) throws Exception {
        if (residentId <= 0) {
            throw new Exception("ID nhân khẩu không hợp lệ");
        }
        
        // Lấy thông tin nhân khẩu trước khi xóa
        Resident resident = residentDAO.getResidentById(residentId);
        if (resident == null) {
            throw new Exception("Không tìm thấy nhân khẩu với ID: " + residentId);
        }
        
        int householdId = resident.getHouseholdId();
        boolean isHeadOfHousehold = "Chủ hộ".equals(resident.getRelationshipWithHead());
        
        try {
            // Bước 1: Kiểm tra nếu là chủ hộ và còn thành viên khác
            if (isHeadOfHousehold) {
                List<Resident> allResidents = residentDAO.getResidentsByHouseholdId(householdId);
                if (allResidents.size() > 1) {
                    throw new Exception("Không thể xóa chủ hộ khi hộ khẩu còn thành viên khác!\n" +
                                      "Vui lòng chuyển quyền chủ hộ cho người khác hoặc xóa tất cả thành viên trước.");
                }
            }
            
            // Bước 2: Xóa nhân khẩu
            boolean residentDeleted = residentDAO.deleteResident(residentId);
            if (!residentDeleted) {
                throw new Exception("Không thể xóa nhân khẩu");
            }
            
            // Bước 3: Kiểm tra và cập nhật hộ khẩu
            List<Resident> remainingResidents = residentDAO.getResidentsByHouseholdId(householdId);
            
            if (remainingResidents.isEmpty()) {
                // Nếu không còn ai trong hộ khẩu, xóa hộ khẩu và reset phòng
                dao.households.HouseholdDAO householdDAO = new dao.households.HouseholdDAOPostgreSQL();
                models.Household household = householdDAO.getHouseholdById(householdId);
                
                if (household != null) {
                    String roomNumber = household.getHouseNumber();
                    
                    // Xóa hộ khẩu
                    boolean householdDeleted = householdDAO.deleteHousehold(householdId);
                    if (!householdDeleted) {
                        System.err.println("Warning: Không thể xóa hộ khẩu rỗng ID " + householdId);
                    }
                    
                    // Reset trạng thái phòng
                    if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                        dao.rooms.RoomDAO roomDAO = new dao.rooms.RoomDAOPostgreSQL();
                        boolean roomUpdated = roomDAO.updateRoomStatus(roomNumber, true);
                        if (!roomUpdated) {
                            System.err.println("Warning: Không thể cập nhật trạng thái phòng " + roomNumber + " về trống");
                        }
                    }
                }
            } else {
                // Nếu còn thành viên, cập nhật số lượng nhân khẩu và kiểm tra chủ hộ
                dao.households.HouseholdDAO householdDAO = new dao.households.HouseholdDAOPostgreSQL();
                
                // Cập nhật số lượng nhân khẩu
                boolean countUpdated = householdDAO.updateResidentsCount(householdId);
                if (!countUpdated) {
                    System.err.println("Warning: Không thể cập nhật số lượng nhân khẩu cho hộ khẩu ID " + householdId);
                }
                
                // Nếu người bị xóa là chủ hộ, cần reset head_resident_id
                if (isHeadOfHousehold) {
                    models.Household household = householdDAO.getHouseholdById(householdId);
                    if (household != null) {
                        household.setHeadResidentId(0); // Reset về null
                        boolean headUpdated = householdDAO.updateHousehold(household);
                        if (!headUpdated) {
                            System.err.println("Warning: Không thể reset chủ hộ cho hộ khẩu ID " + householdId);
                        }
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa nhân khẩu ID " + residentId + ": " + e.getMessage());
            throw new Exception("Không thể xóa nhân khẩu: " + e.getMessage());
        }
    }
    
    public int getResidentCountByHouseholdId(int householdId) throws Exception {
        return residentDAO.getResidentCountByHouseholdId(householdId);
    }
} 