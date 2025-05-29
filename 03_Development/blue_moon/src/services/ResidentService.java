package services;

import java.sql.SQLException;
import java.util.List;
import models.Resident;
import dao.resident.ResidentDAO;

public class ResidentService {
    private final ResidentDAO residentDAO;

    public ResidentService() throws SQLException {
        this.residentDAO = new ResidentDAO();
    }

    // Lấy danh sách tất cả nhân khẩu
    public List<Resident> getAllResidents() {
        return residentDAO.getAllResidents();
    }

    // Lấy nhân khẩu theo ID
    public Resident getResidentById(int id) {
        return residentDAO.getResidentById(id);
    }

    // Thêm nhân khẩu mới
    public void addResident(Resident resident) throws SQLException {
        if (resident.getFullName() == null || resident.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân khẩu không được để trống!");
        }
        residentDAO.addResident(resident);
    }

    // Cập nhật nhân khẩu
    public void updateResident(Resident resident) throws SQLException {
        if (resident.getFullName() == null || resident.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân khẩu không được để trống!");
        }
        residentDAO.updateResident(resident);
    }

    // Xóa nhân khẩu
    public void deleteResident(int id) throws SQLException {
        residentDAO.deleteResident(id);
    }
}
