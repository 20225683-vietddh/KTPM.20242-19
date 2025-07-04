package dao.chargefee;

import java.util.List;
import dto.campaignfee.FeeAmountRecordDTO;
import java.sql.SQLException;
import models.Household;
import java.time.LocalDate;

public interface ChargeFeeDAO {
	public boolean existsRecord(int campaignFeeId, int householdId, int feeId) throws SQLException;
	public void insertRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount, int paidAmount, LocalDate paidDate) throws SQLException;
	public void updateRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount) throws SQLException;
	public FeeAmountRecordDTO getPaymentRecord(int campaignFeeId, int householdId, int feeId) throws SQLException;
	public List<Household> getHouseholds();
	public int countTotalExpectedAmount(int campaignFeeId, int householdId);
	public int countTotalCompulsoryPaidAmount(int campaignFeeId, int householdId);
	public int countTotalOptionalPaidAmount(int campaignFeeId, int householdId);
	public void updatePaidAmount(int campaignFeeId, int householdId, List<Integer> feeIds) throws SQLException;
	public void updatePaidAmount(int campaignFeeId, int householdId, int feeId, int paidAmount) throws SQLException;
}
