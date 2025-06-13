package views;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import controllers.BaseController;

public abstract class BaseScreenHandler extends FXMLScreenHandler {
	protected Stage stage;
	protected Scene scene;
	protected String iconPath;
	protected String pageTitle;
	protected BaseController controller;
	
	public BaseScreenHandler(String screenPath) throws Exception {
		super(screenPath);
	}
	
	public BaseScreenHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
		super(screenPath);
		this.stage = stage;
		this.iconPath = iconPath;
		this.pageTitle = title;
	}
	
	protected void setScene() {
		this.scene = new Scene(content);
	}
	
	protected void setController(BaseController controller) {
		this.controller = controller;
	}
	
	public void show() {
		if (!ScreenNavigator.isNavigatingBack()) {
			ScreenNavigator.push(this);
		}
		stage.setScene(scene);
		stage.setTitle(pageTitle);
		stage.getIcons().add(new Image(getClass().getResource(iconPath).toString()));
		stage.setResizable(true);
		stage.show();
	}
	
	/*
	 * This method aims to show the pop up screen (e.g: show the form to enter new campaign fee's informations)
	 */
	public void showPopup(Stage ownerStage) {
		stage.initOwner(ownerStage);
	    stage.initModality(Modality.APPLICATION_MODAL);
	    stage.setTitle(pageTitle);
	    stage.setScene(scene);
	    stage.setResizable(false);
	    stage.getIcons().add(new Image(getClass().getResource(iconPath).toString()));
	    stage.showAndWait();
	}
}
