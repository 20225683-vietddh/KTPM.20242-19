package services;

import java.util.List;
import java.util.stream.Collectors;
import dao.FeeDAO;
import dto.AddFeeDTO;
import dto.UpdateFeeDTO;
import dto.FeeListDTO;
import models.Fee;
import exception.InvalidInputException;

public class FeeService {
    private FeeDAO feeDAO;

    public FeeService() {
        this.feeDAO = new FeeDAO();
    }

    public boolean addFee(AddFeeDTO dto) throws InvalidInputException {
        try {
            // Validate DTO
            if (dto == null) {
                throw new InvalidInputException("Dữ liệu khoản thu không được để trống");
            }

            // Check if fee with same name exists
            List<Fee> existingFees = feeDAO.getAllFees();
            boolean nameExists = existingFees.stream()
                .anyMatch(fee -> fee.getName().equalsIgnoreCase(dto.getName()));
            if (nameExists) {
                throw new InvalidInputException("Đã tồn tại khoản thu với tên này");
            }

            // Generate ID
            String newId = generateFeeId();

            // Create model
            Fee model = new Fee(
                newId, dto.getName(), dto.getCreatedDate(), dto.getAmount(),
                dto.getIsMandatory(), dto.getStatus(), dto.getDescription()
            );

            // Save to database
            boolean result = feeDAO.addFee(model);
            if (!result) {
                throw new InvalidInputException("Không thể thêm khoản thu vào cơ sở dữ liệu");
            }
            return true;
        } catch (Exception e) {
            if (e instanceof InvalidInputException) {
                throw e;
            }
            throw new InvalidInputException("Lỗi khi thêm khoản thu: " + e.getMessage());
        }
    }

    public boolean updateFee(UpdateFeeDTO dto) throws InvalidInputException {
        try {
            // Validate DTO
            if (dto == null) {
                throw new InvalidInputException("Dữ liệu khoản thu không được để trống");
            }

            // Check if fee exists
            Fee existingFee = feeDAO.getFeeById(dto.getId());
            if (existingFee == null) {
                throw new InvalidInputException("Không tìm thấy khoản thu cần cập nhật");
            }

            // Check if name is changed and if new name exists
            if (!existingFee.getName().equals(dto.getName())) {
                List<Fee> existingFees = feeDAO.getAllFees();
                boolean nameExists = existingFees.stream()
                    .anyMatch(fee -> fee.getName().equalsIgnoreCase(dto.getName()));
                if (nameExists) {
                    throw new InvalidInputException("Đã tồn tại khoản thu với tên này");
                }
            }

            // Create model
            Fee model = new Fee(
                dto.getId(), dto.getName(), dto.getCreatedDate(), dto.getAmount(),
                dto.getIsMandatory(), dto.getStatus(), dto.getDescription()
            );

            // Update in database
            boolean result = feeDAO.updateFee(model);
            if (!result) {
                throw new InvalidInputException("Không thể cập nhật khoản thu trong cơ sở dữ liệu");
            }
            return true;
        } catch (Exception e) {
            if (e instanceof InvalidInputException) {
                throw e;
            }
            throw new InvalidInputException("Lỗi khi cập nhật khoản thu: " + e.getMessage());
        }
    }

    public boolean deleteFee(String feeId) throws InvalidInputException {
        try {
            // Validate feeId
            if (feeId == null || feeId.trim().isEmpty()) {
                throw new InvalidInputException("ID khoản thu không được để trống");
            }

            // Check if fee exists
            Fee existingFee = feeDAO.getFeeById(feeId);
            if (existingFee == null) {
                throw new InvalidInputException("Không tìm thấy khoản thu cần xóa");
            }

            // Delete from database
            boolean result = feeDAO.deleteFee(feeId);
            if (!result) {
                throw new InvalidInputException("Không thể xóa khoản thu khỏi cơ sở dữ liệu");
            }
            return true;
        } catch (Exception e) {
            if (e instanceof InvalidInputException) {
                throw e;
            }
            throw new InvalidInputException("Lỗi khi xóa khoản thu: " + e.getMessage());
        }
    }

    public List<FeeListDTO> getAllFees() throws InvalidInputException {
        try {
            return feeDAO.getAllFees().stream()
                .map(fee -> new FeeListDTO(
                    fee.getId(), fee.getName(), fee.getCreatedDate(),
                    fee.getAmount(), fee.getIsMandatory(), fee.getStatus(), fee.getDescription()
                )).collect(Collectors.toList());
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
        }
    }

    public UpdateFeeDTO getFeeById(String feeId) throws InvalidInputException {
        try {
            // Validate feeId
            if (feeId == null || feeId.trim().isEmpty()) {
                throw new InvalidInputException("ID khoản thu không được để trống");
            }

            Fee fee = feeDAO.getFeeById(feeId);
            if (fee == null) return null;

            return new UpdateFeeDTO(
                fee.getId(), fee.getName(), fee.getCreatedDate(),
                fee.getAmount(), fee.getIsMandatory(), fee.getStatus(), fee.getDescription()
            );
        } catch (Exception e) {
            if (e instanceof InvalidInputException) {
                throw e;
            }
            throw new InvalidInputException("Lỗi khi lấy thông tin khoản thu: " + e.getMessage());
        }
    }

    public FeeListDTO getFeeListById(String feeId) throws InvalidInputException {
        try {
            // Validate feeId
            if (feeId == null || feeId.trim().isEmpty()) {
                throw new InvalidInputException("ID khoản thu không được để trống");
            }

            Fee fee = feeDAO.getFeeById(feeId);
            if (fee == null) return null;

            return new FeeListDTO(
                fee.getId(), fee.getName(), fee.getCreatedDate(),
                fee.getAmount(), fee.getIsMandatory(), fee.getStatus(), fee.getDescription()
            );
        } catch (Exception e) {
            if (e instanceof InvalidInputException) {
                throw e;
            }
            throw new InvalidInputException("Lỗi khi lấy thông tin khoản thu: " + e.getMessage());
        }
    }

    private String generateFeeId() {
        // Generate a unique ID based on timestamp and random number
        return "FEE" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
