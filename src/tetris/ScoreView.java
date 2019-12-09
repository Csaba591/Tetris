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
 * Az oldals� r�sz, ami mutatja a k�vetkez� alakzatot, pontsz�mot �s ir�ny�t�st. 
 */
public class ScoreView extends GridPane {
	private Label scoreLabel;
	private Score score;
	private String[] shapes = {"I", "L", "S", "O", "T", "J", "Z"};
	private SimpleStringProperty nextShape;
	private HashMap<String, Tetromino> tetrominos;
	private Group nextTGroup;
	
	/**
	 * L�trehoz egy ScoreView p�d�nyt, ami a k�vetkez� alakzat, 
	 * pontsz�m �s ir�ny�t�s mutat�s�ra szolg�l a j�t�kt�r mellett.
	 * @param pName A j�t�kos neve, amit a men�ben megadott.
	 * @param nextShape A k�vetkez� alakat bet�k�nt.
	 * @param height Az ablak magass�ga, hogy ugyanakkora legyen mint a j�t�kt�r�.
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
		
		/* A k�vetkez� alakzatot egy Group-ba teszi, hogy az a form�j�nak megfelel�en jelenjen meg
		 * �s ne egym�s al� helyezze a GridPane viselked�s�hez h�en.
		 */
		nextTGroup = new Group();
		nextTGroup.getChildren().addAll(tetrominos.get(this.nextShape.get()).getRectangles());
		this.add(new Label("K�vetkez�:"), 0, 0);
		this.add(new Label("Ponsz�m:"), 0, 1);
		this.add(scoreLabel, 1, 1);
		this.add(nextTGroup, 1, 0);
		VBox controls = new VBox();
		controls.getChildren().addAll(new Label("Ir�ny�t�s:"), new Label("Jobbra: jobbra ny�l"),
				new Label("Balra: balra ny�l"), new Label("Le: lefel� ny�l"), new Label("Forgat�s: felfel� ny�l"),
				new Label("Sz�net: P"), new Label("Kil�p�s a men�be: ESC"), new Label("\nHa feltornyosulnak az\nalakzatok, a j�t�k v�get �r."));
		this.add(controls, 0, 2, 2, 1);
	}
	
	/**
	 * �t�ll�tja a k�vetkez� alakzatot, ami a shape alapj�n kikeresi az annak megfelel� alakzatot,
	 * hogy azt jelen�tse meg a j�t�kosnak.
	 * @param shape A k�vetkez�nek sorsolt alakzat alakja bet�k�nt
	 */
	public void setNextShape(String shape) {
		nextShape.set(shape);
		nextTGroup.getChildren().removeAll(nextTGroup.getChildren());
		nextTGroup.getChildren().addAll(tetrominos.get(this.nextShape.get()).getRectangles());
	}
	
	/**
	 * Ha a j�t�kos feltel�t egy sort a p�ly�n, a pontja egyel n�vekszik.
	 */
	public void incrementScore() {
		score.incrementPoints();
	}
	
	/**
	 * Hozz��rja a j�t�kos �ltal el�rt pontsz�mot egy txt filehoz, ami a pontokat t�rolja.
	 */
	public void appendScoreToHighScoreData() {
		score.save();
	}
	
	/**
	 * @return A j�t�koshoz tartoz� pontsz�m objektum.
	 */
	public Score getScore() {
		return score;
	}
}
