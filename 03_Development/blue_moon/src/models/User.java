package models;

import utils.enums.Role;

public class User {
	private int id;
	private String name;
	private String loginName;
	private String password;
	private Role role;
	
	public User(int id, String name, String loginName, String password, Role role) {
		this.id = id;
		this.name = name;
		this.loginName = loginName;
		this.password = password;
		this.role = role;
	}
	
	public User(String loginName, String password, Role role) {
		this.loginName = loginName;
		this.password = password;
		this.role = role;
	}

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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
