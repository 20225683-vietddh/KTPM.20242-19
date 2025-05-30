package views.campaignfee;

import javafx.scene.layout.HBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import java.lang.Runnable;
import java.util.List;
import models.Fee;

public class FeeCell {
	private final HBox container;
	private final ComboBox<Fee> comboBox;
	private final Button addButton;

	public FeeCell(List<Fee> feeOptions, Runnable onAddNewFee) {
		comboBox = new ComboBox<>(FXCollections.observableArrayList(feeOptions));

		comboBox.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(Fee item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.getName());
			}
		});
		comboBox.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Fee item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.getName());
			}
		});

		comboBox.setStyle(
				"-fx-background-color: white; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-radius: 5px; -fx-font-size: 16px;");
		comboBox.setPrefWidth(510);
		comboBox.setMaxWidth(510);
		comboBox.setPromptText("Chọn khoản thu");

		addButton = new Button("+");
		addButton.setPrefWidth(40);
		addButton.setMaxWidth(40);
		addButton.setStyle(
				"-fx-font-size: 17px; -fx-background-color: #F3F3F3; -fx-background-radius: 5px; -fx-cursor: hand;");
		addButton.setOnAction(e -> onAddNewFee.run());

		container = new HBox(10, comboBox, addButton);
	}
	
	public HBox getContainer() {
		return container;
	}

	public Fee getSelectedFee() {
		return comboBox.getSelectionModel().getSelectedItem();
	}

	public ComboBox<Fee> getComboBox() {
		return comboBox;
	}
}
