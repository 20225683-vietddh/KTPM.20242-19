package dao.statistic;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.statistic.StayAbsenceInfo;;

public class StayAbsenceDAO {

    public List<StayAbsenceInfo> getStayAbsenceRecords(Connection conn, String type, int month, int year) {
        List<StayAbsenceInfo> list = new ArrayList<>();

        String sql = "SELECT r.resident_id, r.full_name, r.gender, r.date_of_birth, r.household_id, " +
                     "s.created_date, s.period, s.request_desc " +
                     "FROM stay_absence_records s " +
                     "JOIN residents r ON s.resident_id = r.resident_id " +
                     "WHERE s.record_type = ? AND EXTRACT(MONTH FROM s.created_date) = ? " +
                     "AND EXTRACT(YEAR FROM s.created_date) = ? " +
                     "ORDER BY s.created_date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);      
            stmt.setInt(2, month);        
            stmt.setInt(3, year);         

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StayAbsenceInfo info = new StayAbsenceInfo(
                    rs.getInt("resident_id"),
                    rs.getString("full_name"),
                    rs.getString("gender"),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getInt("household_id"),
                    rs.getDate("created_date").toLocalDate(),
                    rs.getString("period"),
                    rs.getString("request_desc")
                );
                list.add(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
