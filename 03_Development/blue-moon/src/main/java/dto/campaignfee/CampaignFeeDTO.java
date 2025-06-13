package dto.campaignfee;

import java.util.List;

public class CampaignFeeDTO {
	private int id;
	private String name;
	private String startDay;
	private String startMonth;
	private String startYear;
	private String dueDay;
	private String dueMonth;
	private String dueYear;
	private String description;
	private String status;
	private List<Integer> feeIds;
	
	public CampaignFeeDTO(String name, String startDay, String startMonth, String startYear, String dueDay,
			String dueMonth, String dueYear, String description, List<Integer> feeIds) {
		this.name = name;
		this.startDay = startDay;
		this.startMonth = startMonth;
		this.startYear = startYear;
		this.dueDay = dueDay;
		this.dueMonth = dueMonth;
		this.dueYear = dueYear;
		this.description = description;
		this.feeIds = feeIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getDueDay() {
		return dueDay;
	}

	public void setDueDay(String dueDay) {
		this.dueDay = dueDay;
	}

	public String getDueMonth() {
		return dueMonth;
	}

	public void setDueMonth(String dueMonth) {
		this.dueMonth = dueMonth;
	}

	public String getDueYear() {
		return dueYear;
	}

	public void setDueYear(String dueYear) {
		this.dueYear = dueYear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Integer> getFeeIds() {
		return feeIds;
	}

	public void setFeeIds(List<Integer> feeIds) {
		this.feeIds = feeIds;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
