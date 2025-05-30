package dto.fee;

import java.time.LocalDate;

public class AddFeeDTO extends FeeDTO {
    public AddFeeDTO(String name, LocalDate createdDate, boolean isMandatory, String description) {
        super(name, createdDate, isMandatory, description);
    }
}
