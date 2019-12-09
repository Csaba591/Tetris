package tetristests;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tetris.*;


class ScoreTest {
	Score s;
	
	@BeforeEach
	void init() {
		s = new Score("Test", 11);
	}
	
	@Test
	void testPlayerNameOnlyConstructor() {
		s = new Score("Testtest");
		assertEquals("Testtest", s.getPlayerName());
		assertEquals(0, s.getPoints());
	}
	
	@Test
	void testAddPoints() {
		s.addPoints(3);
		assertEquals(14, s.getPoints());
	}
	
	@Test
	void testIncrementPoints() {
		s.incrementPoints();
		assertEquals(12, s.getPoints());
	}
	
	@Test
	void testSetPlayerName() {
		s.setPlayerName("Player");
		assertEquals("Player", s.getPlayerName());
	}

	@Test
	void testPlayerNameProperty() {
		assertEquals("Test", s.playerNameProperty().get());
	}
	
	@Test
	void testPointsProperty() {
		assertEquals(11, s.pointsProperty().get());
	}
	
	@Test
	void testSetPoints() {
		s.setPoints(32);
		assertEquals(32, s.getPoints());
	}
	
}
