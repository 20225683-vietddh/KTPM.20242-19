package models;

import java.time.LocalDate;
import java.util.List;

public class CampaignFee {
	private String id;
	private String name;
	private LocalDate createdDate;
	private LocalDate startDate;
	private LocalDate dueDate;
	private int status;
	private String description;
	private List<Fee> feeList;
	
	public CampaignFee(String id, String name, LocalDate createdDate, LocalDate startDate, LocalDate dueDate,
			int status, String description) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.status = status;
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
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Fee> getFeeList() {
		return feeList;
	}

	public void setFeeList(List<Fee> feeList) {
		this.feeList = feeList;
	}
}
