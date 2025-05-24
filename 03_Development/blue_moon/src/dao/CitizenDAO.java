package dao;

import models.Citizen;
import java.util.ArrayList;
import java.util.List;

public class CitizenDAO {
    private final List<Citizen> citizens = new ArrayList<>();
    private int idCounter = 1; // Để tự động tăng ID khi thêm mới

    // Lấy danh sách tất cả nhân khẩu
    public List<Citizen> getAllCitizens() {
        return new ArrayList<>(citizens); // Trả về bản sao để tránh thay đổi ngoài ý muốn
    }

    // Thêm nhân khẩu mới
    public void addCitizen(Citizen citizen) {
        citizen.setId(idCounter++);
        citizens.add(citizen);
    }

    // Xóa nhân khẩu theo ID
    public boolean deleteCitizen(int id) {
        return citizens.removeIf(c -> c.getId() == id);
    }

    // Cập nhật thông tin nhân khẩu
    public boolean updateCitizen(Citizen updatedCitizen) {
        for (int i = 0; i < citizens.size(); i++) {
            if (citizens.get(i).getId() == updatedCitizen.getId()) {
                citizens.set(i, updatedCitizen);
                return true;
            }
        }
        return false;
    }

    // Tìm nhân khẩu theo ID
    public Citizen findById(int id) {
        return citizens.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
