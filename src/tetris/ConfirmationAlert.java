package tetris;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 * Egy felugró párbeszédablak, ami a játékostól kér megerõsítést.
 */
public class ConfirmationAlert extends Alert {
	/**
	 * Létrehoz egy párbeszédablakot a megadott címmel és kérdéssel.
	 * @param title A felugró ablak címe.
	 * @param text Az ablak szövege.
	 */
	ConfirmationAlert(String title, String text) {
		super(AlertType.CONFIRMATION, text);
		this.setTitle(title);
		this.setHeaderText(null);
		Button okBtn = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
		okBtn.setText("Igen");
		Button noBtn = (Button) this.getDialogPane().lookupButton(ButtonType.CANCEL);
		noBtn.setText("Nem");
	}
	/**
	 * Megjeleníti a felugró ablakot és megvárja, amíg a felhasználó reagál.
	 */
	public void showAlert() {
		this.showAndWait().ifPresent(response -> { return; });
	}
}
