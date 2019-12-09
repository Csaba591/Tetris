package tetris;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

/**
 * A játéktér komponensei, azaz az azt alkotó alakzatok tárolását és kezelését látja el.
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
	 * Létrehoz egy komponenskezelõ és tároló példányt.
	 * @param gameViewWidth A játéktér szélessége.
	 * @param gameViewHeight A játéktér magassága.
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
	 * Inicializálja a kezdõ alakzatokat:
	 * Beállítja az elsõ alakzatot.
	 * Kisorsolja ez alapján a következõt.
	 * Hozzáadja a nézethez az elsõt.
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
	 * Megvizsgálja hogy a jelenleg irányított alakzat elérte-e már a képernyõ alját vagy egy másik alakzatra esett-e
	 * @return True, ha elérte, false, ha nem.
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
	 * Megvizsgálja, hogy a paraméterként átadott számadik sor tele van-e.
	 * @param row A sor száma
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
	 * Mozgatja az éppen kezelt alakzatot a lenyomott billentyû kódja alapján.
	 * @param direction A leütött billentyû kódja.
	 */
	public void move(KeyCode direction) {
		double newX;
		double blockSideSize = currentT.getSideSize();
		// Ha balra mozgatná és tud is arra mozogni, minden négyzetét egy négyzetoldalnyival balra helyezi
		if (direction.equals(KeyCode.LEFT) && canMove(direction))
			for (Rectangle r : currentT.getRectangles()) {
				newX = r.getLayoutX() - blockSideSize;
				if (newX >= 0)
					r.setLayoutX(newX);
			}
		// Ha jobbra mozgatná és tud is arra mozogni, minden négyzetét egy négyzetoldalnyival jobbra helyezi
		else if (direction.equals(KeyCode.RIGHT) && canMove(direction))
			for (Rectangle r : currentT.getRectangles()) {
				newX = r.getLayoutX() + blockSideSize;
				if (newX + blockSideSize <= width)
					r.setLayoutX(newX);
			}
		// Ha lefelé mozgatná és még nem ért le, minden négyzetét egy négyzetoldalnyival lejjebb helyezi
		else if (direction.equals(KeyCode.DOWN) && canMove(direction))
			currentT.moveDown();
		else if (direction.equals(KeyCode.UP))
			currentT.rotate();
	}
	
	/**
	 * Megvizsgálja, hogy a jelenleg irányított alakzat tud-e a direction irányba mozogni.
	 * Ha oldalra mozogna, de fal vagy másik alakzat van mellette, nem tud oldalra mozogni.
	 * Ha lefelé mozogna, de a képernyõ aljára ért vagy másik alakzat van alatta, nem tud mozogni.
	 * @param direction A mozgás irányának billentyûkódja.
	 * @return True, ha mozoghat, false, ha nem.
	 */
	private boolean canMove(KeyCode direction) {
		double blockSideSize = currentT.getSideSize(); // egy négyzet oldalának mérete
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
		// ha lefelé mozogna
		} else if (direction.equals(KeyCode.DOWN) && hitBottom())
			return false;
		return true;
	}
	
	/**
	 * A lehetséges alakzatok betûs formáinak listáját adja meg.
	 * @return A lehetséges alakzatok betûként egy tömbben.
	 */
	public String[] getShapes() {
		return shapes;
	}
	
	/**
	 * A játéktéren lévõ alakzatok listáját adja meg.
	 * @return A játéktéren lévõ alakzatok egy ArrayListben.
	 */
	public ArrayList<Tetromino> getTetrominos() {
		return tetrominos;
	}
	
	/**
	 * A jelenleg leesõ alakzatot adja meg.
	 * @return A jelenleg leesõ alakzat, amit a játékos irányít.
	 */
	public Tetromino getCurrentT() {
		return currentT;
	}
	
	/**
	 * t-re változtatja a jelenleg leesõ alakzatot.
	 * @param t Az alakzat amely ezután le fog esni.
	 */
	public void setCurrentT(Tetromino t) {
		currentT = t;
	}
	
	/**
	 * Az elõzõnek leesett alakzat alakját adja meg betûként.
	 * @return Az elõzõnek leesett alakzat alakja.
	 */
	public String getLastShape() {
		return lastShape;
	}
	
	/**
	 * A jelenleg leesõ után következõ alakzatot adja meg.
	 * Így a játékos láthatja, hogy mi lesz a következõ.
	 * @return A következõnek sorsolt alakzat alakja betûként.
	 */
	public String getNextShape() {
		return nextShape.get();
	}
	
	/**
	 * A következõ alakzat alakját adja meg, mint Observable String.
	 * Ezzel az oldalsó nézet automatikusan frissíti az ott megjelenítettet.
	 * @return A következõ alakzat alakja Observable Stringként.
	 */
	public SimpleStringProperty nextShapeProperty() {
		return nextShape;
	}
	
	/**
	 * Az elõzõ alakzatot a jelenlegire állítja.
	 * A jelenlegit a következõre.
	 * Kisorsolja a következõ alakzatot, úgy, hogy az különbözzön az elõzõ kettõtõl.
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
	 * Szerializálja a játék jelenlegi állását a ponttal együtt, hogy legközelebb be lehessen tölteni
	 * és innen folytathassa a játékos.
	 * @param out A kiírásra használt ObjectOutputStream.
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
