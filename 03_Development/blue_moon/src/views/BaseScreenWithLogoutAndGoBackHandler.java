package views;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import views.messages.ErrorDialog;
import views.login.LoginPageHandler;

public abstract class BaseScreenWithLogoutAndGoBackHandler extends BaseScreenHandler {
	@FXML
	protected Button btnLogout;
	
	@FXML
	protected Button btnGoBack;
	
	public BaseScreenWithLogoutAndGoBackHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
		super(stage, screenPath, iconPath, title);
	}
	
	@FXML
	public void initialize() {
		btnLogout.setOnAction(e -> handleLogout());
		btnGoBack.setOnAction(e -> handleGoBack());
	}
	
	protected void handleLogout() {
		try {
			ScreenNavigator.clear();
			BaseScreenHandler loginPage = new LoginPageHandler(this.stage);
			loginPage.show();
		} catch (Exception e){
			ErrorDialog.showError("Lỗi hệ thống", "Không thể đăng xuất!");
			e.printStackTrace();
		}
	}
	
	protected void handleGoBack() {
		try {
			ScreenNavigator.goBack();
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể quay lại trang trước đó!");
			e.printStackTrace();
		}
	}
}
