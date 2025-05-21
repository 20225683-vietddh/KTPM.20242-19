package controllers;

import dao.FeeDAO;
import dto.FeeListDTO;
import exception.InvalidInputException;
import models.Fee;
import utils.Status;
import utils.Mandatory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeeListController {
    // private final FeeDAO feeDAO;

    public FeeListController() {
        // this.feeDAO = new FeeDAO();
    }

    public List<FeeListDTO> showFees() throws InvalidInputException {
        try {
            // Comment out database code
            // List<Fee> fees = feeDAO.getAllFees();
            List<FeeListDTO> feeDTOs = new ArrayList<>();
            // for (Fee fee : fees) {
            //     feeDTOs.add(new FeeListDTO(
            //         fee.getId(),
            //         fee.getName(),
            //         fee.getCreatedDate(),
            //         fee.getAmount(),
            //         fee.getIsMandatory(),
            //         fee.getStatus(),
            //         fee.getDescription()
            //     ));
            // }
            // Add sample data
            feeDTOs.add(new FeeListDTO(
                "FEE001",
                "Phí vệ sinh",
                LocalDate.now(),
                50000.0,
                Mandatory.MANDATORY,
                Status.UNPAID,
                "Phí vệ sinh hàng tháng"
            ));
            
            feeDTOs.add(new FeeListDTO(
                "FEE002",
                "Phí an ninh",
                LocalDate.now(),
                100000.0,
                Mandatory.MANDATORY,
                Status.UNPAID,
                "Phí an ninh hàng tháng"
            ));
            
            feeDTOs.add(new FeeListDTO(
                "FEE003",
                "Phí gửi xe",
                LocalDate.now(),
                200000.0,
                Mandatory.OPTIONAL,
                Status.UNPAID,
                "Phí gửi xe hàng tháng"
            ));
            
            return feeDTOs;
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
        }
    }

    public boolean deleteFee(String id) throws InvalidInputException {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new InvalidInputException("ID khoản thu không được để trống");
            }
            // Comment out database code
            // return feeDAO.deleteFee(id);
            return true; // Return true for testing
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi xóa khoản thu: " + e.getMessage());
        }
    }

    public FeeListDTO getFeeById(String id) throws InvalidInputException {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new InvalidInputException("ID khoản thu không được để trống");
            }
            // Comment out database code
            // Fee fee = feeDAO.getFeeById(id);
            // if (fee == null) {
            //     return null;
            // }
            
            // Return sample data for testing
            return new FeeListDTO(
                id,
                "Phí vệ sinh",
                LocalDate.now(),
                50000.0,
                Mandatory.MANDATORY,
                Status.UNPAID,
                "Phí vệ sinh hàng tháng"
            );
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy thông tin khoản thu: " + e.getMessage());
        }
    }
}
