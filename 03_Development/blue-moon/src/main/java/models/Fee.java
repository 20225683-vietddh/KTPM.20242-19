package models;

import java.time.LocalDate;
import java.util.Objects;

public class Fee {
	private int id;
	private String name;
	private LocalDate createdDate;
	private boolean isMandatory;
	private String description;
	
	public Fee(int id, String name, LocalDate createdDate, boolean isMandatory, String description) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.isMandatory = isMandatory;
		this.description = description;
	}
	
	public Fee() {}

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
	
	public boolean getIsMandatory() {
		return isMandatory;
	}
	
	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override 
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Fee fee = (Fee) o;
		return id == fee.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
