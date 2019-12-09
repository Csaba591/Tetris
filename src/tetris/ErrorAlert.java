package tetris;

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
		this.showAndWait().ifPresent(e -> { ; });
	}
}
