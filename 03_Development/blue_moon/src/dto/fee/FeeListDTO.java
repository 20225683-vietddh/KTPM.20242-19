package dto.fee;

import java.time.LocalDate;

public class FeeListDTO extends FeeDTO {
    private int id;

    public FeeListDTO(int id, String name, LocalDate createdDate, boolean isMandatory, String description) {
        super(name, createdDate, isMandatory, description);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}