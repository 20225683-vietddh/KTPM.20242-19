package dto.fee;

import java.time.LocalDate;

public abstract class FeeDTO {
    protected String name;
    protected LocalDate createdDate;
    protected boolean isMandatory;
    protected String description;

    public FeeDTO(String name, LocalDate createdDate, boolean isMandatory, String description) {
        this.name = name;
        this.createdDate = createdDate;
        this.isMandatory = isMandatory;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(boolean isMandatory) { this.isMandatory = isMandatory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
