package views;

import javafx.application.Application;
import javafx.stage.Stage;
import views.login.LoginPageHandler;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginPageHandler loginPage = new LoginPageHandler(primaryStage);
        loginPage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}