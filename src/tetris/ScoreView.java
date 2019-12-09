package tetris;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Az oldalsó rész, ami mutatja a következõ alakzatot, pontszámot és irányítást. 
 */
public class ScoreView extends GridPane {
	private Label scoreLabel;
	private Score score;
	private String[] shapes = {"I", "L", "S", "O", "T", "J", "Z"};
	private SimpleStringProperty nextShape;
	private HashMap<String, Tetromino> tetrominos;
	private Group nextTGroup;
	
	/**
	 * Létrehoz egy ScoreView pédányt, ami a következõ alakzat, 
	 * pontszám és irányítás mutatására szolgál a játéktér mellett.
	 * @param pName A játékos neve, amit a menüben megadott.
	 * @param nextShape A következõ alakat betûként.
	 * @param height Az ablak magassága, hogy ugyanakkora legyen mint a játéktéré.
	 */
	public ScoreView(String pName, SimpleStringProperty nextShape, double height) {
		this.setPrefSize(200, height);
		this.setAlignment(Pos.TOP_CENTER);
		this.setPadding(new Insets(getPrefHeight()/20.0, 0, 0, 0));
		this.setHgap(this.getPrefWidth()/10.0);
		this.setVgap(this.getPrefHeight()/10.0);
		this.setStyle("-fx-font-size: 15 Helvetica Neue;");
		this.nextShape = nextShape;
		tetrominos = new HashMap<String, Tetromino>();
		for(String s : shapes)
			tetrominos.put(s, new Tetromino(s, 0.0));
		score = new Score(pName);
		scoreLabel = new Label();
		scoreLabel.textProperty().bind(score.pointsProperty().asString());
		
		/* A következõ alakzatot egy Group-ba teszi, hogy az a formájának megfelelõen jelenjen meg
		 * és ne egymás alá helyezze a GridPane viselkedéséhez hûen.
		 */
		nextTGroup = new Group();
		nextTGroup.getChildren().addAll(tetrominos.get(this.nextShape.get()).getRectangles());
		this.add(new Label("Következõ:"), 0, 0);
		this.add(new Label("Ponszám:"), 0, 1);
		this.add(scoreLabel, 1, 1);
		this.add(nextTGroup, 1, 0);
		VBox controls = new VBox();
		controls.getChildren().addAll(new Label("Irányítás:"), new Label("Jobbra: jobbra nyíl"),
				new Label("Balra: balra nyíl"), new Label("Le: lefelé nyíl"), new Label("Forgatás: felfelé nyíl"),
				new Label("Szünet: P"), new Label("Kilépés a menübe: ESC"), new Label("\nHa feltornyosulnak az\nalakzatok, a játék véget ér."));
		this.add(controls, 0, 2, 2, 1);
	}
	
	/**
	 * Átállítja a következõ alakzatot, ami a shape alapján kikeresi az annak megfelelõ alakzatot,
	 * hogy azt jelenítse meg a játékosnak.
	 * @param shape A következõnek sorsolt alakzat alakja betûként
	 */
	public void setNextShape(String shape) {
		nextShape.set(shape);
		nextTGroup.getChildren().removeAll(nextTGroup.getChildren());
		nextTGroup.getChildren().addAll(tetrominos.get(this.nextShape.get()).getRectangles());
	}
	
	/**
	 * Ha a játékos feltelít egy sort a pályán, a pontja egyel növekszik.
	 */
	public void incrementScore() {
		score.incrementPoints();
	}
	
	/**
	 * Hozzáírja a játékos által elért pontszámot egy txt filehoz, ami a pontokat tárolja.
	 */
	public void appendScoreToHighScoreData() {
		score.save();
	}
	
	/**
	 * @return A játékoshoz tartozó pontszám objektum.
	 */
	public Score getScore() {
		return score;
	}
}
