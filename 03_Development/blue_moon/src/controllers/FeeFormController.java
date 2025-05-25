package controllers;

import dto.fee.AddFeeDTO;
import dto.fee.UpdateFeeDTO;
import exception.InvalidInputException;
import models.Fee;
import java.time.LocalDate;

public class FeeFormController extends BaseController {
    
    protected boolean validateFeeDTO(Object dto) throws InvalidInputException {
        if (dto == null) {
            throw new InvalidInputException("Dữ liệu khoản thu không được để trống");
        }
        
        String name = null;
        LocalDate createdDate = null;
        boolean isMandatory = false;
        String description = null;
        
        if (dto instanceof AddFeeDTO) {
            AddFeeDTO addDTO = (AddFeeDTO) dto;
            name = addDTO.getName();
            createdDate = addDTO.getCreatedDate();
            isMandatory = addDTO.getIsMandatory();
            description = addDTO.getDescription();
        } else if (dto instanceof UpdateFeeDTO) {
            UpdateFeeDTO updateDTO = (UpdateFeeDTO) dto;
            name = updateDTO.getName();
            createdDate = updateDTO.getCreatedDate();
            isMandatory = updateDTO.getIsMandatory();
            description = updateDTO.getDescription();
        } else {
            throw new InvalidInputException("Loại dữ liệu không hợp lệ");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Tên khoản thu không được để trống");
        }
        
        if (name.length() > 100) {
            throw new InvalidInputException("Tên khoản thu không được vượt quá 100 ký tự");
        }    
        
        if (description != null && description.length() > 500) {
            throw new InvalidInputException("Mô tả không được vượt quá 500 ký tự");
        }
        
        return true;
    }
}
