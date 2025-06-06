package dao.chargefee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDate;
import dao.PostgreSQLConnection;
import models.Household;
import dto.campaignfee.FeeAmountRecordDTO;

public class ChargeFeeDAOPostgreSQL implements ChargeFeeDAO {
	private final Connection conn;

	public ChargeFeeDAOPostgreSQL() throws SQLException {
		this.conn = PostgreSQLConnection.getInstance().getConnection();
	}
	
	@Override
	public boolean existsRecord(int campaignFeeId, int householdId, int feeId) throws SQLException {
        String sql = "SELECT 1 FROM fee_payment_records WHERE campaign_fee_id = ? AND household_id = ? AND fee_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, campaignFeeId);
            stmt.setInt(2, householdId);
            stmt.setInt(3, feeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
	
	@Override
	public FeeAmountRecordDTO getPaymentRecord(int campaignFeeId, int householdId, int feeId) throws SQLException {
		String sql = "SELECT expected_amount, paid_amount, paid_date, areas, f.name FROM fee_payment_records fpr, households h, fees f "
				+ "WHERE campaign_fee_id = ? AND h.household_id = ? AND fpr.fee_id = ? AND fpr.household_id = h.household_id AND f.fee_id = fpr.fee_id";
		
		FeeAmountRecordDTO dto = new FeeAmountRecordDTO();

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);
			stmt.setInt(2, householdId);
			stmt.setInt(3, feeId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					dto.setCampaignFeeId(campaignFeeId);
					dto.setHouseholdId(householdId);
					dto.setFeeId(feeId);

					int expected = rs.getInt("expected_amount");
					dto.setExpectedAmount(rs.wasNull() ? 0 : expected);

					int paid = rs.getInt("paid_amount");
					dto.setPaidAmount(rs.wasNull() ? 0 : paid);

					LocalDate paidDate = rs.getObject("paid_date", LocalDate.class);
					dto.setPaidDate(paidDate != null ? paidDate : null);
					
					int areas = rs.getInt("areas");
					dto.setAreas(areas);
					
					String feeName = rs.getString("name");
					dto.setFeeName(feeName);
				}
			}
		}
		
		return dto;
	}
	
	@Override
	public void insertRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount, int paidAmount, LocalDate paidDate) throws SQLException {
		String sql = "INSERT INTO fee_payment_records "
				+ "(campaign_fee_id, household_id, fee_id, expected_amount, paid_amount, paid_date) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, campaignFeeId);
			stmt.setInt(2, householdId);
			stmt.setInt(3, feeId);
			stmt.setInt(4, expectedAmount);
			stmt.setInt(5, paidAmount);
			stmt.setObject(6, paidDate);
			stmt.executeUpdate();
		}
	}

	@Override
	public void updateRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount) throws SQLException {
		String sql = "UPDATE fee_payment_records SET expected_amount = ? WHERE campaign_fee_id = ? AND household_id = ? AND fee_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, expectedAmount);
			stmt.setInt(2, campaignFeeId);
			stmt.setInt(3, householdId);
			stmt.setInt(4, feeId);
			stmt.executeUpdate();
		}
	}
	
	public void updatePaidAmount(int campaignFeeId, int householdId, List<Integer> feeIds) throws SQLException {
	    if (feeIds == null || feeIds.isEmpty()) return;
	    String sql = """
	        UPDATE fee_payment_records
	        SET paid_amount = expected_amount,
	            paid_date = CURRENT_DATE
	        WHERE campaign_fee_id = ?
	          AND household_id = ?
	          AND fee_id IN (%s)
	    """;

	    String placeholders = feeIds.stream().map(f -> "?").collect(Collectors.joining(","));
	    String finalSql = String.format(sql, placeholders);

	    try (PreparedStatement stmt = conn.prepareStatement(finalSql)) {
            int index = 1;
	        stmt.setInt(index++, campaignFeeId);
	        stmt.setInt(index++, householdId);
	        for (Integer feeId : feeIds) {
	            stmt.setInt(index++, feeId);
	        }
	        stmt.executeUpdate();
	    }
	}
	
	public void updatePaidAmount(int campaignFeeId, int householdId, int feeId, int paidAmount) throws SQLException {
	    String sql = """
	        UPDATE fee_payment_records
	        SET paid_amount = paid_amount + ?,
	            paid_date = CURRENT_DATE
	        WHERE campaign_fee_id = ?
	          AND household_id = ?
	          AND fee_id = ?
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	stmt.setInt(1, paidAmount);
	        stmt.setInt(2, campaignFeeId);
	        stmt.setInt(3, householdId);
	        stmt.setInt(4, feeId);
	        stmt.executeUpdate();
	    }
	}
	
	@Override
	public List<Household> getHouseholds() {
		String sql = "SELECT household_id, house_number FROM households";
		List<Household> households = new ArrayList<Household>();
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Household household = new Household();
				household.setHouseholdId(rs.getInt("household_id"));
				household.setHouseNumber(rs.getString("house_number"));
				
				households.add(household);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return households;
	}
	
	@Override
	public int countTotalExpectedAmount(int campaignFeeId, int householdId) {
	    String sql = """
	        SELECT SUM(fpr.expected_amount) AS total
	        FROM fee_payment_records fpr
	        JOIN fees f ON fpr.fee_id = f.fee_id
	        WHERE fpr.campaign_fee_id = ?
	          AND fpr.household_id = ?
	          AND f.is_mandatory = TRUE
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, campaignFeeId);
	        stmt.setInt(2, householdId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("total"); 
	        }

	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    
	    return 0;
	}
	
	@Override
	public int countTotalCompulsoryPaidAmount(int campaignFeeId, int householdId) {
	    String sql = """
	        SELECT SUM(fpr.paid_amount) AS total
	        FROM fee_payment_records fpr
	        JOIN fees f ON fpr.fee_id = f.fee_id
	        WHERE fpr.campaign_fee_id = ?
	          AND fpr.household_id = ?
	          AND f.is_mandatory = true
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, campaignFeeId);
	        stmt.setInt(2, householdId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("total"); 
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
	
	@Override
	public int countTotalOptionalPaidAmount(int campaignFeeId, int householdId) {
	    String sql = """
	        SELECT SUM(fpr.paid_amount) AS total
	        FROM fee_payment_records fpr
	        JOIN fees f ON fpr.fee_id = f.fee_id
	        WHERE fpr.campaign_fee_id = ?
	          AND fpr.household_id = ?
	          AND f.is_mandatory = false
	    """;

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, campaignFeeId);
	        stmt.setInt(2, householdId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("total"); 
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
}
