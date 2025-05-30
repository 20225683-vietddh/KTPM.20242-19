package dao.trackcampaignfee;

import java.sql.SQLException;
import dto.campaignfee.PaidFeeResponseDTO;

public interface TrackCampaignFeeDAO {
	public int getExpectedAmount(int campaignFeeId) throws SQLException;
	public int getPaidAmount(int campaignFeeId) throws SQLException;
	public PaidFeeResponseDTO getExpectedAndPaidAmount(int campaignFeeId, int feeId) throws SQLException;
	public int count(int campaignFeeId) throws SQLException;
}
