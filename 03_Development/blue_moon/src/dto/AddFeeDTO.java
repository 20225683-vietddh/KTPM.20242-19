package dto;

import java.time.LocalDate;
import utils.Status;
import utils.Mandatory;

public class AddFeeDTO extends FeeDTO {
    public AddFeeDTO(String name, LocalDate createdDate, double amount, Mandatory isMandatory, Status status, String description) {
        super(name, createdDate, amount, isMandatory, status, description);
    }
}
