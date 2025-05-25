package services;

import java.util.List;
import java.sql.SQLException;
import models.Fee;
import java.util.stream.Collectors;
import dao.fee.FeeDAO;
import dao.fee.FeeDAOPostgreSQL;
import dto.fee.AddFeeDTO;
import dto.fee.UpdateFeeDTO;
import dto.fee.FeeListDTO;
import exception.InvalidInputException;

public class FeeService {
	private FeeDAO feeDAO;
	
	public FeeService() {
		try {
			feeDAO = new FeeDAOPostgreSQL();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Không thể kết nối đến cơ sở dữ liệu", e);
		}
	}
	
    public List<FeeListDTO> getAllFees() throws InvalidInputException {
        try {
            List<Fee> fees = feeDAO.getFees();
            return fees.stream()
                .map(fee -> new FeeListDTO(
                    fee.getId(), fee.getName(), fee.getCreatedDate(),
                    fee.getIsMandatory(), fee.getDescription()
                )).collect(Collectors.toList());
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
        }
    }

    public List<Fee> getAllFeesAsModel() throws InvalidInputException {
        try {
            return feeDAO.getFees();
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi lấy danh sách khoản thu: " + e.getMessage());
        }
    }

    public boolean addFee(AddFeeDTO dto) throws InvalidInputException {
        try {
            // Validate DTO
            if (dto == null) {
                throw new InvalidInputException("Dữ liệu khoản thu không được để trống");
            }

            List<Fee> existingFees = feeDAO.getFees();
            boolean nameExists = existingFees.stream()
                .anyMatch(fee -> fee.getName().equalsIgnoreCase(dto.getName()));
            if (nameExists) {
                throw new InvalidInputException("Đã tồn tại khoản thu với tên này");
            }

            Fee model = new Fee(
                0, 
                dto.getName(),
                dto.getCreatedDate(),
                dto.getIsMandatory(),
                dto.getDescription()
            );

            boolean result = feeDAO.addFee(model);
            if (!result) {
                throw new InvalidInputException("Không thể thêm khoản thu vào cơ sở dữ liệu");
            }
            return true;
        } catch (SQLException e) {
            throw new InvalidInputException("Lỗi cơ sở dữ liệu khi thêm khoản thu: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi không xác định khi thêm khoản thu: " + e.getMessage());
        }
    }

    public boolean updateFee(UpdateFeeDTO dto) throws InvalidInputException {
        try {
            if (dto == null) {
                throw new InvalidInputException("Dữ liệu khoản thu không được để trống");
            }

            Fee existingFee = feeDAO.getFeeById(dto.getId());
            if (existingFee == null) {
                throw new InvalidInputException("Không tìm thấy khoản thu cần cập nhật");
            }

            if (!existingFee.getName().equals(dto.getName())) {
                List<Fee> existingFees = feeDAO.getFees();
                boolean nameExists = existingFees.stream()
                    .anyMatch(fee -> fee.getId() != dto.getId() && fee.getName().equalsIgnoreCase(dto.getName()));
                if (nameExists) {
                    throw new InvalidInputException("Đã tồn tại khoản thu với tên này");
                }
            }

            Fee model = new Fee(
                dto.getId(),
                dto.getName(),
                dto.getCreatedDate(),
                dto.getIsMandatory(),
                dto.getDescription()
            );

            boolean result = feeDAO.updateFee(model);
            if (!result) {
                throw new InvalidInputException("Không thể cập nhật khoản thu trong cơ sở dữ liệu");
            }
            return true;
        } catch (SQLException e) {
            throw new InvalidInputException("Lỗi cơ sở dữ liệu khi cập nhật khoản thu: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public boolean deleteFee(int feeId) throws InvalidInputException {
        try {
            Fee existingFee = feeDAO.getFeeById(feeId);
            if (existingFee == null) {
                throw new InvalidInputException("Không tìm thấy khoản thu cần xóa");
            }

            if (feeDAO.isPartOfCampaignFee(feeId)) {
                throw new InvalidInputException("Không thể xóa khoản thu này vì nó đang thuộc về một hoặc nhiều đợt thu. Hãy xóa khoản thu này khỏi đợt thu trước!");
            }

            boolean result = feeDAO.deleteFee(feeId);
            if (!result) {
                throw new InvalidInputException("Không thể xóa khoản thu khỏi cơ sở dữ liệu");
            }
            return true;
        } catch (SQLException e) {
            throw new InvalidInputException("Lỗi cơ sở dữ liệu khi xóa khoản thu: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public UpdateFeeDTO getFeeById(int feeId) throws InvalidInputException {
        try {
            Fee fee = feeDAO.getFeeById(feeId);
            if (fee == null) return null;

            return new UpdateFeeDTO(
                fee.getId(),
                fee.getName(),
                fee.getCreatedDate(),
                fee.getIsMandatory(),
                fee.getDescription()
            );
        } catch (SQLException e) {
            throw new InvalidInputException("Lỗi cơ sở dữ liệu khi lấy thông tin khoản thu: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public FeeListDTO getFeeListById(int feeId) throws InvalidInputException {
        try {
            Fee fee = feeDAO.getFeeById(feeId);
            if (fee == null) return null;

            return new FeeListDTO(
                fee.getId(),
                fee.getName(),
                fee.getCreatedDate(),
                fee.getIsMandatory(),
                fee.getDescription()
            );
        } catch (SQLException e) {
            throw new InvalidInputException("Lỗi cơ sở dữ liệu khi lấy thông tin khoản thu: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }
}
