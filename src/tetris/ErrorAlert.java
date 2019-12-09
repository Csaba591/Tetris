package tetris;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Hiba eset�n figyelmezteti a felhaszn�l�t �s ki�rja a hiba t�pus�t.
 */
public class ErrorAlert extends Alert {
	
	/**
	 * L�trehoz �s megjelen�t egy hiba �rtes�t�st.
	 * @param message A hiba�zenet.
	 */
	ErrorAlert(String message, String title) {
		super(AlertType.ERROR, message);
		this.setTitle(title);
		this.setHeaderText(null);
		// hiba eset�n kil�p a programb�l
		this.showAndWait().ifPresent(e -> { Platform.exit(); System.exit(0); });
	}
}
