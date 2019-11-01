package tetris;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tetromino {
	private ArrayList<Rectangle> tetromino;
	private int[][] shapeArray;
	private String shape;
	private Color color;
	private int blockSideSize = 30;
	public Tetromino(String shape_letter) {
		shape = shape_letter;
		switch(shape_letter) {
		case "L":
			color = Color.ORANGE;
			shapeArray = new int[][]{{1, 0},
									  {1, 0},
									  {1, 1}};
			break;
		case "I":
			color = Color.TURQUOISE;
			shapeArray = new int[][]{{1, 0},
									  {1, 0},
									  {1, 0},
									  {1, 0}};
			break;
		case "O":
			color = Color.YELLOW;
			shapeArray = new int[][]{{1, 1},
									  {1, 1}};
			break;
		case "T":
			color = Color.HOTPINK;
			shapeArray = new int[][]{{1, 0},
									  {1, 1},
									  {1, 0}};
			break;
		case "S":
			color = Color.GREEN;
			shapeArray = new int[][]{{1, 0},
									  {1, 1},
									  {0, 1}};
			break;
		case "Z":
			color = Color.FIREBRICK;
			shapeArray = new int[][]{{0, 1},
									  {1, 1},
									  {1, 0}};
			break;
		case "J":
			color = Color.MEDIUMBLUE;
			shapeArray = new int[][]{{0, 1},
									  {0, 1},
									  {1, 1}};
			break;
		default: System.out.println("HIBAAA"); break;
		}
		tetromino = new ArrayList<Rectangle>();
		Rectangle rect;
		while(tetromino.size() < 4) {
			for(int y = 0; y < shapeArray.length; y++) {
				for(int x = 0; x < shapeArray[y].length; x++) {
					if(shapeArray[y][x] == 1) {
						rect = new Rectangle();
						rect.setLayoutX(x*blockSideSize);
						rect.setLayoutY(y*blockSideSize);
						rect.setWidth(blockSideSize);
						rect.setHeight(blockSideSize);
						rect.setFill(color);
						rect.setStroke(Color.BLACK);
						tetromino.add(rect);
					}
				}
			}
		}
	}
	public ArrayList<Rectangle> getRectangles(){ return tetromino; }
	
	public double getSideSize() { return (double) blockSideSize; }
	public String getShape() { return shape; }
	
	public void removeRow(int row) {
		double height = row * blockSideSize;
		Rectangle r = new Rectangle();
		for(Iterator<Rectangle> it = tetromino.iterator(); it.hasNext();)
			r = it.next();
			if(r.getLayoutY() == height)
				tetromino.remove(r);
	}
	
	public void moveDown() {
		for (Rectangle r : tetromino)
			r.setLayoutY(r.getLayoutY() + blockSideSize);
	}
}
