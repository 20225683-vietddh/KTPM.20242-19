package dao.fee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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

	@Override
	public List<Map<String, Object>> getLastNFees(int n) {
		List<Map<String, Object>> result = new ArrayList<>();
		String sql = "SELECT f.name, f.is_mandatory FROM fees f ORDER BY f.created_date DESC LIMIT ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, n);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> fee = new HashMap<>();
					fee.put("name", rs.getString("name"));
					fee.put("is_mandatory", rs.getBoolean("is_mandatory"));
					result.add(fee);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getLastNFeesWithStatus(int n) {
		return getLastNFees(n);
	}

	@Override
	public List<Map<String, Object>> getFeeStatistics(int months) {
		List<Map<String, Object>> result = new ArrayList<>();
		String sql = "SELECT DATE_TRUNC('month', f.due_date) as month, " +
					"COUNT(DISTINCT f.id) as total_fees, " +
					"COALESCE(SUM(fd.amount), 0) as total_collected, " +
					"COUNT(DISTINCT CASE WHEN fd.payment_status = 'PAID' THEN h.id END) as paid_households " +
					"FROM fee f " +
					"LEFT JOIN fee_detail fd ON f.id = fd.fee_id " +
					"LEFT JOIN household h ON fd.household_id = h.id " +
					"WHERE f.due_date >= CURRENT_DATE - INTERVAL '? months' " +
					"GROUP BY DATE_TRUNC('month', f.due_date) " +
					"ORDER BY month DESC";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, months);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> stat = new HashMap<>();
					stat.put("month", rs.getDate("month"));
					stat.put("total_fees", rs.getInt("total_fees"));
					stat.put("total_collected", rs.getDouble("total_collected"));
					stat.put("paid_households", rs.getInt("paid_households"));
					result.add(stat);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getRecentPayments(int limit) {
		List<Map<String, Object>> result = new ArrayList<>();
		String sql = "SELECT fd.id, f.name as fee_name, h.house_number, " +
					"fd.amount, fd.payment_date, fd.payment_status, fd.payment_method " +
					"FROM fee_detail fd " +
					"JOIN fee f ON fd.fee_id = f.id " +
					"JOIN household h ON fd.household_id = h.id " +
					"ORDER BY fd.payment_date DESC LIMIT ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> payment = new HashMap<>();
					payment.put("id", rs.getInt("id"));
					payment.put("fee_name", rs.getString("fee_name"));
					payment.put("house_number", rs.getString("house_number"));
					payment.put("amount", rs.getDouble("amount"));
					payment.put("payment_date", rs.getDate("payment_date"));
					payment.put("payment_status", rs.getString("payment_status"));
					payment.put("payment_method", rs.getString("payment_method"));
					result.add(payment);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
