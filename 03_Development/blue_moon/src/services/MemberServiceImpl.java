package services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.MemberDAO;
import exception.ServiceException;
import models.Resident;

public class MemberServiceImpl  implements MemberService {

	private final MemberDAO memberDAO = new MemberDAO();
	
	@Override
    public Resident getMemberById(String memberId) throws ServiceException {
		return memberDAO.findById(memberId) ;
    }

    @Override
    public List<Resident> getMembersByHouseholdId(int householdId) throws ServiceException {
    	return memberDAO.findByHouseholdId(householdId);
    }

    @Override
	public List<Resident> getMembersByMemberIds(List<String> memIds) throws ServiceException {
    	List<Resident> mems = new ArrayList<>();
    	for (String memId : memIds) {
    		Resident m = memberDAO.findById(memId);
    		mems.add(m);
    	}
    	return mems;
	}

	@Override
	public List<Resident> getAll() throws ServiceException {
		return memberDAO.findAll();
	}

	@Override
    public boolean memberExists(String memberId) {
    	return memberDAO.memberExists(memberId);
    }

    @Override
    public boolean addMember(Resident member) throws ServiceException, SQLException {
    	memberDAO.add(member);
    	return true;
	}

	@Override
	public boolean updateMember(Resident member) throws ServiceException, SQLException {
		memberDAO.update(member);
		return true;
    }

    @Override
	public void updateMembers(List<Resident> members) throws ServiceException, SQLException {
    	memberDAO.updateMembers(members);
    	
    	
	}

	@Override
    public boolean deleteMember(String memberId) throws ServiceException, SQLException {
        memberDAO.delete(memberId);
        return true;
    }

    @Override
    public Resident getHouseholdHead(int householdId) throws ServiceException {
    	return memberDAO.getHouseholdHead(householdId);
    	
    }

	public void setHouseholdOwnerByMemberId(String ownerId) throws ServiceException, SQLException {
		memberDAO.setHouseholdOwnerByMemberId(ownerId);
	}

}
