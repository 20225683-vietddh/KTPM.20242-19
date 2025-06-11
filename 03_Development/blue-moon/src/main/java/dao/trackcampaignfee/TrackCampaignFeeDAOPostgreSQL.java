package dao.trackcampaignfee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.PostgreSQLConnection;
import dto.campaignfee.PaidFeeResponseDTO;
import dto.campaignfee.HouseholdFeeDetailDTO;

public class TrackCampaignFeeDAOPostgreSQL implements TrackCampaignFeeDAO {
	private Connection conn;
	
	public TrackCampaignFeeDAOPostgreSQL() throws SQLException {
		this.conn = PostgreSQLConnection.getInstance().getConnection();
	}
	
	public int getExpectedAmount(int campaignFeeId) throws SQLException {
		String sql = """
				    SELECT SUM(fpr.expected_amount) AS total
				    FROM fee_payment_records fpr
				    JOIN fees f ON fpr.fee_id = f.fee_id
				    WHERE fpr.campaign_fee_id = ?
				      AND f.is_mandatory = TRUE
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		}
		return 0;
	}
	
	public int getTotalCompulsoryPaidAmount(int campaignFeeId) throws SQLException {
		String sql = """
				    SELECT SUM(fpr.paid_amount) AS total
				    FROM fee_payment_records fpr, fees f 
				    WHERE fpr.campaign_fee_id = ?
				    AND fpr.fee_id = f.fee_id
				    AND f.is_mandatory = TRUE
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		}
		return 0;
	}
	
	public int getTotalOptionalPaidAmount(int campaignFeeId) throws SQLException {
		String sql = """
				    SELECT SUM(fpr.paid_amount) AS total
				    FROM fee_payment_records fpr, fees f 
				    WHERE fpr.campaign_fee_id = ?
				    AND fpr.fee_id = f.fee_id
				    AND f.is_mandatory = FALSE
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		}
		return 0;
	}
	
	public PaidFeeResponseDTO getExpectedAndPaidAmount(int campaignFeeId, int feeId) throws SQLException {
		PaidFeeResponseDTO dto = new PaidFeeResponseDTO();
	    String sql = """
	            SELECT fpr.campaign_fee_id, f.fee_id, f.name, f.is_mandatory,
	                   SUM(fpr.expected_amount) AS expected_amount,
	                   SUM(fpr.paid_amount) AS paid_amount
	            FROM fee_payment_records AS fpr
	            JOIN fees AS f ON f.fee_id = fpr.fee_id
	            WHERE fpr.campaign_fee_id = ? AND f.fee_id = ?
	            GROUP BY fpr.campaign_fee_id, f.fee_id, f.name, f.is_mandatory
	        """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, campaignFeeId);
	        stmt.setInt(2, feeId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                dto.setFeeId(rs.getInt("fee_id"));
	                dto.setFeeName(rs.getString("name"));
	                dto.setMandatory(rs.getBoolean("is_mandatory"));
	                dto.setExpectedAmount(rs.getInt("expected_amount"));
	                dto.setPaidAmount(rs.getInt("paid_amount"));
	            }
	        }
	    }
	    return dto;
	}
	
	public int count(int campaignFeeId) throws SQLException {
		String sql = """
				    SELECT COUNT(campaign_fee_id) AS total
				    FROM fee_payment_records fpr
				    WHERE campaign_fee_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		}
		return 0;
	}
	
	@Override
	public List<HouseholdFeeDetailDTO> getHouseholdDetailsByFee(int campaignFeeId, int feeId) throws SQLException {
		List<HouseholdFeeDetailDTO> result = new ArrayList<>();
		String sql = """
				SELECT h.household_id, h.house_number,
				       COALESCE(fpr.expected_amount, 0) AS expected_amount,
				       COALESCE(fpr.paid_amount, 0) AS paid_amount
				FROM households h
				LEFT JOIN fee_payment_records fpr ON h.household_id = fpr.household_id
				                                   AND fpr.campaign_fee_id = ?
				                                   AND fpr.fee_id = ?
				ORDER BY h.house_number
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);
			stmt.setInt(2, feeId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					HouseholdFeeDetailDTO dto = new HouseholdFeeDetailDTO(
						rs.getInt("household_id"),
						rs.getString("house_number"),
						rs.getInt("expected_amount"),
						rs.getInt("paid_amount")
					);
					result.add(dto);
				}
			}
		}
		return result;
	}
}
