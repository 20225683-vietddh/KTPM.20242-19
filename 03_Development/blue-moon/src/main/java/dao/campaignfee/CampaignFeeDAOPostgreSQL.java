package dao.campaignfee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import dao.PostgreSQLConnection;
import models.CampaignFee;
import models.Fee;
import dto.campaignfee.*;

public class CampaignFeeDAOPostgreSQL implements CampaignFeeDAO {
	private final Connection conn;
	
	public CampaignFeeDAOPostgreSQL() throws SQLException {
		this.conn = PostgreSQLConnection.getInstance().getConnection();
	}
	
	@Override
	public List<CampaignFee> getCampaignFees() {
		Map<Integer, CampaignFee> campaignMap = new LinkedHashMap<>();
		
		String sql = """ 
				SELECT cf.campaign_fee_id, cf.name, cf.created_date, cf.start_date, cf.due_date, cf.status, cf.description, f.fee_id, f.name AS fee_name, f.is_mandatory
                FROM campaign_fees AS cf, campaign_fee_items AS cfi, fees AS f
                WHERE cf.campaign_fee_id = cfi.campaign_fee_id
                AND f.fee_id = cfi.fee_id;
				""";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
		        int campaignId = rs.getInt("campaign_fee_id");

		        CampaignFee campaign = campaignMap.get(campaignId);
		        if (campaign == null) {
		            campaign = new CampaignFee();
		            campaign.setId(campaignId);
		            campaign.setName(rs.getString("name"));
		            campaign.setCreatedDate(rs.getDate("created_date").toLocalDate());
		            campaign.setStartDate(rs.getDate("start_date").toLocalDate());
		            campaign.setDueDate(rs.getDate("due_date").toLocalDate());
		            campaign.setStatus(rs.getString("status"));
		            campaign.setDescription(rs.getString("description"));
		            campaign.setFees(new ArrayList<>());
		            campaignMap.put(campaignId, campaign);
		        }

		        Fee fee = new Fee();
		        fee.setId(rs.getInt("fee_id"));
		        fee.setName(rs.getString("fee_name"));
		        fee.setIsMandatory(rs.getBoolean("is_mandatory"));
		        
		        
		        campaign.getFees().add(fee);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>(campaignMap.values());
	}
	
