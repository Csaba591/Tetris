package tetristests;

import tetris.Tetromino;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TetrominoTest {
	Tetromino t;
	@BeforeEach
	void init() {
		t = new Tetromino("L", 60.0);
	}
	
	@Test
	void testNumberOfRectangles() {
		assertEquals(4, t.getRectangles().size());
		t = new Tetromino("I", 0.0);
		assertEquals(4, t.getRectangles().size());
	}
	
	@Test
	void testSideSize() {
		assertEquals(30.0, t.getSideSize());
	}
	
	@Test
	void testShape() {
		assertEquals("L", t.getShape());
		t = new Tetromino("O", 120.0);
		assertEquals("O", t.getShape());
	}
	
	@Test
	void testRemoveRow() {
		t.removeRow(0);
		assertEquals(3, t.getRectangles().size());
		t = new Tetromino("O", 0.0);
		t.removeRow(0);
		assertEquals(2, t.getRectangles().size());
	}
	
	@Test
	void testMoveDown() {
		double[] startingYCoordinates = new double[4];
		for(int i = 0; i < t.getRectangles().size(); i++)
			startingYCoordinates[i] = t.getRectangles().get(i).getLayoutY();
		t.moveDown();
		double[] movedYCoordinates = new double[4];
		for(int i = 0; i < t.getRectangles().size(); i++)
			movedYCoordinates[i] = t.getRectangles().get(i).getLayoutY();
		
		for(int i = 0; i < startingYCoordinates.length; i++)
			assertEquals(startingYCoordinates[i], movedYCoordinates[i] - t.getSideSize());
	}
	
	@Test
	void testRotate() {
		double[][] startingCoordinates = new double[4][2];
		for(int i = 0; i < t.getRectangles().size(); i++) {
			startingCoordinates[i][0] = t.getRectangles().get(i).getLayoutX();
			startingCoordinates[i][1] = t.getRectangles().get(i).getLayoutY();
		}
		t.rotate();
		double[][] rotatedCoordinates = new double[4][2];
		for(int i = 0; i < t.getRectangles().size(); i++) {
			rotatedCoordinates[i][0] = t.getRectangles().get(i).getLayoutX();
			rotatedCoordinates[i][1] = t.getRectangles().get(i).getLayoutY();
		}
		assertEquals(startingCoordinates[0][0] - 1*t.getSideSize(), rotatedCoordinates[0][0]);
		assertEquals(startingCoordinates[0][1] + 2*t.getSideSize(), rotatedCoordinates[0][1]);
		assertEquals(startingCoordinates[1][0], rotatedCoordinates[1][0]);
		assertEquals(startingCoordinates[1][1] + 1*t.getSideSize(), rotatedCoordinates[1][1]);
		assertEquals(startingCoordinates[2][0] + 1*t.getSideSize(), rotatedCoordinates[2][0]);
		assertEquals(startingCoordinates[2][1], rotatedCoordinates[2][1]);
		assertEquals(startingCoordinates[3][0], rotatedCoordinates[3][0]);
		assertEquals(startingCoordinates[3][1] - 1*t.getSideSize(), rotatedCoordinates[3][1]);
	}
	
	@Test
	void testFullRotation() {
		double[][] startingCoords = new double[4][2];
		for(int i = 0; i < 4; i++) {
			startingCoords[i][0] = t.getRectangles().get(i).getLayoutX();
			startingCoords[i][1] = t.getRectangles().get(i).getLayoutY();
		}
		for(int i = 0; i < 4; i++)
			t.rotate();
		double[][] rotatedCoords = new double[4][2];
		for(int i = 0; i < 4; i++) {
			rotatedCoords[i][0] = t.getRectangles().get(i).getLayoutX();
			rotatedCoords[i][1] = t.getRectangles().get(i).getLayoutY();
		}
		for(int i = 0; i < 4; i++)
			assertArrayEquals(startingCoords[i], rotatedCoords[i]);
	}
}
