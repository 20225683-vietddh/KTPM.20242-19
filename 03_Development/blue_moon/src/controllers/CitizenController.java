package controllers;

import dao.CitizenDAO;
import models.Citizen;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;


public class CitizenController extends BaseController {
    private final CitizenDAO citizenDAO = new CitizenDAO();

   /* public List<Citizen> getAllCitizens() {
        return citizenDAO.getAllCitizens();
    } */
    
    public List<Citizen> getAllCitizens() {
        List<Citizen> citizens = new ArrayList<>();
        citizens.add(new Citizen(1, "Nguyễn Văn A", LocalDate.of(1990, 5, 12), "Nam", "Hà Nội", "123456789"));
        citizens.add(new Citizen(2, "Trần Thị B", LocalDate.of(1985, 8, 22), "Nữ", "Hồ Chí Minh", "987654321"));
        citizens.add(new Citizen(3, "Lê Văn C", LocalDate.of(2000, 3, 15), "Nam", "Đà Nẵng", "567890123"));
        citizens.add(new Citizen(4, "Phạm Thị D", LocalDate.of(1998, 12, 1), "Nữ", "Cần Thơ", "345678901"));
        return citizens;
    }




    public void addCitizen(Citizen citizen) {
        citizenDAO.addCitizen(citizen);
    }

    public void deleteCitizen(int id) {
        citizenDAO.deleteCitizen(id);
    }
}
