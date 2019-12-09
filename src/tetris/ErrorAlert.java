package tetris;

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
		this.showAndWait().ifPresent(e -> { ; });
	}
}
