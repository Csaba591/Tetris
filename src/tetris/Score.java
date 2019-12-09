package tetris;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A játékos pontját tárolja és kezeli.
 * Szerializálható, így amikor a játékos elmenti a játékot a pontszámot is menti.
 */
public class Score implements Serializable {
	private static final long serialVersionUID = -7280582741038120280L;
	private SimpleStringProperty playerName;
	private SimpleIntegerProperty points;
	
	/**
	 * Létrehoz egy pontszám példányt pName játékosnak 0 ponttal.
	 * @param pName A játékos neve.
	 */
	public Score(String pName) {
		playerName = new SimpleStringProperty(pName);
		points = new SimpleIntegerProperty(0);
	}
	
	/**
	 * Létrehoz egy pontszám példányt pName játékosnak, points darab ponttal.
	 * @param pName A játékos neve.
	 * @param points A pontszáma.
	 */
	public Score(String pName, int points) {
		playerName = new SimpleStringProperty(pName);
		this.points = new SimpleIntegerProperty(points);
	}
	
	/**
	 * Egyel növeli a pontszámot.
	 * Egy betelt sor törléséért jár pont.
	 */
	public void incrementPoints() {
		points.set(points.get()+1);
	}
	
	/**
	 * points mennyiségû pontot ad a játékos pontszámához.
	 * @param points A pontszámhoz adandó mennyiség.
	 */
	public void addPoints(int points) {
		this.points.set(this.points.get()+points);
	}
	
	/**
	 * A játékos neve. Observable String objektum.
	 * @return Observable String, ami a játékos neve.
	 */
	public SimpleStringProperty playerNameProperty() {
		return playerName;
	}
	
	/**
	 * A játékos pontszáma. Observable Integer objektum.
	 * @return Observable Integer, ami a játékos pontszáma.
	 */
	public SimpleIntegerProperty pointsProperty() {
		return points;
	}
	
	/**
	 * A játékos neve sima Stringként.
	 * @return A játékos neve.
	 */
	public String getPlayerName() {
		return playerName.get();
	}
	
	/**
	 * A játékos pontszáma primitív int-ként.
	 * @return A játékos pontszáma.
	 */
	public int getPoints() {
		return points.get();
	}
	
	/**
	 * playerName-re állítja a játékos nevét.
	 * @param playerName A játékos új neve.
	 */
	public void setPlayerName(String playerName) {
		this.playerName.set(playerName);
	}
	
	/**
	 * ponts-ra állítja a játékos pontszámát.
	 * @param points A játékos új pontszáma.
	 */
	public void setPoints(Integer points) {
		this.points.set(points);
	}
	
	/**
	 * Elmenti a játékos pontszámát.
	 * Hozzáírja a nevet és pontszámot egy közös adattároló txt-be.
	 * Formátum:
	 * [név]
	 * [pontszám]
	 */
	public void save() {
		try {
			FileWriter fw = new FileWriter("scores.txt", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println(playerName.get());
			pw.println(points.get());
			pw.close();
		} catch (IOException e) {
			new ErrorAlert(e.toString(), "Error");
		}
	}
}
