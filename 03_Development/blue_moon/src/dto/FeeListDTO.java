package dto;

import java.time.LocalDate;
import utils.Status;
import utils.Mandatory;

public class FeeListDTO extends FeeDTO {
    private String id;

    public FeeListDTO(String id, String name, LocalDate createdDate, double amount, Mandatory isMandatory, Status status, String description) {
        super(name, createdDate, amount, isMandatory, status, description);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}