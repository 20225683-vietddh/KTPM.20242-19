package controllers;

import dto.AddFeeDTO;
import dto.UpdateFeeDTO;
import java.time.LocalDate;

public abstract class FeeFormController {
    public FeeFormController() {}

    // Validate FeeDTO (AddFeeDTO/UpdateFeeDTO)
    public boolean validateFeeDTO(Object feeDTO) throws IllegalArgumentException {
        if (feeDTO == null) return false;

        String name = null;
        LocalDate createdDate = null;
        double amount = 0;
        String description = null;

        if (feeDTO instanceof AddFeeDTO) {
            AddFeeDTO dto = (AddFeeDTO) feeDTO;
            name = dto.getName();
            createdDate = dto.getCreatedDate();
            amount = dto.getAmount();
            description = dto.getDescription();
        } else if (feeDTO instanceof UpdateFeeDTO) {
            UpdateFeeDTO dto = (UpdateFeeDTO) feeDTO;
            name = dto.getName();
            createdDate = dto.getCreatedDate();
            amount = dto.getAmount();
            description = dto.getDescription();
        } else {
            return false;
        }

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoản thu không được để trống");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Tên khoản thu không được vượt quá 100 ký tự");
        }

        // Validate created date
        if (createdDate == null) {
            throw new IllegalArgumentException("Ngày tạo không được để trống");
        }
        if (createdDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày tạo không được lớn hơn ngày hiện tại");
        }

        // Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }

        // Validate description
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 500 ký tự");
        }

        return true;
    }

    // Show kết quả (thành công/thất bại)
    public void showResult(boolean success) {
        if (success) {
            System.out.println("Thao tác thành công!");
        } else {
            System.out.println("Thao tác thất bại!");
        }
    }
}
