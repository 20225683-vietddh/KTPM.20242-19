package models;

import java.time.LocalDate;

public class Fee {
	private String id;
	private String name;
	private LocalDate createdDate;
	private int amount;
	private boolean isMandatory;
	private String description;
	
	public Fee(String id, String name, LocalDate createdDate, int amount, boolean isMandatory, String description) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.amount = amount;
		this.isMandatory = isMandatory;
		this.description = description;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public boolean isMandatory() {
		return isMandatory;
	}
	
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
