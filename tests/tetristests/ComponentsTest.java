package tetristests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.input.KeyCode;
import tetris.Components;
import tetris.Tetromino;

class ComponentsTest {
	Components comp;
	
	@BeforeEach
	void setUp() {
		comp = new Components(30.0, 30.0);
		comp.init();
	}
	
	@Test
	void testInit() {
		comp.init();
		assertEquals(comp.getShapes()[0], "I");
	}
	
	@Test
	void testGetShapes() {
		String[] s = { "I", "L", "S", "O", "T", "J", "Z" };
		assertArrayEquals(comp.getShapes(), s);
	}
	
	@Test
	void testGetTetrominos() {
		assertEquals(1, comp.getTetrominos().size());
	}
	
	@Test
	void testGetCurrentT() {
		Tetromino t = comp.getCurrentT();
		assertEquals(4, t.getRectangles().size());
		boolean validShape = false;
		for(int i = 0; i < comp.getShapes().length; i++)
			if(comp.getShapes()[i].equals(t.getShape()))
				validShape = true;
		assertEquals(true, validShape);
	}
	
	@Test
	void testSetCurrentT() {
		comp.setCurrentT(new Tetromino("L", 0.0));
		assertEquals("L", comp.getCurrentT().getShape());
	}
	
	@Test
	void testGetLastShape() {
		assertNotEquals(comp.getNextShape(), comp.getLastShape());
		assertEquals(comp.getCurrentT().getShape(), comp.getLastShape());
	}
	
	@Test
	void testGetNextShape() {
		assertNotEquals(comp.getNextShape(), comp.getLastShape());
		comp.changeToNextShape();
		assertNotEquals(comp.getNextShape(), comp.getLastShape());
	}
	
	@Test
	void testNextShapeProperty() {
		assertEquals(comp.getNextShape(), comp.nextShapeProperty().get());
	}
	
	@Test
	void testChangeToNextShape() {
		String current = comp.getNextShape();
		comp.changeToNextShape();
		assertNotEquals(current, comp.getNextShape());
	}
	
	@Test
	void testSaveGameState() throws FileNotFoundException, IOException {
		assertThrows(IOException.class, () -> comp.saveGameState(new ObjectOutputStream(new FileOutputStream(""))), "IOException-t vártunk");
	}
	
	@Test
	void testHitBottom() {
		assertEquals(true, comp.hitBottom());
		comp = new Components(0.0, 160.0);
		assertEquals(false, comp.hitBottom());
	}
	
	@Test
	void testMove() {
		double[] startingCoords = { comp.getCurrentT().getRectangles().get(0).getLayoutX(), comp.getCurrentT().getRectangles().get(0).getLayoutY() }; 
		comp.move(KeyCode.DOWN);
		double[] downCoords = { comp.getCurrentT().getRectangles().get(0).getLayoutX(), comp.getCurrentT().getRectangles().get(0).getLayoutY() };
		comp.move(KeyCode.RIGHT);
		double[] rightCoords = { comp.getCurrentT().getRectangles().get(0).getLayoutX(), comp.getCurrentT().getRectangles().get(0).getLayoutY() };
		comp.move(KeyCode.LEFT);
		double[] leftCoords = { comp.getCurrentT().getRectangles().get(0).getLayoutX(), comp.getCurrentT().getRectangles().get(0).getLayoutY() };
		assertArrayEquals(startingCoords, downCoords);
		assertArrayEquals(startingCoords, leftCoords);
		assertArrayEquals(startingCoords, rightCoords);
	}
}
