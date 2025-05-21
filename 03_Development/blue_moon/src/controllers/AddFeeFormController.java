package controllers;

import dto.AddFeeDTO;
import services.FeeService;
import exception.InvalidInputException;

public class AddFeeFormController extends FeeFormController {
    private FeeService feeService;

    public AddFeeFormController() {
        this.feeService = new FeeService();
    }

    public boolean saveAddFee(AddFeeDTO feeDTO) throws InvalidInputException {
        try {
            if (!validateFeeDTO(feeDTO)) {
                throw new InvalidInputException("Dữ liệu khoản thu không hợp lệ");
            }
            return feeService.addFee(feeDTO);
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi không xác định khi thêm khoản thu: " + e.getMessage());
        }
    }
}
