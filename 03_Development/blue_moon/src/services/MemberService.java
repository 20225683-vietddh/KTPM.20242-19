package services;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Resident;

//MemberService interface
public interface MemberService {
	
	Resident getMemberById(String memberId) throws ServiceException;

	List<Resident> getMembersByHouseholdId(int householdId) throws ServiceException;
	
	List<Resident> getAll() throws ServiceException;


	List<Resident> getMembersByMemberIds(List<String> memIds) throws ServiceException;
	
	boolean memberExists(String memberId);

	boolean addMember(Resident member) throws ServiceException, SQLException;

	boolean updateMember(Resident member) throws ServiceException, SQLException;
	
	void updateMembers(List<Resident> members) throws ServiceException, SQLException;
	
	

	boolean deleteMember(String memberId) throws ServiceException, SQLException;

	Resident getHouseholdHead(int householdId) throws ServiceException;

	
	
}