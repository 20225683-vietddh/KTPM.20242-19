package controllers;

import java.sql.SQLException;
import java.util.List;
import models.Resident;
import services.ResidentService;
import exception.InvalidInputException;

public class ManageResidentController {
    private final ResidentService service;

    public ManageResidentController() throws SQLException {
        this.service = new ResidentService();
    }

    // Lấy danh sách tất cả nhân khẩu
    public List<Resident> handleGetAllResidents() {
        return service.getAllResidents();
    }

    // Lấy nhân khẩu theo ID
    public Resident handleGetResidentById(int id) {
        return service.getResidentById(id);
    }

    // Thêm nhân khẩu mới
    public void handleAddResident(Resident resident) throws InvalidInputException, SQLException {
        validateResident(resident);
        service.addResident(resident);
    }

    // Cập nhật nhân khẩu
    public void handleUpdateResident(Resident resident) throws InvalidInputException, SQLException {
        validateResident(resident);
        service.updateResident(resident);
    }

    // Xóa nhân khẩu
    public void handleDeleteResident(int id) throws SQLException {
        service.deleteResident(id);
    }

    // Kiểm tra dữ liệu đầu vào
    private void validateResident(Resident resident) throws InvalidInputException {
        if (resident.getFullName() == null || resident.getFullName().trim().isEmpty()) {
            throw new InvalidInputException("Tên nhân khẩu không được để trống!");
        }
        if (resident.getDateOfBirth() == null) {
            throw new InvalidInputException("Ngày sinh không được để trống!");
        }
        if (resident.getCitizenId() == null || resident.getCitizenId().trim().isEmpty()) {
            throw new InvalidInputException("Số căn cước công dân không được để trống!");
        }
    }
}
