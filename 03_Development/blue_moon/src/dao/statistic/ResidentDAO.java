package dao.statistic;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.statistic.AgeGroupStats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;



public class ResidentDAO {
    
    public int getTotalResidents(Connection conn) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM residents";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }



    public double getPercentHousehole(Connection conn){

        double percent = 0;
        String sql_total = "SELECT COUNT(*) FROM households";
        String sql_rented = "SELECT COUNT(*) FROM households WHERE head_resident_id IS NOT NULL";

        try (
        Statement stmtTotal = conn.createStatement();
        Statement stmtRented = conn.createStatement();
        ResultSet rs_total = stmtTotal.executeQuery(sql_total);
        ResultSet rs_rented = stmtRented.executeQuery(sql_rented)
        ) {
            if (rs_total.next() && rs_rented.next()) {
                int total = rs_total.getInt(1);
                int rented = rs_rented.getInt(1);

                if (total > 0) {
                    percent = ((double) rented / total) * 100.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String.format("%.1f%%", percent);
        return percent;
    }


    public Map<String, Integer> getPopulationPerYear(Connection conn) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT EXTRACT(YEAR FROM added_date) AS year, COUNT(*) AS total_population
            FROM residents
            GROUP BY year
            ORDER BY year;
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                 String year = String.valueOf(rs.getInt("year"));
                int count = rs.getInt("total_population");
                data.put(year, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }



    public Map<String, Integer> getGenderStatistics(Connection conn) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT gender, COUNT(*) AS count FROM residents GROUP BY gender";

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt("count");
                map.put(gender, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    
    

    public List<AgeGroupStats> getAgeGroupStatistics(Connection conn) {
        String sql = "SELECT\r\n" + 
                    "  CASE\r\n" + 
                    "    WHEN age BETWEEN 0 AND 9 THEN '0-10'\r\n" + 
                    "    WHEN age BETWEEN 10 AND 19 THEN '10-20'\r\n" + 
                    "    WHEN age BETWEEN 20 AND 29 THEN '20-30'\r\n" + 
                    "    WHEN age BETWEEN 30 AND 39 THEN '30-40'\r\n" + 
                    "    WHEN age BETWEEN 40 AND 49 THEN '40-50'\r\n" + 
                    "    WHEN age BETWEEN 50 AND 59 THEN '50-60'\r\n" + 
                    "    ELSE '60+'\r\n" + 
                    "  END AS age_range,\r\n" + 
                    "  gender,\r\n" + 
                    "  COUNT(*) AS count\r\n" + 
                    "FROM (\r\n" + 
                    "  SELECT gender, DATE_PART('year', AGE(date_of_birth)) AS age\r\n" + 
                    "  FROM residents\r\n" + 
                    ") AS sub\r\n" + 
                    "GROUP BY age_range, gender\r\n" + 
                    "ORDER BY age_range;"; 
        Map<String, Integer> maleMap = new LinkedHashMap<>();
        Map<String, Integer> femaleMap = new LinkedHashMap<>();

    
        List<String> ageRanges = List.of("0-10", "10-20", "20-30", "30-40", "40-50", "50-60", "60+");
        ageRanges.forEach(range -> {
            maleMap.put(range, 0);
            femaleMap.put(range, 0);
        });

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ageRange = rs.getString("age_range");
                String gender = rs.getString("gender");
                int count = rs.getInt("count");

                if (gender.equalsIgnoreCase("Nam")) {
                    maleMap.put(ageRange, count);
                } else if (gender.equalsIgnoreCase("Nữ")) {
                    femaleMap.put(ageRange, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<AgeGroupStats> result = new ArrayList<>();
        for (String range : ageRanges) {
            int male = maleMap.getOrDefault(range, 0);
            int female = femaleMap.getOrDefault(range, 0);
            result.add(new AgeGroupStats(range, male, female));
        }
        return result;
    }



    public double getAverageAge(Connection conn) {
        String sql = "SELECT AVG(DATE_PART('year', AGE(date_of_birth))) AS average_age FROM residents WHERE date_of_birth IS NOT NULL";
        double avgAge = 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                avgAge = rs.getDouble("average_age");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avgAge;
    }



    public String getMostCommonAgeGroup(Connection conn) {
        String sql = "SELECT age_range, COUNT(*) AS count FROM ("
                + "  SELECT CASE "
                + "    WHEN age BETWEEN 0 AND 9 THEN '0-10' "
                + "    WHEN age BETWEEN 10 AND 19 THEN '10-20' "
               + "    WHEN age BETWEEN 20 AND 29 THEN '20-30' "
               + "    WHEN age BETWEEN 30 AND 39 THEN '30-40' "
               + "    WHEN age BETWEEN 40 AND 49 THEN '40-50' "
               + "    WHEN age BETWEEN 50 AND 59 THEN '50-60' "
               + "    ELSE '60+' "
               + "  END AS age_range "
               + "  FROM (SELECT DATE_PART('year', AGE(date_of_birth)) AS age FROM residents WHERE date_of_birth IS NOT NULL) AS sub"
               + ") AS grouped "
               + "GROUP BY age_range "
               + "ORDER BY count DESC "
               + "LIMIT 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("age_range");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Không xác định";
    }



    public Map<String, Integer> getAgeGroupDistribution(Connection conn) {
        Map<String, Integer> ageGroupMap = new LinkedHashMap<>();

        String sql = "SELECT age_range, COUNT(*) AS count FROM ("
               + " SELECT CASE "
               + " WHEN age BETWEEN 0 AND 9 THEN '0-10' "
               + " WHEN age BETWEEN 10 AND 19 THEN '10-20' "
               + " WHEN age BETWEEN 20 AND 29 THEN '20-30' "
               + " WHEN age BETWEEN 30 AND 39 THEN '30-40' "
               + " WHEN age BETWEEN 40 AND 49 THEN '40-50' "
               + " WHEN age BETWEEN 50 AND 59 THEN '50-60' "
               + " ELSE '60+' "
               + " END AS age_range "
               + " FROM (SELECT DATE_PART('year', AGE(date_of_birth)) AS age FROM residents WHERE date_of_birth IS NOT NULL) AS sub"
               + ") AS grouped "
               + "GROUP BY age_range "
               + "ORDER BY age_range";

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ageGroupMap.put(rs.getString("age_range"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ageGroupMap;
    }
}