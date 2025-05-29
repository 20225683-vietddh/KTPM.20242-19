package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Member;
import utils.Gender;
import utils.RelationshipType;

//public class MemberDB {
//	public static final List<Member> MEMBER_SAMPLE = new ArrayList<>(Arrays.asList(
//			new Member("M001", 1, "Nguyen Van A", Gender.MALE, "1975-04-12", "123456789", RelationshipType.FATHER,
//					"Engineer", true),
//			new Member("M002", 1, "Nguyen Thi A1", Gender.FEMALE, "1978-09-20", "123456790", RelationshipType.MOTHER,
//					"Teacher", false),
//			new Member("M003", 1, "Nguyen Van A2", Gender.MALE, "2005-06-01", "123456791", RelationshipType.SON,
//					"Student", false),
//
//			// Household 2
//			new Member("M004", 2, "Tran Thi B", Gender.FEMALE, "1980-01-01", "223456789", RelationshipType.MOTHER,
//					"Doctor", true),
//			new Member("M005", 2, "Nguyen Van B1", Gender.MALE, "1979-05-15", "223456790", RelationshipType.SON,
//					"Architect", false),
//			new Member("M006", 2, "Nguyen Van B2", Gender.MALE, "2003-02-18", "223456791", RelationshipType.SON,
//					"Student", false),
//			new Member("M007", 2, "Tran Van B3", Gender.MALE, "2008-07-12", "223456792", RelationshipType.SON,
//					"Student", false),
//			new Member("M008", 2, "Tran Van B4", Gender.MALE, "2012-03-22", "223456793", RelationshipType.SON,
//					"Student", false),
//
//			// Household 3
//			new Member("M009", 3, "Le Van C", Gender.MALE, "1985-08-08", "323456789", RelationshipType.FATHER, "Driver",
//					true),
//			new Member("M010", 3, "Le Thi C1", Gender.FEMALE, "1986-10-12", "323456790", RelationshipType.MOTHER,
//					"Cashier", false),
//			new Member("M011", 3, "Le Van C2", Gender.MALE, "2010-11-05", "323456791", RelationshipType.SON, "Student",
//					false),
//
//			// Household 4
//			new Member("M012", 4, "Pham Thi D", Gender.FEMALE, "1970-03-30", "423456789", RelationshipType.MOTHER,
//					"Businesswoman", true),
//			new Member("M013", 4, "Pham Van D1", Gender.MALE, "1969-06-10", "423456790", RelationshipType.GRAND_FATHER,
//					"Retired", false),
//			new Member("M014", 4, "Pham Van D2", Gender.MALE, "1995-12-20", "423456791", RelationshipType.SON,
//					"Engineer", false),
//			new Member("M015", 4, "Pham Van D3", Gender.MALE, "1998-09-11", "423456792", RelationshipType.SON,
//					"Salesman", false),
//			new Member("M016", 4, "Pham Thi D4", Gender.FEMALE, "2002-01-25", "423456793", RelationshipType.DAUGHTER,
//					"Student", false),
//			new Member("M017", 4, "Pham Van D5", Gender.MALE, "2005-06-06", "423456794", RelationshipType.SON,
//					"Student", false)));
//}