package controllers;

import dto.fee.UpdateFeeDTO;
import services.FeeService;
import exception.InvalidInputException;

public class UpdateFeeFormController extends FeeFormController {
    private FeeService feeService;

    public UpdateFeeFormController() {
        try {
            this.feeService = new FeeService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveUpdateFee(UpdateFeeDTO feeDTO) throws InvalidInputException {
        try {
            if (!validateFeeDTO(feeDTO)) {
                throw new InvalidInputException("Dữ liệu khoản thu không hợp lệ");
            }
            boolean result = feeService.updateFee(feeDTO);
            return result;
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi không xác định khi cập nhật khoản thu: " + e.getMessage());
        }
    }

    public UpdateFeeDTO getFeeById(int feeId) throws InvalidInputException {
        try {
            return feeService.getFeeById(feeId);
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi không xác định khi lấy thông tin khoản thu: " + e.getMessage());
        }
    }
}
