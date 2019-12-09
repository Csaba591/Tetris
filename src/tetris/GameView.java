package tetris;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A j�t�kt�r, ahol a j�t�kos ir�ny�tja az alakzatokat.
 */
public class GameView extends Pane {
	private Components shapes;
	private SimpleIntegerProperty rowsRemoved;
	private SimpleBooleanProperty gameOver;
	private boolean paused = true;
	private Timer moveDownTimer;
	
	/**
	 * L�trehoz egy j�t�kt�r p�ld�nyt egy Timerrel, ami a folytonos lefel� mozg�st val�s�tja meg.
	 * @param width A j�t�kt�r sz�less�ge.
	 * @param height Az ablak magass�ga.
	 */
	GameView(double width, double height){
		this.setPrefSize(width, height);
		this.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		shapes = new Components(width, height);
		shapes.init();
		rowsRemoved = new SimpleIntegerProperty(0);
		gameOver = new SimpleBooleanProperty(false);
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
								getChildren().addAll(shapes.getCurrentT().getRectangles());
								// ha feltornyosulnak az alakzatok, a j�t�knak v�ge
								if (shapes.stackedUp()) {
									paused = true;
									gameOver.set(true);
								}
							}
						}
					});
				}
			}
		}, 0, 750);
	}
	
	/**
	 * Kezd� helyzetbe �ll�tja a j�t�kteret a kezd� alakzattal a p�ly�n.
	 */
	void init() {
		this.gameOver.set(false);
		this.getChildren().clear();
		shapes.init();
		this.getChildren().addAll(shapes.getCurrentT().getRectangles());
	}
	
	/**
	 * Ki be kapcsolja a sz�neteltet�st.
	 */
	void toggleGamePause() { paused = !paused; }
	/**
	 * Sz�neteltetve van-e a j�t�k.
	 * @return True, ha sz�netel, false, ha nem.
	 */
	boolean isPaused() { return paused; }
	/**
	 * Megadja a j�t�kt�ren l�v� alakzatokat.
	 * @return A j�t�kt�ren l�v� alakzatok, mint Component.
	 */
	Components getShapes() { return shapes; }
	/**
	 * Az eddig elt�ntetett sorok sz�ma observable-k�nt.
	 * Ezzel az oldals� pont n�zet friss�teni tudja a jelenlegi pontsz�mot.
	 * @return Observable Integer-k�nt az eddig elt�ntetett sorok sz�ma.
	 */
	SimpleIntegerProperty rowsRemovedProperty() { return rowsRemoved; }
	/**
	 * A j�t�k v�g�t jelz� boolean observable-k�nt.
	 * A j�t�k v�g�nek jelz�s�re szolg�l.
	 * @return Observable boolean-k�nt, hogy v�ge-e a j�t�knak.
	 */
	SimpleBooleanProperty gameOverProperty() { return gameOver; }
	/**
	 * Az eddig elt�ntetett sorok sz�ma primit�v int-k�nt.
	 * @return Az elt�ntetett sorok sz�ma.
	 */
	int getRowsRemoved() { return rowsRemoved.get(); }
	
	/**
	 * V�gig megy minden soron �s megn�zi, hogy teli-e. 
	 * Ha igen, kit�rli az abban a sorban l�v� alakzatok �rintett n�gyzeteit.
	 */
	public void removeFullRows() {
		double blockSideSize = shapes.getCurrentT().getSideSize();
		double rowHeight;
		for (int row = 0; row < (int) this.getPrefHeight() / blockSideSize; row++) {
			if (shapes.rowIsFull(row)) {
				rowsRemoved.set(rowsRemoved.get()+1);
				rowHeight = row * blockSideSize;
				for (Tetromino t : shapes.getTetrominos()) {
					for (Rectangle r : t.getRectangles()) {
						if (r.getLayoutY() == rowHeight)
							this.getChildren().remove(r);
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
	 * Bet�lti a j�t�kot abb�l a f�jlb�l, aminek a nev�ben szerepel a j�t�kos neve.
	 */
	void loadGame(String fileName) {
		FileInputStream f;
		ObjectInputStream o;
		try {
			f = new FileInputStream(fileName);
			o = new ObjectInputStream(f);
			shapes.getTetrominos().clear();
			this.getChildren().clear();
			/* hogy az oldals� n�zetben is friss�tse a bet�lt�tt pontsz�mra, egy Change Eventnek kell t�rt�nnie,
			 * viszont, ha ugyanaz a pontsz�m, mint volt az el�z� k�rben nem fog ez megt�rt�nni, ez�rt kell -1-re �ll�tani
			 */
			rowsRemoved.set(-1);
			rowsRemoved.set(o.readInt());
			Tetromino next;
			boolean read = true;
			while (read) {
				try {
					next = (Tetromino) o.readObject();
					if(next.getRectangles().size() > 0)
						shapes.getTetrominos().add(next);
				} catch (EOFException e) {
					read = false;
				} catch (ClassNotFoundException e) {
					new ErrorAlert(e.toString(), "Hiba");
				}
			}
			o.close();
			f.close();
			shapes.getTetrominos().forEach(t -> this.getChildren().addAll(t.getRectangles()));
			shapes.setCurrentT(shapes.getTetrominos().get(shapes.getTetrominos().size() - 1));
			
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Hiba");
		}
	}
	
	/**
	 * Elmenti a j�t�k �llapot�t �s a szerzett pontokat, egy a j�t�kos nev�vel ell�tott f�jlba.
	 * Ennek seg�ts�g�vel, ha valaki elmenteti a j�t�kot, legk�zelebb ugyanonnan folytathatja.
	 */
	void saveGame(String fileName) {
		try {
			FileOutputStream f = new FileOutputStream(fileName);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeInt(rowsRemoved.get());
			shapes.saveGameState(o);
			o.close();
			f.close();
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Hiba");
		}
	}
}
