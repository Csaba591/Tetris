package tetris;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import tetris.ScoreView;

/**
 * Az ablakban megjelenítendõ nézetek és játékmenet kezelõje.
 */
public class MainFrame extends FlowPane {
	private ScoreView scoreView;
	private GameView gameView;
	private MenuView menuView;
	private double width = 300.0;
	private double height = 600.0;

	/**
	 * Létrehozza a megjelenítéskezelõt, és megnyitja a menüt.
	 */
	public MainFrame() {
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		this.gameView = new GameView(width, height);
		this.menuView = new MenuView();
		menuView.playerNameProperty().addListener(c -> {
			// amikor a menü újra megjelenik, visszaállítja ""-ra a játékos nevét, ami szintén notify-olná
			if(!menuView.getPlayerName().isEmpty()) {
				initScoreView();
				initGameView();
				this.getChildren().remove(menuView);
				this.getChildren().addAll(gameView, scoreView);
				gameView.toggleGamePause();
			}
		});
		this.getChildren().add(menuView);
		// ha a gameView gameOver propertyjét kell figyelni, hogy vége-e a játéknak
		gameView.gameOverProperty().addListener(c -> {
			new ErrorAlert("A játéknak vége!\n" + scoreView.getScore().getPoints() + " pontot szereztél.", "Játék vége");
			scoreView.appendScoreToHighScoreData();
			getChildren().removeAll(gameView, scoreView);
			getChildren().add(menuView);
			menuView.refreshHighscores(); 
		});
	}

	/**
	 * Alaphelyzetbe állítja a játéktér elemeit. 
	 * Ha van a játékos nevéhez tartozó mentés a fájlok közt, megkérdezi, hogy betöltse-e.
	 */
	private void initGameView() {
		gameView.init();
		if(Files.exists(Paths.get("gamesave_" + menuView.getPlayerName()))) {
			ConfirmationAlert load = new ConfirmationAlert("Betöltés", "Be szeretnéd tölteni az elõzõ mentést?");
			Button okBtn = (Button) load.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.setOnAction(e -> gameView.loadGame("gamesave_" + menuView.getPlayerName()));
			load.showAlert();
		}
	}

	/**
	 * Inicializálja a játéktér melletti részt, ami a következõ elemet, pontszámot és irányítást mutatja
	 */
	private void initScoreView() {
		scoreView = new ScoreView(menuView.getPlayerName(), gameView.getShapes().nextShapeProperty(), height);
		gameView.getShapes().nextShapeProperty().addListener(c -> scoreView.setNextShape(gameView.getShapes().getNextShape()));
		gameView.rowsRemovedProperty().addListener(c -> scoreView.getScore().setPoints(gameView.getRowsRemoved()));
	}

	/**
	 * Kezeli a felhasználó által adott billentyûzetes bemenetet a lenyomott billentyû kódja alapján.
	 */
	public class InputHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent key) {
			if (key.getCode().equals(KeyCode.P) && !getChildren().contains(menuView))
				gameView.toggleGamePause();
			else if (key.getCode().equals(KeyCode.ESCAPE) && !getChildren().contains(menuView)) {
				gameView.toggleGamePause();
				ConfirmationAlert save = new ConfirmationAlert("Mentés", "El szeretnéd menteni a játékot?");
				Button okBtn = (Button) save.getDialogPane().lookupButton(ButtonType.OK);
				okBtn.setOnAction(e -> gameView.saveGame("gamesave_" + menuView.getPlayerName()));
				save.showAlert();
				scoreView.appendScoreToHighScoreData();
				getChildren().removeAll(gameView, scoreView);
				getChildren().add(menuView);
				menuView.refreshHighscores();
			}
			else if (!gameView.isPaused())
				gameView.getShapes().move(key.getCode());
		}
	}	
}
