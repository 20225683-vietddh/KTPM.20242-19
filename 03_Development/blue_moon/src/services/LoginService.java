package services;

import dao.user.*;
import dto.login.*;
import exception.*;
import java.sql.SQLException;

public class LoginService {
	private final UserDAO userDAO;
	
	public LoginService() throws SQLException {
		this.userDAO = new UserDAOPostgreSQL();
	}
	
    public LoginResponseDTO authenticate(LoginRequestDTO requestDTO) throws AuthenticationException {
        LoginResponseDTO responseDTO = userDAO.findByCredentials(requestDTO);

        if (responseDTO == null) {
            throw new AuthenticationException("Sai tên đăng nhập, hoặc mật khẩu, hoặc vai trò. Hãy kiểm tra lại!");
        }

        return responseDTO;
    }
}