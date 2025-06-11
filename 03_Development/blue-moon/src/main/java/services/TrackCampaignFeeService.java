package services;

import dao.trackcampaignfee.*;
import dto.campaignfee.PaidFeeResponseDTO;
import dto.campaignfee.HouseholdFeeDetailDTO;
import java.sql.SQLException;
import java.util.List;

public class TrackCampaignFeeService {
	private final TrackCampaignFeeDAO dao;
	
	public TrackCampaignFeeService() throws SQLException {
		this.dao = new TrackCampaignFeeDAOPostgreSQL();
	}
	
	public int getExpectedAmount(int campaignFeeId) throws SQLException {
		return dao.getExpectedAmount(campaignFeeId);
	}
	
	public int getTotalCompulsoryPaidAmount(int campaignFeeId) throws SQLException {
		return dao.getTotalCompulsoryPaidAmount(campaignFeeId);
	}
	
	public int getTotalOptionalPaidAmount(int campaignFeeId) throws SQLException {
		return dao.getTotalOptionalPaidAmount(campaignFeeId);
	}
	
	public boolean isCampaignFeeAssigned(int campaignFeeId) throws SQLException {
		return dao.count(campaignFeeId) > 0 ? true : false;
	}
	
	public PaidFeeResponseDTO getExpectedAndPaidAmount(int campaignFeeId, int feeId) throws SQLException {
		return dao.getExpectedAndPaidAmount(campaignFeeId, feeId);
	}
	
	public List<HouseholdFeeDetailDTO> getHouseholdDetailsByFee(int campaignFeeId, int feeId) throws SQLException {
		return dao.getHouseholdDetailsByFee(campaignFeeId, feeId);
	}
}
