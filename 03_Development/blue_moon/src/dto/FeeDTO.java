package dto;

import java.time.LocalDate;
import utils.Status;
import utils.Mandatory;

public abstract class FeeDTO {
    protected String name;
    protected LocalDate createdDate;
    protected double amount;
    protected Mandatory isMandatory;
    protected Status status;
    protected String description;

    public FeeDTO(String name, LocalDate createdDate, double amount, Mandatory isMandatory, Status status, String description) {
        this.name = name;
        this.createdDate = createdDate;
        this.amount = amount;
        this.isMandatory = isMandatory;
        this.status = status;
        this.description = description;
    }

    // Getters and setters cho các trường chung
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Mandatory getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Mandatory isMandatory) { this.isMandatory = isMandatory; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
