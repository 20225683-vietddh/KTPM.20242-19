package services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.MemberDAO;
import dao.MemberDB;
import exception.ServiceException;
import models.Member;

public class MemberServiceImpl  implements MemberService {

	private final MemberDAO memberDAO = new MemberDAO();
	
	@Override
    public Member getMemberById(String memberId) throws ServiceException {
//        return MemberDB.MEMBER_SAMPLE.stream()
//                .filter(member -> member.getId().equals(memberId))
//                .findFirst()
//                .orElseThrow(() -> new ServiceException("Member with ID " + memberId + " not found."));
		return memberDAO.findById(memberId) ;
    }

    @Override
    public List<Member> getMembersByHouseholdId(int householdId) throws ServiceException {
//        List<Member> result = new ArrayList<>();
//        for (Member member : MemberDB.MEMBER_SAMPLE) {
//            if (member.getHouseholdId() == householdId) {
//                result.add(member);
//            }
//        }
//        if (result.isEmpty()) {
//            throw new ServiceException("No members found for household ID " + householdId);
//        }
//        return result;
    	return memberDAO.findByHouseholdId(householdId);
    }

    @Override
    public boolean memberExists(String memberId) {
//        return MemberDB.MEMBER_SAMPLE.stream()
//                .anyMatch(member -> member.getId().equals(memberId));
    	return memberDAO.memberExists(memberId);
    }

    @Override
    public boolean addMember(Member member) throws ServiceException, SQLException {
//        if (memberExists(member.getId())) {
//            throw new ServiceException("Member with ID " + member.getId() + " already exists.");
//        }
//        return MemberDB.MEMBER_SAMPLE.add(member);
    	memberDAO.add(member);
    	return true;
	}

	@Override
	public boolean updateMember(Member member) throws ServiceException, SQLException {
//		if (member == null || member.getId() == null) {
//			throw new IllegalArgumentException("Member or Member ID must not be null.");
//		}
//
//		for (int i = 0; i < MemberDB.MEMBER_SAMPLE.size(); i++) {
//			Member existing = MemberDB.MEMBER_SAMPLE.get(i);
//			if (member.getId().equals(existing.getId())) {
//				System.out.println(
//						"update: hh head = " + member.getFullName() + " is hh head = " + member.isHouseholdHead());
//				MemberDB.MEMBER_SAMPLE.set(i, member);
//				return true;
//			}
//		}
//		throw new ServiceException("Cannot update. Member with ID " + member.getId() + " not found.");
		memberDAO.update(member);
		return true;
    }

    @Override
	public void updateMembers(List<Member> members) throws ServiceException, SQLException {
//    	for (Member m : members) {
//    		updateMember(m);
//    	}
    	memberDAO.updateMembers(members);
    	
    	
	}

	@Override
    public boolean deleteMember(String memberId) throws ServiceException, SQLException {
//        return MemberDB.MEMBER_SAMPLE.removeIf(member -> member.getId().equals(memberId));
        memberDAO.delete(memberId);
        return true;
    }

    @Override
    public Member getHouseholdHead(int householdId) throws ServiceException {
//        return MemberDB.MEMBER_SAMPLE.stream()
//                .filter(member -> member.getHouseholdId() == householdId && member.isHouseholdHead())
//                .findFirst()
//                .orElseThrow(() -> new ServiceException("Household head not found for household ID " + householdId));
    	return memberDAO.getHouseholdHead(householdId);
    	
    }

	public void setHouseholdOwnerByMemberId(String ownerId) throws ServiceException, SQLException {
//		getMemberById(ownerId).setHouseholdHead(true);
		memberDAO.setHouseholdOwnerByMemberId(ownerId);
	}

}
