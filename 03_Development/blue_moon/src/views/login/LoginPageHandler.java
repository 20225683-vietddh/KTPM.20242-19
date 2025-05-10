package views.login;

import views.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import utils.Role;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import dto.LoginDTO;
import controllers.LoginController;
import exception.*;
import views.messages.ErrorDialog;

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
		// Initialize combo box with a list of roles
		cbRole.getItems().addAll(Role.values());
		
		// Attach an event handler for login button
		btnLogin.setOnAction(e -> handleLogin());
	}
	
	private void handleLogin() {
		String loginName = tfLoginName.getText();
		String password = pfPassword.getText();
		Role selectedRole = cbRole.getValue();
		
		// System.out.println(loginName + " " + password + " " + selectedRole);
				
		try {
			LoginDTO dto = new LoginDTO(loginName, password, selectedRole);
			this.controller = new LoginController();
		    BaseScreenHandler homepage = ((LoginController) controller).handleLogin(dto, this.stage);
		    homepage.show();
			
		} catch (InvalidInputException e) {
			ErrorDialog.showError("Lỗi nhập liệu", e.getMessage());
		} catch (AuthenticationException e) {
			ErrorDialog.showError("Lỗi xác thực", e.getMessage());
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không tải được trang chủ!");
			e.printStackTrace();
		}
	}
}
