package dto.login;

import utils.enums.Role;

public class LoginResponseDTO {
	public String userName;
	public Role role;
	
	public LoginResponseDTO(String userName, Role role) {
		this.userName = userName;
		this.role = role;
	}
	
	public LoginResponseDTO() {}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
