package controllers;

import models.User;
import dto.LoginDTO;
import exception.*;
import services.LoginService;
import java.util.List;
import java.util.ArrayList;

public class LoginController extends BaseController {
	private final LoginService loginService = new LoginService();
	
	public User handleLogin(LoginDTO dto) throws InvalidInputException, AuthenticationException {
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
        
        return loginService.authenticate(dto);
	}
}
