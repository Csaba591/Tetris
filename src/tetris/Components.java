package tetris;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

/**
 * A j�t�kt�r komponensei, azaz az azt alkot� alakzatok t�rol�s�t �s kezel�s�t l�tja el.
 */
public class Components {
	private ArrayList<Tetromino> tetrominos;
	private String[] shapes = { "I", "L", "S", "O", "T", "J", "Z" };
	private Random rand = new Random(System.currentTimeMillis());
	private Tetromino currentT;
	private double startingX;
	private String lastShape;
	private double width, height;
	private SimpleStringProperty nextShape;
	
	/**
	 * L�trehoz egy komponenskezel� �s t�rol� p�ld�nyt.
	 * @param gameViewWidth A j�t�kt�r sz�less�ge.
	 * @param gameViewHeight A j�t�kt�r magass�ga.
	 */
	public Components(double gameViewWidth, double gameViewHeight) {
		width = gameViewWidth;
		height = gameViewHeight;
		startingX = width / 2.0 - 30.0;
		tetrominos = new ArrayList<Tetromino>();
		nextShape = new SimpleStringProperty();
		init();
	}
	
	/**
	 * Inicializ�lja a kezd� alakzatokat:
	 * Be�ll�tja az els� alakzatot.
	 * Kisorsolja ez alapj�n a k�vetkez�t.
	 * Hozz�adja a n�zethez az els�t.
	 */
	public void init() {
		tetrominos.clear();
		lastShape = shapes[rand.nextInt(shapes.length)];
		nextShape.set(lastShape);
		while (nextShape.get().equals(lastShape))
			nextShape.set(shapes[rand.nextInt(shapes.length)]);
		tetrominos.add(new Tetromino(lastShape, startingX));
		currentT = tetrominos.get(0);
	}
	
	/**
	 * Megvizsg�lja hogy a jelenleg ir�ny�tott alakzat el�rte-e m�r a k�perny� alj�t vagy egy m�sik alakzatra esett-e
	 * @return True, ha el�rte, false, ha nem.
	 */
	public boolean hitBottom() {
		double blockSideSize = currentT.getSideSize();
		for (Rectangle r : currentT.getRectangles())
			if (r.getLayoutY() + blockSideSize >= height)
				return true;
		for (int i = 0; i < tetrominos.size(); i++)
			for (Rectangle r : tetrominos.get(i).getRectangles())
				for (Rectangle curr : currentT.getRectangles())
					if (tetrominos.get(i).equals(currentT))
						break;
					else if (curr.getLayoutY() + blockSideSize == r.getLayoutY()
							&& curr.getLayoutX() == r.getLayoutX()) {
						return true;
					}
		return false;
	}
	
	public boolean stackedUp() {
		double blockSideSize = currentT.getSideSize();
		int blockCount = 1;
		for(double y = blockSideSize; y < height; y += blockSideSize)
			outer:
			for(Tetromino t : tetrominos)
				for(Rectangle r : t.getRectangles())
					if(r.getLayoutY() == y && (r.getLayoutX() == startingX || r.getLayoutX() == startingX + blockSideSize)) {
						blockCount++; 
						break outer;
					}
		return blockCount == (int) height / blockSideSize;
	}
	
	/**
	 * Megvizsg�lja, hogy a param�terk�nt �tadott sz�madik sor tele van-e.
	 * @param row A sor sz�ma
	 * @return True, ha tele van, false, ha nincs.
	 */
	public boolean rowIsFull(double row) {
		double blockSideSize = currentT.getSideSize();
		int blockCount = 0;
		for (Tetromino t : tetrominos)
			for (Rectangle r : t.getRectangles())
				if (r.getLayoutY() == row * blockSideSize)
					blockCount++;
		return blockCount == (int) (width / blockSideSize);
	}
	
	/**
	 * Mozgatja az �ppen kezelt alakzatot a lenyomott billenty� k�dja alapj�n.
	 * @param direction A le�t�tt billenty� k�dja.
	 */
	public void move(KeyCode direction) {
		double newX;
		double blockSideSize = currentT.getSideSize();
		// Ha balra mozgatn� �s tud is arra mozogni, minden n�gyzet�t egy n�gyzetoldalnyival balra helyezi
		if (direction.equals(KeyCode.LEFT) && canMove(direction))
			for (Rectangle r : currentT.getRectangles()) {
				newX = r.getLayoutX() - blockSideSize;
				if (newX >= 0)
					r.setLayoutX(newX);
			}
		// Ha jobbra mozgatn� �s tud is arra mozogni, minden n�gyzet�t egy n�gyzetoldalnyival jobbra helyezi
		else if (direction.equals(KeyCode.RIGHT) && canMove(direction))
			for (Rectangle r : currentT.getRectangles()) {
				newX = r.getLayoutX() + blockSideSize;
				if (newX + blockSideSize <= width)
					r.setLayoutX(newX);
			}
		// Ha lefel� mozgatn� �s m�g nem �rt le, minden n�gyzet�t egy n�gyzetoldalnyival lejjebb helyezi
		else if (direction.equals(KeyCode.DOWN) && canMove(direction))
			currentT.moveDown();
		else if (direction.equals(KeyCode.UP))
			currentT.rotate();
	}
	
