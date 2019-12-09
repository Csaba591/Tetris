package tetris;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.Tetromino;
import tetris.ScoreView;

/**
 * Az ablakban megjelen�tend� n�zetek �s j�t�kmenet kezel�je.
 */
public class MainFrame extends FlowPane {
	private ScoreView scoreView;
	private Pane gameView;
	private MenuView menuView;
	private Components shapes;
	private double width = 300.0;
	private double height = 600.0;
	private Timer moveDownTimer;
	private boolean paused = true;

	/**
	 * L�trehozza a megjelen�t�skezel�t, �s megnyitja a men�t.
	 */
	public MainFrame() {
		this.setOrientation(Orientation.VERTICAL);
		this.setAlignment(Pos.CENTER);
		shapes = new Components(width, height);
		shapes.init();
		
		this.menuView = new MenuView();
		menuView.playerNameProperty().addListener(c -> {
			// amikor a men� �jra megjelenik, vissza�ll�tja ""-ra a j�t�kos nev�t, ami szint�n notify-oln�
			if(!menuView.getPlayerName().isEmpty()) {
				System.out.println("�j j�t�kos " + menuView.getPlayerName());
				initScoreView();
				initGameView();
				this.getChildren().remove(menuView);
				this.getChildren().addAll(gameView, scoreView);
				paused = false;
			}
		});
		this.getChildren().add(menuView);
		moveDownTimer = new Timer();
		moveDownTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!paused) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (!shapes.hitBottom() && !shapes.stackedUp())
								shapes.getCurrentT().moveDown();
							else {
								removeFullRows();
								shapes.changeToNextShape();
								gameView.getChildren().addAll(shapes.getCurrentT().getRectangles());
								// ha feltornyosulnak az alakzatok, a j�t�knak v�ge �s kil�p a men�be
								if (shapes.stackedUp()) {
									paused = true;
									new ErrorAlert("A j�t�knak v�ge!\n" + scoreView.getScore().getPoints() + 
											" pontot szerezt�l.", "J�t�k v�ge");
									scoreView.appendScoreToHighScoreData();
									getChildren().removeAll(gameView, scoreView);
									getChildren().add(menuView);
									menuView.refreshHighscores();
								}
							}
						}
					});
				}
			}
		}, 0, 750);
	}

	/**
	 * Alaphelyzetbe �ll�tja a j�t�kt�r elemeit. 
	 * Ha van a j�t�kos nev�hez tartoz� ment�s a f�jlok k�zt, megk�rdezi, hogy bet�ltse-e.
	 */
	private void initGameView() {
		gameView = new Pane();
		gameView.setPrefSize(width, height);
		gameView.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		gameView.getChildren().clear();
		shapes.init();
		gameView.getChildren().addAll(shapes.getCurrentT().getRectangles());
		if(Files.exists(Paths.get("gamesave_" + menuView.getPlayerName()))) {
			ConfirmationAlert load = new ConfirmationAlert("Bet�lt�s", "Be szeretn�d t�lteni az el�z� ment�st?");
			Button okBtn = (Button) load.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.setOnAction(e -> loadGame());
			load.showAlert();
		}
	}

	/**
	 * Inicializ�lja a j�t�kt�r melletti r�szt, ami a k�vetkez� elemet, pontsz�mot �s ir�ny�t�st mutatja
	 */
	private void initScoreView() {
		scoreView = new ScoreView(menuView.getPlayerName(), shapes.nextShapeProperty(), height);
		shapes.nextShapeProperty().addListener(c -> scoreView.setNextShape(shapes.getNextShape()));
	}

	/**
	 * V�gig megy minden soron �s megn�zi, hogy teli-e. 
	 * Ha igen, kit�rli az abban a sorban l�v� alakzatok �rintett n�gyzeteit.
	 */
	private void removeFullRows() {
		double blockSideSize = shapes.getCurrentT().getSideSize();
		double rowHeight;
		for (int row = 0; row < (int) height / blockSideSize; row++) {
			if (shapes.rowIsFull(row)) {
				scoreView.incrementScore();
				rowHeight = row * blockSideSize;
				for (Tetromino t : shapes.getTetrominos()) {
					for (Rectangle r : t.getRectangles()) {
						if (r.getLayoutY() == rowHeight)
							gameView.getChildren().remove(r);
					}
					t.removeRow(row);
				}
				for (Tetromino t : shapes.getTetrominos()) {
					for (Rectangle r : t.getRectangles()) {
						if (r.getLayoutY() < rowHeight)
							r.setLayoutY(r.getLayoutY() + blockSideSize);
					}
				}
			}
		}
	}

	/**
	 * Kezeli a felhaszn�l� �ltal adott billenty�zetes bemenetet a lenyomott billenty� k�dja alapj�n.
	 */
	public class InputHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent key) {
			if (key.getCode().equals(KeyCode.P) && !getChildren().contains(menuView))
				paused = !paused;
			else if (key.getCode().equals(KeyCode.ESCAPE) && !getChildren().contains(menuView)) {
				paused = true;
				ConfirmationAlert save = new ConfirmationAlert("Ment�s", "El szeretn�d menteni a j�t�kot?");
				Button okBtn = (Button) save.getDialogPane().lookupButton(ButtonType.OK);
				okBtn.setOnAction(e -> saveGame());
				save.showAlert();
				scoreView.appendScoreToHighScoreData();
				getChildren().removeAll(gameView, scoreView);
				getChildren().add(menuView);
				menuView.refreshHighscores();
			}
			else if (!paused)
				shapes.move(key.getCode());
		}
	}
	
	/**
	 * Elmenti a j�t�k �llapot�t �s a szerzett pontokat, egy a j�t�kos nev�vel ell�tott f�jlba.
	 * Ennek seg�ts�g�vel, ha valaki elmenteti a j�t�kot, legk�zelebb ugyanonnan folytathatja.
	 */
	private void saveGame() {
		try {
			FileOutputStream f = new FileOutputStream("gamesave_" + menuView.getPlayerName());
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeInt(scoreView.getScore().getPoints());
			shapes.saveGameState(o);
			o.close();
			f.close();
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "J�t�k v�ge");
		}
	}

	/**
	 * Bet�lti a j�t�kot abb�l a f�jlb�l, aminek a nev�ben szerepel a j�t�kos neve.
	 */
	private void loadGame() {
		FileInputStream f;
		ObjectInputStream o;
		try {
			f = new FileInputStream("gamesave_" + menuView.getPlayerName());
			o = new ObjectInputStream(f);
			gameView.getChildren().clear();
			shapes.getTetrominos().clear();
			scoreView.getScore().setPoints(o.readInt());
			Tetromino next;
			boolean read = true;
			while (read) {
				try {
					next = (Tetromino) o.readObject();
					if(next.getRectangles().size() > 0)
						shapes.getTetrominos().add(next);
				} catch (EOFException e) {
					read = false;
				} catch (ClassNotFoundException | IOException e) {
					o.close();
					f.close();
					e.printStackTrace();
				}
			}
			shapes.getTetrominos().forEach(t -> gameView.getChildren().addAll(t.getRectangles()));
			shapes.setCurrentT(shapes.getTetrominos().get(shapes.getTetrominos().size() - 1));
			o.close();
			f.close();
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Error");
		}
	}
}
