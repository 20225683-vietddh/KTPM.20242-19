package dao.user;

import dto.login.*;

public interface UserDAO {
	public LoginResponseDTO findByCredentials(LoginRequestDTO requestDTO);
}