	/**
	 * Megvizsg�lja, hogy a jelenleg ir�ny�tott alakzat tud-e a direction ir�nyba mozogni.
	 * Ha oldalra mozogna, de fal vagy m�sik alakzat van mellette, nem tud oldalra mozogni.
	 * Ha lefel� mozogna, de a k�perny� alj�ra �rt vagy m�sik alakzat van alatta, nem tud mozogni.
	 * @param direction A mozg�s ir�ny�nak billenty�k�dja.
	 * @return True, ha mozoghat, false, ha nem.
	 */
	private boolean canMove(KeyCode direction) {
		double blockSideSize = currentT.getSideSize(); // egy n�gyzet oldal�nak m�rete
		// ha jobbra vagy balra mozogna
		if (direction.equals(KeyCode.LEFT) || direction.equals(KeyCode.RIGHT)) {
			for (Rectangle r : currentT.getRectangles()) {
				if (direction.equals(KeyCode.LEFT) && r.getLayoutX() - blockSideSize < 0)
					return false;
				else if (direction.equals(KeyCode.RIGHT) && r.getLayoutX() + blockSideSize >= width)
					return false;
			}
			for (int i = 0; i < tetrominos.size(); i++) {
				if (tetrominos.get(i).equals(currentT))
					break;
				for (Rectangle r : tetrominos.get(i).getRectangles())
					for (Rectangle curr : currentT.getRectangles())
						if (direction.equals(KeyCode.LEFT) && curr.getLayoutX() - blockSideSize == r.getLayoutX()
								&& curr.getLayoutY() == r.getLayoutY())
							return false;
						else if (direction.equals(KeyCode.RIGHT)
								&& curr.getLayoutX() + blockSideSize == r.getLayoutX()
								&& curr.getLayoutY() == r.getLayoutY())
							return false;
			}
		// ha lefel� mozogna
		} else if (direction.equals(KeyCode.DOWN) && hitBottom())
			return false;
		return true;
	}
	
	/**
	 * A lehets�ges alakzatok bet�s form�inak list�j�t adja meg.
	 * @return A lehets�ges alakzatok bet�k�nt egy t�mbben.
	 */
	public String[] getShapes() {
		return shapes;
	}
	
	/**
	 * A j�t�kt�ren l�v� alakzatok list�j�t adja meg.
	 * @return A j�t�kt�ren l�v� alakzatok egy ArrayListben.
	 */
	public ArrayList<Tetromino> getTetrominos() {
		return tetrominos;
	}
	
	/**
	 * A jelenleg lees� alakzatot adja meg.
	 * @return A jelenleg lees� alakzat, amit a j�t�kos ir�ny�t.
	 */
	public Tetromino getCurrentT() {
		return currentT;
	}
	
	/**
	 * t-re v�ltoztatja a jelenleg lees� alakzatot.
	 * @param t Az alakzat amely ezut�n le fog esni.
	 */
	public void setCurrentT(Tetromino t) {
		currentT = t;
	}
	
	/**
	 * Az el�z�nek leesett alakzat alakj�t adja meg bet�k�nt.
	 * @return Az el�z�nek leesett alakzat alakja.
	 */
	public String getLastShape() {
		return lastShape;
	}
	
	/**
	 * A jelenleg lees� ut�n k�vetkez� alakzatot adja meg.
	 * �gy a j�t�kos l�thatja, hogy mi lesz a k�vetkez�.
	 * @return A k�vetkez�nek sorsolt alakzat alakja bet�k�nt.
	 */
	public String getNextShape() {
		return nextShape.get();
	}
	
	/**
	 * A k�vetkez� alakzat alakj�t adja meg, mint Observable String.
	 * Ezzel az oldals� n�zet automatikusan friss�ti az ott megjelen�tettet.
	 * @return A k�vetkez� alakzat alakja Observable Stringk�nt.
	 */
	public SimpleStringProperty nextShapeProperty() {
		return nextShape;
	}
	
	/**
	 * Az el�z� alakzatot a jelenlegire �ll�tja.
	 * A jelenlegit a k�vetkez�re.
	 * Kisorsolja a k�vetkez� alakzatot, �gy, hogy az k�l�nb�zz�n az el�z� kett�t�l.
	 */
	public void changeToNextShape() {
		lastShape = currentT.getShape();
		String newShape = nextShape.get();
		nextShape.set(shapes[rand.nextInt(shapes.length)]);
		while (nextShape.get().equals(newShape) || nextShape.get().equals(lastShape))
			nextShape.set(shapes[rand.nextInt(shapes.length)]);
		tetrominos.add(new Tetromino(newShape, width / 2 - currentT.getSideSize()));
		currentT = tetrominos.get(tetrominos.size() - 1);
	}
	
	/**
	 * Szerializ�lja a j�t�k jelenlegi �ll�s�t a ponttal egy�tt, hogy legk�zelebb be lehessen t�lteni
	 * �s innen folytathassa a j�t�kos.
	 * @param out A ki�r�sra haszn�lt ObjectOutputStream.
	 */
	public void saveGameState(ObjectOutputStream out) {
		try {
			for (Tetromino t : this.tetrominos)
				out.writeObject(t);
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Error");
		}
	}
}
