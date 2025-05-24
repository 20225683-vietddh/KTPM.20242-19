package services;

import java.util.List;
import java.sql.SQLException;
import dao.fee.*;
import models.Fee;

public class FeeService {
	private final FeeDAO feeDAO;
	
	public FeeService() throws SQLException {
		feeDAO = new FeeDAOPostgreSQL();
	}
	
	public List<Fee> getAllFees() {
		return feeDAO.getFees();
	}
}
