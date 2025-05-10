package controllers;

import models.User;
import dto.LoginDTO;
import exception.*;
import services.LoginService;
import java.util.List;
import java.util.ArrayList;
import javafx.stage.Stage;
import views.BaseScreenHandler;
import views.homepage.*;

public class LoginController extends BaseController {
	private final LoginService loginService = new LoginService();
	
	public BaseScreenHandler handleLogin(LoginDTO dto, Stage stage) throws InvalidInputException, AuthenticationException, Exception {
		validateLoginInput(dto);
		User user = authenticateLoginInput(dto);
		return navigateToHomePage(user, stage);
	}
	
	private void validateLoginInput(LoginDTO dto) throws InvalidInputException {
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
	
	private User authenticateLoginInput(LoginDTO dto) throws AuthenticationException {
        return loginService.authenticate(dto);
	}
	
	private BaseScreenHandler navigateToHomePage(User user, Stage stage) throws Exception {
		BaseScreenHandler handler;
		
		switch (user.getRole()) {
		case ACCOUNTANT:
			handler = new AccountantHomePageHandler(stage);
			break;
		default:
			throw new UnsupportedOperationException("Vai trò chưa được hỗ trợ.");
		}
		
		return handler;
	}
}
