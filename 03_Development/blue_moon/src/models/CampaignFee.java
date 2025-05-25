package models;

import java.time.LocalDate;
import java.util.List;

public class CampaignFee {
	private int id;
	private String name;
	private LocalDate createdDate;
	private LocalDate startDate;
	private LocalDate dueDate;
	private String status;
	private String description;
	private List<Fee> fees;
	
	public CampaignFee(int id, String name, LocalDate createdDate, LocalDate startDate, LocalDate dueDate,
			String status, String description) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.status = status;
		this.description = description;
	}
	
	public CampaignFee() {}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
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
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	public LocalDate getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Fee> getFees() {
		return fees;
	}

	public void setFees(List<Fee> fees) {
		this.fees = fees;
	}
}
