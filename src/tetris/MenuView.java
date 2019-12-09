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
 * Menü a "Játék" gombbal és a ponttáblázattal.
 * A gombra kattintva a játékos megadhatja a nevét és játszhat.
 * A táblázatban az eddig játszott játékosok által elért eredményeket lehet megtekinteni.
 */
public class MenuView extends GridPane {
	private Button playBtn;
	private SimpleStringProperty playerName;
	private HighscoreView hsArea;
	
	/**
	 * Létrehoz egy menü nézetet.
	 */
	public MenuView() {
		this.setPrefSize(500, 600);
		playerName = new SimpleStringProperty();
		playBtn = new Button("Játék");
		playBtn.setFont(new Font(40));
		playBtn.setPrefSize(this.getPrefWidth()/2, this.getPrefHeight()/6);
		// a Játék gomb megnyomásával felugrik egy párbeszédablak, ahol a játékos megadhatja a nevét
		playBtn.setOnAction(e -> {
			playerName.set("");
			NameInputDialog nameinp = new NameInputDialog();
			Optional<String> result = nameinp.showAndWait();
			result.ifPresent(name -> playerName.set(name));
		});
		hsArea = new HighscoreView("scores.txt");
		
		// a cellák eloszlásának beállítása Constraintekkel
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
	 * A játékos neve. Observable String objektum.
	 * @return Observable String, ami a játékos neve.
	 */
	public SimpleStringProperty playerNameProperty() { return this.playerName; }
	
	/**
	 * A játékos neve mint sima String.
	 * @return A játékos neve.
	 */
	public String getPlayerName() { return this.playerName.get(); }
	
	/**
	 * Belsõ, csak itt használatos párbeszédablak implementáció.
	 * Egy szövegdobozban kéri a nevet. Amíg ez nincs megadva, az oké gombot nem lehet megnyomni.
	 * Ha a mégsére vagy X-re kattint, marad a menüben.
	 */
	private class NameInputDialog extends TextInputDialog {
		public NameInputDialog() {
			this.setContentText("Név: ");
			this.setHeaderText(null);
			this.setTitle("Add meg a neved!");
			Button okBtn = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.disableProperty().bind(Bindings.isEmpty(this.getEditor().textProperty()));
		}
	}
	
	/**
	 * Az alul található ponttáblázatot tölti újra.
	 */
	public void refreshHighscores() {
		hsArea.refresh();
	}
}
