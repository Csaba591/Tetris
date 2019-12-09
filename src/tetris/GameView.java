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
 * A játéktér, ahol a játékos irányítja az alakzatokat.
 */
public class GameView extends Pane {
	private Components shapes;
	private SimpleIntegerProperty rowsRemoved;
	private SimpleBooleanProperty gameOver;
	private boolean paused = true;
	private Timer moveDownTimer;
	
	/**
	 * Létrehoz egy játéktér példányt egy Timerrel, ami a folytonos lefelé mozgást valósítja meg.
	 * @param width A játéktér szélessége.
	 * @param height Az ablak magassága.
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
								// ha feltornyosulnak az alakzatok, a játéknak vége
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
	 * Kezdõ helyzetbe állítja a játékteret a kezdõ alakzattal a pályán.
	 */
	void init() {
		this.gameOver.set(false);
		this.getChildren().clear();
		shapes.init();
		this.getChildren().addAll(shapes.getCurrentT().getRectangles());
	}
	
	/**
	 * Ki be kapcsolja a szüneteltetést.
	 */
	void toggleGamePause() { paused = !paused; }
	/**
	 * Szüneteltetve van-e a játék.
	 * @return True, ha szünetel, false, ha nem.
	 */
	boolean isPaused() { return paused; }
	/**
	 * Megadja a játéktéren lévõ alakzatokat.
	 * @return A játéktéren lévõ alakzatok, mint Component.
	 */
	Components getShapes() { return shapes; }
	/**
	 * Az eddig eltüntetett sorok száma observable-ként.
	 * Ezzel az oldalsó pont nézet frissíteni tudja a jelenlegi pontszámot.
	 * @return Observable Integer-ként az eddig eltüntetett sorok száma.
	 */
	SimpleIntegerProperty rowsRemovedProperty() { return rowsRemoved; }
	/**
	 * A játék végét jelzõ boolean observable-ként.
	 * A játék végének jelzésére szolgál.
	 * @return Observable boolean-ként, hogy vége-e a játéknak.
	 */
	SimpleBooleanProperty gameOverProperty() { return gameOver; }
	/**
	 * Az eddig eltüntetett sorok száma primitív int-ként.
	 * @return Az eltüntetett sorok száma.
	 */
	int getRowsRemoved() { return rowsRemoved.get(); }
	
	/**
	 * Végig megy minden soron és megnézi, hogy teli-e. 
	 * Ha igen, kitörli az abban a sorban lévõ alakzatok érintett négyzeteit.
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
	 * Betölti a játékot abból a fájlból, aminek a nevében szerepel a játékos neve.
	 */
	void loadGame(String fileName) {
		FileInputStream f;
		ObjectInputStream o;
		try {
			f = new FileInputStream(fileName);
			o = new ObjectInputStream(f);
			shapes.getTetrominos().clear();
			this.getChildren().clear();
			/* hogy az oldalsó nézetben is frissítse a betöltött pontszámra, egy Change Eventnek kell történnie,
			 * viszont, ha ugyanaz a pontszám, mint volt az elõzõ körben nem fog ez megtörténni, ezért kell -1-re állítani
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
	 * Elmenti a játék állapotát és a szerzett pontokat, egy a játékos nevével ellátott fájlba.
	 * Ennek segítségével, ha valaki elmenteti a játékot, legközelebb ugyanonnan folytathatja.
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