	@Override
	public void addNewCampaignFee(CampaignFeeDTO dto) throws SQLException {
	    String campaignFeeSQL = "INSERT INTO campaign_fees (name, created_date, start_date, due_date, status, description) VALUES (?, ?, ?, ?, ?, ?)";
	    String itemSQL = "INSERT INTO campaign_fee_items (fee_id, campaign_fee_id) VALUES (?, ?)";
	    String selectHouseholdsSQL = "SELECT household_id FROM households";
	    String insertPaymentRecordSQL = "INSERT INTO fee_payment_records (campaign_fee_id, household_id, fee_id) VALUES (?, ?, ?)";
	    LocalDate startDate = utils.Utils.parseDateSafely(dto.getStartDay(), dto.getStartMonth(), dto.getStartYear());
	    LocalDate dueDate = utils.Utils.parseDateSafely(dto.getDueDay(), dto.getDueMonth(), dto.getDueYear());

	    try {
	        conn.setAutoCommit(false); 

	        // Bước 1: Insert vào campaign_fees
	        PreparedStatement stmt = conn.prepareStatement(campaignFeeSQL, Statement.RETURN_GENERATED_KEYS);
	        stmt.setString(1, dto.getName());
	        stmt.setObject(2, LocalDate.now());
	        stmt.setObject(3, startDate);
	        stmt.setObject(4, dueDate);
	        stmt.setString(5, "Mới tạo");
	        stmt.setString(6, dto.getDescription());
	        stmt.executeUpdate();

	        ResultSet rs = stmt.getGeneratedKeys();
	        if (!rs.next()) {
	            conn.rollback();
	            throw new SQLException("Không thể thêm được thông tin đợt thu phí mới vào CSDL.");
	        }
	        int campaignFeeId = rs.getInt(1); 
	        rs.close();
	        stmt.close();

	        // Bước 2: Insert vào campaign_fee_items
	        try (PreparedStatement itemStmt = conn.prepareStatement(itemSQL)) {
	            for (int feeId : dto.getFeeIds()) {
	                itemStmt.setInt(1, feeId);
	                itemStmt.setInt(2, campaignFeeId);
	                itemStmt.addBatch();
	            }
	            itemStmt.executeBatch();
	        }

	        // Bước 3: Lấy danh sách household_id
	        List<Integer> householdIds = new ArrayList<>();
	        try (PreparedStatement householdStmt = conn.prepareStatement(selectHouseholdsSQL);
	             ResultSet householdRs = householdStmt.executeQuery()) {
	            while (householdRs.next()) {
	                householdIds.add(householdRs.getInt("household_id"));
	            }
	        }

	        // Bước 4: Insert vào fee_payment_records
	        try (PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentRecordSQL)) {
	            for (int householdId : householdIds) {
	                for (int feeId : dto.getFeeIds()) {
	                    paymentStmt.setInt(1, campaignFeeId);
	                    paymentStmt.setInt(2, householdId);
	                    paymentStmt.setInt(3, feeId);
	                    paymentStmt.addBatch();
	                }
	            }
	            paymentStmt.executeBatch();
	        }
	        conn.commit(); 
	    } catch (SQLException e) {
	        if (conn != null) conn.rollback(); 
	        throw e;
	    } finally {
	        if (conn != null) conn.setAutoCommit(true); 
	    }
	}
	
	@Override
	public void deleteCampaignFee(CampaignFee campaignFee) throws SQLException {
	    String deleteItemsSQL = "DELETE FROM campaign_fee_items WHERE campaign_fee_id = ?";
	    String deleteFeeSQL = "DELETE FROM campaign_fees WHERE campaign_fee_id = ?"; 

	    try {
	        conn.setAutoCommit(false);

	        try (
	            PreparedStatement deleteItemsStmt = conn.prepareStatement(deleteItemsSQL);
	            PreparedStatement deleteFeeStmt = conn.prepareStatement(deleteFeeSQL)
	        ) {
	            deleteItemsStmt.setInt(1, campaignFee.getId());
	            deleteItemsStmt.executeUpdate();

	            deleteFeeStmt.setInt(1, campaignFee.getId());
	            int rowsAffected = deleteFeeStmt.executeUpdate();

	            if (rowsAffected == 0) {
	                throw new SQLException("Không xóa được đợt thu phí " + campaignFee.getName());
	            }

	            conn.commit(); 

	        } catch (Exception e) {
	            conn.rollback(); 
	            throw new SQLException("Lỗi khi xoá đợt thu phí", e);
	        } finally {
	            conn.setAutoCommit(true); 
	        }

	    } catch (SQLException e) {
	        throw e;
	    }
	}
	
	@Override
	public void updateCampaignFee(CampaignFeeDTO dto) throws SQLException {
		String updateSQL = "UPDATE campaign_fees SET name = ?, start_date = ?, due_date = ?, status = ?, description = ? WHERE campaign_fee_id = ?";
		String deleteItemsSql = "DELETE FROM campaign_fee_items WHERE campaign_fee_id = ?";
	    String insertItemSql = "INSERT INTO campaign_fee_items (campaign_fee_id, fee_id) VALUES (?, ?)";
	    String getExistingFeesSql = "SELECT fee_id FROM campaign_fee_items WHERE campaign_fee_id = ?";
	    String selectHouseholdsSQL = "SELECT household_id FROM households";
	    String insertPaymentRecordSQL = "INSERT INTO fee_payment_records (campaign_fee_id, household_id, fee_id) VALUES (?, ?, ?)";
	    
		LocalDate startDate = utils.Utils.parseDateSafely(dto.getStartDay(), dto.getStartMonth(), dto.getStartYear());
		LocalDate dueDate = utils.Utils.parseDateSafely(dto.getDueDay(), dto.getDueMonth(), dto.getDueYear());
		
		try {
			conn.setAutoCommit(false);
			
			// Bước 1: Lấy danh sách khoản thu hiện tại
			List<Integer> existingFeeIds = new ArrayList<>();
			try (PreparedStatement existingStmt = conn.prepareStatement(getExistingFeesSql)) {
				existingStmt.setInt(1, dto.getId());
				try (ResultSet rs = existingStmt.executeQuery()) {
					while (rs.next()) {
						existingFeeIds.add(rs.getInt("fee_id"));
					}
				}
			}
			
			// Bước 2: Tìm ra khoản thu mới (có trong dto.getFeeIds() nhưng không có trong existingFeeIds)
			List<Integer> newFeeIds = new ArrayList<>();
			for (Integer feeId : dto.getFeeIds()) {
				if (!existingFeeIds.contains(feeId)) {
					newFeeIds.add(feeId);
				}
			}
			
			try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
		            PreparedStatement deleteStmt = conn.prepareStatement(deleteItemsSql);
		            PreparedStatement insertStmt = conn.prepareStatement(insertItemSql)) {
				// Bước 3: Cập nhật thông tin chính của đợt thu
				updateStmt.setString(1, dto.getName());
				updateStmt.setObject(2, startDate);
				updateStmt.setObject(3, dueDate);
				updateStmt.setString(4, dto.getStatus());
				updateStmt.setString(5, dto.getDescription());
				updateStmt.setInt(6, dto.getId());
				updateStmt.executeUpdate();
				
				// Bước 4: Cập nhật campaign_fee_items
				deleteStmt.setInt(1, dto.getId());
	            deleteStmt.executeUpdate();

	            for (Integer feeId : dto.getFeeIds()) {
	                insertStmt.setInt(1, dto.getId());
	                insertStmt.setInt(2, feeId);
	                insertStmt.addBatch();
	            }
	            insertStmt.executeBatch();
	            
	            // Bước 5: Tạo fee_payment_records cho khoản thu mới
	            if (!newFeeIds.isEmpty()) {
	            	// Lấy danh sách household_id
	            	List<Integer> householdIds = new ArrayList<>();
	            	try (PreparedStatement householdStmt = conn.prepareStatement(selectHouseholdsSQL);
	            		 ResultSet householdRs = householdStmt.executeQuery()) {
	            		while (householdRs.next()) {
	            			householdIds.add(householdRs.getInt("household_id"));
	            		}
	            	}
	            	
	            	// Tạo records cho khoản thu mới
	            	try (PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentRecordSQL)) {
	            		for (int householdId : householdIds) {
	            			for (int newFeeId : newFeeIds) {
	            				paymentStmt.setInt(1, dto.getId());
	            				paymentStmt.setInt(2, householdId);
	            				paymentStmt.setInt(3, newFeeId);
	            				paymentStmt.addBatch();
	            			}
	            		}
	            		paymentStmt.executeBatch();
	            	}
	            }
	            
	            conn.commit();
			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			} finally {
				conn.setAutoCommit(true);
			}  
		} catch (SQLException e) {
			throw e;
		}
	}
	
	@Override
	public boolean isFeesExisted(int campaignFeeId, List<Integer> feeIds) throws SQLException {
		String sql = "SELECT 1 FROM fee_payment_records WHERE campaign_fee_id = ? AND fee_id = ? AND paid_amount > 0 LIMIT 1";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        for (int feeId : feeIds) {
	            stmt.setInt(1, campaignFeeId);
	            stmt.setInt(2, feeId);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return true; // Tìm thấy ít nhất 1 record có paid_amount > 0
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e;
	    }
	    
	    return false; // Không tìm thấy record nào có paid_amount > 0
	}

	@Override
	public List<Map<String, Object>> getLastNCampaigns(int n) {
		List<Map<String, Object>> result = new ArrayList<>();
		String sql = "SELECT cf.campaign_fee_id, cf.name, cf.start_date, cf.due_date, " +
					"COALESCE(SUM(fpr.paid_amount), 0) as total_amount " +
					"FROM campaign_fees cf " +
					"LEFT JOIN fee_payment_records fpr ON cf.campaign_fee_id = fpr.campaign_fee_id " +
					"GROUP BY cf.campaign_fee_id, cf.name, cf.start_date, cf.due_date " +
					"ORDER BY cf.start_date DESC LIMIT ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, n);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> campaign = new LinkedHashMap<>();
					campaign.put("id", rs.getInt("campaign_fee_id"));
					campaign.put("name", rs.getString("name"));
					campaign.put("start_date", rs.getDate("start_date"));
					campaign.put("end_date", rs.getDate("due_date"));
					campaign.put("total_amount", rs.getDouble("total_amount"));
					result.add(campaign);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getLastNCampaignsWithStatus(int n) {
		List<Map<String, Object>> result = new ArrayList<>();
		String sql = "SELECT cf.campaign_fee_id, cf.name, cf.start_date, cf.due_date, " +
					"COUNT(DISTINCT h.household_id) as total_households, " +
					"COUNT(DISTINCT CASE WHEN fpr.is_fully_paid THEN h.household_id END) as paid_households, " +
					"CASE WHEN COUNT(DISTINCT h.household_id) > 0 " +
					"THEN ROUND(COUNT(DISTINCT CASE WHEN fpr.is_fully_paid THEN h.household_id END) * 100.0 / COUNT(DISTINCT h.household_id), 2) " +
					"ELSE 0 END as completion_rate " +
					"FROM campaign_fees cf " +
					"LEFT JOIN fee_payment_records fpr ON cf.campaign_fee_id = fpr.campaign_fee_id " +
					"LEFT JOIN households h ON fpr.household_id = h.household_id " +
					"GROUP BY cf.campaign_fee_id, cf.name, cf.start_date, cf.due_date " +
					"ORDER BY cf.start_date DESC LIMIT ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, n);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> campaign = new LinkedHashMap<>();
					campaign.put("id", rs.getInt("campaign_fee_id"));
					campaign.put("name", rs.getString("name"));
					campaign.put("start_date", rs.getDate("start_date"));
					campaign.put("end_date", rs.getDate("due_date"));
					campaign.put("total_households", rs.getInt("total_households"));
					campaign.put("paid_households", rs.getInt("paid_households"));
					campaign.put("completion_rate", rs.getDouble("completion_rate"));
					result.add(campaign);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
