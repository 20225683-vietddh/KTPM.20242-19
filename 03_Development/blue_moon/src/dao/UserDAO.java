package dao;

import models.User;
import dto.LoginDTO;

public class UserDAO {
	public User findByCredentials(LoginDTO dto) {
		// SELECT * FROM users WHERE login_name=? AND password=? AND role=?
		return new User(dto.getLoginName(), dto.getPassword(), dto.getRole());
	}
}
