package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Fee;
import utils.DatabaseConnection;
import utils.Status;
import utils.Mandatory;

public class FeeDAO {
    private Connection connection;

    public FeeDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean addFee(Fee fee) {
        String sql = "INSERT INTO fees (id, name, created_date, amount, is_mandatory, status, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fee.getId());
            stmt.setString(2, fee.getName());
            stmt.setDate(3, java.sql.Date.valueOf(fee.getCreatedDate()));
            stmt.setDouble(4, fee.getAmount());
            stmt.setString(5, fee.getIsMandatory().getDisplayName());
            stmt.setString(6, fee.getStatus().getDisplayName());
            stmt.setString(7, fee.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFee(Fee fee) {
        String sql = "UPDATE fees SET name = ?, created_date = ?, amount = ?, is_mandatory = ?, status = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fee.getName());
            stmt.setDate(2, java.sql.Date.valueOf(fee.getCreatedDate()));
            stmt.setDouble(3, fee.getAmount());
            stmt.setString(4, fee.getIsMandatory().getDisplayName());
            stmt.setString(5, fee.getStatus().getDisplayName());
            stmt.setString(6, fee.getDescription());
            stmt.setString(7, fee.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFee(String feeId) {
        String sql = "DELETE FROM fees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, feeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Fee> getAllFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fees.add(mapResultSetToFee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public Fee getFeeById(String feeId) {
        String sql = "SELECT * FROM fees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, feeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Fee mapResultSetToFee(ResultSet rs) throws SQLException {
        return new Fee(
            rs.getString("id"),
            rs.getString("name"),
            rs.getDate("created_date").toLocalDate(),
            rs.getDouble("amount"),
            Mandatory.fromDisplayName(rs.getString("is_mandatory")),
            Status.fromDisplayName(rs.getString("status")),
            rs.getString("description")
        );
    }
}
