package services;

import java.sql.SQLException;
import java.util.List;
import dao.chargefee.*;
import models.Household;
import dto.campaignfee.FeeAmountRecordDTO;
import java.time.LocalDate;

public class ChargeFeeService {
	private final ChargeFeeDAO dao;
	
	public ChargeFeeService() throws SQLException {
		dao = new ChargeFeeDAOPostgreSQL();
	}
	
	public boolean isRecordExisted(int campaignFeeId, int householdId, int feeId) throws SQLException {
		return dao.existsRecord(campaignFeeId, householdId, feeId);
	}
	
	public void insertRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount, int paidAmount, LocalDate paidDate) throws SQLException {
		dao.insertRecord(campaignFeeId, householdId, feeId, expectedAmount, paidAmount, paidDate);
	}
	
	public void updateRecord(int campaignFeeId, int householdId, int feeId, int expectedAmount) throws SQLException {
		dao.updateRecord(campaignFeeId, householdId, feeId, expectedAmount);
	}
	
	public FeeAmountRecordDTO getPaymentRecord(int campaignFeeId, int householdId, int feeId) throws SQLException {
		return dao.getPaymentRecord(campaignFeeId, householdId, feeId);
	}
	
	public int countTotalExpectedAmount(int campaignFeeId, int householdId) {
		return dao.countTotalExpectedAmount(campaignFeeId, householdId);
	}
	
	public int countTotalPaidAmount(int campaignFeeId, int householdId) {
		return dao.countTotalPaidAmount(campaignFeeId, householdId);
	}
	
	public List<Household> getAllHouseholds() {
		return dao.getHouseholds();
	}
}
