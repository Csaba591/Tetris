package tetris;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Egy alakzat, másnéven tetromino, amit a játékos irányítani tud.
 */
public class Tetromino implements Serializable {
	private static final long serialVersionUID = 4329602614430061481L;
	private transient ArrayList<Rectangle> tetromino;
	private int[][] shapeArray;
	private String shape;
	private transient Color color;
	private int blockSideSize = 30;
	private ArrayList<int[][]> rotations;
	private int nextRotationIndex = 0;
	
	/**
	 * Létrehoz egy shape_letter alakú alakzatot a startingX x pozíción.
	 * Az y koordinátája 0 lesz a legfelsõ négyzetnek, mivel a tetejérõl esik.
	 * @param shape_letter Az alakzat alakja.
	 * @param startingX A kezdõ x koordináta.
	 */
	public Tetromino(String shape_letter, double startingX) {
		shape = shape_letter;
		
		// a shape_letter alapján inicializálja az alakzat alakját és színét
		try {
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
				default: throw new IllegalArgumentException();
			}
		}
		catch (IllegalArgumentException e) {
			new ErrorAlert(e.toString(), "Hiba");
		}
		initRotations();
		tetromino = new ArrayList<Rectangle>();
		
		// a shapeArray alapján létrehozza az alakzatot alkotó négyzeteket
		Rectangle rect;
		while(tetromino.size() < 4) {
			for(int y = 0; y < shapeArray.length; y++) {
				for(int x = 0; x < shapeArray[y].length; x++) {
					if(shapeArray[y][x] == 1) {
						rect = new Rectangle();
						rect.setLayoutX(x*blockSideSize + startingX);
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
	
	/**
	 * @return Az alakzatot alkotó négyzetek tömbje.
	 */
	public ArrayList<Rectangle> getRectangles(){ return tetromino; }
	/**
	 * @return Egy négyzet oldalhossza.
	 */
	public double getSideSize() { return (double) blockSideSize; }
	/**
	 * @return Az alakzat alakja karakteres formában.
	 */
	public String getShape() { return shape; }
	
	/**
	 * Kitörli az alakzat négyzeteibõl, azokat amelyek egy adott sorban találhatóak a képernyõn.
	 * @param row A törlendõ sor száma.
	 */
	public void removeRow(int row) {
		double height = row * blockSideSize;
		ArrayList<Rectangle> toRemove = new ArrayList<Rectangle>();
		for(Rectangle r : tetromino)
			if(r.getLayoutY() == height)
				toRemove.add(r);
		tetromino.removeAll(toRemove);
	}
	
	/**
	 * Egy négyzetoldalnyival lejjebb viszi az alakzatot.
	 */
	public void moveDown() {
		for (Rectangle r : tetromino)
			r.setLayoutY(r.getLayoutY() + blockSideSize);
	}
	
	/**
	 * A megadott forgatási minta szerint elforgatja az alakzatot, annak négyzeteinek áthelyezésével.
	 */
	public void rotate() {
		int[] shift;
		for(int i = 0; i < tetromino.size(); i++) {
			shift = rotations.get(nextRotationIndex)[i];
			if(shift[0] != 0)
				tetromino.get(i).setLayoutX(tetromino.get(i).getLayoutX() + (double) shift[0] * blockSideSize);
			if(shift[1] != 0)
				tetromino.get(i).setLayoutY(tetromino.get(i).getLayoutY() + (double) shift[1] * blockSideSize);
		}
		// ha már teljesen átfordult, elõröl kezdõdik a forgatás
		if(nextRotationIndex < rotations.size() - 1)
			nextRotationIndex++;
		else
			nextRotationIndex = 0;
	}
	
	/**
	 * Kiszámolja, hogy mik lesznek a következõ forgatás utáni koordniáták, de nem forgatja el.
	 * @return A forgatás utáni koordináták.
	 */
	double[] getNextRotationXCoordinates() {
		int[] shift;
		double[] rotated = new double[tetromino.size()];
		for(int i = 0; i < tetromino.size(); i++) {
			shift = rotations.get(nextRotationIndex)[i];
			if(shift[0] != 0)
				rotated[i] = tetromino.get(i).getLayoutX() + (double) shift[0] * blockSideSize;
		}
		return rotated;
	}
	
	/**
	 * Inicializálja az alakzathoz tartozó forgatási mintát, ami megadja, hogy a következõ
	 * forgatáshoz hogyan kell áthelyezni az alakzat négyzeteit.
	 */
	private void initRotations() {
		int[][][][] allRotationCoordinates = {
				{ //L
					{{-1, 2}, {0, 1}, {1, 0}, {0, -1}},
					{{1, 0}, {0, -1}, {-1, -2}, {-2, -1}},
					{{1, -1}, {0, 0}, {-1, 1}, {0, 2}},
					{{-1, -1}, {0, 0}, {1, 1}, {2, 0}}
				},
				{ //I
					{{-2, 2}, {-1, 1}, {0, 0}, {1, -1}},
					{{2, -2}, {1, -1}, {0, 0}, {-1, 1}}
				},
				{ //O
					{{0, 0}, {0, 0}, {0, 0}, {0, 0}}
				},
				{ //T
					{{-1, 1}, {0, 0}, {-1, -1}, {1, -1}},
					{{1, -1}, {0, 0}, {-1, 1}, {-1, 1}},
					{{-1, 1}, {0, 0}, {1, 1}, {1, -1}},
					{{1, -1}, {0, 0}, {1, -1}, {-1, 1}}
				},
				{ //S
					{{-1, 2}, {0, 1}, {-1, 0}, {0, -1}},
					{{1, -2}, {0, -1}, {1, 0}, {0, 1}}
				},
				{ //Z
					{{-2, 1}, {0, 1}, {-1, 0}, {1, 0}},
					{{2, -1}, {0, -1}, {1, 0}, {-1, 0}}
				},
				{ //J
					{{-1, 1}, {0, 0}, {2, 0}, {1, -1}},
					{{1, 1}, {0, 0}, {0, -2}, {-1, -1}},
					{{1, -1}, {0, 0}, {-2, 0}, {-1, 1}},
					{{-1, -1}, {0, 0}, {0, 2}, {1, 1}}
				},
			};
		ArrayList<String> shapes = new ArrayList<String>(Arrays.asList("L", "I", "O", "T", "S", "Z", "J"));
		rotations = new ArrayList<int[][]>();
		rotations.addAll(Arrays.asList(allRotationCoordinates[shapes.indexOf(shape)]));
	}
	
	/**
	 * A Serializable kiíró függvényének implementációja.
	 * ObjectOutputStream példány.writeObject(Tetromino példány) módon lehet használni.
	 * @param out A kimeneti stream, amire az objektumot írja.
	 */
	private void writeObject(ObjectOutputStream out) {
		try {
			out.defaultWriteObject();
			out.writeInt(tetromino.size());
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Error");
		}
		for(Rectangle r : tetromino)
			try {
				out.writeDouble(r.getLayoutX());
				out.writeDouble(r.getLayoutY());
				out.writeDouble(color.getRed());
				out.writeDouble(color.getGreen());
				out.writeDouble(color.getBlue());
				out.writeDouble(color.getOpacity());
			} catch (IOException e) {
				new ErrorAlert(e.toString(), "Error");
			}
	}
	
	/**
	 * Beolvas egy ObjectOutputStream-re kiírt Tetromino objektumot.
	 * @param in A bemeneti stream, amirõl olvas.
	 */
	private void readObject(ObjectInputStream in) {
		tetromino = new ArrayList<Rectangle>();
		Rectangle r;
		int numberOfRects;
		try {
			in.defaultReadObject();
			numberOfRects = in.readInt();
			for(int i = 0; i < numberOfRects; i++) {		
				r = new Rectangle(blockSideSize, blockSideSize);
				r.setLayoutX(in.readDouble());
				r.setLayoutY(in.readDouble());
				color = new Color(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
				r.setFill(color);
				r.setStroke(Color.BLACK);
				tetromino.add(r);
			}
		} catch (IOException | ClassNotFoundException e) {
			new ErrorAlert(e.toString(), "Játék vége");
		}
	}
}
