package dao;

import java.util.List;
import dao.HouseholdDAO;
import models.Household;
import exception.HouseholdNotExist;

public class HouseholdDAOTest {
    
    public static void main(String[] args) {
        HouseholdDAO dao = new HouseholdDAO();
        
        System.out.println("=== Testing Database Connection ===");
        if (dao.testConnection()) {
            System.out.println("✓ Database connection successful!");
        } else {
            System.out.println("✗ Database connection failed!");
            return;
        }
        
        System.out.println("\n=== Testing findAll() ===");
        try {
            List<Household> households = dao.findAll();
            System.out.println("Found " + households.size() + " households:");
            
            for (Household h : households) {
                System.out.println("- " + h.toString());
            }
            
            if (households.isEmpty()) {
                System.out.println("⚠️  No households found. Check if data was inserted correctly.");
            }
            
        } catch (Exception e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Testing findById(1) ===");
        try {
            Household household = dao.findById(1);
            System.out.println("Found household: " + household.toString());
        } catch (HouseholdNotExist e) {
            System.err.println("Household with ID 1 not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in findById(): " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Testing findByHouseholdNumber('HH1') ===");
        try {
            Household household = dao.findByHouseholdNumber("HH1");
            System.out.println("Found household: " + household.toString());
        } catch (HouseholdNotExist e) {
            System.err.println("Household 'HH1' not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in findByHouseholdNumber(): " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Testing findByIdWithMembers(1) ===");
        try {
            Household household = dao.findByIdWithMembers(1);
            System.out.println("Found household with " + household.getMembers().size() + " members:");
            System.out.println(household.toString());
            
            household.getMembers().forEach(member -> {
                System.out.println("  Member: " + member.getFullName() + " (" + member.getRelationship() + ")");
            });
            
        } catch (HouseholdNotExist e) {
            System.err.println("Household with ID 1 not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in findByIdWithMembers(): " + e.getMessage());
            e.printStackTrace();
        }
    }
}