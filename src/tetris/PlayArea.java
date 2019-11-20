package tetris;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.Tetromino;

public class PlayArea extends Pane {
	private ArrayList<Tetromino> tetrominos;
	private String[] shapes = {"I", "L", "S", "O", "T", "J", "Z"};
	private Random rand = new Random(System.currentTimeMillis());
	private Tetromino currentT;
	private String lastShape;
	private double width = 300.0;
	private double height = 600.0;
	private Timer moveDownTimer;
	public PlayArea() {
		setPrefSize(width, height);
		setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		tetrominos = new ArrayList<Tetromino>();
		String firstShape = shapes[rand.nextInt(shapes.length)];
		lastShape = firstShape;
		tetrominos.add(new Tetromino(firstShape));
		currentT = tetrominos.get(0);
		getChildren().addAll(currentT.getRectangles());
		moveDownTimer = new Timer();
		moveDownTimer.scheduleAtFixedRate(new TimerTask() {
        	@Override
            public void run() {
        		Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(!hitBottom())
		        			currentT.moveDown();
		        		else {
		        			removeFullRows();
		        			lastShape = currentT.getShape();
		        			String newShape = shapes[rand.nextInt(shapes.length)];
		        			while(newShape.equals(lastShape))
		        				newShape = shapes[rand.nextInt(shapes.length)];
		        			tetrominos.add(new Tetromino(newShape));
		        			currentT = tetrominos.get(tetrominos.size() - 1);
		        			getChildren().addAll(currentT.getRectangles());
		        		}
					}
				});
            }
	    }, 0, 1000);
	}
	
	private boolean hitBottom() {
		double blockSideSize = currentT.getSideSize();
		for(Rectangle r : currentT.getRectangles())
			if(r.getLayoutY() + blockSideSize >= height) {
				System.out.println(r.getLayoutY() + blockSideSize + " " + height);
				return true;
			}
		for(int i = 0; i < tetrominos.size(); i++)
			for(Rectangle r : tetrominos.get(i).getRectangles())
				for(Rectangle curr : currentT.getRectangles())
					if(tetrominos.get(i).equals(currentT)) break;
					else if(curr.getLayoutY() + blockSideSize == r.getLayoutY() && curr.getLayoutX() == r.getLayoutX()) {
						System.out.println("masik");
						return true;
					}
		return false;
	}
	
	private boolean rowIsFull(double row) {
		double blockSideSize = currentT.getSideSize();
		int blockCount = 0;
		for(Tetromino t : tetrominos)
			for(Rectangle r : t.getRectangles())
				if(r.getLayoutY() == row * blockSideSize)
					blockCount++;
		return  blockCount == (int) (width / blockSideSize);
	}
	
	private void removeFullRows() {
		double blockSideSize = currentT.getSideSize();
		double rowHeight;
		for(int row = 0; row < (int) height / blockSideSize; row++) {
			if(rowIsFull(row)) {
				rowHeight = row * blockSideSize;
				for(Tetromino t : tetrominos) {
					for(Rectangle r : t.getRectangles()) {
						if(r.getLayoutY() == rowHeight)
							this.getChildren().remove(r);
					}
					t.removeRow(row);
				}
				for(Tetromino t : tetrominos) {
					for(Rectangle r : t.getRectangles()) {
						if(r.getLayoutY() < rowHeight)
							r.setLayoutY(r.getLayoutY() + blockSideSize);
					}
				}
			}
		}
	}
	
	public class MovementHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent key) {
			move(key.getCode());
		}
		private void move(KeyCode direction) {
			double newX;
			double blockSideSize = currentT.getSideSize();
            if(direction.equals(KeyCode.LEFT) && canMove(direction))
            	for(Rectangle r : currentT.getRectangles()) {
                	newX = r.getLayoutX() - blockSideSize;
                    if(newX >= 0)
                    	r.setLayoutX(newX);
                }
            else if(direction.equals(KeyCode.RIGHT) && canMove(direction))
            	for(Rectangle r : currentT.getRectangles()) {
                	newX =  r.getLayoutX() + blockSideSize;
                	if(newX + blockSideSize <= width)
                		r.setLayoutX(newX);
            	}
            else if(direction.equals(KeyCode.DOWN) && canMove(direction))
            	currentT.moveDown();
            else System.out.println("Hiba");
		}
		private boolean canMove(KeyCode direction) {
			double blockSideSize = currentT.getSideSize();
			if(direction.equals(KeyCode.LEFT) || direction.equals(KeyCode.RIGHT)) {
				for(Rectangle r : currentT.getRectangles()) {
		        	if (direction.equals(KeyCode.LEFT) && r.getLayoutX() - blockSideSize < 0)
		        		return false;
		        	else if (direction.equals(KeyCode.RIGHT) && r.getLayoutX() + blockSideSize >= width)
		        		return false;
	        	}
				for(int i = 0; i < tetrominos.size(); i++) {
					if(tetrominos.get(i).equals(currentT)) break;
					for(Rectangle r : tetrominos.get(i).getRectangles())
						for(Rectangle curr : currentT.getRectangles())
							if (direction.equals(KeyCode.LEFT) && curr.getLayoutX() - blockSideSize == r.getLayoutX() && curr.getLayoutY() == r.getLayoutY())
								return false;
							else if (direction.equals(KeyCode.RIGHT) && curr.getLayoutX() + blockSideSize == r.getLayoutX() && curr.getLayoutY() == r.getLayoutY())
								return false;
				}
			}
			else if (direction.equals(KeyCode.DOWN) && hitBottom())
	    		return false;
        	return true;
		}
	}
	
}
