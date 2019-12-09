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
 * Egy alakzat, m�sn�ven tetromino, amit a j�t�kos ir�ny�tani tud.
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
	 * L�trehoz egy shape_letter alak� alakzatot a startingX x poz�ci�n.
	 * Az y koordin�t�ja 0 lesz a legfels� n�gyzetnek, mivel a tetej�r�l esik.
	 * @param shape_letter Az alakzat alakja.
	 * @param startingX A kezd� x koordin�ta.
	 */
	public Tetromino(String shape_letter, double startingX) {
		shape = shape_letter;
		
		// a shape_letter alapj�n inicializ�lja az alakzat alakj�t �s sz�n�t
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
		
		// a shapeArray alapj�n l�trehozza az alakzatot alkot� n�gyzeteket
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
	 * @return Az alakzatot alkot� n�gyzetek t�mbje.
	 */
	public ArrayList<Rectangle> getRectangles(){ return tetromino; }
	/**
	 * @return Egy n�gyzet oldalhossza.
	 */
	public double getSideSize() { return (double) blockSideSize; }
	/**
	 * @return Az alakzat alakja karakteres form�ban.
	 */
	public String getShape() { return shape; }
	
	/**
	 * Kit�rli az alakzat n�gyzeteib�l, azokat amelyek egy adott sorban tal�lhat�ak a k�perny�n.
	 * @param row A t�rlend� sor sz�ma.
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
	 * Egy n�gyzetoldalnyival lejjebb viszi az alakzatot.
	 */
	public void moveDown() {
		for (Rectangle r : tetromino)
			r.setLayoutY(r.getLayoutY() + blockSideSize);
	}
	
	/**
	 * A megadott forgat�si minta szerint elforgatja az alakzatot, annak n�gyzeteinek �thelyez�s�vel.
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
		// ha m�r teljesen �tfordult, el�r�l kezd�dik a forgat�s
		if(nextRotationIndex < rotations.size() - 1)
			nextRotationIndex++;
		else
			nextRotationIndex = 0;
	}
	
	/**
	 * Kisz�molja, hogy mik lesznek a k�vetkez� forgat�s ut�ni koordni�t�k, de nem forgatja el.
	 * @return A forgat�s ut�ni koordin�t�k.
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
	 * Inicializ�lja az alakzathoz tartoz� forgat�si mint�t, ami megadja, hogy a k�vetkez�
	 * forgat�shoz hogyan kell �thelyezni az alakzat n�gyzeteit.
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
	 * A Serializable ki�r� f�ggv�ny�nek implement�ci�ja.
	 * ObjectOutputStream p�ld�ny.writeObject(Tetromino p�ld�ny) m�don lehet haszn�lni.
	 * @param out A kimeneti stream, amire az objektumot �rja.
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
	 * Beolvas egy ObjectOutputStream-re ki�rt Tetromino objektumot.
	 * @param in A bemeneti stream, amir�l olvas.
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
			new ErrorAlert(e.toString(), "J�t�k v�ge");
		}
	}
}
