package dao.trackcampaignfee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dao.PostgreSQLConnection;
import dto.campaignfee.PaidFeeResponseDTO;

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
	
	public int getPaidAmount(int campaignFeeId) throws SQLException {
		String sql = """
				    SELECT SUM(fpr.paid_amount) AS total
				    FROM fee_payment_records fpr
				    WHERE fpr.campaign_fee_id = ?
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

	                dto.setExpectedAmount(rs.getObject("expected_amount") != null
	                        ? rs.getInt("expected_amount") : null);

	                dto.setPaidAmount(rs.getObject("paid_amount") != null
	                        ? rs.getInt("paid_amount") : null);
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
}
