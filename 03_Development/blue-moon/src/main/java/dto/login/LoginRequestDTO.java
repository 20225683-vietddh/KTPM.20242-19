/**
 * This class aims to store the informations of unauthorized user
 */
package dto.login;

import utils.Role;

public class LoginRequestDTO {
	private String loginName;
	private String password;
	private Role role;
	
	public LoginRequestDTO(String loginName, String password, Role role) {
		this.loginName = loginName;
		this.password = password;
		this.role = role;
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
