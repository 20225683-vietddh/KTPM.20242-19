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
				Fee fee = new Fee();
				fee.setId(rs.getInt("fee_id"));
				fee.setName(rs.getString("name"));
				fee.setCreatedDate(rs.getDate("created_date").toLocalDate());
				fee.setMandatory(rs.getBoolean("is_mandatory"));
				fee.setDescription(rs.getString("description"));
				
				fees.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return fees;
	}
}
