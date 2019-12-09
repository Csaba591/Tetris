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
 * Az ablakban megjelen�tend� n�zetek �s j�t�kmenet kezel�je.
 */
public class MainFrame extends FlowPane {
	private ScoreView scoreView;
	private GameView gameView;
	private MenuView menuView;
	private double width = 300.0;
	private double height = 600.0;

	/**
	 * L�trehozza a megjelen�t�skezel�t, �s megnyitja a men�t.
	 */
	public MainFrame() {
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		this.gameView = new GameView(width, height);
		this.menuView = new MenuView();
		menuView.playerNameProperty().addListener(c -> {
			// amikor a men� �jra megjelenik, vissza�ll�tja ""-ra a j�t�kos nev�t, ami szint�n notify-oln�
			if(!menuView.getPlayerName().isEmpty()) {
				initScoreView();
				initGameView();
				this.getChildren().remove(menuView);
				this.getChildren().addAll(gameView, scoreView);
				gameView.toggleGamePause();
			}
		});
		this.getChildren().add(menuView);
		// ha a gameView gameOver propertyj�t kell figyelni, hogy v�ge-e a j�t�knak
		gameView.gameOverProperty().addListener(c -> {
			new ErrorAlert("A j�t�knak v�ge!\n" + scoreView.getScore().getPoints() + " pontot szerezt�l.", "J�t�k v�ge");
			scoreView.appendScoreToHighScoreData();
			getChildren().removeAll(gameView, scoreView);
			getChildren().add(menuView);
			menuView.refreshHighscores(); 
		});
	}

	/**
	 * Alaphelyzetbe �ll�tja a j�t�kt�r elemeit. 
	 * Ha van a j�t�kos nev�hez tartoz� ment�s a f�jlok k�zt, megk�rdezi, hogy bet�ltse-e.
	 */
	private void initGameView() {
		gameView.init();
		if(Files.exists(Paths.get("gamesave_" + menuView.getPlayerName()))) {
			ConfirmationAlert load = new ConfirmationAlert("Bet�lt�s", "Be szeretn�d t�lteni az el�z� ment�st?");
			Button okBtn = (Button) load.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.setOnAction(e -> gameView.loadGame("gamesave_" + menuView.getPlayerName()));
			load.showAlert();
		}
	}

	/**
	 * Inicializ�lja a j�t�kt�r melletti r�szt, ami a k�vetkez� elemet, pontsz�mot �s ir�ny�t�st mutatja
	 */
	private void initScoreView() {
		scoreView = new ScoreView(menuView.getPlayerName(), gameView.getShapes().nextShapeProperty(), height);
		gameView.getShapes().nextShapeProperty().addListener(c -> scoreView.setNextShape(gameView.getShapes().getNextShape()));
		gameView.rowsRemovedProperty().addListener(c -> scoreView.getScore().setPoints(gameView.getRowsRemoved()));
	}

	/**
	 * Kezeli a felhaszn�l� �ltal adott billenty�zetes bemenetet a lenyomott billenty� k�dja alapj�n.
	 */
	public class InputHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent key) {
			if (key.getCode().equals(KeyCode.P) && !getChildren().contains(menuView))
				gameView.toggleGamePause();
			else if (key.getCode().equals(KeyCode.ESCAPE) && !getChildren().contains(menuView)) {
				gameView.toggleGamePause();
				ConfirmationAlert save = new ConfirmationAlert("Ment�s", "El szeretn�d menteni a j�t�kot?");
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
