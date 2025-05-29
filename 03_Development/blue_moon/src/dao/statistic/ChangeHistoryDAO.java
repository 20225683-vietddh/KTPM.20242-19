package dao.statistic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.statistic.ChangeHistoryInfo;

public class ChangeHistoryDAO {
    
    public List<ChangeHistoryInfo> getChangeHistoryByPeriod(Connection conn, LocalDate startDate, LocalDate endDate) {
        List<ChangeHistoryInfo> list = new ArrayList<>();
        String sql = """
            SELECT r.resident_id, r.full_name, r.date_of_birth, r.gender, r.household_id,
                    c.change_type, c.change_date
            FROM residents r
            JOIN change_history_records c ON r.resident_id = c.resident_id
            WHERE c.change_date BETWEEN ? AND ?
            ORDER BY c.change_date;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChangeHistoryInfo info = new ChangeHistoryInfo(
                        rs.getInt("resident_id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("gender"),
                        rs.getInt("household_id"),
                        rs.getString("change_type"),
                        rs.getDate("change_date").toLocalDate()
                        );
                        list.add(info);
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
