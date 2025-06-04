package dao.fee;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import models.Fee;

public interface FeeDAO {
	public List<Fee> getFees();
	
	public boolean addFee(Fee fee) throws SQLException;
	
	public boolean updateFee(Fee fee) throws SQLException;
	
	public boolean deleteFee(int feeId) throws SQLException;
	
	public Fee getFeeById(int feeId) throws SQLException;
	
	public boolean isPartOfCampaignFee(int feeId) throws SQLException;
	
	List<Map<String, Object>> getLastNFees(int n);
	List<Map<String, Object>> getLastNFeesWithStatus(int n);
	List<Map<String, Object>> getFeeStatistics(int months);
	List<Map<String, Object>> getRecentPayments(int limit);
}
