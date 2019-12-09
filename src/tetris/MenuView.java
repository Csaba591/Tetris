package tetris;

import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

/**
 * Men� a "J�t�k" gombbal �s a pontt�bl�zattal.
 * A gombra kattintva a j�t�kos megadhatja a nev�t �s j�tszhat.
 * A t�bl�zatban az eddig j�tszott j�t�kosok �ltal el�rt eredm�nyeket lehet megtekinteni.
 */
public class MenuView extends GridPane {
	private Button playBtn;
	private SimpleStringProperty playerName;
	private HighscoreView hsArea;
	
	/**
	 * L�trehoz egy men� n�zetet.
	 */
	public MenuView() {
		this.setPrefSize(500, 600);
		playerName = new SimpleStringProperty();
		playBtn = new Button("J�t�k");
		playBtn.setFont(new Font(40));
		playBtn.setPrefSize(this.getPrefWidth()/2, this.getPrefHeight()/6);
		// a J�t�k gomb megnyom�s�val felugrik egy p�rbesz�dablak, ahol a j�t�kos megadhatja a nev�t
		playBtn.setOnAction(e -> {
			playerName.set("");
			NameInputDialog nameinp = new NameInputDialog();
			Optional<String> result = nameinp.showAndWait();
			result.ifPresent(name -> playerName.set(name));
		});
		hsArea = new HighscoreView("scores.txt");
		
		// a cell�k eloszl�s�nak be�ll�t�sa Constraintekkel
		ColumnConstraints col1const = new ColumnConstraints();
		col1const.setPercentWidth(100);
		this.getColumnConstraints().add(col1const);
		RowConstraints row1const = new RowConstraints();
		row1const.setPercentHeight(30);
		RowConstraints row2const = new RowConstraints();
		row2const.setPercentHeight(70);
		this.getRowConstraints().addAll(row1const, row2const);
		
		this.addRow(0, playBtn);
		this.addRow(1, hsArea);
		GridPane.setHalignment(playBtn, HPos.CENTER);
	}
	
	/**
	 * A j�t�kos neve. Observable String objektum.
	 * @return Observable String, ami a j�t�kos neve.
	 */
	public SimpleStringProperty playerNameProperty() { return this.playerName; }
	
	/**
	 * A j�t�kos neve mint sima String.
	 * @return A j�t�kos neve.
	 */
	public String getPlayerName() { return this.playerName.get(); }
	
	/**
	 * Bels�, csak itt haszn�latos p�rbesz�dablak implement�ci�.
	 * Egy sz�vegdobozban k�ri a nevet. Am�g ez nincs megadva, az ok� gombot nem lehet megnyomni.
	 * Ha a m�gs�re vagy X-re kattint, marad a men�ben.
	 */
	private class NameInputDialog extends TextInputDialog {
		public NameInputDialog() {
			this.setContentText("N�v: ");
			this.setHeaderText(null);
			this.setTitle("Add meg a neved!");
			Button okBtn = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.disableProperty().bind(Bindings.isEmpty(this.getEditor().textProperty()));
		}
	}
	
	/**
	 * Az alul tal�lhat� pontt�bl�zatot t�lti �jra.
	 */
	public void refreshHighscores() {
		hsArea.refresh();
	}
}
