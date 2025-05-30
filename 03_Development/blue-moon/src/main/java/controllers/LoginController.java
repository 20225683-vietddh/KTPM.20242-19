package controllers;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import exception.*;
import services.LoginService;
import dto.login.*;
import utils.Utils;

public class LoginController extends BaseController {
	private final LoginService loginService;
	
	public LoginController() throws SQLException {
		this.loginService = new LoginService();
	}
	
	public LoginResponseDTO handleLogin(LoginRequestDTO requestDTO) throws InvalidInputException, AuthenticationException {
		validateLoginInput(requestDTO);
		LoginResponseDTO responseDTO = authenticateLoginInput(requestDTO);
		return responseDTO;
	}
	
	private void validateLoginInput(LoginRequestDTO dto) throws InvalidInputException {
		List<String> errorMessages = new ArrayList<>();

		if (dto.getLoginName() == null || dto.getLoginName().isEmpty()) {
		    errorMessages.add("Vui lòng nhập tên đăng nhập!");
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
		    errorMessages.add("Vui lòng nhập mật khẩu!");
		}

		if (dto.getRole() == null) {
		    errorMessages.add("Vui lòng chọn vai trò của bạn!");
		}

		if (!errorMessages.isEmpty()) {
		    throw new InvalidInputException(String.join("\n", errorMessages));
		}
	}
	
	private LoginResponseDTO authenticateLoginInput(LoginRequestDTO requestDTO) throws AuthenticationException {
		String plainTextPassword = requestDTO.getPassword();
		String cipherTextPassword = Utils.toSHA256(plainTextPassword);
		requestDTO.setPassword(cipherTextPassword);
        return loginService.authenticate(requestDTO);
	}
}
