import dao.CitizenDAO;
import models.Citizen;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        CitizenDAO dao = new CitizenDAO();

        // Thêm nhân khẩu
        dao.addCitizen(new Citizen(0, "Nguyễn Văn A", LocalDate.of(1990, 5, 20), "Nam", "Hà Nội", "123456789"));
        dao.addCitizen(new Citizen(0, "Trần Thị B", LocalDate.of(1985, 11, 15), "Nữ", "TP HCM", "987654321"));

        // In danh sách nhân khẩu
        System.out.println("Danh sách nhân khẩu:");
        dao.getAllCitizens().forEach(System.out::println);

        // Xóa nhân khẩu
        dao.deleteCitizen(1);
        System.out.println("Sau khi xóa:");
        dao.getAllCitizens().forEach(System.out::println);
    }
}
