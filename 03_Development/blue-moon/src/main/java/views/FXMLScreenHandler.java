package views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public abstract class FXMLScreenHandler {
	protected FXMLLoader loader;
	protected Parent content;
	
	public FXMLScreenHandler(String screenPath) throws Exception {
		loader = new FXMLLoader(getClass().getResource(screenPath));
	}
	
	protected void setContent() throws Exception {
		this.content = loader.load();
	}
}
