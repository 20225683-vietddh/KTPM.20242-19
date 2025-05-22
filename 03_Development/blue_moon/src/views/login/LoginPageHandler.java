package views.login;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import views.BaseScreenHandler;
import views.homepage.*;
import controllers.LoginController;
import dto.login.LoginRequestDTO;
import dto.login.LoginResponseDTO;
import exception.*;
import views.messages.ErrorDialog;
import utils.Role;

public class LoginPageHandler extends BaseScreenHandler {
	@FXML
	private TextField tfLoginName;
	
	@FXML
	private PasswordField pfPassword;
	
	@FXML
	private ComboBox<Role> cbRole;
	
	@FXML
	private Button btnLogin;
	
	public LoginPageHandler(Stage stage) throws Exception {
		super(stage, utils.Configs.LOGIN_PAGE_PATH, utils.Configs.LOGO_PATH, "Đăng nhập");
		loader.setController(this);
		this.setContent();
		this.setScene();
	}
	
	@FXML
	public void initialize() {
		cbRole.getItems().addAll(Role.values());
		btnLogin.setOnAction(e -> handleLogin());
	}
	
	private void handleLogin() {		
		LoginRequestDTO requestDTO = getUserInput();	
		try {
			this.controller = new LoginController();
		    LoginResponseDTO responseDTO = ((LoginController) controller).handleLogin(requestDTO);
		    navigate(responseDTO);
		} catch (InvalidInputException e) {
			ErrorDialog.showError("Lỗi nhập liệu", e.getMessage());
		} catch (AuthenticationException e) {
			ErrorDialog.showError("Lỗi xác thực", e.getMessage());
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không tải được trang chủ!");
			e.printStackTrace();
		}
	}
	
	private LoginRequestDTO getUserInput() {
		String loginName = tfLoginName.getText();
		String password = pfPassword.getText();
		Role selectedRole = cbRole.getValue();
		return new LoginRequestDTO(loginName, password, selectedRole);
	}
	
	private void navigate(LoginResponseDTO responseDTO) throws Exception {
		BaseScreenHandler handler;
		switch (responseDTO.getRole()) {
		case ACCOUNTANT:
			handler = new AccountantHomePageHandler(this.stage, responseDTO.getUserName());
			handler.show();
			break;
		case LEADER:
			handler = new LeaderHomePageHandler(this.stage, responseDTO.getUserName());
			handler.show();
			break;
		default:
			throw new Exception("Vai trò chưa được hỗ trợ");
		}	
	}
}
