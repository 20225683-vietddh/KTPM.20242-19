package services;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Member;

//MemberService interface
public interface MemberService {
	Member getMemberById(String memberId) throws ServiceException;

	List<Member> getMembersByHouseholdId(int householdId) throws ServiceException;


	boolean memberExists(String memberId);

	boolean addMember(Member member) throws ServiceException, SQLException;

	boolean updateMember(Member member) throws ServiceException, SQLException;
	
	void updateMembers(List<Member> members) throws ServiceException, SQLException;
	
	

	boolean deleteMember(String memberId) throws ServiceException, SQLException;

	Member getHouseholdHead(int householdId) throws ServiceException;

	
	
}