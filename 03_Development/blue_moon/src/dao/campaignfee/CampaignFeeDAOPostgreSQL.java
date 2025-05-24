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
				SELECT cf.campaign_fee_id, cf.name, cf.created_date, cf.start_date, cf.due_date, cf.status, cf.description, f.fee_id, f.name AS fee_name
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
		LocalDate startDate = utils.Utils.parseDateSafely(dto.getStartDay(), dto.getStartMonth(), dto.getStartYear());
		LocalDate dueDate = utils.Utils.parseDateSafely(dto.getDueDay(), dto.getDueMonth(), dto.getDueYear());
        		
		PreparedStatement stmt = conn.prepareStatement(campaignFeeSQL, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, dto.getName());
		stmt.setObject(2, LocalDate.now());
		stmt.setObject(3, startDate);
		stmt.setObject(4, dueDate);
		stmt.setString(5, "Mới tạo");
		stmt.setString(6, dto.getDescription());
		stmt.executeUpdate();
		
		ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int campaignFeeId = rs.getInt(1); 

            String itemSQL = "INSERT INTO campaign_fee_items (fee_id, campaign_fee_id) VALUES (?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSQL)) {
                for (int feeId : dto.getFeeIds()) {
                    itemStmt.setLong(1, feeId);          
                    itemStmt.setLong(2, campaignFeeId); 
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }
        } else {
            throw new SQLException("Không thể thêm được thông tin đợt thu phí mới vào CSDL.");
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
	    
		LocalDate startDate = utils.Utils.parseDateSafely(dto.getStartDay(), dto.getStartMonth(), dto.getStartYear());
		LocalDate dueDate = utils.Utils.parseDateSafely(dto.getDueDay(), dto.getDueMonth(), dto.getDueYear());
		
		try {
			conn.setAutoCommit(false);
			
			try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
		            PreparedStatement deleteStmt = conn.prepareStatement(deleteItemsSql);
		            PreparedStatement insertStmt = conn.prepareStatement(insertItemSql)) {
				updateStmt.setString(1, dto.getName());
				updateStmt.setObject(2, startDate);
				updateStmt.setObject(3, dueDate);
				updateStmt.setString(4, dto.getStatus());
				updateStmt.setString(5, dto.getDescription());
				updateStmt.setInt(6, dto.getId());
				updateStmt.executeUpdate();
				
				deleteStmt.setInt(1, dto.getId());
	            deleteStmt.executeUpdate();

	            for (Integer feeId : dto.getFeeIds()) {
	                insertStmt.setInt(1, dto.getId());
	                insertStmt.setInt(2, feeId);
	                insertStmt.addBatch();
	            }
	            insertStmt.executeBatch();
	            
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
}
