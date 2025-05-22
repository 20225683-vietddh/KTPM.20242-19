package dao.campaignfee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import dao.PostgreSQLConnection;
import models.CampaignFee;
import models.Fee;

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
	
}
