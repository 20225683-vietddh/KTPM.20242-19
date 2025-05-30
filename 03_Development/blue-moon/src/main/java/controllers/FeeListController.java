package controllers;

import dto.fee.FeeListDTO;
import exception.InvalidInputException;
import services.FeeService;
import java.util.List;

public class FeeListController extends BaseController {
    private FeeService feeService;

    public FeeListController() {
        try {
            this.feeService = new FeeService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FeeListDTO> showFees() throws InvalidInputException {
        try {
            return feeService.getAllFees();
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
        }
    }

    public boolean deleteFee(int id) throws InvalidInputException {
        try {
            return feeService.deleteFee(id);
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi xóa khoản thu: " + e.getMessage());
        }
    }

    public FeeListDTO getFeeById(int id) throws InvalidInputException {
        try {
            return feeService.getFeeListById(id);
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy thông tin khoản thu: " + e.getMessage());
        }
    }
}
