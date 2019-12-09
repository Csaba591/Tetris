package tetris;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Hiba esetén figyelmezteti a felhasználót és kiírja a hiba típusát.
 */
public class ErrorAlert extends Alert {
	
	/**
	 * Létrehoz és megjelenít egy hiba értesítést.
	 * @param message A hibaüzenet.
	 */
	ErrorAlert(String message, String title) {
		super(AlertType.ERROR, message);
		this.setTitle(title);
		this.setHeaderText(null);
		// hiba esetén kilép a programból
		this.showAndWait().ifPresent(e -> { Platform.exit(); System.exit(0); });
	}
}
