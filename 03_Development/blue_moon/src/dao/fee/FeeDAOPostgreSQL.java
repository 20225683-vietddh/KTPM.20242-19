package dao.fee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import dao.PostgreSQLConnection;
import models.Fee;

public class FeeDAOPostgreSQL implements FeeDAO {
	private final Connection conn;

	public FeeDAOPostgreSQL() throws SQLException {
		this.conn = PostgreSQLConnection.getInstance().getConnection();
	}
	
	@Override
	public List<Fee> getFees() {
		String sql = "SELECT * FROM fees";
		
		List<Fee> fees = new ArrayList<>();
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				fees.add(mapResultSetToFee(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return fees;
	}

	@Override
	public boolean addFee(Fee fee) throws SQLException {
		String sql = "INSERT INTO fees (name, created_date, is_mandatory, description) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, fee.getName());
			stmt.setDate(2, java.sql.Date.valueOf(fee.getCreatedDate()));
			stmt.setBoolean(3, fee.getIsMandatory());
			stmt.setString(4, fee.getDescription());
			return stmt.executeUpdate() > 0;
		}
	}

	@Override
	public boolean updateFee(Fee fee) throws SQLException {
		String sql = "UPDATE fees SET name = ?, created_date = ?, is_mandatory = ?, description = ? WHERE fee_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, fee.getName());
			stmt.setDate(2, java.sql.Date.valueOf(fee.getCreatedDate()));
			stmt.setBoolean(3, fee.getIsMandatory());
			stmt.setString(4, fee.getDescription());
			stmt.setInt(5, fee.getId());
			return stmt.executeUpdate() > 0;
		}
	}

	@Override
	public boolean deleteFee(int feeId) throws SQLException {
		String sql = "DELETE FROM fees WHERE fee_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, feeId);
			return stmt.executeUpdate() > 0;
		}
	}

	@Override
	public Fee getFeeById(int feeId) throws SQLException {
		String sql = "SELECT * FROM fees WHERE fee_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, feeId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return mapResultSetToFee(rs);
			}
		}
		return null;
	}
	
	@Override
	public boolean isPartOfCampaignFee(int feeId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM campaign_fee_items WHERE fee_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, feeId);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
			return false;
		}
	}
	
	private Fee mapResultSetToFee(ResultSet rs) throws SQLException {
		return new Fee(
			rs.getInt("fee_id"),
			rs.getString("name"),
			rs.getDate("created_date").toLocalDate(),
			rs.getBoolean("is_mandatory"),
			rs.getString("description")
		);
	}
}
