package tetris;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 * Egy felugr� p�rbesz�dablak, ami a j�t�kost�l k�r meger�s�t�st.
 */
public class ConfirmationAlert extends Alert {
	/**
	 * L�trehoz egy p�rbesz�dablakot a megadott c�mmel �s k�rd�ssel.
	 * @param title A felugr� ablak c�me.
	 * @param text Az ablak sz�vege.
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
	 * Megjelen�ti a felugr� ablakot �s megv�rja, am�g a felhaszn�l� reag�l.
	 */
	public void showAlert() {
		this.showAndWait().ifPresent(response -> { return; });
	}
}
